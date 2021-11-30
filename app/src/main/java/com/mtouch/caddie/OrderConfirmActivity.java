package com.mtouch.caddie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mtouch.caddie.data.Schedual;
import com.mtouch.caddie.databinding.ActivityOrderConfirmBinding;
import com.mtouch.caddie.network.CaddieAPIService;
import com.mtouch.caddie.network.NetworkManager;
import com.mtouch.caddie.network.model.Amount;
import com.mtouch.caddie.network.model.Order;
import com.mtouch.caddie.network.model.OrderDetail;
import com.mtouch.caddie.network.model.OrderListDetailResponse;
import com.mtouch.ksnet.dpt.action.PayResultActivity;
import com.mtouch.ksnet.dpt.design.util.LOG;
import com.mtouch.ksnet.dpt.design.util.MtouchDialog;
import com.mtouch.ksnet.dpt.design.util.SharedPreferenceUtil;
import com.mtouch.ksnet.dpt.ks03.bluetooth.DeviceRegistActivity;
import com.mtouch.ksnet.dpt.ks03.pay.Constants;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class OrderConfirmActivity extends DefaultActivity {

    private ActivityOrderConfirmBinding binding;
    private Order order;
    private Schedual schedual;

    private CaddieAPIService caddieAPIService;

    private ArrayList<OrderDetail> orderDetailArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_order_confirm);
        binding = ActivityOrderConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();
    }

    private void init() {
        if (getIntent() != null) {
            order = (Order) getIntent().getSerializableExtra(Order.INTENT_KEY_ORDER);
            schedual = (Schedual) getIntent().getSerializableExtra(Schedual.INTENT_KEY_SCHEDUAL);
            LOG.w("list: " + order.toString());
        }

        caddieAPIService = NetworkManager.getAPIService(getApplicationContext());
    }

    private void bindView() {
        ((TextView) binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("결제정보 확인");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        if (order != null) {
            int count = Integer.parseInt(order.getReqPayCount());
            for (int i = 0; i < count; i++) {
                Amount amount = order.getAmountList().get(i);
                if (i == 0) {
                    binding.number1TextView.setText(amount.getPhone());
                    binding.tip1TextView.setText(String.format("%,d", Integer.parseInt(amount.getTip())));
                    binding.amount1TextView.setText(String.format("%,d", Integer.parseInt(amount.getAmount())));
                } else if (i == 1) {
                    binding.number2TextView.setText(amount.getPhone());
                    binding.tip2TextView.setText(String.format("%,d", Integer.parseInt(amount.getTip())));
                    binding.amount2TextView.setText(String.format("%,d", Integer.parseInt(amount.getAmount())));
                } else if (i == 2) {
                    binding.number3TextView.setText(amount.getPhone());
                    binding.tip3TextView.setText(String.format("%,d", Integer.parseInt(amount.getTip())));
                    binding.amount3TextView.setText(String.format("%,d", Integer.parseInt(amount.getAmount())));
                } else if (i == 3) {
                    binding.number4TextView.setText(amount.getPhone());
                    binding.tip4TextView.setText(String.format("%,d", Integer.parseInt(amount.getTip())));
                    binding.amount4TextView.setText(String.format("%,d", Integer.parseInt(amount.getAmount())));
                }
            }
            if (count == 1) {
            } else if (count == 2) {
                binding.number2Layout.setVisibility(View.VISIBLE);
            } else if (count == 3) {
                binding.number2Layout.setVisibility(View.VISIBLE);
                binding.number3Layout.setVisibility(View.VISIBLE);
            } else if (count == 4) {
                binding.number2Layout.setVisibility(View.VISIBLE);
                binding.number3Layout.setVisibility(View.VISIBLE);
                binding.number4Layout.setVisibility(View.VISIBLE);
            }
        }

        binding.modifyButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        binding.smsButton.setOnClickListener(v -> {
            applyOrder(() -> new MtouchDialog(this, vv -> {
                setResult(RESULT_OK);
                finish();
            }, false).setImageResource(R.drawable.ic_icon_check).setTitleText("전송완료").setContentText("SMS를 성공적으로 전송 하였습니다.").show());
        });

        binding.directButton.setOnClickListener(v -> {
            for (Amount amount : order.getAmountList()) {
                amount.setPayType("수기");
            }
            applyOrder(() -> {
                setResult(RESULT_OK);
                finish();
                Intent intent = new Intent(this, OrderDirectPaymentActivity.class);
                intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST, orderDetailArrayList);
                startActivity(intent);
            });
        });

        binding.cashButton.setOnClickListener(v -> {
            //todo 현금영수증 처리?
            for (Amount amount : order.getAmountList()) {
                amount.setPayType("현금");
            }


            applyOrder(() -> {
                new MtouchDialog(this,vv->{
                    setResult(RESULT_OK);
                    finish();
                },false).setImageResource(R.drawable.ic_icon_check).setTitleText("전송완료").setContentText("결제완료 처리되었습니다.").show();

            });
        });

        binding.readerPayButton.setOnClickListener(v->{
            String address = SharedPreferenceUtil.getData(OrderConfirmActivity.this, Constants.KEY_MAC_ADDRESS, "NONE");
            if(address.equals("NONE")){
                new MtouchDialog(this,vv->{
                    SharedPreferenceUtil.putData(this, Constants.KEY_MAC_ADDRESS,"NONE");
                    startActivity(new Intent(this, DeviceRegistActivity.class));
                },vv->{

                }).setPositiveButtonText("등록하기").setTitleText("알림").setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).setContentText("블루투스 리더기가 등록되어 있지 않습니다.\n등록후 시도해주세요.").show();
                return;
            }


            for (Amount amount : order.getAmountList()) {
                amount.setPayType("단말기");
            }
            applyOrder(() -> {
                setResult(RESULT_OK);
                finish();
                Intent intent = new Intent(this, OrderPaymentActivity.class);
                intent.putExtra(OrderDetail.INTENT_KEY_ORDER_DETAIL_LIST, orderDetailArrayList);
                startActivity(intent);
            });
        });

        //PYS : 단말기결제 안보이게 처리
        binding.readerPayButton.setVisibility(View.GONE);
    }

    private void applyOrder(Runnable callback) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("reqPayCount", order.getReqPayCount());
        map.put("amount", order.getAmount());
        map.put("paymentInfo", order.getPaymentInfo());
        map.put("amountInfoArray", order.getAmountList());
        showLoading();
        caddieAPIService.orderApply(map).enqueue(new Callback<OrderListDetailResponse>() {
            @Override
            public void onResponse(Call<OrderListDetailResponse> call, retrofit2.Response<OrderListDetailResponse> response) {
                hideLoading();
                try {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            orderDetailArrayList = response.body().getList();
                            SharedPreferenceUtil.removeSchedual(OrderConfirmActivity.this, schedual);
//                            for (OrderDetail orderDetail : orderDetailArrayList) {
//                                orderDetail.setTip(order.getAmountInfo());
//                            }
                            callback.run();
                        } else {
                            String resultMsg = new String(response.errorBody().bytes());
                            new MtouchDialog(OrderConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                        }
                    } else {
                        String resultMsg = new String(response.errorBody().bytes());
                        new MtouchDialog(OrderConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(resultMsg).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderListDetailResponse> call, Throwable t) {
                hideLoading();
                String resultMsg = getString(R.string.network_error_msg);
                new MtouchDialog(OrderConfirmActivity.this, v -> finish()).setTitleText("알림").setContentText(getString(R.string.network_error_msg)).setImageResource(R.drawable.ic_icon_awesome_exclamation_circle).show();
            }
        });
    }
}