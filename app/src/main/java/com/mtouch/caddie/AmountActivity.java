package com.mtouch.caddie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.mtouch.caddie.databinding.ActivityAmountBinding;
import com.mtouch.ksnet.dpt.design.util.LOG;

public class AmountActivity extends AppCompatActivity {

    private ActivityAmountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_amount);
        binding = ActivityAmountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }




    public void onNumPadClick(View v){
        int id = v.getId();
        String number = binding.amountTextView.getText().toString().replaceAll(",","").trim();

        if(id == R.id.numpad_0){
            number += "0";
        }else if(id == R.id.numpad_00){
            number += "00";
        }else if(id == R.id.numpad_back){
            if(number!=null && number.length()>1){
                number = number.substring(0,number.length()-1);
            }else{
                number = "0";
            }
        }else if(id == R.id.numpad_1){
            number += "1";
        }else if(id == R.id.numpad_2){
            number += "2";
        }else if(id == R.id.numpad_3){
            number += "3";
        }else if(id == R.id.numpad_4){
            number += "4";
        }else if(id == R.id.numpad_5){
            number += "5";
        }else if(id == R.id.numpad_6){
            number += "6";
        }else if(id == R.id.numpad_7){
            number += "7";
        }else if(id == R.id.numpad_8){
            number += "8";
        }else if(id == R.id.numpad_9){
            number += "9";
        }

        try {
            int numberInt = Integer.parseInt(number);
            binding.amountTextView.setText(String.format("%,d",numberInt));

        }catch (Exception e){e.printStackTrace();}

        LOG.w("numpad click : "+number);
    }
}