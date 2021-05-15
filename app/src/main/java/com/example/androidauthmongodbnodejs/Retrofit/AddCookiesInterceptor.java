package com.example.androidauthmongodbnodejs.Retrofit;

import java.io.IOException;
import java.util.HashSet;

import android.content.Context;
import android.util.Log;

import okhttp3.Interceptor;
import okhttp3.Request;

public class AddCookiesInterceptor implements Interceptor {
    private CookieSharedPreferences mDsp;
    public AddCookiesInterceptor(Context context){
        mDsp = CookieSharedPreferences.getInstanceOf(context);
    }
    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        HashSet<String> preferences = (HashSet) mDsp.getHashSet(CookieSharedPreferences.KEY_COOKIE, new HashSet<String>());
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
            Log.v("OkHttp", "Adding Header: " + cookie);
        }
        return chain.proceed(builder.build());
    }
}