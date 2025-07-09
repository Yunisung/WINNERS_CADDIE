package com.bkwinners.ksnet.dpt.design;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.bkwinners.caddie.R;
import com.bkwinners.caddie.network.NetworkManager;
import com.bkwinners.caddie.network.model.Order;
import com.bkwinners.caddie.network.model.OrderListResponse;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HistoryActivity extends AppCompatActivity {

    private static final int REQEUST_CODE_DETAIL = 10000;

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

    private TextView todayTextView;
    private TextView yesterdayTextView;
    private TextView weekTextView;
    private TextView customTextView;

    private androidx.recyclerview.widget.RecyclerView historyRecyclerview;
    private HistoryRecyclerViewAdapter historyRecyclerViewAdapter;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForParse = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private CalendarDialog calendarDialog;

    private boolean isEndData = false;

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

        todayTextView = (TextView) findViewById(R.id.todayTextView);
        yesterdayTextView = (TextView) findViewById(R.id.yesterdayTextView);
        weekTextView = (TextView) findViewById(R.id.weekTextView);
        customTextView = (TextView) findViewById(R.id.customTextView);

        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        headerTitleTextView.setText("조회하기");

        startDayTextView.setText(simpleDateFormat.format(new Date()));
        endDayTextView.setText(simpleDateFormat.format(new Date()));

        findViewById(R.id.startLayout).setOnClickListener(v -> showCalendarDialog());
        findViewById(R.id.endLayout).setOnClickListener(v -> showCalendarDialog());

        searchHistoryButton.setOnClickListener(v -> {
            historyRecyclerViewAdapter.clearItem();
            searchHistory();
        });

        todayButton.setOnClickListener(v -> {
            historyRecyclerViewAdapter.clearItem();

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
            historyRecyclerViewAdapter.clearItem();

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
            historyRecyclerViewAdapter.clearItem();

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
            historyRecyclerViewAdapter.clearItem();

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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        bindViews();
        init();


    }

    private void init() {

        historyRecyclerViewAdapter = new HistoryRecyclerViewAdapter();
        historyRecyclerview.setAdapter(historyRecyclerViewAdapter);

        calendarDialog = new CalendarDialog(this, (List<Day> selectedDays) -> {
            if (selectedDays == null || selectedDays.size() == 0) {
                new MtouchDialog(HistoryActivity.this, false).setTitleText("알림").setContentText(getString(R.string.select_days_error)).show();
                return;
            }
            Date startDate = selectedDays.get(0).getCalendar().getTime();
            Date endDate = selectedDays.get(selectedDays.size() - 1).getCalendar().getTime();

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
        isEndData = false;
        this.searchHistory(null);
    }

    private void searchHistory(String lastRegDay) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("startDay", startDayTextView.getText().toString().replaceAll("-", "").trim());
        params.put("endDay", endDayTextView.getText().toString().replaceAll("-", "").trim());
        if (lastRegDay != null) {
            params.put("lastRegDay", lastRegDay);
        }

        NetworkManager.getAPIService(this).orderList(params).enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, retrofit2.Response<OrderListResponse> response) {
                try {
                    //status code 확인
                    if (response.isSuccessful()) {

                        //resultCd확인
                        if (response.body().isSuccess()) {


                            if (lastRegDay == null)
                                historyRecyclerViewAdapter.clearItem();

                            ArrayList<Order> list = response.body().getList();
                            historyRecyclerViewAdapter.addList(list);


                            if (list != null && list.size() > 0) {
                                historyRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                if(historyRecyclerViewAdapter.orderArrayList.size()==0  && !isEndData){
                                    Toast.makeText(HistoryActivity.this, "조회데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                                isEndData = true;
                            }


                        } else {
                            String resultMsg = response.body().getResultMsg();
                            new MtouchDialog(HistoryActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                        }
                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        new MtouchDialog(HistoryActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).show();
                    }

                } catch (Exception e) {

                }
            }

            //네트워크 자체에러
            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                String resultMsg = getString(R.string.network_error_msg);
                String error = t.toString();
                new MtouchDialog(HistoryActivity.this, v -> finish()).setTitleText("알림").setContentText(error).show();
            }
        });
    }


    class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MtouchHistoryViewHolder> {
        private ArrayList<Order> orderArrayList = new ArrayList<>();

        public void setList(ArrayList<Order> list) {
            if (list != null) orderArrayList = list;
        }

        public void addList(ArrayList<Order> list){
            if (list != null) {
                orderArrayList.addAll(list);
            }
        }

        public void addItem(Order order) {
            orderArrayList.add(order);
        }

        public void clearItem() {
            orderArrayList.clear();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MtouchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MtouchHistoryViewHolder(View.inflate(parent.getContext(), R.layout.item_history_layout, null));
        }

        @Override
        public void onBindViewHolder(@NonNull MtouchHistoryViewHolder holder, int position) {
            Order item = orderArrayList.get(position);
            if (!isEndData && orderArrayList.size() - 1 == position) {
                LOG.w("test size: " + orderArrayList.size() + " position: " + position);
                new Handler().postDelayed(() -> searchHistory(item.getReqDay()), 500);
            }

            holder.itemView.setTag(item);

            int reqPayCount = Integer.parseInt(item.getReqPayCount());
            int resultCount = Integer.parseInt(item.getResultCount());
            if (reqPayCount == resultCount) {
                holder.resultTextView.setText("결제완료 ("+resultCount+"/"+reqPayCount+")");
                holder.resultTextView.setTextColor(ContextCompat.getColor(HistoryActivity.this, R.color.greeny_blue));
            } else {
                holder.resultTextView.setText("미완료 ("+resultCount+"/"+reqPayCount+")");
                holder.resultTextView.setTextColor(ContextCompat.getColor(HistoryActivity.this, R.color.coral));
            }

            holder.timeTextView.setText(item.getRegDate());
            holder.approvalNumberTextView.setText(item.getTrackId());

            holder.brandTextView.setText(item.getPaymentInfo().getPlace());
            holder.amountTextView.setText(String.format("%,d", Integer.parseInt(item.getAmount())) + " 원");
        }

        @Override
        public int getItemCount() {
            if (orderArrayList == null) {
                return 0;
            } else {
                return orderArrayList.size();
            }
        }


        class MtouchHistoryViewHolder extends RecyclerView.ViewHolder {

            public TextView resultTextView;
            public TextView timeTextView;
            public TextView approvalNumberTextView;
            public TextView brandTextView;
            public TextView amountTextView;

            public MtouchHistoryViewHolder(@NonNull View itemView) {
                super(itemView);

                resultTextView = itemView.findViewById(R.id.resultTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);
                approvalNumberTextView = itemView.findViewById(R.id.approvalNumberTextView);
                brandTextView = itemView.findViewById(R.id.brandTextView);
                amountTextView = itemView.findViewById(R.id.amountTextView);


                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(HistoryActivity.this, HistoryDetailActivity.class);
                    intent.putExtra(Order.INTENT_KEY_ORDER, (Order) v.getTag());
                    startActivityForResult(intent, REQEUST_CODE_DETAIL);
                });
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQEUST_CODE_DETAIL) {
            if (resultCode == RESULT_CANCELED) {

            } else if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, data);
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }
}
