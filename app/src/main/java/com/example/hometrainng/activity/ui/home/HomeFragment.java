package com.example.hometrainng.activity.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.activity.CounselingDateActivity;
import com.example.hometrainng.activity.CounselingGridActivity;
import com.example.hometrainng.activity.IjkplayerDetailActivity;
import com.example.hometrainng.activity.InformationDetailActivity;
import com.example.hometrainng.activity.InformationDoctorDetailActivity;
import com.example.hometrainng.activity.aimsDetailActivity;
import com.example.hometrainng.adapter.MainHomeAdapter;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.customview.RoundProgressBar;
import com.example.hometrainng.db.Completion;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Issues;
import com.example.hometrainng.db.Messages;
import com.example.hometrainng.db.Notice;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.Bean;
import com.example.hometrainng.entity.CardViewEntity;
import com.example.hometrainng.entity.GoalModel;
import com.example.hometrainng.entity.IssueModel;
import com.example.hometrainng.entity.MessageModel;
import com.example.hometrainng.entity.NoticeModel;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxSPTool;
import com.zyp.cardview.YcCardView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {


    @BindView(R.id.cardview_doctor)
    YcCardView cardviewDoctor;
    @BindView(R.id.chart_line)
    LineChart chartLine;
    @BindView(R.id.aims_text)
    TextView aimsText;
    @BindView(R.id.issues_tv)
    TextView issuesTv;
    @BindView(R.id.notice_title)
    TextView noticeTitle;
    @BindView(R.id.notice_content)
    TextView noticeContent;
    @BindView(R.id.notice_create_time)
    TextView noticeCreateTime;
    @BindView(R.id.message_title)
    TextView messageTitle;
    @BindView(R.id.message_content)
    TextView messageContent;
    @BindView(R.id.message_create_time)
    TextView messageCreateTime;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.month_tv)
    TextView monthTv;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.aims_linearLayout)
    LinearLayout aimsLinearLayout;
    @BindView(R.id.subject_icon_linearLayout)
    LinearLayout subjectIconLinearLayout;
    @BindView(R.id.roundProgressBar2)
    RoundProgressBar roundProgressBar2;
    @BindView(R.id.time_button)
    Button timeButton;
    @BindView(R.id.time_button2)
    Button timeButton2;
    @BindView(R.id.time_frameLayout)
    FrameLayout timeFrameLayout;
    @BindView(R.id.times_linearLayout)
    LinearLayout timesLinearLayout;
    @BindView(R.id.all_linearlayout)
    LinearLayout all_linearlayout;
    @BindView(R.id.number_text)
    TextView numberText;
    @BindView(R.id.home_week_textView)
    TextView homeWeekText;
    @BindView(R.id.tv_percent)
    TextView tv_percent;
    @BindView(R.id.tv_percent1)
    TextView tv_percent1;
    @BindView(R.id.aims_new_icon)
    ImageView aimsNewIcon;
    @BindView(R.id.subject_news_icon)
    ImageView subjectNewsIcon;
    @BindView(R.id.notice_news_icon)
    ImageView notice_news_icon;
    @BindView(R.id.message_news_icon)
    ImageView messageNewsIcon;
    @BindView(R.id.times_clock_view)
    View timesClockView;
    @BindView(R.id.times_icon)
    ImageView timesIcon;
    @BindView(R.id.cardview_information)
    YcCardView cardviewInformation;
    private String[] arr = new String[7];

    private Unbinder unbinder;

    @BindView(R.id.fragment_home_recycleView)
    RecyclerView fragmentHomeRecycleView;

    private HomeViewModel homeViewModel;
    private List<CardViewEntity> cardViewEntityList;
    private Context mContext;
    private String token;
    private int userId;

    private LocalDate localDate;
    int i = 0;

    private static final String TAG = "HomeFragment";

    static class MyHandler extends Handler {
        SoftReference<HomeFragment> fragmentSoftReference;

        public MyHandler(HomeFragment myFragment) {
            this.fragmentSoftReference = new SoftReference<>(myFragment);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 4) {
                if (fragmentSoftReference.get() != null) {
                    fragmentSoftReference.get().initData();
                    fragmentSoftReference.get().initRecycleView();
                    fragmentSoftReference.get().initWPAndroid_Line();
                }
            }
        }
    }

    Handler firstHandler = new MyHandler(this);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    private void checkCurrentCounselingDateTime() {
        String month = RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_MONTH).trim();
        String date = RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_DATE).trim();
        String times = RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_TIME);
        String[] timeArr = times.split("~");
        if (timeArr.length == 2 && !TextUtils.isEmpty(timeArr[1])) {
            String counselingEndDateTime = Utils.counselingEndDate(month, date, timeArr[1]);
            LocalDateTime counselingEndDatetime2 = LocalDateTime.parse(counselingEndDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (LocalDateTime.now().isAfter(counselingEndDatetime2)) {
                RxSPTool.putInt(getContext(), Constants.SCHEDULE_STATUS, -1);
                RxSPTool.remove(getContext(), Constants.SCHEDULE_ID);
                RxSPTool.remove(getContext(), Constants.SCHEDULE_STATUS_MONTH);
                RxSPTool.remove(getContext(), Constants.SCHEDULE_STATUS_DATE);
                RxSPTool.remove(getContext(), Constants.SCHEDULE_STATUS_TIME);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, root);

        mContext = getActivity();

        token = RxSPTool.getString(mContext, Constants.TOKEN);
        userId = RxSPTool.getInt(mContext, Constants.USER_ID);

        localDate = LocalDate.now();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LocalDate date = LocalDate.now();
                if (ChronoUnit.DAYS.between(localDate, date) == 1) {
                    if (getActivity()!=null) {
                        getActivity().runOnUiThread(() -> {
//                            onResume();
//                            checkCurrentCounselingDateTime();
                            PLog.delFile();
                            localDate = LocalDate.now();
                            cardViewEntityList = new ArrayList<>();
                            initData();
                            initRecycleView();
                            initWPAndroid_Line();
//                           Intent intent=new Intent(mContext, MainHomeActivity.class);
//                           startActivity(intent);
                        });
                    }
                }
            }
        }, 0, 1000);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> checkCounseling());

    }

    private void bgAlpha(float f) {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.alpha = f;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    public void showPopupWindow(String message) {
        int ScreenWidth = RxDeviceTool.getScreenWidth(mContext);
        int ScreenHeight = RxDeviceTool.getScreenHeight(mContext);
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main_dialog, null);
        AutoDismissPopupWindow popupWindow = new AutoDismissPopupWindow(contentView, ScreenWidth / 15 * 8, ScreenHeight / 15 * 8, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
        popupWindow.setOnDismissListener(() -> bgAlpha(1.0f));
    }

    /**
     * Remind today's appointment information once a day
     */
    private void checkCounseling() {
        //Do not prompt before 8 o'clock
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime eightLocalDateTime = LocalDateTime.parse(LocalDate.now().toString() + " 08:00:00", formatter);

        if (eightLocalDateTime.isBefore(LocalDateTime.now())) {
            return;
        }
        String month = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_MONTH);
        String date = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_DATE);
        String time = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_TIME);
        if (TextUtils.isEmpty(month) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
            return;
        }

        int year = LocalDateTime.now().getYear();
        String[] counseling_time = time.split("~");
        String[] counseling_start_time = counseling_time[0].split(":");
        int hour = Integer.parseInt(counseling_start_time[0]);
        int minute = Integer.parseInt(counseling_start_time[1]);
        LocalDateTime counselingDatetime = LocalDateTime.of(year, Integer.parseInt(month.trim()), Integer.parseInt(date.trim()), hour, minute);
        if (!counselingDatetime.isBefore(LocalDateTime.now())) {
            return;
        }
        if (counselingDatetime.getDayOfYear() == LocalDateTime.now().getDayOfYear()) {
            String showDataTime = RxSPTool.getString(mContext, Constants.SHOW_TODAY_COUNSELING);
            String counselingDateMsg = getString(R.string.tip_today_meeting) + counselingDatetime.toLocalTime() + "!";
            if (TextUtils.isEmpty(showDataTime)) {
                showPopupWindow(counselingDateMsg);
                Utils.playNotificationRing(mContext);
                RxSPTool.putString(mContext, Constants.SHOW_TODAY_COUNSELING, LocalDateTime.now().toString());
            } else {
                LocalDateTime showDataTime2 = LocalDateTime.parse(showDataTime);
                if (showDataTime2.getDayOfYear() != LocalDateTime.now().getDayOfYear()) {
                    showPopupWindow(counselingDateMsg);
                    Utils.playNotificationRing(mContext);
                    RxSPTool.putString(mContext, Constants.SHOW_TODAY_COUNSELING, LocalDateTime.now().toString());
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        checkCurrentCounselingDateTime();
        PLog.delFile();
        localDate = LocalDate.now();
        cardViewEntityList = new ArrayList<>();
        if (RxSPTool.getBoolean(mContext, Constants.FIRST_LOGIN)) {
            RetrieveData();
            RxSPTool.putBoolean(mContext, Constants.FIRST_LOGIN, false);
//            addDatabase();
        } else {
            initData();
            initRecycleView();
            initWPAndroid_Line();
        }
    }

    private void addDatabase() {
        List<RecordVideo> recordVideoList = new ArrayList<>();
        for (int i = 1; i < 1001; i++) {
            RecordVideo recordVideo = new RecordVideo();
            recordVideo.setId(i);
            recordVideo.setVideoId(129);
            recordVideo.setDuration("03:10");
            recordVideo.setRecordPath(Constants.RecordVideoPath + "/202006_" + i + ".mp4");
            recordVideo.setRecordImg(Constants.RecordVideoImage + "/" + "1. (" + i + ").png");
            recordVideo.setRecordDate("2020-06-05");
            recordVideoList.add(recordVideo);
        }
        LitePal.saveAll(recordVideoList);
    }

    /**
     * 接口获取详情数据
     */
    private void RetrieveData() {

        achievegoalsDetail();
        achieveIssuesDetail();
        achieveNoticeDetail();
        achieveMessageDetail();


    }

    /**
     * 疗法师履历
     */
    private void achieveMessageDetail() {
        Call<MessageModel> messagesCall = HttpHelper.getInstance().create(HomeTrainService.class).messageDetail(token, userId);
        messagesCall.enqueue(new Callback<MessageModel>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MessageModel> call, Response<MessageModel> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        List<Messages> messagesList = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Messages messages = new Messages();
                            messages.setMessageId(response.body().getData().get(i).getId());
                            messages.setUserId(response.body().getData().get(i).getUserId());
                            messages.setCreateTime(response.body().getData().get(i).getUpdateTime());
                            try {
                                messages.setContent(Utils.setBaseAECMsg(response.body().getData().get(i).getContent()));
                                messages.setTitle(Utils.setBaseAECMsg(response.body().getData().get(i).getTitle()));
                            } catch (Exception e) {
                                PLog.e(TAG + "achieveMessageDetail", e.toString());
                            }
                            messagesList.add(messages);
                        }
                        LitePal.saveAll(messagesList);
                        i++;
                        firstHandler.sendEmptyMessage(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageModel> call, Throwable t) {
                PLog.e(TAG, "achieveMessageDetail/" + t.getMessage());
            }
        });
    }

    /**
     * 通知详情履历
     */
    private void achieveNoticeDetail() {
        Call<NoticeModel> noticeModelCall = HttpHelper.getInstance().create(HomeTrainService.class).noticeDetail(token);
        noticeModelCall.enqueue(new Callback<NoticeModel>() {
            @Override
            public void onResponse(Call<NoticeModel> call, Response<NoticeModel> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        List<Notice> noticeList = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Notice notice = new Notice();
                            notice.setNoticeId(response.body().getData().get(i).getId());
                            notice.setCreateTime(response.body().getData().get(i).getUpdateTime());
                            notice.setContent(response.body().getData().get(i).getContent());
                            notice.setTitle(response.body().getData().get(i).getTitle());
                            notice.setUrl(response.body().getData().get(i).getUrl());
                            noticeList.add(notice);
                        }
                        LitePal.saveAll(noticeList);
                        i++;
                        firstHandler.sendEmptyMessage(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<NoticeModel> call, Throwable t) {
                PLog.e(TAG, "achieveNoticeDetail/" + t.getMessage());
            }
        });

    }

    /**
     * 课题详情接口
     */
    private void achieveIssuesDetail() {
        Call<IssueModel> issueModelCall = HttpHelper.getInstance().create(HomeTrainService.class).issueDetail(token, userId);
        issueModelCall.enqueue(new Callback<IssueModel>() {
            @Override
            public void onResponse(Call<IssueModel> call, Response<IssueModel> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        List<Issues> issuesList = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Issues issues = new Issues();
                            issues.setIssueId(response.body().getData().get(i).getId());
                            try {
                                issues.setIssue(Utils.setBaseAECMsg(response.body().getData().get(i).getIssue()));
                            } catch (Exception e) {
                                PLog.e(TAG + "achieveIssuesDetail", e.toString());
                            }
                            issues.setUserId(response.body().getData().get(i).getUserId());
                            issues.setCreateTime(response.body().getData().get(i).getUpdateTime());
                            issuesList.add(issues);
                        }
                        LitePal.saveAll(issuesList);
                        i++;
                        firstHandler.sendEmptyMessage(i);
                    }
                }
            }

            @Override
            public void onFailure(Call<IssueModel> call, Throwable t) {
                PLog.e(TAG, "achieveIssuesDetail/" + t.getMessage());
            }
        });
    }

    /**
     * 目标详情接口
     */
    private void achievegoalsDetail() {
        Call<GoalModel> goalModelCall = HttpHelper.getInstance().create(HomeTrainService.class).goalDetail(token, userId);
        goalModelCall.enqueue(new Callback<GoalModel>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<GoalModel> call, Response<GoalModel> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        List<Goals> goalsList = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Goals goals = new Goals();
                            goals.setUserId(response.body().getData().get(i).getUserId());
                            goals.setGoalId(response.body().getData().get(i).getId());
                            try {
                                goals.setGoal(Utils.setBaseAECMsg(response.body().getData().get(i).getGoal()));
                            } catch (Exception e) {
                                PLog.e(TAG + "achievegoalsDetail", e.toString());
                            }
                            goals.setCreateTime(response.body().getData().get(i).getUpdateTime());
                            goalsList.add(goals);
                        }
                        LitePal.saveAll(goalsList);
                        i++;
                        firstHandler.sendEmptyMessage(i);
                    } else {
                        Toast.makeText(mContext, response.body().getMsg(), Constants.Toast_Length).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GoalModel> call, Throwable t) {
                PLog.e(TAG, "achievegoalsDetail/" + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWPAndroid_Line() {
        chartLine.setTouchEnabled(false);
        chartLine.setDragEnabled(false);
        chartLine.setScaleEnabled(false);
        chartLine.setScaleXEnabled(false);
        chartLine.setScaleYEnabled(false);

        //获取x轴
        XAxis xAxis = chartLine.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(20, 160, 100));
        xAxis.setGridColor(Color.rgb(183, 211, 50));
        xAxis.setGridLineWidth(1f);
        xAxis.setAxisLineWidth(1f);
        xAxis.setTextSize(12);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(1f);


        //获取y轴
        YAxis yAxis = chartLine.getAxisLeft();
        yAxis.setDrawGridLines(true);
        yAxis.setEnabled(true);
        yAxis.setGridLineWidth(1f);
        yAxis.setAxisLineWidth(1f);
        yAxis.setTextSize(12);
        yAxis.setTextColor(Color.rgb(20, 160, 100));
        yAxis.setGridColor(Color.rgb(183, 211, 50));
        yAxis.setDrawLabels(true);
        yAxis.setGranularity(25);
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(100);
        yAxis.setLabelCount(5);


        /**
         * 日本星期
         */
        String a0 = String.valueOf(localDate.minus(6, ChronoUnit.DAYS).getDayOfWeek());
        String a1 = String.valueOf(localDate.minus(5, ChronoUnit.DAYS).getDayOfWeek());
        String a2 = String.valueOf(localDate.minus(4, ChronoUnit.DAYS).getDayOfWeek());
        String a3 = String.valueOf(localDate.minus(3, ChronoUnit.DAYS).getDayOfWeek());
        String a4 = String.valueOf(localDate.minus(2, ChronoUnit.DAYS).getDayOfWeek());
        String a5 = String.valueOf(localDate.minus(1, ChronoUnit.DAYS).getDayOfWeek());
        String a6 = String.valueOf(localDate.getDayOfWeek());


        /**
         * 设置横坐标
         */
        arr[0] = Utils.weekOf(a0);
        arr[1] = Utils.weekOf(a1);
        arr[2] = Utils.weekOf(a2);
        arr[3] = Utils.weekOf(a3);
        arr[4] = Utils.weekOf(a4);
        arr[5] = Utils.weekOf(a5);
//        arr[6] = Utils.weekOf(a6);
        arr[6] = "今日";

        /**
         * 如果不存在最近七日，则加进数据库
         */
        List<Completion> completionListAll = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = localDate.minus(i, ChronoUnit.DAYS);
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Completion> completionList = LitePal.select("*").where("localDate = ?", dateString).find(Completion.class);
            if (completionList.size() == 0) {
                Completion completion = new Completion();
                completion.setLocalDate(dateString);
                completion.setGoals(0);
                completion.setWeek(String.valueOf(date.getDayOfWeek()));
                completionListAll.add(completion);
            }
        }
        LitePal.saveAll(completionListAll);


        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return arr[(int) value];
            }
        });

        yAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }

        });
        setLinearDate();

        Legend legend = chartLine.getLegend();

        //隐藏Lengend
        legend.setEnabled(false);
        //隐藏描述
        Description description = new Description();
        description.setEnabled(false);
        chartLine.setDescription(description);
        chartLine.getAxisRight().setEnabled(false);
        chartLine.invalidate();
        chartLine.setExtraTopOffset(10);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setLinearDate() {
        List<Entry> valsComp1 = new ArrayList<>();
        List<Bean> beans = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = localDate.minus(6L - i, ChronoUnit.DAYS);
            String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            List<Completion> completionList = LitePal.select("goals").where("localDate = ?", dateString).find(Completion.class);
            Bean bean = new Bean();
            bean.setI(i);
            bean.setMsg(String.valueOf(completionList.get(0).getGoals()));
            beans.add(bean);
        }


        for (int i = 0; i < 7; i++) {
            valsComp1.add(new Entry(i, Float.parseFloat(beans.get(i).getMsg())));
        }

        LineDataSet setComp1 = new LineDataSet(valsComp1, "");
        //圆心是否空心
        setComp1.setDrawCircleHole(false);
        setComp1.setColor(Color.rgb(20, 160, 100));

//        setComp1.setCircleColor(Color.rgb(20, 160, 100));


        List<Integer> colors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (beans.get(i).getMsg().equals("100")) {
                colors.add(0xFFFF9D00);
            } else {
                colors.add(Color.rgb(20, 160, 100));
            }
        }
        colors.add(0xFFFF9D00);

        setComp1.setCircleColors(colors);

        //折线宽度字体等
        setComp1.setFormLineWidth(1f);
        setComp1.setFormSize(15.f);
        setComp1.setValueTextSize(10f);
        //是否显示点相应的圆
        setComp1.setDrawCircles(true);
        setComp1.setMode(LineDataSet.Mode.LINEAR);
        //不显示具体值
        setComp1.setDrawValues(false);
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);

        /**
         * 统计目标达成日
         */
        numberText.setText(String.valueOf(LitePal.where("goals = ?", "100").count(Completion.class)));

        LineData lineData = new LineData(dataSets);
        chartLine.setData(lineData);
        chartLine.invalidate();

    }

    /**
     * 加载数据
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initData() {
        int watchNum = 0;
        /**
         * 数据源给recycleview调用
         */
        String dateString = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<Video> videoList = LitePal.select("*").where("specifyStartTime <= ? and specifyEndTime >? and specifyEndTime is not null", dateString, dateString).find(Video.class);
        List<Video> videoList1 = LitePal.select("*").where("specifyEndTime is null and specifyStartTime<= ?", dateString).find(Video.class);
        List<Video> videoListAll = new ArrayList<>();
        videoListAll.addAll(videoList);
        videoListAll.addAll(videoList1);


        for (int i = 0; i < videoListAll.size(); i++) {
            CardViewEntity cardViewEntity = new CardViewEntity();
            String a = Constants.PhotoPath + videoListAll.get(i).getThumbnailPath();
            cardViewEntity.setImgUrl(Constants.PhotoPath + videoListAll.get(i).getThumbnailPath());
            cardViewEntity.setName(videoListAll.get(i).getTitle());
            cardViewEntity.setVideoId(videoListAll.get(i).getVideoId());
            //判断是否观看过
            int count = LitePal.where("videoId = ? and playEndTime = ?", String.valueOf(videoListAll.get(i).getVideoId()), dateString).count(VideoHistory.class);
            if (count == 0) {
                String watched = "no";
                cardViewEntity.setWatched(watched);
            } else {
                watchNum++;
                cardViewEntity.setWatched("yes");
            }
            cardViewEntityList.add(cardViewEntity);
        }


        /**
         * 查询目标
         */
        List<Goals> goals = LitePal.select("*").limit(1).order("createTime desc").find(Goals.class);
        if (goals.size() != 0) {
            aimsText.setText(goals.get(0).getGoal());
            LocalDate goalsDate = LocalDate.parse(goals.get(0).getCreateTime().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
            int day = Math.abs((int) ChronoUnit.DAYS.between(localDate, goalsDate));
            if (day < 3) {
                aimsNewIcon.setVisibility(View.VISIBLE);
            }
        }

        /**
         * 查询课题
         */
        List<Issues> issuesList = LitePal.select("*").limit(1).order("createTime desc").find(Issues.class);
        if (issuesList.size() != 0) {
            issuesTv.setText(issuesList.get(0).getIssue());
            LocalDate issueDate = LocalDate.parse(issuesList.get(0).getCreateTime().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
            int day = Math.abs((int) ChronoUnit.DAYS.between(localDate, issueDate));
            if (day < 3) {
                subjectNewsIcon.setVisibility(View.VISIBLE);
            }
        }
        /**
         * 查询履历
         */
        List<Notice> noticeList = LitePal.select("*").limit(1).order("createTime desc").find(Notice.class);
        if (noticeList.size() != 0) {
            noticeTitle.setText(noticeList.get(0).getTitle());
            String noticeTime = noticeList.get(0).getCreateTime().substring(0, 10);
            noticeCreateTime.setText(Utils.getToDate(noticeTime));
            noticeContent.setText(noticeList.get(0).getContent());

            LocalDate noticeDate = LocalDate.parse(noticeList.get(0).getCreateTime().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
            int day = Math.abs((int) ChronoUnit.DAYS.between(localDate, noticeDate));
            if (day < 3) {
                notice_news_icon.setVisibility(View.VISIBLE);
            }
        }

        /**
         * 查询疗法履历
         */
        List<Messages> messageList = LitePal.select("*").limit(1).order("createTime desc").find(Messages.class);
        if (messageList.size() != 0) {
            messageTitle.setText(messageList.get(0).getTitle());
            messageContent.setText(messageList.get(0).getContent());
            String msgTime = messageList.get(0).getCreateTime().substring(0, 10);
            messageCreateTime.setText(Utils.getToDate(msgTime));

            LocalDate msgDate = LocalDate.parse(messageList.get(0).getCreateTime().substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE);
            int day = Math.abs((int) ChronoUnit.DAYS.between(localDate, msgDate));
            if (day < 3) {
                messageNewsIcon.setVisibility(View.VISIBLE);
            }
        }

        /**
         * 首页时间,3种情况
         */

        initScheduleData();


        /**
         * 查询百分比
         */
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

            Completion completion = new Completion();
            completion.setGoals(percent);
            String day = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
            completion.updateAll("localDate=?", day);

        } catch (ArithmeticException e) {
            PLog.e(TAG + "initDate", e.toString());
        }
    }

    @SuppressLint("WrongConstant")
    private void initRecycleView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
        fragmentHomeRecycleView.setLayoutManager(gridLayoutManager);
        MainHomeAdapter adapter = new MainHomeAdapter(mContext, cardViewEntityList);
        fragmentHomeRecycleView.setAdapter(adapter);
        adapter.setOnItemClickListener((view, position) -> {

            String videoId = String.valueOf(cardViewEntityList.get(position).getVideoId());
            List<Video> videoList = LitePal.select("*").where("videoId= ?", videoId).find(Video.class);
            if (videoList.isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.tip_video_play_no_exist), Constants.Toast_Length).show();
                return;
            }

            AssetManager am = mContext.getAssets();
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.cardview_information, R.id.cardview_doctor, R.id.aims_linearLayout, R.id.subject_icon_linearLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cardview_information:
                startActivity(new Intent(mContext, InformationDetailActivity.class));
                break;
            case R.id.cardview_doctor:
                startActivity(new Intent(mContext, InformationDoctorDetailActivity.class));
                break;
            case R.id.aims_linearLayout:
                Intent intent = new Intent(mContext, aimsDetailActivity.class);
                intent.putExtra("aims_subject_type", "aims");
                startActivity(intent);
                break;
            case R.id.subject_icon_linearLayout:
                Intent intent1 = new Intent(mContext, aimsDetailActivity.class);
                intent1.putExtra("aims_subject_type", "subject");
                startActivity(intent1);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        if (event == null) {
            return;
        }
        if (event.getType().equals("schedule")) {
            if (Arrays.asList("schedule_update", "schedule_cancel_confirm", "schedule_approve", "schedule_web_delete").contains(event.getMessage())) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        initScheduleData();
                    });
                }
            }
        }
    }

    private void initScheduleData() {

        if (mContext == null) {
            return;
        }

        int status = RxSPTool.getInt(mContext, Constants.SCHEDULE_STATUS);

        if (status == 1) {
            timeButton.setVisibility(View.GONE);
            timeButton2.setVisibility(View.GONE);
            all_linearlayout.setVisibility(View.VISIBLE);
            timeFrameLayout.setVisibility(View.VISIBLE);
            timesLinearLayout.setVisibility(View.VISIBLE);
            monthTv.setVisibility(View.VISIBLE);
            dateTv.setVisibility(View.VISIBLE);
            timeTv.setVisibility(View.VISIBLE);
            String month = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_MONTH).trim();
            String date = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_DATE).trim();

            monthTv.setText(Utils.formatMonthString(month));
            dateTv.setText(Utils.formatDayString(date));
            timeTv.setText(RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_TIME));
            String week = Utils.getWeek(LocalDate.now().getYear(), month, date);
            homeWeekText.setText("(" + week + ")");
            int textColor = ContextCompat.getColor(getContext(), R.color.button_color);
            if (Utils.counselingDateIsToday(month, date)) {
                textColor = ContextCompat.getColor(getContext(), R.color.yellow_text_color);
                timesIcon.setImageResource(R.drawable.clock_3);
            } else {
                timesIcon.setImageResource(R.mipmap.times_clock);
            }
            monthTv.setTextColor(textColor);
            dateTv.setTextColor(textColor);
            timeTv.setTextColor(textColor);
            homeWeekText.setTextColor(textColor);
            timesClockView.setBackgroundColor(textColor);
        } else if (status == 0) {
            all_linearlayout.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
            timeButton.setText("予約日時の" + "\n" + "確認中");
            timeButton.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), CounselingDateActivity.class));
            });
            timeButton2.setVisibility(View.GONE);
            timesLinearLayout.setVisibility(View.GONE);
            timeFrameLayout.setVisibility(View.GONE);
        } else if (status == -1) {
            timeButton2.setVisibility(View.VISIBLE);
            timeButton2.setText("予約する");
            all_linearlayout.setVisibility(View.GONE);
            timeButton.setVisibility(View.GONE);
            timeButton2.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), CounselingGridActivity.class));
            });
        } else if (status == 2) {
            all_linearlayout.setVisibility(View.VISIBLE);
            timeButton.setVisibility(View.VISIBLE);
            timeButton.setText("予約キャンセル確認中");
            timeButton.setEnabled(false);
            timeButton2.setVisibility(View.GONE);
            timesLinearLayout.setVisibility(View.GONE);
            timeFrameLayout.setVisibility(View.GONE);
        }
    }
}
