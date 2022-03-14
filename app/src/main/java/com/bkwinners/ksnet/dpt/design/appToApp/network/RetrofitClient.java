package com.bkwinners.ksnet.dpt.design.appToApp.network;

import android.os.Build;

import com.bkwinners.ksnet.dpt.ks03.pay.Constants;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;
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
                            .header("User-Agent","caddie_version : " + BuildConfig.VERSION_NAME
                                    + " / token : " + Constants.KEY_TOKEN
                                    + " / log_os : " + Build.VERSION.SDK_INT
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

