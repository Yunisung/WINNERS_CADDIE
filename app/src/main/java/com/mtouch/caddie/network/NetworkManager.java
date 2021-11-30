package com.mtouch.caddie.network;

import android.content.Context;
import android.os.Build;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.mtouch.caddie.BuildConfig;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    public static String BASE_URL = BuildConfig.BASE_URL; //"https://svctms.mtouch.com";
    //    public static final String BASE_URL = "http://192.168.0.53:10005";
    public static final String BASE_API_URL = BuildConfig.BASE_API_URL;
    public static final String BASE_SMS_URL = "https://sms.supersms.co:7020";

    public static PersistentCookieJar cookieJar = null;
    private static Retrofit retrofit = null;

    public static CaddieAPIService getAPIService(Context context) {
        if(cookieJar==null) cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
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
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(CaddieAPIService.class);
    }

}
