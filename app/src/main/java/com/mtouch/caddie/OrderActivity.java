package com.mtouch.caddie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.data.Schedual;
import com.mtouch.caddie.databinding.ActivityOrderBinding;
import com.mtouch.caddie.network.model.Amount;
import com.mtouch.caddie.network.model.Order;
import com.mtouch.caddie.network.model.PaymentInfo;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.pswseoul.util.CommonUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1000;

    public static final String INTENT_KEY_PLACE_NAME = "placeName";
    public static final String INTENT_KEY_NAME = "name";
    public static final String INTENT_KEY_TIME = "time";
    public static final String INTENT_KEY_COURSE_NAME = "courseName";

    private final SimpleDateFormat simpleDateFormatFull = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("HH:mm");

    private ActivityOrderBinding binding;
    private String placeName="";
    private String name="";
    private long day = new Date().getTime();
    private String courseName="";
    private String reqPayCount = "1";

    private Order order = null;
    private Schedual schedual;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();

        test();
    }
    private void test(){


    }

    private void init() {
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if(getIntent()!=null){
//            placeName = getIntent().getStringExtra(INTENT_KEY_PLACE_NAME);
//            name = getIntent().getStringExtra(INTENT_KEY_NAME);
//            time = getIntent().getStringExtra(INTENT_KEY_TIME);
//            courseName = getIntent().getStringExtra(INTENT_KEY_COURSE_NAME);
//
//            try {
//                order = (Order) getIntent().getSerializableExtra(Order.INTENT_KEY_ORDER);
//            }catch (Exception e){}

            schedual = (Schedual) getIntent().getSerializableExtra(Schedual.INTENT_KEY_SCHEDUAL);
            if(schedual!=null) {
                placeName = schedual.getPlaceName();
                name = schedual.getName();
                day = schedual.getDay();
                courseName = schedual.getCourseName();
                reqPayCount = schedual.getCount()+"";

                binding.number1EditText.setText(schedual.getPhoneNumber1());
                binding.number2EditText.setText(schedual.getPhoneNumber2());
                binding.number3EditText.setText(schedual.getPhoneNumber3());
                binding.number4EditText.setText(schedual.getPhoneNumber4());

                binding.name1EditText.setText(schedual.getName1());
                binding.name2EditText.setText(schedual.getName2());
                binding.name3EditText.setText(schedual.getName3());
                binding.name4EditText.setText(schedual.getName4());
            }else{

                placeName = SharedPreferenceUtil.getData(this, "ccName");
            }
        }else{
            placeName = SharedPreferenceUtil.getData(this, "ccName");
        }


    }

    private void bindView() {
        ((TextView)binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("결제요청");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v->{
            finish();
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                int total = 0;
                int roundingAmount = 0;
                int tipAmount = 0;
                try{ roundingAmount = Integer.parseInt(binding.amountEditText.getText().toString().trim()); }catch (Exception e) {roundingAmount=0; }
                try{ tipAmount = Integer.parseInt(binding.tipEditText.getText().toString().trim()); }catch (Exception e) {tipAmount=0; }
                total = roundingAmount + tipAmount;
                binding.totalAmountTextView.setText(String.format("%,d",total));
            }
        };

        binding.countSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reqPayCount=String.valueOf(position+1);
                if(position==0){
                    binding.number2Layout.setVisibility(View.GONE);
                    binding.number3Layout.setVisibility(View.GONE);
                    binding.number4Layout.setVisibility(View.GONE);
                    binding.tip2Checkbox.setChecked(false);
                    binding.tip3Checkbox.setChecked(false);
                    binding.tip4Checkbox.setChecked(false);

                }else if(position==1){
                    binding.number2Layout.setVisibility(View.VISIBLE);
                    binding.number3Layout.setVisibility(View.GONE);
                    binding.number4Layout.setVisibility(View.GONE);
                    binding.tip3Checkbox.setChecked(false);
                    binding.tip4Checkbox.setChecked(false);
                }else if(position==2){
                    binding.number2Layout.setVisibility(View.VISIBLE);
                    binding.number3Layout.setVisibility(View.VISIBLE);
                    binding.number4Layout.setVisibility(View.GONE);
                    binding.tip4Checkbox.setChecked(false);
                }else if(position==3){
                    binding.number2Layout.setVisibility(View.VISIBLE);
                    binding.number3Layout.setVisibility(View.VISIBLE);
                    binding.number4Layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //data set
        binding.placeEditText.setText(placeName);
        binding.nameEditText.setText(name);
        binding.timeEditText.setText(simpleDateFormatFull.format(new Date(day)));
        binding.courseNameEditText.setText(courseName);
        binding.countSpinner.setSelection(Integer.parseInt(reqPayCount)-1);

        binding.amountEditText.addTextChangedListener(textWatcher);
        binding.tipEditText.addTextChangedListener(textWatcher);

        binding.detailLayout.setOnClickListener(v->{
            if(v.isSelected()){
                binding.detailContentLayout.setVisibility(View.INVISIBLE);
                binding.detailLayoutIconImageView.setImageResource(R.drawable.ic_next__1_);
            }else{
                binding.detailContentLayout.setVisibility(View.VISIBLE);
                binding.detailLayoutIconImageView.setImageResource(R.drawable.ic_next);
            }
            v.setSelected(!v.isSelected());
        });


        binding.cancelButton.setOnClickListener(v->finish());

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            inputMethodManager.hideSoftInputFromWindow(buttonView.getWindowToken(),0);

            boolean tipCheck1 = binding.tip1Checkbox.isChecked();
            boolean tipCheck2 = binding.tip2Checkbox.isChecked();
            boolean tipCheck3 = binding.tip3Checkbox.isChecked();
            boolean tipCheck4 = binding.tip4Checkbox.isChecked();

            //모두 체크되지 않았을경우
            if(!tipCheck1 && !tipCheck2 && !tipCheck3 && !tipCheck4){
                if(binding.tipCheckBox.isChecked()) binding.tipCheckBox.setChecked(false);
            }else{
                if(!binding.tipCheckBox.isChecked()) binding.tipCheckBox.setChecked(true);
            }

        };
        binding.tip1Checkbox.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.tip2Checkbox.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.tip3Checkbox.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.tip4Checkbox.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.tipCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){

            }else{
                binding.tip1Checkbox.setChecked(false);
                binding.tip2Checkbox.setChecked(false);
                binding.tip3Checkbox.setChecked(false);
                binding.tip4Checkbox.setChecked(false);
                binding.tipEditText.setText("0");
            }

        });
        binding.applyButton.setOnClickListener(v -> {
            int tipCheck1 = binding.tip1Checkbox.isChecked()?1:0;
            int tipCheck2 = binding.tip2Checkbox.isChecked()?1:0;
            int tipCheck3 = binding.tip3Checkbox.isChecked()?1:0;
            int tipCheck4 = binding.tip4Checkbox.isChecked()?1:0;

            if(binding.tipEditText.getText().toString().trim().length()>0){
                int tipCount = tipCheck1+tipCheck2+tipCheck3+tipCheck4;
                int tip = Integer.parseInt(binding.tipEditText.getText().toString().trim());
                if(tip>0 && tipCount==0){
                    //팁을 입력한 경우인데 체크를 안할때
                    new MtouchDialog(this).setTitleText("알림").setContentText("팁을 결제할 대상을 선택해주세요.").show();
                    return;
                }else if(tip==0 && tipCount>0){
                    //팁은 0인데 팁체크를 한 경우.
                    new MtouchDialog(this,v1->{
                        binding.tipCheckBox.setChecked(false);
                        binding.applyButton.performClick();
                    },v2->{
                        binding.tipEditText.requestFocus();
                        binding.tipEditText.setText("");
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.tipEditText,0),100);
                    }).setTitleText("알림").setContentText("팁금액이 0원입니다.\n계속 진행하시겠습니까?").show();
                    return;
                }
//                if(binding.tipCheckBox.isChecked()){
//                    //팁체크되어있음.
//                    new MtouchDialog(this).setTitleText("알림").setContentText("팁포함 체크되어있습니다.\n대상을 선택해주세요.").show();
//                    return;
//                }
            }

            if(CommonUtil.parseInt(binding.amountEditText.getText().toString().trim())==0){
                //금액 설정오류
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText("결제 금액을 확인해주세요.");
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.amountEditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.amountEditText,0),100);
                });
                mtouchDialog.show();
                return;
            }


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
            if (binding.courseNameEditText.getText().toString().trim().length() == 0) {
                String message = "코스를 입력하세요.";
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText(message);
                mtouchDialog.setOnDismissListener(dialog -> {
                    binding.courseNameEditText.requestFocus();
                    new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.courseNameEditText,0),100);
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

            int number1Length = binding.number1EditText.getText().toString().trim().length();
            int number2Length = binding.number2EditText.getText().toString().trim().length();
            int number3Length = binding.number3EditText.getText().toString().trim().length();
            int number4Length = binding.number4EditText.getText().toString().trim().length();

            boolean numberCheck1 = number1Length>5;
            boolean numberCheck2 = binding.number2Layout.getVisibility() == View.VISIBLE?number2Length>5:true;
            boolean numberCheck3 = binding.number3Layout.getVisibility() == View.VISIBLE?number3Length>5:true;
            boolean numberCheck4 = binding.number4Layout.getVisibility() == View.VISIBLE?number4Length>5:true;

            //인원정보 확인.
            if(!numberCheck1 || !numberCheck2 || !numberCheck3 || !numberCheck4){
                MtouchDialog mtouchDialog = new MtouchDialog(this).setTitleText("알림").setContentText("전화번호가 올바르지 않습니다.\n다시 확인해주세요.");
                mtouchDialog.setOnDismissListener(dialog -> {
                    if(!numberCheck1){
                        binding.number1EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.number1EditText,0),100);
                    }else if(!numberCheck2){
                        binding.number2EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.number2EditText,0),100);
                    }else if(!numberCheck3){
                        binding.number3EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.number3EditText,0),100);
                    }else if(!numberCheck4){
                        binding.number4EditText.requestFocus();
                        new Handler().postDelayed(()->inputMethodManager.showSoftInput(binding.number4EditText,0),100);
                    }
                });
                mtouchDialog.show();
                return;
            }

            boolean nameCheck1 = binding.name1EditText.getText().toString().trim().length()>0;
            boolean nameCheck2 = binding.number2Layout.getVisibility() == View.VISIBLE?binding.name2EditText.getText().toString().trim().length()>0:true;
            boolean nameCheck3 = binding.number3Layout.getVisibility() == View.VISIBLE?binding.name3EditText.getText().toString().trim().length()>0:true;
            boolean nameCheck4 = binding.number4Layout.getVisibility() == View.VISIBLE?binding.name4EditText.getText().toString().trim().length()>0:true;

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


            if(order==null) order = new Order();

            order.setReqPayCount(reqPayCount);
            order.setAmount(binding.totalAmountTextView.getText().toString().replaceAll(",",""));
            order.setPaymentInfo(new PaymentInfo(binding.placeEditText.getText().toString().trim()));
            ArrayList<Amount> amountArrayList = new ArrayList<>();
            int count = Integer.parseInt(order.getReqPayCount());

            for(int i=1;i<=count;i++){
                setAmountData(i,count, amountArrayList);
            }
            order.setAmountList(amountArrayList);

            Intent intent = new Intent(this, OrderConfirmActivity.class);
            intent.putExtra(Order.INTENT_KEY_ORDER, order);
            intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL,schedual);
            startActivityForResult(intent, REQUEST_CODE);
        });

    }

    private void setAmountData(int index,int count, ArrayList<Amount> amountArrayList) {
        int tipCheck1 = binding.tip1Checkbox.isChecked()?1:0;
        int tipCheck2 = binding.tip2Checkbox.isChecked()?1:0;
        int tipCheck3 = binding.tip3Checkbox.isChecked()?1:0;
        int tipCheck4 = binding.tip4Checkbox.isChecked()?1:0;
        int tipFirstIndex=0;
        if(tipCheck4==1)    tipFirstIndex=4;
        if(tipCheck3==1)    tipFirstIndex=3;
        if(tipCheck2==1)    tipFirstIndex=2;
        if(tipCheck1==1)    tipFirstIndex=1;

        int tipCount = tipCheck1+tipCheck2+tipCheck3+tipCheck4;
        int tipAmount = 0;
        try{ tipAmount = Integer.parseInt(binding.tipEditText.getText().toString().trim()); }catch (Exception e){tipAmount = 0;}
        int roundingAmount = 0;
        try{ roundingAmount = Integer.parseInt(binding.amountEditText.getText().toString().trim()); }catch (Exception e){roundingAmount = 0;}
        String payType = "";
        String phoneNumber= "";
        String name= "";

        String roundingResult = String.valueOf(roundingAmount / count);
        int roundingTemp = (int) Math.round((roundingAmount / count) / 1000.0) * 1000;
        int roundingQuotient = roundingAmount - (roundingTemp * count);//잔돈

        String tipResult="0";
        int tipTemp=0;
        int tipQuotient =0;

        if(tipCount!=0) {
            try {
                tipResult = String.valueOf(tipAmount / tipCount);
                tipTemp = (int) Math.round((tipAmount / tipCount) / 1000.0) * 1000;
                tipQuotient = tipAmount - (tipTemp * tipCount);//잔돈
            } catch (Exception e) {
            }
        }

        String tip = "0";
        String rounding = "0";
        if(roundingQuotient==0){
            rounding = roundingResult;
        }else if(index==1){ //첫번째 사람에게 적용.
            rounding = String.valueOf(roundingTemp+roundingQuotient);
        }else{
            rounding = String.valueOf(roundingTemp);
        }

        if(tipQuotient==0){
            tip = tipResult;
        }else if(index==tipFirstIndex){ //첫번째 사람에게 적용.
            tip = String.valueOf(tipTemp+tipQuotient);
        }else {
            tip = String.valueOf(tipTemp);
        }

        if(index==1) {
            payType = "SMS";
            phoneNumber = binding.number1EditText.getText().toString().trim();
            name = binding.name1EditText.getText().toString().trim();
            boolean isTip = binding.tip1Checkbox.isChecked();
            if(!isTip) tip = "0";
        }else if(index==2){
            payType = "SMS";
            phoneNumber = binding.number2EditText.getText().toString().trim();
            name = binding.name2EditText.getText().toString().trim();
            boolean isTip = binding.tip2Checkbox.isChecked();
            if(!isTip) tip = "0";
        }else if(index==3){
            payType = "SMS";
            phoneNumber = binding.number3EditText.getText().toString().trim();
            name = binding.name3EditText.getText().toString().trim();
            boolean isTip = binding.tip3Checkbox.isChecked();
            if(!isTip) tip = "0";
        }else if(index==4){
            payType = "SMS";
            phoneNumber = binding.number4EditText.getText().toString().trim();
            name = binding.name4EditText.getText().toString().trim();
            boolean isTip = binding.tip4Checkbox.isChecked();
            if(!isTip) tip = "0";
        }

        amountArrayList.add(new Amount(payType, phoneNumber, String.valueOf(Integer.parseInt(tip)+Integer.parseInt(rounding)),tip,rounding,name));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {
            if(resultCode == RESULT_OK){
                finish();
                overridePendingTransition(0, 0);
            }else if(resultCode == RESULT_CANCELED){
                //수정하러 돌아옴
            }
        }

    }
}