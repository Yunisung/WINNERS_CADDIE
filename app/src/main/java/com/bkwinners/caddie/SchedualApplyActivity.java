package com.bkwinners.caddie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.bkwinners.caddie.data.Schedual;
import com.bkwinners.caddie.databinding.ActivitySchedualApplyBinding;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.MtouchListDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class SchedualApplyActivity extends AppCompatActivity {

    private ActivitySchedualApplyBinding binding;

    private CalendarDialog calendarDialog;
    private SimpleDateFormat simpleDateFormatDetail = new SimpleDateFormat("yyyyMMddHHmm");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    private Date day = new Date();
    private int count = 1;
    private String hour = "00";
    private String minute = "00";

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_schedual_apply);
        binding = ActivitySchedualApplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();
    }

    private void init() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        calendarDialog = new CalendarDialog(this, (List<Day> selectedDays) -> {
            if (selectedDays == null || selectedDays.size() == 0) {
                new MtouchDialog(SchedualApplyActivity.this, false).setTitleText("알림").setContentText(getString(R.string.select_days_error)).show();
                return;
            }
            day = selectedDays.get(0).getCalendar().getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(day);

            binding.yearTextView.setText(calendar.get(Calendar.YEAR) + " 년");
            binding.monthTextView.setText((calendar.get(Calendar.MONTH) + 1) + " 월");
            binding.dayTextView.setText(calendar.get(Calendar.DAY_OF_MONTH) + " 일");
        });

        if (getIntent() != null && getIntent().hasExtra(Schedual.INTENT_KEY_SCHEDUAL)) {
            Schedual schedual = (Schedual) getIntent().getSerializableExtra(Schedual.INTENT_KEY_SCHEDUAL);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(schedual.getDay()));

            binding.yearTextView.setText(calendar.get(Calendar.YEAR) + " 년");
            binding.monthTextView.setText((calendar.get(Calendar.MONTH) + 1) + " 월");
            binding.dayTextView.setText(calendar.get(Calendar.DAY_OF_MONTH) + " 일");
            binding.hourTextView.setText(String.format("%02d 시", calendar.get(Calendar.HOUR)));
            binding.minuteTextView.setText(String.format("%02d 분", calendar.get(Calendar.MINUTE)));

            binding.placeEditText.setText(schedual.getPlaceName());
            binding.courseEditText.setText(schedual.getCourseName());
            count = schedual.getCount();
            binding.countTextView.setText(count + "");
            binding.nameEditText.setText(schedual.getName());
            binding.phoneNumber1EditText.setText(schedual.getPhoneNumber1());
            binding.name1EditText.setText(schedual.getName1());
            if (count == 2) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setText(schedual.getPhoneNumber2());
                binding.name2EditText.setText(schedual.getName2());

            } else if (count == 3) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setText(schedual.getPhoneNumber2());
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber3EditText.setText(schedual.getPhoneNumber3());
                binding.name2EditText.setText(schedual.getName2());
                binding.name3EditText.setText(schedual.getName3());
            } else if (count == 4) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber2EditText.setText(schedual.getPhoneNumber2());
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber3EditText.setText(schedual.getPhoneNumber3());
                binding.phoneNumber4Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4EditText.setVisibility(View.VISIBLE);
                binding.phoneNumber4EditText.setText(schedual.getPhoneNumber4());
                binding.name2EditText.setText(schedual.getName2());
                binding.name3EditText.setText(schedual.getName3());
                binding.name4EditText.setText(schedual.getName4());
            }



        } else if (getIntent() != null && getIntent().hasExtra("selectDate") && getIntent().getSerializableExtra("selectDate") != null) {
            Date selectDate = (Date) getIntent().getSerializableExtra("selectDate");

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectDate);

            binding.yearTextView.setText(calendar.get(Calendar.YEAR) + " 년");
            binding.monthTextView.setText((calendar.get(Calendar.MONTH) + 1) + " 월");
            binding.dayTextView.setText(calendar.get(Calendar.DAY_OF_MONTH) + " 일");
            binding.hourTextView.setText(String.format("%02d 시", calendar.get(Calendar.HOUR)));
            binding.minuteTextView.setText(String.format("%02d 분", calendar.get(Calendar.MINUTE)));

            binding.placeEditText.setText(SharedPreferenceUtil.getData(this, "ccName"));
        } else {
            binding.yearTextView.setText(Calendar.getInstance().get(Calendar.YEAR) + " 년");
            binding.monthTextView.setText((Calendar.getInstance().get(Calendar.MONTH) + 1) + " 월");
            binding.dayTextView.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + " 일");

            binding.placeEditText.setText(SharedPreferenceUtil.getData(this, "ccName"));
        }
    }

    private void bindView() {
        ((TextView) binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("일정 등록");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });

        binding.minusImageView.setOnClickListener(v -> {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if (count > 1) count--;
            binding.countTextView.setText(count + "");
            if (count == 1) {
                binding.phoneNumber2Layout.setVisibility(View.GONE);
                binding.phoneNumber3Layout.setVisibility(View.GONE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 2) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.GONE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 3) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 4) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4Layout.setVisibility(View.VISIBLE);
            }
        });
        binding.plusImageView.setOnClickListener(v -> {
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            if (count < 4) count++;
            binding.countTextView.setText(count + "");
            if (count == 1) {
                binding.phoneNumber2Layout.setVisibility(View.GONE);
                binding.phoneNumber3Layout.setVisibility(View.GONE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 2) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.GONE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 3) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4Layout.setVisibility(View.GONE);
            } else if (count == 4) {
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4Layout.setVisibility(View.VISIBLE);
            }
        });

        binding.yearTextView.setOnClickListener(v -> showCalendarDialog());
        binding.monthTextView.setOnClickListener(v -> showCalendarDialog());
        binding.dayTextView.setOnClickListener(v -> showCalendarDialog());

        binding.hourTextView.setOnClickListener(v -> {
            MtouchListDialog mtouchListDialog = new MtouchListDialog(this, (indexString, otherString) -> {
                hour = otherString;
                binding.hourTextView.setText(indexString);
            });
            for (int i = 0; i < 24; i++) {
                mtouchListDialog.addList(String.format("%02d 시", i), i + "");
            }
            mtouchListDialog.show();
        });

        binding.minuteTextView.setOnClickListener(v -> {
            MtouchListDialog mtouchListDialog = new MtouchListDialog(this, (indexString, otherString) -> {
                minute = otherString;
                binding.minuteTextView.setText(indexString);
            });
            for (int i = 0; i < 60; i = i + 10) {
                mtouchListDialog.addList(String.format("%02d 분", i), i + "");
            }
            mtouchListDialog.show();
        });


        binding.cancelButton.setOnClickListener(v -> finish());
        binding.applyButton.setOnClickListener(v -> {
            if (binding.placeEditText.getText().toString().trim().length() == 0) {
                String message = "골프장을 입력하세요.";
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText(message);
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.placeEditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.placeEditText,0),100);
                });
                mtouchDialog.show();
                return;
            }
            if (binding.courseEditText.getText().toString().trim().length() == 0) {
                String message = "코스를 입력하세요.";
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText(message);
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.courseEditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.courseEditText,0),100);
                });
                mtouchDialog.show();
                return;
            }
            if (binding.nameEditText.getText().toString().trim().length() == 0) {
                String message = "예약자명을 입력하세요.";
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText(message);
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.nameEditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.nameEditText,0),100);
                });
                mtouchDialog.show();
                return;
            }




            boolean nameCheck1 = binding.name1EditText.getText().toString().trim().length()>0;
            boolean nameCheck2 = binding.phoneNumber2Layout.getVisibility() == View.VISIBLE?binding.name2EditText.getText().toString().trim().length()>0:true;
            boolean nameCheck3 = binding.phoneNumber3Layout.getVisibility() == View.VISIBLE?binding.name3EditText.getText().toString().trim().length()>0:true;
            boolean nameCheck4 = binding.phoneNumber4Layout.getVisibility() == View.VISIBLE?binding.name4EditText.getText().toString().trim().length()>0:true;

            //이름정보 확인.
            if(!nameCheck1 || !nameCheck2 || !nameCheck3 || !nameCheck4){
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText("이름을 입력해주세요.");
                mtouchDialog.setOnDismissListener(dialog -> {
                    if(!nameCheck1){
                        binding.name1EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.name1EditText,0),100);
                    }else if(!nameCheck2){
                        binding.name2EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.name2EditText,0),100);
                    }else if(!nameCheck3){
                        binding.name3EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.name3EditText,0),100);
                    }else if(!nameCheck4){
                        binding.name4EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.name4EditText,0),100);
                    }
                });
                mtouchDialog.show();
                return;
            }


            int number1Length = binding.phoneNumber1EditText.getText().toString().trim().length();
            int number2Length = binding.phoneNumber2EditText.getText().toString().trim().length();
            int number3Length = binding.phoneNumber3EditText.getText().toString().trim().length();
            int number4Length = binding.phoneNumber4EditText.getText().toString().trim().length();

            boolean numberCheck1 = number1Length>5;
            boolean numberCheck2 = binding.phoneNumber2Layout.getVisibility() == View.VISIBLE?number2Length>5:true;
            boolean numberCheck3 = binding.phoneNumber3Layout.getVisibility() == View.VISIBLE?number3Length>5:true;
            boolean numberCheck4 = binding.phoneNumber4Layout.getVisibility() == View.VISIBLE?number4Length>5:true;

            //인원정보 확인.
            if(!numberCheck1 || !numberCheck2 || !numberCheck3 || !numberCheck4){
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText("전화번호가 올바르지 않습니다.\n다시 확인해주세요.");
                mtouchDialog.setOnDismissListener(dialog -> {
                    if(!numberCheck1){
                        binding.phoneNumber1EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.phoneNumber1EditText,0),100);
                    }else if(!numberCheck2){
                        binding.phoneNumber2EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.phoneNumber2EditText,0),100);
                    }else if(!numberCheck3){
                        binding.phoneNumber3EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.phoneNumber3EditText,0),100);
                    }else if(!numberCheck4){
                        binding.phoneNumber4EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.phoneNumber4EditText,0),100);
                    }
                });
                mtouchDialog.show();
                return;
            }

            if (binding.phoneNumber1EditText.getText().toString().trim().length() == 0) {
                String message = "전화번호를 입력하세요.";
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText(message);
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.phoneNumber1EditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.phoneNumber1EditText,0),100);
                });
                mtouchDialog.show();
                return;
            }

            String year = binding.yearTextView.getText().toString().replaceAll("년", "").trim();
            String month = binding.monthTextView.getText().toString().replaceAll("월", "").trim();
            String dayString = binding.dayTextView.getText().toString().replaceAll("일", "").trim();
            String hour = binding.hourTextView.getText().toString().replaceAll("시", "").trim();
            String minute = binding.minuteTextView.getText().toString().replaceAll("분", "").trim();

            try {
                day = simpleDateFormatDetail.parse(year + String.format("%2d", Integer.parseInt(month)) + dayString + hour + minute);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Schedual schedual = new Schedual();
            schedual.setDay(day.getTime());
            schedual.setPlaceName(binding.placeEditText.getText().toString().trim());
            schedual.setCourseName(binding.courseEditText.getText().toString().trim());
            schedual.setName(binding.nameEditText.getText().toString().trim());
            schedual.setCount(count);
            schedual.setName1(binding.name1EditText.getText().toString().trim());
            schedual.setPhoneNumber1(binding.phoneNumber1EditText.getText().toString().trim());
            if (binding.phoneNumber2EditText.getVisibility() == View.VISIBLE) {
                schedual.setName2(binding.name2EditText.getText().toString().trim());
                schedual.setPhoneNumber2(binding.phoneNumber2EditText.getText().toString().trim());
            }
            if (binding.phoneNumber3EditText.getVisibility() == View.VISIBLE) {
                schedual.setName3(binding.name3EditText.getText().toString().trim());
                schedual.setPhoneNumber3(binding.phoneNumber3EditText.getText().toString().trim());
            }
            if (binding.phoneNumber4EditText.getVisibility() == View.VISIBLE) {
                schedual.setName4(binding.name4EditText.getText().toString().trim());
                schedual.setPhoneNumber4(binding.phoneNumber4EditText.getText().toString().trim());
            }

            SharedPreferenceUtil.putSchedual(this, day.getTime() + "", schedual);

            Intent intent = new Intent(this, SchedualCompleActivity.class);
            intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, schedual);
            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        });


    }


    private void showCalendarDialog() {
        if (calendarDialog == null) return;
        calendarDialog.show();
        calendarDialog.setFirstDayOfWeek(1);
        calendarDialog.setSelectionType(SelectionType.SINGLE);
        calendarDialog.setSelectedDayBackgroundEndColor(ContextCompat.getColor(this, R.color.watermelon));
        calendarDialog.setSelectedDayBackgroundStartColor(ContextCompat.getColor(this, R.color.watermelon));
        calendarDialog.setSelectedDayBackgroundColor(ContextCompat.getColor(this, R.color.orange_pink));
    }

}

