package com.example.androidauthmongodbnodejs.Retrofit;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;


public class ReceivedCookiesInterceptor implements Interceptor {
    private CookieSharedPreferences mDsp;

    public ReceivedCookiesInterceptor(Context context){
        mDsp = CookieSharedPreferences.getInstanceOf(context);
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

            okhttp3.Response originalResponse = chain.proceed(chain.request());

            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                HashSet<String> cookies = new HashSet<>();

                for (String header : originalResponse.headers("Set-Cookie")) {
                    cookies.add(header);
                }
                mDsp.putHashSet(CookieSharedPreferences.KEY_COOKIE, cookies);
            }
            return originalResponse;
    }
}