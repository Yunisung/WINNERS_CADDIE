package com.bkwinners.ksnet.dpt.design;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.bkwinners.caddie.MainActivity;
import com.bkwinners.caddie.network.CaddieAPIService;
import com.bkwinners.caddie.network.MtouchLoadingDialog;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.ksnet.dpt.design.appToApp.ResponseObj;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;
import com.bkwinners.ksnet.dpt.design.util.GsonUtil;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchSMSDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.bkwinners.ksnet.dpt.ks03.obj.KsnetPrnObj;
import com.bkwinners.ksnet.dpt.ks03.pay.Constants;
import com.bkwinners.ksnet.dpt.ks03.pay.ksnet.CyrexNetworkStatus;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TotalActivity extends DeviceCheckActivity {

    protected MtouchLoadingDialog loadingDialog;

    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    protected SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    protected SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ImageView backButton;
    private TextView headerTitleTextView;

    private LinearLayout todayButton;
    private View todayLineView;
    private LinearLayout yesterdayButton;
    private View yesterdayLineView;
    private LinearLayout weekButton;
    private View weekLineView;
    private LinearLayout customButton;
    private View customLineView;
    private LinearLayout customSearchLayout;
    private TextView startDayTextView;
    private TextView endDayTextView;
    private ImageButton searchHistoryButton;
    private TextView todayTextView;
    private TextView yesterdayTextView;
    private TextView weekTextView;
    private TextView customTextView;
    private TextView totalTermTextView;
    private TextView paymentCountTextView;
    private TextView paymentAmountTextView;
    private TextView paymentCancelCountTextView;
    private TextView paymentCancelAmountTextView;
    private TextView paymentTotalCountTextView;
    private TextView paymentTotalAmountTextView;
    private Button printButtn;
    private Button smsButtn;
    private Button finishButtn;

    private CalendarDialog calendarDialog;

    private String amount;
    private String startDay;
    private String cnt;
    private String payCnt;
    private String rfdAmt;
    private String payAmt;
    private String rfdCnt;
    private String result;
    private String _idx;
    private String endDay;

    private CaddieAPIService caddieAPIService;

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        todayButton = (LinearLayout) findViewById(R.id.todayButton);
        todayLineView = (View) findViewById(R.id.todayLineView);
        yesterdayButton = (LinearLayout) findViewById(R.id.yesterdayButton);
        yesterdayLineView = (View) findViewById(R.id.yesterdayLineView);
        weekButton = (LinearLayout) findViewById(R.id.weekButton);
        weekLineView = (View) findViewById(R.id.weekLineView);
        customButton = (LinearLayout) findViewById(R.id.customButton);
        customLineView = (View) findViewById(R.id.customLineView);
        customSearchLayout = (LinearLayout) findViewById(R.id.customSearchLayout);
        startDayTextView = (TextView) findViewById(R.id.startDayTextView);
        endDayTextView = (TextView) findViewById(R.id.endDayTextView);
        searchHistoryButton = (ImageButton) findViewById(R.id.searchHistoryButton);
        totalTermTextView = (TextView) findViewById(R.id.totalTermTextView);
        paymentCountTextView = (TextView) findViewById(R.id.paymentCountTextView);
        paymentAmountTextView = (TextView) findViewById(R.id.paymentAmountTextView);
        paymentCancelCountTextView = (TextView) findViewById(R.id.paymentCancelCountTextView);
        paymentCancelAmountTextView = (TextView) findViewById(R.id.paymentCancelAmountTextView);
        paymentTotalCountTextView = (TextView) findViewById(R.id.paymentTotalCountTextView);
        paymentTotalAmountTextView = (TextView) findViewById(R.id.paymentTotalAmountTextView);
        todayTextView = (TextView) findViewById(R.id.todayTextView);
        yesterdayTextView = (TextView) findViewById(R.id.yesterdayTextView);
        weekTextView = (TextView) findViewById(R.id.weekTextView);
        customTextView = (TextView) findViewById(R.id.customTextView);
        printButtn = (Button) findViewById(R.id.printButtn);
        smsButtn = (Button) findViewById(R.id.smsButtn);
        finishButtn = (Button) findViewById(R.id.finishButtn);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("집계내역");

        startDayTextView.setText(simpleDateFormat.format(new Date()));
        endDayTextView.setText(simpleDateFormat.format(new Date()));

        finishButtn.setOnClickListener(v->{
            setResult(RESULT_CANCELED);
            finish();
        });

        startDayTextView.setOnClickListener(v -> {
            showCalendarDialog();
        });
        endDayTextView.setOnClickListener(v -> {
            showCalendarDialog();
        });

        searchHistoryButton.setOnClickListener(v -> {
            searchHistory();
        });

        todayButton.setOnClickListener(v -> {

            todayTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            todayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));

            startDayTextView.setText(simpleDateFormat.format(new Date()));
            endDayTextView.setText(simpleDateFormat.format(new Date()));
            searchHistory();

            yesterdayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            yesterdayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            weekTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            weekLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            customTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            customLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));
            customSearchLayout.setVisibility(View.GONE);
        });

        yesterdayButton.setOnClickListener(v -> {
            yesterdayTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            yesterdayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));

            Calendar calendar = Calendar.getInstance();

            endDayTextView.setText(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, -1);
            startDayTextView.setText(simpleDateFormat.format(calendar.getTime()));
            searchHistory();

            todayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            todayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            weekTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            weekLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            customTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            customLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));
            customSearchLayout.setVisibility(View.GONE);
        });

        weekButton.setOnClickListener(v -> {
            weekTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            weekLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));

            Calendar calendar = Calendar.getInstance();

            endDayTextView.setText(simpleDateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, -7);
            startDayTextView.setText(simpleDateFormat.format(calendar.getTime()));
            searchHistory();

            yesterdayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            yesterdayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            todayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            todayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            customTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            customLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            customSearchLayout.setVisibility(View.GONE);
        });
        customButton.setOnClickListener(v -> {
            customTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            customLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));

            customSearchLayout.setVisibility(View.VISIBLE);

            yesterdayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            yesterdayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            todayTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            todayLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));

            weekTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            weekLineView.setBackgroundColor(ContextCompat.getColor(this, R.color.white_5));
        });

//        printButtn.setOnClickListener(v -> {
//            print();
//        });

        smsButtn.setOnClickListener(v -> {
            String sendMsg = "";
            sendMsg += "--------------------------------\n";
            sendMsg += "시 작 일 자: " + startDay + "\n";
            sendMsg += "종 료 일 자: " + endDay + "\n";
            sendMsg += "승 인 건 수: " + payCnt + "\n";
            sendMsg += "승 인 금 액: " + payAmt + "\n";
            sendMsg += "취 소 건 수: " + rfdCnt + "\n";
            sendMsg += "취 소 금 액: " + rfdAmt + "\n";
            sendMsg += "합 계 건 수: " + cnt + "\n";
            sendMsg += "합 계 금 액: " + amount + "\n";
            sendMsg += "--------------------------------";

            new MtouchSMSDialog(this).setTitleText("집계내역 SMS 전송").setContentText("집계내역을 SMS로 전송합니다.\n받는 분의 연락처를 입력해 주세요.").setSendData(sendMsg).setOriginal(true).show();
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);
        loadingDialog = new MtouchLoadingDialog(this);

        bindViews();
        init();
    }
    protected void showLoading(){
        try {
            if (!isFinishing() && !loadingDialog.isShowing() && loadingDialog != null) {
                loadingDialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void hideLoading(){
        try {
            if (!isFinishing() && loadingDialog != null) {
                loadingDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init() {
        setNeedRegist(false);
        initialize();

        calendarDialog = new CalendarDialog(this, (List<Day> selectedDays) -> {
            Date startDate = selectedDays.get(0).getCalendar().getTime();
            Date endDate = selectedDays.get(selectedDays.size() - 1).getCalendar().getTime();

            startDayTextView.setText(simpleDateFormat.format(startDate));
            endDayTextView.setText(simpleDateFormat.format(endDate));
        });

        todayButton.performClick();
    }

    private void showCalendarDialog() {
        if (calendarDialog == null) return;
        calendarDialog.show();
        calendarDialog.setSelectionType(SelectionType.RANGE);
        calendarDialog.setSelectedDayBackgroundEndColor(ContextCompat.getColor(this, R.color.watermelon));
        calendarDialog.setSelectedDayBackgroundStartColor(ContextCompat.getColor(this, R.color.watermelon));
        calendarDialog.setSelectedDayBackgroundColor(ContextCompat.getColor(this, R.color.orange_pink));
    }

    private void searchHistory() {
//        Request request = new Request();
//        request.put("startDay", startDayTextView.getText().toString().replaceAll("-", "").trim());
//        request.put("endDay", endDayTextView.getText().toString().replaceAll("-", "").trim());

        caddieAPIService = NetworkManager.getAPIService(this);

        HashMap<String, Object> param = new HashMap<>();
        param.put("mchtId", SharedPreferenceUtil.getData(this, "mchtId"));
        param.put("tmnId", SharedPreferenceUtil.getData(this, "tmnId"));
        param.put("startDay", startDayTextView.getText().toString().replaceAll("-", "").trim());
        param.put("endDay", endDayTextView.getText().toString().replaceAll("-", "").trim());

        showLoading();

        caddieAPIService.getStatistics(param).enqueue(new Callback<com.bkwinners.caddie.network.model.Response>() {
            @Override
            public void onResponse(Call<com.bkwinners.caddie.network.model.Response> call, Response<com.bkwinners.caddie.network.model.Response> response) {
                hideLoading();
                if (response.isSuccessful()) {
                    HashMap<String, Object> responseData = response.body().getData();

                    amount = responseData.get("amount").toString();
                    startDay = startDayTextView.getText().toString();
                    cnt = responseData.get("cnt").toString();
                    payCnt = responseData.get("payCnt").toString();
                    rfdAmt = responseData.get("rfdAmt").toString();
                    payAmt = responseData.get("payAmt").toString();
                    rfdCnt = responseData.get("rfdCnt").toString();
                    result = responseData.get("result").toString();
                    _idx = responseData.get("_idx").toString();
                    endDay = endDayTextView.getText().toString();

                    /**
                     * {
                     *   "data": {
                     *     "amount": "16,064",
                     *     "startDay": "2020-04-19",
                     *     "cnt": "18",
                     *     "payCnt": "17",
                     *     "rfdAmt": "-1,004",
                     *     "payAmt": "17,068",
                     *     "rfdCnt": "1",
                     *     "result": "조회성공",
                     *     "_idx": "1",
                     *     "endDay": "2020-04-20"
                     *   }
                     * }
                     */

                    totalTermTextView.setText(startDay + " ~ " + endDay);
                    paymentCountTextView.setText(responseData.get("payCnt").toString() + "건");
                    paymentAmountTextView.setText(responseData.get("payAmt").toString() + "원");
                    paymentCancelCountTextView.setText(responseData.get("rfdCnt").toString() + "건");
                    paymentCancelAmountTextView.setText(responseData.get("rfdAmt").toString() + "원");
                    paymentTotalCountTextView.setText(responseData.get("cnt").toString() + "건");
                    paymentTotalAmountTextView.setText(responseData.get("amount").toString() + "원");
                }
            }

            @Override
            public void onFailure(Call<com.bkwinners.caddie.network.model.Response> call, Throwable t) {
                hideLoading();
                String resultMsg = t.getMessage();
                try {
                    new MtouchDialog(TotalActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*
        ApiUtils.getAPIService().getPaymentStatistics(SharedPreferenceUtil.getData(this, "key"), request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideLoading();
                try {
                    if (response.isSuccessful()) {
                        String responseData = new String(response.body().bytes());
                        ResponseObj responseObj = (ResponseObj) GsonUtil.fromJson(responseData, ResponseObj.class);



                        amount = responseObj.getStringValue("amount");
                        startDay = responseObj.getStringValue("startDay");
                        cnt = responseObj.getStringValue("cnt");
                        payCnt = responseObj.getStringValue("payCnt");
                        rfdAmt = responseObj.getStringValue("rfdAmt");
                        payAmt = responseObj.getStringValue("payAmt");
                        rfdCnt = responseObj.getStringValue("rfdCnt");
                        result = responseObj.getStringValue("result");
                        _idx = responseObj.getStringValue("_idx");
                        endDay = responseObj.getStringValue("endDay");

                        totalTermTextView.setText(responseObj.getStringValue("startDay") + " ~ " + responseObj.getStringValue("endDay"));
                        paymentCountTextView.setText(responseObj.getStringValue("payCnt") + "건");
                        paymentAmountTextView.setText(responseObj.getStringValue("payAmt") + "원");
                        paymentCancelCountTextView.setText(responseObj.getStringValue("rfdCnt") + "건");
                        paymentCancelAmountTextView.setText(responseObj.getStringValue("rfdAmt") + "원");
                        paymentTotalCountTextView.setText(responseObj.getStringValue("cnt") + "건");
                        paymentTotalAmountTextView.setText(responseObj.getStringValue("amount") + "원");


                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        try {
                            new MtouchDialog(TotalActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
                String resultMsg = t.getMessage();
                try {
                    new MtouchDialog(TotalActivity.this).setTitleText("알림").setContentText(resultMsg).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        */
    }


    private void print() {
        try {
            KsnetPrnObj prnObj = new KsnetPrnObj();
            prnObj.amount = amount;
            prnObj.startDay = startDay;
            prnObj.cnt = cnt;
            prnObj.payCnt = payCnt;
            prnObj.rfdAmt = rfdAmt;
            prnObj.payAmt = payAmt;
            prnObj.rfdCnt = rfdCnt;
            prnObj.result = result;
            prnObj.endDay = endDay;

            Message msg = mHandler.obtainMessage(CyrexNetworkStatus.HANDLER_CALC_PRINTER);
            Bundle bundle = new Bundle();
            bundle.putString("data", prnObj.toJsonString());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
