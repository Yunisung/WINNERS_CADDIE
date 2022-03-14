package com.bkwinners.ksnet.dpt.design;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.Amount;
import com.bkwinners.caddie.network.model.OrderDetail;
import com.bkwinners.caddie.network.model.Response;
import com.bkwinners.ksnet.dpt.design.appToApp.network.APIService;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.DirectPayment;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchInstallmentDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchListDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class DirectPaymentActivity extends AppCompatActivity {

    private APIService mAPIService;
    private APIService mAPIDirectService;
    private APIService mAPISMSService;

    private CaddieAPIService caddieAPIService;

    private ImageView backButton;
    private TextView headerTitleTextView;

    private TextView placeTextView;
    private TextView amountInfoTextView;

    private EditText cardNumberEditText;
    private EditText amountEditText;
    private RelativeLayout yearButton;
    private TextView yearTextView;
    private RelativeLayout mouthButton;
    private TextView mouthTextView;
    private RelativeLayout installmentButton;
    private TextView installmentTextView;
    private EditText customerTelEditText;
    private EditText customerNameEditText;
    private EditText customerEmailEditText;

    private EditText productNameEditText;
    private Button payButton;


    private String yearString;
    private String mouthString;
    private String installmentString;

    private ArrayList<OrderDetail> orderDetailList;
    private OrderDetail orderDetail;

    private void bindViews() {
        placeTextView= findViewById(R.id.placeTextView);
        amountInfoTextView= findViewById(R.id.amountInfoTextView);

        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        amountEditText = findViewById(R.id.amountEditText);
        yearButton = findViewById(R.id.yearButton);
        yearTextView = findViewById(R.id.yearTextView);
        mouthButton = findViewById(R.id.mouthButton);
        mouthTextView = findViewById(R.id.mouthTextView);
        installmentButton = findViewById(R.id.installmentButton);
        installmentTextView = findViewById(R.id.installmentTextView);


        productNameEditText = findViewById(R.id.productNameEditText);
        payButton = findViewById(R.id.payButton);

        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);


        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("결제하기");


        yearButton.setOnClickListener(v->{
            MtouchListDialog mtouchListDialog = new MtouchListDialog(this, item -> {
                yearString = item;
                yearTextView.setText(item);
            }).setTitleText("유효기간(년)");

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);

            mtouchListDialog.addList(true,year+"");
            for(int i=1;i<10;i++){
                mtouchListDialog.addList(false,(year+i)+"");
            }

            mtouchListDialog.show();

        });
        mouthButton.setOnClickListener(v->{
            MtouchListDialog mtouchListDialog = new MtouchListDialog(this, item -> {
                mouthString = item;
                mouthTextView.setText(item);
            }).setTitleText("유효기간(월)");
            mtouchListDialog.addList(true,"01");
            for(int i=2;i<=12;i++){
                mtouchListDialog.addList(false, String.format("%02d",i));
            }

            mtouchListDialog.show();
        });
        installmentButton.setOnClickListener(v->{
            new MtouchInstallmentDialog(this, index -> {
                installmentString = index;
                if (installmentString.equals("0")) {
                    installmentTextView.setText("일시불");
                } else {
                    installmentTextView.setText(installmentString + "개월");
                }

            }).setTitleText("할부 기간")
                    .setPositiveButtonText("확인")
                    .setMaxInstallment(Integer.parseInt(SharedPreferenceUtil.getData(this, "apiMaxInstall", "0"))).show();
        });

        payButton.setOnClickListener(v->{
            payStart();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_payment_layout);

        bindViews();
        init();
    }

    private void init() {
        if(getIntent()!=null){
            orderDetailList = (ArrayList<OrderDetail>) getIntent().getSerializableExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST);
            if(orderDetailList!=null && orderDetailList.size()>0) {
                orderDetail = orderDetailList.get(0);
                orderDetailList.remove(0);


                //setting info
                String phoneNumber = orderDetail.getNumber();
                String amount = orderDetail.getTotalAmount();
//                String place = orderDetail.getPayInfo();
//                Amount amountInfo = orderDetail.getAmount();

                if(phoneNumber!=null && phoneNumber.length()>0)
                    customerTelEditText.setText(orderDetail.getNumber().trim());
                if(amount!=null && amount.length()>0)
                    amountEditText.setText(String.format("%,d",Integer.parseInt(orderDetail.getTotalAmount())));

//                if(place!=null && place.length()>0)
//                    placeTextView.setText(place.trim());
//                if(amountInfo!=null)
//                    amountInfoTextView.setText(amountInfo.trim());
            }
        }

        mAPIService = ApiUtils.getAPIService();
        mAPIDirectService = ApiUtils.getAPIDirectService();
        mAPISMSService = ApiUtils.getSMSSendService();
        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        yearString = year+"";
        yearTextView.setText(yearString);

        mouthString = "01";
        mouthTextView.setText(mouthString);

        installmentString = "0";
        installmentTextView.setText("일시불");

    }

    private void payStart() {
        String cardNumber = cardNumberEditText.getText().toString();
        if(cardNumber==null || cardNumber.length()==0){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("카드번호를 입력하세요.").show();
            return;
        }
        if(cardNumber.length()<14 || cardNumber.length()>16){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("카드번호는 14~16자리만 허용합니다.").show();
            return;
        }
        String amount = amountEditText.getText().toString();
        if(amount==null || amount.length()==0){
            new MtouchDialog(this,false).setTitleText("알림").setContentText("금액을 입력하세요.").show();
            return;
        }



//        DirectPayment pay = new DirectPayment();
//        pay.pay.put("trxType", "ONTR");
//        String trackId = orderDetail.getTrackId();
//        pay.pay.put("trackId", trackId);
//        pay.pay.put("amount", amount.replaceAll(",",""));
//        pay.pay.put("payerName", customerNameEditText.getText().toString().trim());
//        pay.pay.put("payerEmail", customerEmailEditText.getText().toString().trim());
//        pay.pay.put("payerTel", customerTelEditText.getText().toString().trim());
//        pay.pay.put("udf1", "");
//        pay.pay.put("udf2", "");
//        ArrayList products = new ArrayList<>();
//        HashMap<String, Object> map = new HashMap<>();
//        map.put("name", "캐디상품");
//        map.put("qty", 1);
//        map.put("price", amount);
//        map.put("desc", orderDetail.getAmountInfo());
//
//        products.add(map);
//        pay.pay.put("products", products);
//
//        final HashMap<String, Object> card = new HashMap<>();
//        card.put("number", cardNumber);
//        card.put("expiry", yearString.substring(2) + "" + mouthString);
//        card.put("installment", installmentString);
//        pay.pay.put("card", card);
//
//        HashMap<String, Object> metadata = new HashMap<>();
//        metadata.put("cardAuth", "false");//
//        metadata.put("authPw", ""); //카드비밀번호 두자리
//        metadata.put("authDob", "");
//        pay.pay.put("metadata", metadata);

        payButton.setEnabled(false);

//        HashMap<String, Object> params = new HashMap<>();
//        params.put("payKey", SharedPreferenceUtil.getData(this,"payKey"));
//        params.put("smsKey", orderDetail.getSmsKey());
//        params.put("trackId", orderDetail.getTrackId());
//        params.put("amount", amountEditText.getText().toString().trim().replaceAll(",",""));
//        params.put("payerName", customerNameEditText.getText().toString().trim());
//        params.put("payerEmail", customerEmailEditText.getText().toString().trim());
//        params.put("payerTel", customerEmailEditText.getText().toString().trim());
//        params.put("cardNumber", cardNumberEditText.getText().toString().trim());
//        params.put("expiry", yearString.substring(2) + "" + mouthString);
//        params.put("installment", installmentString);
//
//        caddieAPIService.smsPay(params).enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//
//            }
//        });
//        mAPIDirectService.sendDirectPayment(SharedPreferenceUtil.getData(this,"payKey"), pay).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    if (response.isSuccessful()) {
//                        /**
//                         *  "result": {
//                         *         "resultCd": "0000",
//                         *         "resultMsg": "정상",
//                         *         "advanceMsg": "정상승인",
//                         *         "create": "20200421170421"
//                         *       },
//                         *       "pay": {
//                         *         "authCd": "30036611",
//                         *         "card": {
//                         *           "cardId": "card_2b23-b4265c-e10-72cd7",
//                         *           "installment": 0,
//                         *           "bin": "540926",
//                         *           "last4": "0012",
//                         *           "issuer": "국민",
//                         *           "cardType": "신용",
//                         *           "acquirer": "국민",
//                         *           "issuerCode": "02",
//                         *           "acquirerCode": "02"
//                         *         },
//                         *         "products": [
//                         *           {
//                         *             "prodId": "",
//                         *             "name": "test",
//                         *             "qty": 1,
//                         *             "price": 1004,
//                         *             "desc": "서울시 서초구"
//                         *           }
//                         *         ],
//                         *         "transactionDate": "20200421170421",
//                         *         "trxId": "T200421215180",
//                         *         "trxType": "ONTR",
//                         *         "tmnId": "test0005",
//                         *         "trackId": "AXD_1587456245186",
//                         *         "amount": 1004,
//                         *         "udf1": "",
//                         *         "udf2": ""
//                         *       }
//                         *     }
//                         */
//
//                        String responseData = new String(response.body().bytes());
//
//
//                        JSONObject json = new JSONObject(responseData);
//                        if ("0000".equals(json.getJSONObject("result").getString("resultCd"))) {
//                            JSONObject cardJSON = json.getJSONObject("pay").getJSONObject("card");
//                            String trackId = json.getJSONObject("pay").getString("trackId");
//                            String amountString = json.getJSONObject("pay").getString("amount");
//                            String PurchaseName = cardJSON.getString("acquirer");
//                            String number = cardJSON.getString("bin") + "******" + cardJSON.getString("last4");
//                            String type = "승인";
//                            String authCd = json.getJSONObject("pay").getString("authCd");
//                            String AuthNum = json.getJSONObject("pay").getString("authCd");
//                            String TotalAmount = json.getJSONObject("pay").getString("amount");
//                            String installment = cardJSON.getString("installment").length() == 1 ? "0" + cardJSON.getString("installment") : cardJSON.getString("installment");
//                            String regDate = json.getJSONObject("pay").getString("transactionDate").substring(2);
//                            String CardNo = cardJSON.getString("bin") + "******" + cardJSON.getString("last4");
//                            String Message2 = "OK: " + json.getJSONObject("pay").getString("authCd");
//                            String Classification = "";
//                            String Status = "O";
//                            String Authdate = json.getJSONObject("pay").getString("transactionDate").substring(2);
//                            String authDay = Authdate.substring(0,6);
//                            String authTime = Authdate.substring(6);
//                            String trxId = json.getJSONObject("pay").getString("trxId");
//                            String TelegramType = "0210";
//                            String CardName = cardJSON.getString("issuer");
//                            String processingCd = "1004";
//                            String PurchaseCode = cardJSON.getString("acquirerCode");
//                            String issuer = cardJSON.getString("issuer");
//                            String IssueCode = cardJSON.getString("issuerCode");
//                            String purchaseName = cardJSON.getString("acquirer");
//                            String resultText = json.getJSONObject("result").getString("advanceMsg");
//
//                            String authDate = "";// "2019-11-08 13:33:45";
//                            try {//210129 171840
//                                authDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyMMddHHmmss").parse(Authdate));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            int vat = Integer.parseInt(amountString) / 11;
//                            int amount = Integer.parseInt(amountString) - vat;
//
//
//                            String name = SharedPreferenceUtil.getData(DirectPaymentActivity.this,"name");
//                            String ceoName = SharedPreferenceUtil.getData(DirectPaymentActivity.this,"ceoName");
//                            String identity = SharedPreferenceUtil.getData(DirectPaymentActivity.this,"identity");
//                            String telNo = SharedPreferenceUtil.getData(DirectPaymentActivity.this,"telNo");
//                            String addr = SharedPreferenceUtil.getData(DirectPaymentActivity.this,"addr");
//
////                            String htmlText;
////                            String bodyText = "";
////                            htmlText = "<html>";
////                            htmlText += HtmlUtil.setHead();
////
////                            htmlText += "<body id='mybody'>";
////
////                            bodyText += HtmlUtil.drawDashLine();
////                            bodyText += "<span style = \"font-size:1.3em;\">";
////                            bodyText += "가 맹 점 명: " + name + "<br>";
////                            bodyText += "대 표 자 명: " + ceoName + "<br>";
////                            bodyText += "사업자 번호: " + identity + "<br>";
////                            bodyText += "전 화 번 호: " + telNo + "<br>";
////                            bodyText += "주       소: " + addr + "<br>";
////                            bodyText += "</span>";
////                            bodyText += HtmlUtil.drawDashLine();
////
////                            bodyText += HtmlUtil.setTitle("** 신용승인정보 **");
////                            bodyText += "<span style = \"font-size:1.3em;\">";
////                            bodyText += "거래  일시: " + authDate + "<br>";
////                            bodyText += "승인  번호: " + authCd + "<br>";
//////                    bodyText += "카드  종류: " + cardName + "<br>";
////                            bodyText += "카드발급사: " + issuer + "<br>";
////                            bodyText += "카드  번호: " + CardNo + "<br>";
////                            bodyText += "결제  방법: " + (installment.equals("00") ? "일시불" : installment + "개월") + "<br>";
////                            bodyText += "전표매입사: " + purchaseName + "<br>";
////                            bodyText += "</span>";
////
////                            bodyText += HtmlUtil.drawEqualLine();
////                            bodyText += HtmlUtil.addTableHeader();
////                            bodyText += HtmlUtil.addTableColumn("공  급  가:",  String.format("%,d", amount) + " 원");
////                            bodyText += HtmlUtil.addTableColumn("부  가  세:",  String.format("%,d", vat) + " 원");
////                            try {
//////                        if(taxxpt!=null && Integer.parseInt(taxxpt) > 0)
//////                            bodyText += HtmlUtil.addTableColumn("면세  금액:", (delngSe.equals("1") ? "" : "-")+String.format("%,d", Integer.parseInt(taxxpt)) + " 원");
//////                        if (svcpc > 0)
//////                            bodyText += HtmlUtil.addTableColumn("봉  사  료:", (delngSe.equals("1") ? "" : "-") + String.format("%,d", svcpc) + " 원");
////                            } catch (Exception e) {
////                            }
////                            bodyText += HtmlUtil.addTableColumn("승인  금액:",  String.format("%,d", amount+vat) + " 원");
////                            bodyText += HtmlUtil.addTableTail();
////
////                            bodyText += HtmlUtil.drawEqualLine();
////
////                            bodyText += HtmlUtil.addTableHeader();
////                            bodyText += HtmlUtil.addTableColumn(resultText);
////
////                            bodyText += HtmlUtil.addTableTail();
////
////                            //앱설정에따라 한번더 출력
////                            if (SharedPreferenceUtil.getData(DirectPaymentActivity.this, "printPageCount").equals("2")) {
////                                htmlText += bodyText + "<br><br><br><br>" + bodyText + "<br><br><br><br>";
////                            } else {
////                                htmlText += bodyText;
////                            }
////
//////                    htmlText += "<br>";
////                            htmlText += "</body>";
////                            htmlText += "</html>";
//
//                            Intent completeIntent = new Intent(DirectPaymentActivity.this, PaymentCompleteActivity.class);
//                            completeIntent.putExtra("trackId",trackId);
//                            completeIntent.putExtra("cardNumber",CardNo);
//                            completeIntent.putExtra("amount",amountString);
//                            completeIntent.putExtra("approvalDay",authDay);
//                            completeIntent.putExtra("approvalNumber",authCd);
//                            completeIntent.putExtra("trxId",trxId);
//                            completeIntent.putExtra("delngSe","승인");
//                            completeIntent.putExtra("authDate",Authdate);
//                            completeIntent.putExtra("issuCmpnyNm",issuer);
//                            completeIntent.putExtra("instlmtMonth",installment);
//                            completeIntent.putExtra("puchasCmpnyNm",purchaseName);
//                            completeIntent.putExtra("setleMssage",resultText);
//                            completeIntent.putExtra("splpc",amount+"");
//                            completeIntent.putExtra("vatt",vat+"");
//                            completeIntent.putExtra("brand",issuer);
////                            completeIntent.putExtra("printText",htmlText);
//                            startActivity(completeIntent);
//                            finish();
//
//
//                        } else {
//                            payButton.setEnabled(true);
//                            try {
//                                new MtouchDialog(DirectPaymentActivity.this, false).setTitleText("알림").setContentText(json.getJSONObject("result").getString("advanceMsg")).show();
//                            }catch (Exception e){e.printStackTrace();}
//                        }
//
//                    } else {
//                        payButton.setEnabled(true);
//                        String resultMsg = new String(response.errorBody().bytes());
//                        try {
//                            new MtouchDialog(DirectPaymentActivity.this, false).setTitleText("알림").setContentText(resultMsg).show();
//                        }catch (Exception e){e.printStackTrace();}
//
//                    }
//                } catch (Exception e) {
//                    payButton.setEnabled(true);
//                    e.printStackTrace();
//                    new MtouchDialog(DirectPaymentActivity.this, false).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                payButton.setEnabled(true);
//                try {
//                    new MtouchDialog(DirectPaymentActivity.this, false).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).show();
//                }catch (Exception e){e.printStackTrace();}
//            }
//
//        });
    }
}
