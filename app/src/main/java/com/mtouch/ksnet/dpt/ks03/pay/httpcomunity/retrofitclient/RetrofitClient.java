package com.mtouch.ksnet.dpt.ks03.pay.httpcomunity.retrofitclient;

import android.os.Build;

import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest;

                    newRequest = request.newBuilder()
                            .header("User-Agent","[AppToApp] ksr03_version : "+ BuildConfig.VERSION_NAME
                                    + " / token : " + Constants.KEY_TOKEN
                                    + " / log_os : " +Build.VERSION.SDK_INT
                                    +" / log_model : "+Build.MODEL)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();


        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}

