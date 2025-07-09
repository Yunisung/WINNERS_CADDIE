package com.bkwinners.caddie;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.bkwinners.caddie.data.Schedual;
import com.bkwinners.caddie.databinding.ActivitySchedualBinding;
import com.bkwinners.ksnet.dpt.design.util.LOG;
import com.bkwinners.ksnet.dpt.design.util.MtouchDialog;
import com.bkwinners.ksnet.dpt.design.util.SharedPreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.bkwinners.caddie.R;
import com.bkwinners.caddie.BuildConfig;

public class SchedualActivity extends DefaultActivity {

    private static final int REQUEST_DETAIL = 2000;
    private static final int REQUEST_APPLY = 3000;
    private static final int REQUEST_EDIT = 3000;

    private ActivitySchedualBinding binding;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat simpleDateFormatForTime = new SimpleDateFormat("HH:mm");

    private SchedualRecyclerViewAdapter schedualRecyclerViewAdapter;
    private SchedualDayRecyclerViewAdapter schedualDayRecyclerViewAdapter;

    private ArrayList<Schedual> scheduallist;

    private boolean isMonthTab = true;

    private Date selectDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_schedual);
        binding = ActivitySchedualBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        bindView();
    }

    private void bindView() {
        ((TextView) binding.getRoot().findViewById(R.id.headerTitleTextView)).setText("일정");
        binding.getRoot().findViewById(R.id.backButton).setVisibility(View.VISIBLE);
        binding.getRoot().findViewById(R.id.backButton).setOnClickListener(v -> {
            finish();
        });

        binding.applyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SchedualApplyActivity.class);
            intent.putExtra("selectDate", selectDate);
            startActivityForResult(intent, REQUEST_APPLY);
        });
        binding.plusButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, SchedualApplyActivity.class), REQUEST_APPLY);
        });
        binding.beforeImageView.setOnClickListener(v -> binding.compactcalendarView.scrollLeft());
        binding.afterImageView.setOnClickListener(v -> binding.compactcalendarView.scrollRight());

        binding.compactcalendarView.setFirstDayOfWeek(Calendar.SUNDAY);

        binding.monthTextView.setOnClickListener(v -> {
            isMonthTab = true;
            binding.monthTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            binding.monthView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));
            binding.monthLayout.setVisibility(View.VISIBLE);

            binding.listTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            binding.listView.setBackgroundColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            binding.listLayout.setVisibility(View.GONE);

            if (schedualDayRecyclerViewAdapter.getItemCount() > 0) {
                binding.applyButton.setVisibility(View.GONE);
                binding.deleteButton.setVisibility(View.VISIBLE);
                binding.editButton.setVisibility(View.VISIBLE);
            } else {
                binding.applyButton.setVisibility(View.VISIBLE);
                binding.deleteButton.setVisibility(View.GONE);
                binding.editButton.setVisibility(View.GONE);
            }
        });

        binding.listTextView.setOnClickListener(v -> {
            isMonthTab = false;
            binding.listTextView.setTextColor(ContextCompat.getColor(this, R.color.greeny_blue));
            binding.listView.setBackgroundColor(ContextCompat.getColor(this, R.color.greeny_blue));
            binding.listLayout.setVisibility(View.VISIBLE);

            binding.monthTextView.setTextColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            binding.monthView.setBackgroundColor(ContextCompat.getColor(this, R.color.greyish_brown_two));
            binding.monthLayout.setVisibility(View.GONE);

            if (schedualRecyclerViewAdapter.getItemCount() > 0) {
                binding.applyButton.setVisibility(View.GONE);
                binding.deleteButton.setVisibility(View.VISIBLE);
                binding.editButton.setVisibility(View.VISIBLE);
            } else {
                binding.applyButton.setVisibility(View.VISIBLE);
                binding.deleteButton.setVisibility(View.GONE);
                binding.editButton.setVisibility(View.GONE);
            }
        });

        binding.compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectDate = dateClicked;

                String currentDay = new SimpleDateFormat("yyyyMMdd").format(dateClicked);
                ArrayList<Schedual> daySchedual = new ArrayList<>();
                for (Schedual schedual : scheduallist) {
                    if (schedual.getDayToString().equals(currentDay)) {
                        daySchedual.add(schedual);
                    }
                }


                if (daySchedual.size() == 0) {
                    binding.monthRecyclerView.setVisibility(View.GONE);
                    binding.plusButton.setVisibility(View.INVISIBLE);

                    binding.applyButton.setVisibility(View.VISIBLE);
                    binding.deleteButton.setVisibility(View.GONE);
                    binding.editButton.setVisibility(View.GONE);
                } else {
                    binding.monthRecyclerView.setVisibility(View.VISIBLE);
                    binding.plusButton.setVisibility(View.VISIBLE);

                    binding.applyButton.setVisibility(View.GONE);
                    binding.deleteButton.setVisibility(View.VISIBLE);
                    binding.editButton.setVisibility(View.VISIBLE);
                }

                schedualDayRecyclerViewAdapter.clearItem();
                schedualDayRecyclerViewAdapter.addList(daySchedual);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                String currentDay = new SimpleDateFormat("yyyyMMdd").format(firstDayOfNewMonth);
                ArrayList<Schedual> daySchedual = new ArrayList<>();
                for (Schedual schedual : scheduallist) {
                    if (schedual.getDayToString().equals(currentDay)) {
                        daySchedual.add(schedual);
                    }
                }
                schedualDayRecyclerViewAdapter.clearItem();
                schedualDayRecyclerViewAdapter.addList(daySchedual);

                if (daySchedual.size() == 0) {
                    binding.monthRecyclerView.setVisibility(View.GONE);
                    binding.plusButton.setVisibility(View.INVISIBLE);

                    binding.applyButton.setVisibility(View.VISIBLE);
                    binding.deleteButton.setVisibility(View.GONE);
                    binding.editButton.setVisibility(View.GONE);
                } else {
                    binding.monthRecyclerView.setVisibility(View.VISIBLE);
                    binding.plusButton.setVisibility(View.VISIBLE);

                    binding.applyButton.setVisibility(View.GONE);
                    binding.deleteButton.setVisibility(View.VISIBLE);
                    binding.editButton.setVisibility(View.VISIBLE);
                }
                binding.dayTextView.setText(new SimpleDateFormat("yyyy.MM").format(firstDayOfNewMonth));
            }
        });


        binding.deleteButton.setOnClickListener(v -> {
            if (isMonthTab) {
                schedualDayRecyclerViewAdapter.removeSchedual();
            } else {
                schedualRecyclerViewAdapter.removeSchedual();
            }
        });
        binding.editButton.setOnClickListener(v -> {
            if (isMonthTab) {
                schedualDayRecyclerViewAdapter.editSchedual();
            } else {
                schedualRecyclerViewAdapter.editSchedual();
            }
        });
    }

    private void init() {
        schedualRecyclerViewAdapter = new SchedualRecyclerViewAdapter();
        schedualDayRecyclerViewAdapter = new SchedualDayRecyclerViewAdapter();
        binding.schedualRecyclerview.setAdapter(schedualRecyclerViewAdapter);
        binding.monthRecyclerView.setAdapter(schedualDayRecyclerViewAdapter);
        binding.dayTextView.setText(new SimpleDateFormat("yyyy.MM").format(new Date()));
        Map<String, Object> map = SharedPreferenceUtil.getAllSchedual(this);
        scheduallist = new ArrayList<>();

        ArrayList<Schedual> daySchedual = new ArrayList<>();
        String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());


        if (map != null && map.size() > 0) {
            binding.compactcalendarView.removeAllEvents();
            for (Object key : map.keySet()) {
                Schedual schedual = Schedual.fromJSONString((String) map.get(key));
                scheduallist.add(schedual);

                binding.compactcalendarView.addEvent(new Event(ContextCompat.getColor(this, R.color.greeny_blue), schedual.getDay()));
                LOG.w("event: " + new Date(schedual.getDay()));
                if (schedual.getDayToString().equals(currentDay)) {
                    daySchedual.add(schedual);
                }

            }
        }

        if (daySchedual.size() == 0) {
            binding.monthRecyclerView.setVisibility(View.GONE);
            binding.plusButton.setVisibility(View.INVISIBLE);

            binding.applyButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);
            binding.editButton.setVisibility(View.GONE);
        } else {
            binding.monthRecyclerView.setVisibility(View.VISIBLE);
            binding.plusButton.setVisibility(View.VISIBLE);

            binding.applyButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);
            binding.editButton.setVisibility(View.VISIBLE);
        }

        schedualRecyclerViewAdapter.addList(scheduallist);
        schedualDayRecyclerViewAdapter.clearItem();
        schedualDayRecyclerViewAdapter.addList(daySchedual);

    }


    class SchedualDayRecyclerViewAdapter extends RecyclerView.Adapter<SchedualDayRecyclerViewAdapter.SchedualDayViewHolder> {
        private ArrayList<Schedual> schedualArrayList = new ArrayList<>();

        public void setList(ArrayList<Schedual> list) {
            if (list != null) schedualArrayList = list;
            notifyDataSetChanged();
        }

        public void addList(ArrayList<Schedual> list) {
            if (list != null) {
                schedualArrayList.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void addItem(Schedual schedual) {
            schedualArrayList.add(schedual);
        }

        public void clearItem() {
            schedualArrayList = new ArrayList<>();
            notifyDataSetChanged();
        }

        public void removeSchedual() {
            Date removeDate = null;
            Schedual removeSchedual = null;
            for (Schedual schedual : schedualArrayList) {
                if (schedual.isCheck()) {
                    removeDate = new Date(schedual.getDay());
                    removeSchedual = schedual;
                    break;
                }
            }

            if (removeSchedual != null) {
                Schedual finalRemoveSchedual = removeSchedual;
                Date finalRemoveDate = removeDate;
                new MtouchDialog(SchedualActivity.this, v -> {
                    scheduallist.remove(finalRemoveSchedual);
                    schedualArrayList.remove(finalRemoveSchedual);
                    SharedPreferenceUtil.removeSchedual(SchedualActivity.this, finalRemoveSchedual);
                    new MtouchDialog(SchedualActivity.this).setImageResource(R.drawable.ic_icon_check)
                            .setTitleText("삭제 완료").setContentText("일정이 삭제되었습니다.").show();

                    if (finalRemoveDate != null) {
                        binding.compactcalendarView.removeAllEvents();

                        String currentDay = new SimpleDateFormat("yyyyMMdd").format(finalRemoveDate);
                        ArrayList<Schedual> daySchedual = new ArrayList<>();
                        for (Schedual schedual : scheduallist) {
                            if (schedual.getDayToString().equals(currentDay)) {
                                daySchedual.add(schedual);
                            }
                            binding.compactcalendarView.addEvent(new Event(ContextCompat.getColor(SchedualActivity.this, R.color.greeny_blue), schedual.getDay()));
                        }


                        if (daySchedual.size() == 0) {
                            binding.monthRecyclerView.setVisibility(View.GONE);
                            binding.plusButton.setVisibility(View.INVISIBLE);

                            binding.applyButton.setVisibility(View.VISIBLE);
                            binding.deleteButton.setVisibility(View.GONE);
                            binding.editButton.setVisibility(View.GONE);
                        } else {
                            binding.monthRecyclerView.setVisibility(View.VISIBLE);
                            binding.plusButton.setVisibility(View.VISIBLE);

                            binding.applyButton.setVisibility(View.GONE);
                            binding.deleteButton.setVisibility(View.VISIBLE);
                            binding.editButton.setVisibility(View.VISIBLE);
                        }

                        schedualDayRecyclerViewAdapter.clearItem();
                        schedualDayRecyclerViewAdapter.addList(daySchedual);
                    }
                }, v -> {

                }).setImageResource(R.drawable.ic_delete_calendar).setTitleText("일정 삭제")
                        .setContentText("선택하신 일정을 삭제 하시겠습니까?").show();
            }else {
                new MtouchDialog(SchedualActivity.this).setTitleText("알림").setContentText("선택된 일정이 없습니다.").show();
            }
        }

        public void editSchedual() {
            Schedual editScedual = null;
            for (Schedual schedual : schedualArrayList) {
                if (schedual.isCheck()) {
                    editScedual = schedual;
                    break;
                }
            }

            if (editScedual != null) {
                Intent intent = new Intent(SchedualActivity.this, SchedualApplyActivity.class);
                intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, editScedual);
                startActivityForResult(intent, REQUEST_EDIT);
            } else {
                new MtouchDialog(SchedualActivity.this).setTitleText("알림").setContentText("선택된 일정이 없습니다.").show();
            }
        }


        @NonNull
        @Override
        public SchedualDayRecyclerViewAdapter.SchedualDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SchedualDayRecyclerViewAdapter.SchedualDayViewHolder(View.inflate(parent.getContext(), R.layout.schdule_item_layout, null));
        }

        @Override
        public void onBindViewHolder(@NonNull SchedualDayRecyclerViewAdapter.SchedualDayViewHolder holder, int position) {
            Schedual schedual = schedualArrayList.get(position);
            StringBuilder sb = new StringBuilder();
            sb.append(simpleDateFormatForTime.format(new Date(schedual.getDay())));
            sb.append(" / ");
            sb.append(schedual.getCount() + "명");
            sb.append(" / ");
            sb.append(schedual.getName());
            sb.append(" / ");
            sb.append(schedual.getPhoneNumber1());
//            holder.radioButton.setText(sb.toString());
            holder.contentTextView.setText(sb.toString());
            holder.radioButton.setChecked(schedual.isCheck());
            holder.itemView.setTag(schedual);
        }

        @Override
        public int getItemCount() {
            if (schedualArrayList == null) {
                return 0;
            } else {
                return schedualArrayList.size();
            }
        }


        class SchedualDayViewHolder extends RecyclerView.ViewHolder {

            public RadioButton radioButton;
            public TextView contentTextView;

            public SchedualDayViewHolder(@NonNull View itemView) {
                super(itemView);

                radioButton = itemView.findViewById(R.id.radioButton);
                contentTextView = itemView.findViewById(R.id.contentTextView);

                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        for (Schedual schedual : schedualArrayList) {
                            schedual.setCheck(schedual == itemView.getTag());
                        }
                        notifyDataSetChanged();
                    }
                });

                contentTextView.setOnClickListener(v -> {
                    Intent intent = new Intent(SchedualActivity.this, SchedualDetailActivity.class);
                    intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, (Schedual) itemView.getTag());
                    startActivityForResult(intent, REQUEST_DETAIL);
                });
            }
        }

    }


    class SchedualRecyclerViewAdapter extends RecyclerView.Adapter<SchedualRecyclerViewAdapter.SchedualViewHolder> {
        private ArrayList<Schedual> schedualArrayList = new ArrayList<>();

        public void setList(ArrayList<Schedual> list) {
            if (list != null) schedualArrayList = list;
            notifyDataSetChanged();
        }

        public void addList(ArrayList<Schedual> list) {
            if (list != null) {
                schedualArrayList.addAll(list);
                notifyDataSetChanged();
            }
        }

        public void addItem(Schedual schedual) {
            schedualArrayList.add(schedual);
        }

        public void clearItem() {
            schedualArrayList.clear();
            notifyDataSetChanged();
        }

        public void removeSchedual() {
            Date removeDate = null;
            Schedual removeSchedual = null;
            for (Schedual schedual : schedualArrayList) {
                if (schedual.isCheck()) {
                    removeDate = new Date(schedual.getDay());
                    removeSchedual = schedual;
                    break;
                }
            }

            if (removeSchedual != null) {
                Schedual finalRemoveSchedual = removeSchedual;
                Date finalRemoveDate = removeDate;

                new MtouchDialog(SchedualActivity.this, v -> {
                    SharedPreferenceUtil.removeSchedual(SchedualActivity.this, finalRemoveSchedual);
                    new MtouchDialog(SchedualActivity.this).setImageResource(R.drawable.ic_icon_check)
                                   .setTitleText("삭제 완료").setContentText("일정이 삭제되었습니다.").show();
                    init();
                }, v -> {

                }).setImageResource(R.drawable.ic_delete_calendar).setTitleText("일정 삭제")
                        .setContentText("선택하신 일정을 삭제 하시겠습니까?").show();

            }else{
                new MtouchDialog(SchedualActivity.this).setTitleText("알림").setContentText("선택된 일정이 없습니다.").show();
            }
        }

        public void editSchedual() {
            Schedual editScedual = null;
            for (Schedual schedual : schedualArrayList) {
                if (schedual.isCheck()) {
                    editScedual = schedual;
                    break;
                }
            }

            if (editScedual != null) {
                Intent intent = new Intent(SchedualActivity.this, SchedualApplyActivity.class);
                intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, editScedual);
                startActivityForResult(intent, REQUEST_EDIT);
            } else {
                new MtouchDialog(SchedualActivity.this).setTitleText("알림").setContentText("선택된 일정이 없습니다.").show();
            }
        }

        @NonNull
        @Override
        public SchedualRecyclerViewAdapter.SchedualViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SchedualRecyclerViewAdapter.SchedualViewHolder(View.inflate(parent.getContext(), R.layout.item_schedual_layout, null));
        }

        @Override
        public void onBindViewHolder(@NonNull SchedualRecyclerViewAdapter.SchedualViewHolder holder, int position) {
            Schedual schedual = schedualArrayList.get(position);
            holder.timeTextView.setText(simpleDateFormatForTime.format(new Date(schedual.getDay())));
            holder.dayTextView.setText(simpleDateFormat.format(new Date(schedual.getDay())));
            holder.countTextView.setText(schedual.getCount() + "");
            holder.placeTextView.setText(schedual.getPlaceName());
            holder.nameTextView.setText(schedual.getName());
            holder.courseTextView.setText(schedual.getCourseName());
            holder.phoneNumberTextView.setText(schedual.getPhoneNumber1());

            holder.itemView.setTag(schedual);
        }

        @Override
        public int getItemCount() {
            if (schedualArrayList == null) {
                return 0;
            } else {
                return schedualArrayList.size();
            }
        }


        class SchedualViewHolder extends RecyclerView.ViewHolder {

            private final RadioButton radioButton;
            private final LinearLayout detailLayout;
            private final TextView dayTextView;
            private final TextView timeTextView;
            private final TextView placeTextView;
            private final TextView courseTextView;
            private final TextView countTextView;
            private final TextView nameTextView;
            private final TextView phoneNumberTextView;

            public SchedualViewHolder(@NonNull View itemView) {
                super(itemView);

                radioButton = itemView.findViewById(R.id.radioButton);
                dayTextView = itemView.findViewById(R.id.dayTextView);
                timeTextView = itemView.findViewById(R.id.timeTextView);
                placeTextView = itemView.findViewById(R.id.placeTextView);
                courseTextView = itemView.findViewById(R.id.courseTextView);
                countTextView = itemView.findViewById(R.id.countTextView);
                nameTextView = itemView.findViewById(R.id.nameTextView);
                phoneNumberTextView = itemView.findViewById(R.id.phoneNumberTextView);
                detailLayout = itemView.findViewById(R.id.detailLayout);

                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        for (Schedual schedual : schedualArrayList) {
                            schedual.setCheck(schedual == itemView.getTag());
                        }
                        notifyDataSetChanged();

                    }
                });

                detailLayout.setOnClickListener(v -> {
                    Intent intent = new Intent(SchedualActivity.this, SchedualDetailActivity.class);
                    intent.putExtra(Schedual.INTENT_KEY_SCHEDUAL, (Schedual) itemView.getTag());
                    startActivityForResult(intent, REQUEST_DETAIL);
                });

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DETAIL) {
            if (resultCode == RESULT_OK) {
                finish();
                overridePendingTransition(0, 0);
            }
        } else if (requestCode == REQUEST_APPLY) {
            if (resultCode == RESULT_OK) {
                init();
            }
        } else if (requestCode == REQUEST_EDIT) {
            init();
        }
    }
}