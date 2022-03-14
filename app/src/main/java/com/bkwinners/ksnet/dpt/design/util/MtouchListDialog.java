package com.bkwinners.ksnet.dpt.design.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import java.util.ArrayList;

public class MtouchListDialog extends Dialog {

    public interface OnListClickListener {
        void onItemClick(String item);
    }

    public interface OnListClickOtherDataListener{
        void onItemClick(String index, String otherData);
    }

    private ArrayList<Data> list = new ArrayList<>();

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private TextView titleTextView;
    private Button mPositiveButton;

    private OnListClickListener onListClickListener;
    private OnListClickOtherDataListener onListClickOtherDataListener;

    private String titleText = null;
    private String positiveText = null;

    public MtouchListDialog(@NonNull Context context, OnListClickListener onListClickListener) {
        super(context);
        this.onListClickListener = onListClickListener;
        setCancelable(true);
    }

    public MtouchListDialog(@NonNull Context context, OnListClickListener onListClickListener, boolean isCancelable) {
        super(context);
        this.onListClickListener = onListClickListener;
        setCancelable(isCancelable);
    }

    public MtouchListDialog(Context context, OnListClickOtherDataListener onListClickOtherDataListener){
        super(context);
        this.onListClickOtherDataListener = onListClickOtherDataListener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.mtouch_dialog_list_layout);

        recyclerViewAdapter = new RecyclerViewAdapter();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setAdapter(recyclerViewAdapter);

        //셋팅
        mPositiveButton = findViewById(R.id.confirmButton);
        titleTextView = findViewById(R.id.titleTextView);

        if (positiveText != null && positiveText.length() > 0)
            mPositiveButton.setText(positiveText);
        if (titleText != null && titleText.length() > 0)
            titleTextView.setText(titleText);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton.setOnClickListener(v -> {
            dismiss();
            if (onListClickListener != null)
                onListClickListener.onItemClick(recyclerViewAdapter.getIndexString());

            if(onListClickOtherDataListener!=null) {
                Data data = recyclerViewAdapter.getItem();
                if(data!=null) onListClickOtherDataListener.onItemClick(data.indexString,data.otherData);
            }
        });


    }

    public MtouchListDialog setTitleText(String titleText) {
        this.titleText = titleText;
        return this;
    }

    public MtouchListDialog setPositiveButtonText(String text) {
        positiveText = text;
        return this;
    }

    public void addList(boolean isChecked, String indexString){
        list.add(new Data(isChecked,indexString));
    }

    public void addList(String indexString, String otherData){
        list.add(new Data(indexString,otherData));
    }

    class Data {
        private boolean isChecked = false;
        private String indexString = "0";
        private String otherData;

        public Data(String indexString) {
            this.indexString = indexString;
            this.isChecked = false;
        }

        public Data(boolean isChecked, String indexString) {
            this.isChecked = isChecked;
            this.indexString = indexString;
        }

        public Data(String indexString, String otherData) {
            this.indexString = indexString;
            this.otherData = otherData;
        }

        public String getOtherData() {
            return otherData;
        }

        public void setOtherData(String otherData) {
            this.otherData = otherData;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public Data setChecked(boolean checked) {
            isChecked = checked;
            return this;
        }

        public String getIndexString() {
            return indexString;
        }

        public Data setIndexString(String indexString) {
            this.indexString = indexString;
            return this;
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MtouchDialogViewHolder> {

        public void addItem(Data installData) {
            list.add(installData);
        }

        @NonNull
        @Override
        public RecyclerViewAdapter.MtouchDialogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerViewAdapter.MtouchDialogViewHolder(View.inflate(parent.getContext(), R.layout.item_installment_layout, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerViewAdapter.MtouchDialogViewHolder holder, int position) {
            Data item = list.get(position);

            holder.installmentTextView.setText(item.getIndexString());

            holder.radioButton.setChecked(item.isChecked);
            holder.itemView.setTag(item);
        }

        @Override
        public int getItemCount() {
            if (list == null) {
                return 0;
            } else {
                return list.size();
            }
        }

        public Data getItem(){
            for (Data item : list) {
                if (item.isChecked) {
                    return item;
                }
            }
            return null;
        }
        public String getIndexString() {
            for (Data item : list) {
                if (item.isChecked) {
                    return item.getIndexString();
                }
            }
            return "0";
        }

        private void resetData(Data data) {
            for (Data item : list) {
                if (data != null && data == item) {
                    item.setChecked(true);
                } else {
                    item.setChecked(false);
                }
            }
            notifyDataSetChanged();
        }

        class MtouchDialogViewHolder extends RecyclerView.ViewHolder {

            private RadioButton radioButton;
            private TextView installmentTextView;

            public MtouchDialogViewHolder(@NonNull View itemView) {
                super(itemView);

                radioButton = itemView.findViewById(R.id.radioButton);
                installmentTextView = itemView.findViewById(R.id.installmentTextView);

                itemView.setOnClickListener(v -> {
                    Data item = (Data) v.getTag();
                    resetData(item);
                });
            }
        }
    }
}
