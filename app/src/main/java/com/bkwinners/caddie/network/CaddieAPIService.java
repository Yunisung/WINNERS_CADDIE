package com.bkwinners.caddie.network;

import com.bkwinners.caddie.network.model.OrderListDetailResponse;
import com.bkwinners.caddie.network.model.OrderListResponse;
import com.bkwinners.caddie.network.model.OrderPayResponse;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.DirectPayment;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.FormUrlEncoded;

public interface CaddieAPIService {

/* 터미널 서버에서 넘어온것들 */
    //PYS : 결제내역조회
    @FormUrlEncoded
    @POST("/v0/trx/summary")
    Call<Response> getSummary(@FieldMap HashMap<String, Object> body);

    //PYS : 터미널 정보와 가맹점 정보조회
    @FormUrlEncoded
    @POST("/v0/key")
    Call<Response> getStringToken(@FieldMap HashMap<String, Object> body);

    //PYS : 결제 총 금액, 결제횟수 조회
    @FormUrlEncoded
    @POST("/v0/trx/statistics")
    Call<Response> getStatistics(@FieldMap HashMap<String, Object> body);

/* 터미널 서버에서 넘어온것들 */

    //PYS : 영수증 전송
    @FormUrlEncoded
    @POST("receipt/sms")
    Call<Response> sendReceiptSMS(@FieldMap HashMap<String, Object> body);

    //PYS : 인증번호 전송
    @FormUrlEncoded
    @POST("login/sms")
    Call<Response> sendSMS(@FieldMap HashMap<String, Object> body);

    //PYS : 인증번호 확인
    @FormUrlEncoded
    @POST("login/smsauth")
    Call<Response> sendAuthSMS(@FieldMap HashMap<String, Object> body);

    //PYS : 회원가입
    @FormUrlEncoded
    @POST("login/signup")
    Call<Response> signup(@FieldMap HashMap<String, Object> body);

    //PYS : 주민번호 or 사업자번호 등록되있는지 조회
    @FormUrlEncoded
    @POST("login/signUpCheck")
    Call<Response> signUpCheck(@FieldMap HashMap<String, Object> body);

    //PYS : 로그인 절차
    @FormUrlEncoded
    @POST("login/in")
    Call<Response> login(@FieldMap HashMap<String, Object> body);

    //PYS : 로그아웃
    @FormUrlEncoded
    @POST("login/out")
    Call<Response> logout(@FieldMap HashMap<String, Object> body);

    //PYS : GC_USER 테이블에 아이디 존재하는지 확인
    @FormUrlEncoded
    @POST("mcht/check")
    Call<Response> check(@FieldMap HashMap<String, Object> body);

    //PYS : 사용안함
    @FormUrlEncoded
    @POST("/mcht/apply")
    Call<Response> mchtApply(@FieldMap HashMap<String, Object> body);

    //PYS : 결제
    @FormUrlEncoded
    @POST("/mcht/order")
    Call<OrderListDetailResponse> orderApply(@FieldMap HashMap<String, Object> body);

    //PYS : 결제 내역
    @FormUrlEncoded
    @POST("/mcht/orderList")
    Call<OrderListResponse> orderList(@FieldMap HashMap<String, Object> body);

    //PYS : 결제 내역 자세히
    @FormUrlEncoded
    @POST("/mcht/orderListDetail")
    Call<OrderListDetailResponse> orderListDetail(@FieldMap HashMap<String, Object> body);

    //PYS : SMS결제
    @FormUrlEncoded
    @POST("/sms/pay")
    Call<OrderPayResponse> smsPay(@FieldMap HashMap<String, Object> body);

    //PYS : SMS결제 성공처리
    @FormUrlEncoded
    @POST("/sms/payPush")
    Call<Response> smsPayPush(@FieldMap HashMap<String, Object> body);

    //PYS : SMS결제 상태체크
    @FormUrlEncoded
    @POST("/sms/payCheck")
    Call<Response> smsPayCheck(@FieldMap HashMap<String, Object> body);

    //PYS : 카드결제
    @FormUrlEncoded
    @POST("/card/pay")
    Call<OrderPayResponse> cardPay(@FieldMap HashMap<String, Object> body);











    //Eform
    @FormUrlEncoded
    @POST("/eform/eform_token")
    Call<Response> getEformToken(@FieldMap HashMap<String, Object> body);

    @FormUrlEncoded
    @POST("/eform/form")
    Call<Response> getEform(@FieldMap HashMap<String, Object> body);

    @FormUrlEncoded
    @POST("/eform/eform_detail")
    Call<Response> getEformDetail(@FieldMap HashMap<String, Object> body);

    @FormUrlEncoded
    @POST("/eform/eform_send")
    Call<Response> sendEform(@FieldMap HashMap<String, Object> body);













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


//    @Headers({"Content-Type: application/json", "Accept: application/json", "Accept-Language: ko_KR"})
//    @POST("/sms/v3/multiple-destinations")
//    Call<ResponseBody> sendSMS(@Header("Authorization") String token,
//                               @Body HashMap body
//    );

    @Multipart
//    @Headers({"Content-Type: multipart/form-data; boundary=\"mtouch_sms_file\"","Accept: application/json","Accept-Language: ko_KR"})
    @POST("/sms/v3/file")
    Call<ResponseBody> uploadReceipt(@Header("Authorization") String token,
                                     @PartMap Map<String, RequestBody> params
//                                     @Part("file\"; filename=\"receipt.jpeg\" ") RequestBody file


    );

}