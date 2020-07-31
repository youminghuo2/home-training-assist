package com.example.hometrainng.activity.ui.tab3;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.activity.NotificationDetailActivity;
import com.example.hometrainng.activity.VideoEditActivity;
import com.example.hometrainng.adapter.NotificationFragmentAdapter;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxSPTool;

import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class NotificationsFragment extends Fragment {

    @BindView(R.id.right_back_img)
    ImageView rightBackImg;
    @BindView(R.id.left_back_img)
    ImageView leftBackImg;
    @BindView(R.id.data_time_tv)
    TextView dataTimeTv;
    @BindView(R.id.data_spinner)
    AppCompatSpinner dataSpinner;
    @BindView(R.id.fragment_dashboard_recycleView)
    RecyclerView fragmentDashboardRecycleView;
    @BindView(R.id.total_percent_tv)
    TextView totalPercentTv;
    @BindView(R.id.record_progress)
    ProgressBar recordProgress;
    @BindView(R.id.edit_button)
    Button editButton;
    @BindView(R.id.empty_relayout)
    RelativeLayout emptyRelayout;
    @BindView(R.id.image_text)
    TextView imageText;
    private NotificationsViewModel notificationsViewModel;
    private Unbinder unbinder;
    private Context mContext;


    private LocalDate today, beginDate, endDate;
    //首次进入选中第几个
    private int countPosition;

    //跳转前选中第几个
    private int jumpPosition;

    //判断前後半月
    private String isWhich;
    private int beginYear, endYear, endMonth, beginMonth;
    private int i;
    private int leftState, rightState;

    //接受时间
    private String firstDay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        unbinder = ButterKnife.bind(this, root);
        mContext = getActivity();
        initImageText();
        return root;
    }

    //显示文本
    private void initImageText() {
        int number = (int) (Math.random() * 12 + 1);
        switch (number) {
            case 1:
                imageText.setText(R.string.image_text_1);
                break;
            case 2:
                imageText.setText(R.string.image_text_2);
                break;
            case 3:
                imageText.setText(R.string.image_text_3);
                break;
            case 4:
                imageText.setText(R.string.image_text_4);
                break;
            case 5:
                imageText.setText(R.string.image_text_5);
                break;
            case 6:
                imageText.setText(R.string.image_text_6);
                break;
            case 7:
                imageText.setText(R.string.image_text_7);
                break;
            case 8:
                imageText.setText(R.string.image_text_8);
                break;
            case 9:
                imageText.setText(R.string.image_text_9);
                break;
            case 10:
                imageText.setText(R.string.image_text_10);
                break;
            case 11:
                imageText.setText(R.string.image_text_11);
                break;
            case 12:
                imageText.setText(R.string.image_text_12);
                break;
        }
    }

    private void setcheckToday() {
        /**
         * 获取今天的时间
         */
        today = LocalDate.now();

        List<TimeDate> timeDateList = LitePal.select("*").limit(1).find(TimeDate.class);
        TimeDate timeDate = timeDateList.get(0);


        ContentValues values = new ContentValues();
        values.put("endDate", today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        LitePal.updateAll(TimeDate.class, values);
        /**
         * 开始结束时间初始化
         */
        endDate = today;
        beginDate = LocalDate.parse(timeDate.getVideoDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        List<String> city;
        beginYear = beginDate.getYear();
        beginMonth = beginDate.getMonthValue();
        endYear = endDate.getYear();
        endMonth = endDate.getMonthValue();

        if (endMonth < 7) {
            isWhich = "前半";
        } else {
            isWhich = "後半";
        }

        /**
         * 如果当前日期在时间范围内，则创建timeDate对象,格式化year和month
         */

        city = Utils.getDesc(beginDate, endDate);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, city);
        dataSpinner.setAdapter(stringArrayAdapter);

        countPosition = city.size() - 1;
        jumpPosition = countPosition;
        dataSpinner.setSelection(countPosition);

        dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());


        /**
         * 计算起始日，来加载recycleView
         */
        String firstDay = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
        String todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        CommonFilter(firstDay, todayString);

    }

    @OnClick(R.id.edit_button)
    public void onViewClicked(View view) {
        if (view.getId() == R.id.edit_button) {
            Intent intent = new Intent(getActivity(), VideoEditActivity.class);
            intent.putExtra("firToJump", true);
            intent.putExtra("dateTimeDate", dataTimeTv.getText().toString());
            intent.putExtra("jumpPosition", jumpPosition);
            intent.putExtra("rightState", rightState);
            intent.putExtra("leftState", leftState);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        if (RxSPTool.getBoolean(mContext, "JumpState")) {
//            RxSPTool.putBoolean(mContext, "JumpState", false);
            Intent intent = getActivity().getIntent();
            countPosition = intent.getIntExtra("jumpPosition", 0);
            firstDay = intent.getStringExtra("dateTimeDate");
            rightState = intent.getIntExtra("rightState", 0);
            leftState = intent.getIntExtra("leftState", 0);
            setcheckToday2();
            initDate();
        } else {
            setcheckToday();
            initDate();
        }

    }


    private void setcheckToday2() {
        /**
         * 获取今天的时间
         */
        today = LocalDate.now();

        List<TimeDate> timeDateList = LitePal.select("*").limit(1).find(TimeDate.class);
        TimeDate timeDate = timeDateList.get(0);

        /**
         * 开始结束时间初始化
         */
        endDate = today;
        beginDate = LocalDate.parse(timeDate.getVideoDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));


        List<String> city;

        beginYear = beginDate.getYear();
        beginMonth = beginDate.getMonthValue();
        endYear = endDate.getYear();
        endMonth = endDate.getMonthValue();

        if (endMonth < 7) {
            isWhich = "前半";
        } else {
            isWhich = "後半";
        }

        /**
         * 如果当前日期在时间范围内，则创建timeDate对象,格式化year和month
         */

        city = Utils.getDesc(beginDate, endDate);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, city);
        dataSpinner.setAdapter(stringArrayAdapter);


        dataSpinner.setSelection(countPosition);
        dataTimeTv.setText(firstDay);
        String first = firstDay.substring(0, firstDay.indexOf("~"));
        String end = firstDay.substring(0, 5) + firstDay.substring(firstDay.indexOf("~") + 1);
        today = Utils.getLocalDate2(first);
        String firstDay = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
        String lastday = String.valueOf(today.with(TemporalAdjusters.lastDayOfMonth()));
        CommonFilter(firstDay, lastday);

    }


    private void initRecycleView(List<RecordVideo> recordVideoList) {
        if (recordVideoList.isEmpty()) {
            emptyRelayout.setVisibility(View.VISIBLE);
            fragmentDashboardRecycleView.setVisibility(View.GONE);
        } else {
            emptyRelayout.setVisibility(View.GONE);
            fragmentDashboardRecycleView.setVisibility(View.VISIBLE);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
            fragmentDashboardRecycleView.setLayoutManager(layoutManager);
            NotificationFragmentAdapter adapter = new NotificationFragmentAdapter(mContext, recordVideoList);
            fragmentDashboardRecycleView.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position) -> {
                Intent intent = new Intent(mContext, NotificationDetailActivity.class);
                intent.putExtra("videoId", recordVideoList.get(position).getVideoId());
                intent.putExtra("recordId", recordVideoList.get(position).getId());
                startActivity(intent);
            });
        }

    }

    private void initDate() {
        List<RecordVideo> recordVideoList = LitePal.select("*").find(RecordVideo.class);
        if (recordVideoList.size() != 0) {
            totalPercentTv.setText(recordVideoList.size() + "/1000");
            int number = recordVideoList.size();
            recordProgress.setProgress(number * 100 / 1000);
        } else {
            totalPercentTv.setText("0/1000");
        }


        /**
         * 箭头点击事件
         */
        leftBackImg.setOnClickListener(view -> {
            rightBackImg.setClickable(true);
            rightBackImg.setImageResource(R.mipmap.right_back_img);
            rightState = 1;
            //减去一个月后的时间
            today = today.minusMonths(1);

            if (isWhich.equals("前半")) {
                if (today.getMonthValue() == 1) {
                    leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                    leftBackImg.setClickable(false);
                    leftState = -1;
                }
            } else if (isWhich.equals("後半")) {
                if (today.getMonthValue() == 7) {
                    leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                    leftBackImg.setClickable(false);
                    leftState = -1;
                }
            }

            dataTimeTv.setText(Utils.dataMinus(today));
            String firstday = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
            String lastday = String.valueOf(today.with(TemporalAdjusters.lastDayOfMonth()));
            CommonFilter(firstday, lastday);
        });

        rightBackImg.setOnClickListener(view -> {
            leftBackImg.setClickable(true);
            leftBackImg.setImageResource(R.mipmap.left_back);
            leftState = 1;
            today = today.plusMonths(1);

            if (today.getYear() == endYear && today.getMonthValue() == endMonth) {
                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                rightBackImg.setClickable(false);
                rightState = -1;
                dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());
                String firstday = String.valueOf(endDate.with(TemporalAdjusters.firstDayOfMonth()));
                String lastday = String.valueOf(endDate.with(TemporalAdjusters.lastDayOfMonth()));
                CommonFilter(firstday, lastday);
            } else {
                if (isWhich.equals("前半")) {
                    if (today.getMonthValue() == 6) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
                        rightState = -1;
                    }
                } else if (isWhich.equals("後半")) {
                    if (today.getMonthValue() == 12) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
                        rightState = -1;
                    }
                }
                dataTimeTv.setText(Utils.dataMinus(today));
                String firstday = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
                String lastday = String.valueOf(today.with(TemporalAdjusters.lastDayOfMonth()));
                CommonFilter(firstday, lastday);
            }

        });

        dataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.button_color));
                textView.setTextSize(18);
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setPadding(15, -5, 0, 0);

                String desc = (String) adapterView.getItemAtPosition(position);
                jumpPosition = position;

                leftBackImg.setClickable(true);
                leftBackImg.setImageResource(R.mipmap.left_back);
                rightBackImg.setClickable(true);
                rightBackImg.setImageResource(R.mipmap.right_back_img);
                if (!RxSPTool.getBoolean(mContext, "JumpState")) {
                    leftState = 1;
                    rightState = 1;

                }
                //判断是前半年还是後半年
                isWhich = desc.substring(desc.indexOf("/") + 1);
                if (isWhich.equals("前半")) {
                    if (i != 0) {
                        if (today.getYear() == beginDate.getYear()) {
                            if (today.getMonthValue() == beginDate.getMonthValue()) {
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                                rightState = -1;
                            }
                        }
                        i++;
                    } else {
                        if (RxSPTool.getBoolean(mContext, "JumpState")) {
                            RxSPTool.putBoolean(mContext, "JumpState", false);
                            if (leftState == 1) {
                                leftBackImg.setClickable(true);
                                leftBackImg.setImageResource(R.mipmap.left_back);
                            }
                            if (leftState == -1) {
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            }
                            if (rightState == 1) {
                                rightBackImg.setClickable(true);
                                rightBackImg.setImageResource(R.mipmap.right_back_img);
                            }
                            if (rightState == -1) {
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            }
                            i++;
                        } else {
                            if (today.getMonthValue() == 1) {
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                                leftState = -1;
                            }
                            if (today.getYear()==beginDate.getYear() && today.getMonthValue()==beginDate.getMonthValue()){
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                                leftState = -1;
                            }
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState = -1;
                            i++;
                        }
                    }
                } else if (isWhich.equals("後半")) {
                    //后半的情况下
                    if (i != 0) {
                        leftBackImg.setClickable(false);
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftState = -1;
                        i++;
                    } else {
                        if (RxSPTool.getBoolean(mContext, "JumpState")) {
                            RxSPTool.putBoolean(mContext, "JumpState", false);
                            if (leftState == 1) {
                                leftBackImg.setClickable(true);
                                leftBackImg.setImageResource(R.mipmap.left_back);
                            }
                            if (leftState == -1) {
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            }
                            if (rightState == 1) {
                                rightBackImg.setClickable(true);
                                rightBackImg.setImageResource(R.mipmap.right_back_img);
                            }
                            if (rightState == -1) {
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            }
                            i++;
                        } else {
                            if (today.getMonthValue() == 7) {
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                                leftState = -1;
                            }
                            if (today.getYear()==beginDate.getYear() && today.getMonthValue()==beginDate.getMonthValue()){
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                                leftState = -1;
                            }
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState = -1;
                            i++;
                        }
                    }
                }

                if (i > 1) {
                    //判断选择的年份
                    int descYear = Integer.parseInt(desc.substring(0, desc.indexOf("年")));
                    dataTimeTv.setText(Utils.getSpinnerTextDateVideo(descYear, isWhich, endDate).get(0));
                    String start = Utils.getSpinnerTextDateVideo(descYear, isWhich, endDate).get(1);
                    String end = Utils.getSpinnerTextDateVideo(descYear, isWhich, endDate).get(2);
                    if (descYear == endDate.getYear()) {
                        end = Utils.getSpinnerTextDate(descYear, isWhich, endDate).get(2);
                        leftBackImg.setClickable(false);
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftState = -1;
                        if ((isWhich.equals("前半") && Utils.getLocalDate(start).getMonthValue() == 6) || (Utils.getLocalDate(start).getMonthValue() == endDate.getMonthValue())) {
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState = -1;
                        } else if (isWhich.equals("後半") && Utils.getLocalDate(end).getMonthValue() >= endDate.getMonthValue()) {
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState = -1;
                        } else {
                            rightBackImg.setClickable(true);
                            rightBackImg.setImageResource(R.mipmap.right_back_img);
                            rightState = 1;
                        }
                    } else {
                        if (isWhich.equals("前半")) {
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            leftState = -1;
                            if (Utils.getLocalDate(start).getMonthValue() == 6) {
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                                rightState = -1;
                            }
                        }
                    }
                    today = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    CommonFilter(start, end);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        leftState = 1;
        rightState = 1;
    }

    /**
     * 剥离共同方法，用于筛选
     *
     * @param firstday
     * @param lastday
     */
    private void CommonFilter(String firstday, String lastday) {
        List<RecordVideo> recordVideoListAll = LitePal.select("*").where("recordDate >=? and recordDate <=?", firstday, lastday).order("recordDateTime desc").find(RecordVideo.class);
        initRecycleView(recordVideoListAll);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
