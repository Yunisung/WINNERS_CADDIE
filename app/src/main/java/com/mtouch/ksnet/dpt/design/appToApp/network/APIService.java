package com.mtouch.ksnet.dpt.design.appToApp.network;

import com.mtouch.ksnet.dpt.design.appToApp.network.model.DirectPayment;
import com.mtouch.ksnet.dpt.design.appToApp.network.model.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface APIService {

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/summary")
    Call<ResponseBody> getSummary(@Header("Authorization") String token,
                                  @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/key")
    Call<ResponseBody> getStringToken(@Header("Authorization") String token,
                                      @Body Request body
    );

    @Headers("Content-Type: application/json")
    @GET("/v0/key")
    Call<ResponseBody> getStringToken(@Header("Authorization") String token
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/rule")
    Call<ResponseBody> getCheckApprove(@Header("Authorization") String token,
                                       @Body Request body
    );
    @Headers("Content-Type: application/json")
    @POST("/v0/trx/crule")
    Call<ResponseBody> getRefundCheckApprove(@Header("Authorization") String token,
                                             @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/mcht/name")
    Call<ResponseBody> getMchtName(@Body Request body);

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/push")
    Call<ResponseBody> getApproveComplete(@Header("Authorization") String token,
                                          @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/statistics")
    Call<ResponseBody> getPaymentStatistics(@Header("Authorization") String token,
                                            @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/list")
    Call<ResponseBody> getPaymentList(@Header("Authorization") String token,
                                      @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/trx/check")
    Call<ResponseBody> checkTrxId(@Header("Authorization") String token,
                                  @Body Request body
    );

    @Headers("Content-Type: application/json")
    @POST("/v0/wallet/check")
    Call<ResponseBody> checkWallet(@Header("Authorization") String token,
                                   @Body Request body
    );


    @Headers({"Content-Type: application/json", "Accept-Language: ko_KR"})
    @POST("/api/pay")
    Call<ResponseBody> sendDirectPayment(@Header("Authorization") String paykey,
                                         @Body DirectPayment body
    );

    @Headers({"Content-Type: application/json", "Accept-Language: ko_KR"})
    @POST("/api/refund")
    Call<ResponseBody> cancelDirectPayment(@Header("Authorization") String paykey,
                                           @Body HashMap body
    );

    @Headers({"Content-Type: application/json", "Accept-Language: ko_KR"})
    @GET("/api/get/{trxId}")
    Call<ResponseBody> getDirectPayment(@Header("Authorization") String paykey,
                                        @Path("trxId") String trxId
    );


    @Headers({"Content-Type: application/json", "Accept: application/json", "Accept-Language: ko_KR"})
    @POST("/sms/v3/multiple-destinations")
    Call<ResponseBody> sendSMS(@Header("Authorization") String token,
                               @Body HashMap body
    );

    @Multipart
//    @Headers({"Content-Type: multipart/form-data; boundary=\"mtouch_sms_file\"","Accept: application/json","Accept-Language: ko_KR"})
    @POST("/sms/v3/file")
    Call<ResponseBody> uploadReceipt(@Header("Authorization") String token,
                                     @PartMap Map<String, RequestBody> params
//                                     @Part("file\"; filename=\"receipt.jpeg\" ") RequestBody file


    );

}