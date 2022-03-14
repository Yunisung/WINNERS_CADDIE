package com.bkwinners.ksnet.dpt.design;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.google.gson.internal.LinkedTreeMap;
import com.bkwinners.ksnet.dpt.ReceiptActivity;
import com.bkwinners.ksnet.dpt.common.Utils;
import com.bkwinners.ksnet.dpt.db.PaymentInfo;
import com.bkwinners.ksnet.dpt.design.appToApp.ResponseObj;
import com.bkwinners.ksnet.dpt.design.appToApp.network.ApiUtils;
import com.bkwinners.ksnet.dpt.design.appToApp.network.model.Request;
import com.bkwinners.ksnet.dpt.design.util.GsonUtil;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocalHistoryActivity extends AppCompatActivity {

    private Realm realm = Realm.getDefaultInstance();

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
    private TextView startDayTextView;
    private TextView endDayTextView;
    private ImageButton searchHistoryButton;
    private LinearLayout customSearchLayout;

    private androidx.recyclerview.widget.RecyclerView historyRecyclerview;
    private HistoryRecyclerViewAdapter historyRecyclerViewAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat simpleDateFormatForSearch = new SimpleDateFormat("yyyyMMdd");
    private CalendarDialog calendarDialog;

    private Date startDate;
    private Date endDate;

    private void bindViews() {

        backButton = findViewById(R.id.backButton);
        headerTitleTextView = findViewById(R.id.headerTitleTextView);

        todayButton = findViewById(R.id.todayButton);
        todayLineView = findViewById(R.id.todayLineView);
        yesterdayButton = findViewById(R.id.yesterdayButton);
        yesterdayLineView = findViewById(R.id.yesterdayLineView);
        weekButton = findViewById(R.id.weekButton);
        weekLineView = findViewById(R.id.weekLineView);
        customButton = findViewById(R.id.customButton);
        customLineView = findViewById(R.id.customLineView);
        startDayTextView = findViewById(R.id.startDayTextView);
        endDayTextView = findViewById(R.id.endDayTextView);
        searchHistoryButton = findViewById(R.id.searchHistoryButton);
        historyRecyclerview = findViewById(R.id.historyRecyclerview);
        customSearchLayout = findViewById(R.id.customSearchLayout);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        startDayTextView.setText(simpleDateFormat.format(new Date()));
        endDayTextView.setText(simpleDateFormat.format(new Date()));

        findViewById(R.id.startLayout).setOnClickListener(v -> showCalendarDialog());
        findViewById(R.id.endLayout).setOnClickListener(v -> showCalendarDialog());

        searchHistoryButton.setOnClickListener(v -> {
            historyRecyclerViewAdapter.clearItem();
            searchHistory();
        });

        todayButton.setOnClickListener(v->{
            historyRecyclerViewAdapter.clearItem();

            todayLineView.setVisibility(View.VISIBLE);

            startDayTextView.setText(simpleDateFormat.format(new Date()));
            endDayTextView.setText(simpleDateFormat.format(new Date()));
            startDate = new Date();
            endDate = new Date();

            searchHistory();

            yesterdayLineView.setVisibility(View.GONE);
            weekLineView.setVisibility(View.GONE);
            customLineView.setVisibility(View.GONE);
            customSearchLayout.setVisibility(View.GONE);
        });

        yesterdayButton.setOnClickListener(v->{
            historyRecyclerViewAdapter.clearItem();

            yesterdayLineView.setVisibility(View.VISIBLE);

            Calendar calendar = Calendar.getInstance();

            endDate = calendar.getTime();
            endDayTextView.setText(simpleDateFormat.format(endDate));
            calendar.add(Calendar.DATE,-1);
            startDate = calendar.getTime();
            startDayTextView.setText(simpleDateFormat.format(startDate));

            searchHistory();

            todayLineView.setVisibility(View.GONE);
            weekLineView.setVisibility(View.GONE);
            customLineView.setVisibility(View.GONE);
            customSearchLayout.setVisibility(View.GONE);
        });
        weekButton.setOnClickListener(v->{
            historyRecyclerViewAdapter.clearItem();

            weekLineView.setVisibility(View.VISIBLE);

            Calendar calendar = Calendar.getInstance();

            endDate = calendar.getTime();
            endDayTextView.setText(simpleDateFormat.format(endDate));
            calendar.add(Calendar.DATE,-7);
            startDate = calendar.getTime();
            startDayTextView.setText(simpleDateFormat.format(startDate));

            searchHistory();

            todayLineView.setVisibility(View.GONE);
            yesterdayLineView.setVisibility(View.GONE);
            customLineView.setVisibility(View.GONE);
            customSearchLayout.setVisibility(View.GONE);
        });
        customButton.setOnClickListener(v->{
            historyRecyclerViewAdapter.clearItem();

            customSearchLayout.setVisibility(View.VISIBLE);

            customLineView.setVisibility(View.VISIBLE);
            todayLineView.setVisibility(View.GONE);
            yesterdayLineView.setVisibility(View.GONE);
            weekLineView.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_history);


        bindViews();
        init();

    }

    private void init() {

        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter();
        historyRecyclerview.setAdapter(historyRecyclerViewAdapter);

        calendarDialog = new CalendarDialog(this, (List<Day> selectedDays) -> {
            if(selectedDays==null || selectedDays.size()==0){
                new MtouchDialog(this, false).setTitleText("알림").setContentText(getString(R.string.select_days_error)).show();
                return;
            }
            startDate = selectedDays.get(0).getCalendar().getTime();
            endDate = selectedDays.get(selectedDays.size() - 1).getCalendar().getTime();

            startDayTextView.setText(simpleDateFormat.format(startDate));
            endDayTextView.setText(simpleDateFormat.format(endDate));

            searchHistory();
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
        if(startDate==null || endDate == null ){
            Toast.makeText(this, "조회날짜가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        String startDay = simpleDateFormatForSearch.format(startDate);
        String endDay = simpleDateFormatForSearch.format(endDate);

        try {
            startDate = simpleDateFormatForSearch.parse(startDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endDate = simpleDateFormatForSearch.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        historyRecyclerViewAdapter.clearItem();

        RealmResults<PaymentInfo> paymentInfoArrayList;
        RealmQuery query = realm.where(PaymentInfo.class);

        Date startDate = new Date(this.startDate.getTime());
        Date endDate = new Date(this.endDate.getTime());

        LOG.w("startDay: "+startDay);
        LOG.w("endDay: "+endDay);

        /*
        //startDay
        query = query.beginsWith("regDate", startDay);

        LOG.w("@@@@@@@@@ SEARCH @@@@@@@@@@@");

        if(!startDay.equals(endDay)){
            //next day
            startDate.setTime(startDate.getTime()+(1000*60*60*24));
            startDay = simpleDateFormatForSearch.format(startDate);
        }

        for(;!startDay.equals(endDay);startDay = simpleDateFormatForSearch.format(startDate)) {
            LOG.w("@@@@@@@@@ LOOP @@@@@@@@@@@");
            LOG.w("startDay: "+startDay);
            LOG.w("endDay: "+endDay);

            query = query.or().beginsWith("regDate", startDay);
            startDate.setTime(startDate.getTime()+(1000*60*60*24));
        }

        //endDay
        query = query.or().beginsWith("regDate", endDay);

        LOG.w("@@@@@@@@@ END @@@@@@@@@@@");

         */
        paymentInfoArrayList = query.findAll();

        if(paymentInfoArrayList==null || paymentInfoArrayList.size()==0){
            Toast.makeText(this, "조회데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        historyRecyclerViewAdapter.setItems(paymentInfoArrayList.subList(0,paymentInfoArrayList.size()));
        historyRecyclerViewAdapter.notifyDataSetChanged();
    }

    class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MtouchHistoryViewHolder> {
        private List<PaymentInfo> paymentInfos = new ArrayList<>();

        public void setItems(List<PaymentInfo> items){
            if(items == null && items.size()==0){
                return;
            }
            for(int i = items.size()-1;i>=0;i--){
                paymentInfos.add(items.get(i));
            }
        }
        public void addItem(PaymentInfo paymentInfo) {
            paymentInfos.add(paymentInfo);
        }

        public void clearItem() {
            paymentInfos.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public HistoryRecyclerViewAdapter.MtouchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new HistoryRecyclerViewAdapter.MtouchHistoryViewHolder(View.inflate(parent.getContext(), R.layout.item_local_history_layout, null));
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.MtouchHistoryViewHolder holder, int position) {
            PaymentInfo item = paymentInfos.get(position);

            holder.itemView.setTag(item);

            if(item.getDelngSe().equals("승인")){
                holder.resultTextView.setTextColor(ContextCompat.getColor(LocalHistoryActivity.this,R.color.algae_green));
            }else if(item.getDelngSe().equals("승인취소")){
                holder.resultTextView.setTextColor(ContextCompat.getColor(LocalHistoryActivity.this,R.color.tomato));
            }

            try {
                holder.timeTextView.setText(simpleDateFormatDetail.format(simpleDateFormatForParse.parse((item.getRegDate()))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.resultTextView.setText(item.getDelngSe());

            holder.approvalNumberTextView.setText(item.getConfmNo());
            holder.brandTextView.setText(item.getIssuCmpnyNm());
            holder.amountTextView.setText(String.format("%,d", Integer.parseInt(item.getSplpc()))+" 원");

            if(item.getMchtName()!=null) {
                holder.nameTextView.setText(item.getMchtName());
            }else{
                holder.nameTextView.setText("");
            }
            if(item.getTrackId()!=null) {
                holder.paymentTypeTextView.setText("PG");
                holder.paymentTypeTextView.setTextColor(ContextCompat.getColor(LocalHistoryActivity.this,R.color.watermelon));
            }else{
                holder.paymentTypeTextView.setText("VAN");
                holder.paymentTypeTextView.setTextColor(ContextCompat.getColor(LocalHistoryActivity.this,R.color.dark_sky_blue));
            }
        }

        @Override
        public int getItemCount() {
            if (paymentInfos == null) {
                return 0;
            } else {
                return paymentInfos.size();
            }
        }


        class MtouchHistoryViewHolder extends RecyclerView.ViewHolder {

            public TextView resultTextView;
            public TextView timeTextView;
            public TextView approvalNumberTextView;
            public TextView brandTextView;
            public TextView amountTextView;
            public TextView paymentTypeTextView;
            public TextView nameTextView;

            public MtouchHistoryViewHolder(@NonNull View itemView) {
                super(itemView);

                resultTextView = itemView.findViewById(R.id.resultTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);
                approvalNumberTextView = itemView.findViewById(R.id.approvalNumberTextView);
                brandTextView = itemView.findViewById(R.id.brandTextView);
                amountTextView = itemView.findViewById(R.id.amountTextView);
                paymentTypeTextView = itemView.findViewById(R.id.paymentTypeTextView);
                nameTextView = itemView.findViewById(R.id.nameTextView);


                itemView.setOnClickListener(v->{
                    PaymentInfo paymentInfo = (PaymentInfo) itemView.getTag();
                    Intent intent = new Intent(LocalHistoryActivity.this, ReceiptActivity.class);
                    intent.putExtra("amount",paymentInfo.getSplpc());
                    intent.putExtra("authDate",paymentInfo.getRegDate());
                    intent.putExtra("authNum",paymentInfo.getConfmNo());
                    intent.putExtra("payType",paymentInfo.getDelngSe());
                    intent.putExtras(getIntent());
                    startActivity(intent);
                });
            }
        }
    }

}