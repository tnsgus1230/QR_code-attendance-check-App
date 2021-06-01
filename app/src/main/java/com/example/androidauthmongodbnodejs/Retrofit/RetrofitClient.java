package com.example.androidauthmongodbnodejs.Retrofit;

import android.content.Context;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit instance = null;

    public static Retrofit getInstance(Context context) {

        AddCookiesInterceptor in1 = new AddCookiesInterceptor(context);
        ReceivedCookiesInterceptor in2 = new ReceivedCookiesInterceptor(context);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder ()
                .addNetworkInterceptor(in1)
                .addInterceptor(in2)
                .build ();

        if (instance==null) {
            instance = new Retrofit.Builder()
//                    .baseUrl("http://192.168.0.31:3070/")
                    .baseUrl("https://www.babyforce.xyz/")
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return instance;
    }
}