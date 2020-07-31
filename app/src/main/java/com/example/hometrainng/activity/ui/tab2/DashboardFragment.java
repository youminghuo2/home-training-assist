package com.example.hometrainng.activity.ui.tab2;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.activity.IjkplayerDetailActivity;
import com.example.hometrainng.adapter.MainHomeAdapter;
import com.example.hometrainng.customview.RoundProgressBar;
import com.example.hometrainng.db.Messages;
import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.CardViewEntity;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DashboardFragment extends Fragment {


    @BindView(R.id.circle_icon_title_tv)
    TextView circleIconTitleTv;
    @BindView(R.id.circle_icon_data_tv)
    TextView circleIconDataTv;
    @BindView(R.id.circle_icon_text_tv)
    TextView circleIconTextTv;
    @BindView(R.id.roundProgressBar2)
    RoundProgressBar roundProgressBar2;
    @BindView(R.id.left_back_img)
    ImageView leftBackImg;
    @BindView(R.id.right_back_img)
    ImageView rightBackImg;
    @BindView(R.id.data_time_tv)
    TextView dataTimeTv;
    @BindView(R.id.data_spinner)
    AppCompatSpinner dataSpinner;
    @BindView(R.id.fragment_dashboard_recycleView)
    RecyclerView fragmentDashboardRecycleView;
    @BindView(R.id.tv_percent)
    TextView tv_percent;
    @BindView(R.id.tv_percent1)
    TextView tv_percent1;
    @BindView(R.id.empty_relayout)
    RelativeLayout emptyRelayout;
    private DashboardViewModel dashboardViewModel;

    private Unbinder unbinder;
    private LocalDate today, beginDate, endDate;

    //首次进入选中第几个
    private int countPosition;


    private List<CardViewEntity> cardViewEntityList;
    private Context mContext;
    //判断前後半月
    private String isWhich;
    private int beginYear, endYear, endMonth, beginMonth;
    private int i;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        unbinder = ButterKnife.bind(this, root);
        mContext = getActivity();
        return root;
    }

    private void setcheckToday() {
        /**
         * 获取今天的时间
         */
        today = LocalDate.now();
//        TimeDate timeDate = LitePal.find(TimeDate.class, 1);


        List<TimeDate> timeDateList = LitePal.select("*").limit(1).find(TimeDate.class);
        TimeDate timeDate = timeDateList.get(0);

        ContentValues values = new ContentValues();
        values.put("endDate", today.format(DateTimeFormatter.ISO_LOCAL_DATE));


        LitePal.updateAll(TimeDate.class, values);
        /**
         * 开始结束时间初始化
         */
        endDate = today;
        beginDate = LocalDate.parse(timeDate.getBeginDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));


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
        dataSpinner.setSelection(countPosition);

        dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());


        /**
         * 计算起始日，来加载recycleView
         */
        String firstDay = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
        String todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
        CommonFilter(firstDay, todayString);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        i = 0;
        initBar();
        setcheckToday();
        initTextView();
        initDate();

    }

    /**
     * 圆环进度，查询百分比
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initBar() {
        cardViewEntityList = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        String localDateString = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        int watchNum = 0;

        List<Video> videoList = LitePal.select("*").where("specifyStartTime <=? and specifyEndTime > ? and specifyEndTime is not null", localDateString, localDateString).find(Video.class);
        List<Video> videoList1 = LitePal.select("*").where("specifyEndTime is null and specifyStartTime<= ?", localDateString).find(Video.class);
        List<Video> videoListAll = new ArrayList<>();
        videoListAll.addAll(videoList);
        videoListAll.addAll(videoList1);
        for (int i = 0; i < videoListAll.size(); i++) {
            CardViewEntity cardViewEntity = new CardViewEntity();
            cardViewEntity.setImgUrl(Constants.PhotoPath + videoListAll.get(i).getThumbnailPath());
            cardViewEntity.setName(videoListAll.get(i).getTitle());
            cardViewEntity.setVideoId(videoListAll.get(i).getVideoId());
            //判断是否观看过
            int count = LitePal.where("videoId = ? and playEndTime = ?", String.valueOf(videoListAll.get(i).getVideoId()), localDateString).count(VideoHistory.class);
            if (count == 0) {
                String watched = "no";
                cardViewEntity.setWatched(watched);
            } else {
                watchNum++;
                cardViewEntity.setWatched("yes");
            }
            cardViewEntityList.add(cardViewEntity);
        }
        try {
            int watched = watchNum;
            int percent = 0;
//            percent = 100;
            if (videoListAll.size() > 0) {
                tv_percent1.setVisibility(View.VISIBLE);
                int total = videoListAll.size();
                percent = watched * 100 / total;
                roundProgressBar2.setProgress(percent);
                tv_percent.setText(percent + "");
                if (percent == 100) {
                    tv_percent.setTextColor(Color.parseColor("#FF9D00"));
                    tv_percent1.setTextColor(Color.parseColor("#FF9D00"));
                } else {
                    tv_percent.setTextColor(Color.parseColor("#14CE5B"));
                    tv_percent1.setTextColor(Color.parseColor("#14CE5B"));
                }
            } else {
                roundProgressBar2.setProgress(percent);
                tv_percent.setText("--");
                tv_percent.setTextColor(Color.parseColor("#14CE5B"));
                tv_percent1.setVisibility(View.GONE);
            }

        } catch (ArithmeticException e) {
            PLog.e(TAG + "initBar", e.toString());
        }

    }


    /**
     * 初始化，填入左边textview
     */
    private void initTextView() {
        List<Messages> messagesList = LitePal.limit(1).order("createTime desc").find(Messages.class);
        if (messagesList.size() != 0) {
            circleIconTitleTv.setText(messagesList.get(0).getTitle());
            circleIconDataTv.setText(Utils.getDatePoint(messagesList.get(0).getCreateTime().substring(0, 10)));
            circleIconTextTv.setText(messagesList.get(0).getContent());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initDate() {
        /**
         * 箭头点击事件
         */
        leftBackImg.setOnClickListener(view -> {
            rightBackImg.setClickable(true);
            rightBackImg.setImageResource(R.mipmap.right_back_img);
            //减去一个月后的时间
            today = today.minusMonths(1);
            String firstday = "";
            if (today.getYear() == beginDate.getYear()) {
                if (isWhich.equals("前半")) {
                    if (today.getMonthValue() <= beginDate.getMonthValue()) {
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftBackImg.setClickable(false);
                        firstday = beginDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } else {
                        firstday = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
                    }
                } else if (isWhich.equals("後半")) {
                    if (today.getMonthValue() <= beginDate.getMonthValue()) {
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftBackImg.setClickable(false);
                        firstday = beginDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    } else {
                        if (today.getMonthValue() == 7) {
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            leftBackImg.setClickable(false);
                        }
                        firstday = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
                    }
                }
            } else {
                if (isWhich.equals("前半")) {
                    if (today.getMonthValue() == 1) {
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftBackImg.setClickable(false);
                    }
                } else if (isWhich.equals("後半")) {
                    if (today.getMonthValue() == 7) {
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftBackImg.setClickable(false);
                    }
                }
                firstday = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
            }
            dataTimeTv.setText(Utils.dataMinus(today));
            String lastday = String.valueOf(today.with(TemporalAdjusters.lastDayOfMonth()));
            CommonFilter(firstday, lastday);
            dataTimeTv.setText(Utils.getToDate(firstday) + "~" + today.with(TemporalAdjusters.lastDayOfMonth()).getMonthValue() + "." + today.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
        });

        rightBackImg.setOnClickListener(view -> {
            leftBackImg.setClickable(true);
            leftBackImg.setImageResource(R.mipmap.left_back);
            today = today.plusMonths(1);

            if (today.getYear() == endYear && today.getMonthValue() == endMonth) {
                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                rightBackImg.setClickable(false);
                dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());
                String firstday = String.valueOf(endDate.with(TemporalAdjusters.firstDayOfMonth()));
                String lastday = String.valueOf(endDate);
                CommonFilter(firstday, lastday);
            } else {
                if (isWhich.equals("前半")) {
                    if (today.getMonthValue() == 6) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
                    }
                } else if (isWhich.equals("後半")) {
                    if (today.getMonthValue() == 12) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
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
                String desc = (String) adapterView.getItemAtPosition(position);

                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.button_color));
                textView.setTextSize(18);
                textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setPadding(15, -5, 0, 0);

                leftBackImg.setClickable(true);
                leftBackImg.setImageResource(R.mipmap.left_back);
                rightBackImg.setClickable(true);
                rightBackImg.setImageResource(R.mipmap.right_back_img);


                //判断是前半年还是後半年
                isWhich = desc.substring(desc.indexOf("/") + 1);
                if (isWhich.equals("前半")) {
                    if (i != 0) {
                        if (today.getYear() == beginDate.getYear()) {
                            if (today.getMonthValue() == beginDate.getMonthValue()) {
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            }
                        }
                        i++;
                    } else {
                        if (today.getMonthValue() == 1) {
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        }
                        if (today.getYear()==beginDate.getYear() && today.getMonthValue()==beginDate.getMonthValue()){
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        }
                        rightBackImg.setClickable(false);
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        i++;
                    }
                } else if (isWhich.equals("後半")) {
                    //后半的情况下
                    if (i != 0) {
                        leftBackImg.setClickable(false);
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        i++;
                    } else {
                        if (today.getMonthValue() == 7) {
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        }
                        if (today.getYear()==beginDate.getYear() && today.getMonthValue()==beginDate.getMonthValue()){
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        }
                        rightBackImg.setClickable(false);
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        i++;
                    }
                }

                if (i > 1) {
                    //判断选择的年份
                    int descYear = Integer.parseInt(desc.substring(0, desc.indexOf("年")));
                    String end;
                    dataTimeTv.setText(Utils.getSpinnerTextDate(descYear, isWhich, endDate).get(0));
                    String start = Utils.getSpinnerTextDate(descYear, isWhich, endDate).get(1);
                    end = Utils.getSpinnerTextDate(descYear, isWhich, endDate).get(2);
                    if (descYear == endDate.getYear()) {
                        end = Utils.getSpinnerTextDate(descYear, isWhich, endDate).get(2);
                        leftBackImg.setClickable(false);
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        if ((isWhich.equals("前半") && Utils.getLocalDate(start).getMonthValue() == 6)||(Utils.getLocalDate(start).getMonthValue()==endDate.getMonthValue())){
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        } else if (isWhich.equals("後半") && Utils.getLocalDate(end).getMonthValue()>= endDate.getMonthValue()) {
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        }else {
                            rightBackImg.setClickable(true);
                            rightBackImg.setImageResource(R.mipmap.right_back_img);
                        }
                    } else {
                        if (isWhich.equals("前半")) {
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            if (Utils.getLocalDate(start).getMonthValue()==6){
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
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

    /**
     * 独立出来的筛选时间
     *
     * @param firstday
     * @param lastday
     */
    private void CommonFilter(String firstday, String lastday) {
        LocalDate localFirst=LocalDate.parse(firstday, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate localLast=LocalDate.parse(lastday,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate localDateFirst=localFirst.plusDays(1);
        LocalDate localDateLast=localLast.plusDays(1);
        String  firstPlusDay=localDateFirst.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String lastPlusDay=localDateLast.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<Video> videoList = LitePal.select("*").where("(specifyEndTime is null and specifyStartTime <=?) or (specifyEndTime > ? and specifyStartTime <=? and specifyEndTime is not null) or (specifyEndTime > ? and specifyStartTime <=? and specifyEndTime is not null) or(specifyStartTime >= ? and specifyEndTime <= ? and specifyEndTime is not null )", lastday,firstday,firstday,lastday,lastday,firstday,lastday).order("title").find(Video.class);

        List<Video> videoListAll = new ArrayList<>();
        List<Integer> videoIds = new ArrayList<>();
        if (videoList != null && videoList.size() > 0) {
            for (Video video : videoList) {
                if (!videoIds.contains(video.getVideoId())) {
                    videoIds.add(video.getVideoId());
                    videoListAll.add(video);
                }
            }
        }
        addVideList(videoListAll);
    }

    /**
     * 处理数据，点击事件
     *
     * @param videoList
     */
    private void addVideList(List<Video> videoList) {
        cardViewEntityList = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        String dateString = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        if (videoList.size() != 0) {
            for (int i = 0; i < videoList.size(); i++) {
                CardViewEntity cardViewEntity = new CardViewEntity();
                cardViewEntity.setImgUrl(Constants.PhotoPath + videoList.get(i).getThumbnailPath());
                cardViewEntity.setName(videoList.get(i).getTitle());
                cardViewEntity.setVideoId(videoList.get(i).getVideoId());
                int count = LitePal.where("videoId = ? and playEndTime = ?", String.valueOf(videoList.get(i).getVideoId()), dateString).count(VideoHistory.class);
                if (count == 0) {
                    String watched = "no";
                    cardViewEntity.setWatched(watched);
                } else {
                    cardViewEntity.setWatched("yes");
                }
                cardViewEntityList.add(cardViewEntity);
            }
        }
        initRecycleView();
    }


    @SuppressLint("WrongConstant")
    private void initRecycleView() {
        if (cardViewEntityList.isEmpty()) {
            fragmentDashboardRecycleView.setVisibility(View.GONE);
            emptyRelayout.setVisibility(View.VISIBLE);
        } else {
            fragmentDashboardRecycleView.setVisibility(View.VISIBLE);
            emptyRelayout.setVisibility(View.GONE);
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
            fragmentDashboardRecycleView.setLayoutManager(layoutManager);
            MainHomeAdapter adapter = new MainHomeAdapter(mContext, cardViewEntityList);
            fragmentDashboardRecycleView.setAdapter(adapter);
            adapter.setOnItemClickListener((view, position) -> {
                AssetManager am = mContext.getAssets();
                String videoId = String.valueOf(cardViewEntityList.get(position).getVideoId());
                List<Video> videoList = LitePal.select("*").where("videoId= ?", videoId).order("specifyStartTime desc").find(Video.class);
                String mPath = videoList.get(0).getVideoFileName();
                try {
                    if (TextUtils.equals("", mPath)) {
                        AssetFileDescriptor afd = am.openFd(Constants.VideoPath + mPath);
                        Toast.makeText(getActivity(), getString(R.string.tip_video_play_no_exist), Constants.Toast_Length).show();
                    } else {
                        AssetFileDescriptor afd = am.openFd(Constants.VideoPath + mPath);
                        Intent intent = new Intent(getActivity(), IjkplayerDetailActivity.class);
                        intent.putExtra("videoTitle", cardViewEntityList.get(position).getName());
                        intent.putExtra("videoId", cardViewEntityList.get(position).getVideoId());
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "動画がありません");
                    Toast.makeText(getContext(), getString(R.string.tip_video_play_no_exist), Constants.Toast_Length).show();
                }

            });
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
