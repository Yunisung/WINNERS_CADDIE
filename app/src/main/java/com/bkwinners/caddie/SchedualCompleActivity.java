package com.bkwinners.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bkwinners.caddie.data.Schedual;
import com.bkwinners.caddie.databinding.ActivitySchedualCompleBinding;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class SchedualCompleActivity extends AppCompatActivity {

    private ActivitySchedualCompleBinding binding;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("HH : mm");

    private String title = "일정상세";
    private Schedual schedual;
    private boolean isFirst = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_schedual_comple);
        binding = ActivitySchedualCompleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        init();
        bindView();
    }

    private void bindView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText(title);
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            finish();
        });

        if(schedual!=null){
            binding.dayTextView.setText(simpleDateFormat.format(new Date(schedual.getDay())));
            binding.placeTextView.setText(schedual.getPlaceName());
            binding.timeTextView.setText(simpleDateFormatForTime.format(new Date(schedual.getDay())));
            binding.courseTextView.setText(schedual.getCourseName());
            binding.nameTextView.setText(schedual.getName());
            binding.countTextView.setText(schedual.getCount()+"");
            binding.phoneNumber1TextView.setText(schedual.getPhoneNumber1());
            if(schedual.getName1()!=null) binding.name1TextView.setText(schedual.getName1());

            if(schedual.getPhoneNumber2()!=null && schedual.getPhoneNumber2().length()>0){
                binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber2TextView.setText(schedual.getPhoneNumber2());
                if(schedual.getName2()!=null) binding.name2TextView.setText(schedual.getName2());
            }
            if(schedual.getPhoneNumber3()!=null && schedual.getPhoneNumber3().length()>0){
                binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber3TextView.setText(schedual.getPhoneNumber3());
                if(schedual.getName3()!=null) binding.name3TextView.setText(schedual.getName3());
            }
            if(schedual.getPhoneNumber4()!=null && schedual.getPhoneNumber4().length()>0){
                binding.phoneNumber4Layout.setVisibility(View.VISIBLE);
                binding.phoneNumber4TextView.setText(schedual.getPhoneNumber4());
                if(schedual.getName4()!=null) binding.name4TextView.setText(schedual.getName4());
            }
        }


        binding.exitButtn.setOnClickListener(v->{
            finish();
        });

        binding.nextButtn.setOnClickListener(v->{
            startActivity(new Intent(this, SchedualApplyActivity.class));
            finish();
        });

//        binding.applyButton.setOnClickListener(v->{
//            Intent intent = new Intent(this, OrderActivity.class);
//            intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, schedual);
//            startActivity(intent);
//            setResult(RESULT_OK);
//            finish();
//        });
    }

    private void init() {
       if(getIntent()!=null){
           schedual = (Schedual) getIntent().getSerializableExtra(Schedual.INTENT_KEY_SCHEDUAL);



       }
    }
}