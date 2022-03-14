package com.bkwinners.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bkwinners.caddie.data.Schedual;
import com.bkwinners.caddie.databinding.ActivitySchedualDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SchedualDetailActivity extends AppCompatActivity {

    private ActivitySchedualDetailBinding binding;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("HH : mm");

    private Schedual schedual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySchedualDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        schedual = (Schedual) getIntent().getSerializableExtra(Schedual.INTENT_KEY_SCHEDUAL);

        binding.dayTextView.setText(simpleDateFormat.format(new Date(schedual.getDay())));
        binding.placeTextView.setText(schedual.getPlaceName());
        binding.timeTextView.setText(simpleDateFormatForTime.format(new Date(schedual.getDay())));
        binding.courseTextView.setText(schedual.getCourseName());
        binding.nameTextView.setText(schedual.getName());
        binding.countTextView.setText(schedual.getCount()+"");
        binding.phoneNumber1TextView.setText(schedual.getPhoneNumber1());
        binding.name1TextView.setText(schedual.getName1());
        if(schedual.getPhoneNumber2()!=null && schedual.getPhoneNumber2().length()>0){
            binding.phoneNumber2Layout.setVisibility(View.VISIBLE);
            binding.phoneNumber2TextView.setText(schedual.getPhoneNumber2());
            binding.name2TextView.setText(schedual.getName2());
        }
        if(schedual.getPhoneNumber3()!=null && schedual.getPhoneNumber3().length()>0){
            binding.phoneNumber3Layout.setVisibility(View.VISIBLE);
            binding.phoneNumber3TextView.setText(schedual.getPhoneNumber3());
            binding.name3TextView.setText(schedual.getName3());
        }
        if(schedual.getPhoneNumber4()!=null && schedual.getPhoneNumber4().length()>0){
            binding.phoneNumber4Layout.setVisibility(View.VISIBLE);
            binding.phoneNumber4TextView.setText(schedual.getPhoneNumber4());
            binding.name4TextView.setText(schedual.getName4());
        }


        binding.exitButtn.setOnClickListener(v->{
            setResult(RESULT_OK);
            finish();
        });

        binding.payButton.setOnClickListener(v->{
            Intent intent = new Intent(this, OrderActivity.class);
            intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, schedual);
            startActivity(intent);
            setResult(RESULT_OK);
            finish();
        });
    }
}