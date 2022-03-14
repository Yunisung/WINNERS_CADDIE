package com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.retrofitclient;

import android.os.Build;

import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.httpcomunity.api.APIService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = BuildConfig.BASE_TMS_URL; //"https://svctms.mtouch.com";
    public static final String BASE_API_URL = BuildConfig.BASE_API_URL;//"https://svcapi.mtouch.com";
    //public static final String BASE_SMS_URL = "https://sms.supersms.co:7020";
    public static final String BASE_SMS_URL = "http://link.smsceo.co.kr/sendlms_utf8.php";
    public static final String BASE_SMS_FILE_UPLOAD_URL = "https://file.supersms.co:7010";

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static APIService getAPIDirectService(){
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
                                    + " / token : " + Constants.TOKEN
                                    + " / log_os : " +Build.VERSION.SDK_INT
                                    +" / log_model : "+Build.MODEL)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();



        return retrofit.create(APIService.class);
    }

    public static APIService getSMSSendService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_SMS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit.create(APIService.class);
    }

    public static APIService getSMSFileUploadService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_SMS_FILE_UPLOAD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit.create(APIService.class);
    }

}