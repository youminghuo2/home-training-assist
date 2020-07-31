package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.AppUtils;
import com.example.hometrainng.R;
import com.example.hometrainng.adapter.VideoEditAdapter;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoEditActivity extends AppCompatActivity {


    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.left_back_img)
    ImageView leftBackImg;
    @BindView(R.id.right_back_img)
    ImageView rightBackImg;
    @BindView(R.id.data_time_tv)
    TextView dataTimeTv;
    @BindView(R.id.data_spinner)
    AppCompatSpinner dataSpinner;
    @BindView(R.id.grid_view)
    RecyclerView gridView;
    @BindView(R.id.btn_select_all)
    Button btnSelectAll;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.empty_relayout)
    RelativeLayout emptyRelayout;
    private VideoEditAdapter adapter;
    private Context mContext;
    private List<RecordVideo> cardViewEntityList;

    private LocalDate today, beginDate, endDate;
    //首次进入选中第几个
    private int countPosition;

    //判断前後半月
    private String isWhich;
    private int endYear, endMonth;
    private int i;

    private boolean firToJump;
    private String firstDay;


    //跳转前选中第几个
    private int JumpPosition;

    AutoDismissPopupWindow autoDismissPopupWindow;
    private int leftState,rightState;

    private int state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        Intent intent = getIntent();
        countPosition = intent.getIntExtra("jumpPosition", 0);
        firToJump = intent.getBooleanExtra("firToJump", false);
        firstDay = intent.getStringExtra("dateTimeDate");
        rightState=intent.getIntExtra("rightState",0);
        leftState=intent.getIntExtra("leftState",0);
        state=1;
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        i = 0;
        setcheckToday();
        initDate();
    }

    @OnClick({R.id.back_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                RxSPTool.putBoolean(getApplicationContext(), "JumpState", true);
                Intent intent = new Intent(this, MainHomeActivity.class);
                intent.putExtra("id", 4);
                intent.putExtra("dateTimeDate", dataTimeTv.getText().toString());
                intent.putExtra("jumpPosition", JumpPosition);
                intent.putExtra("rightState", rightState);
                intent.putExtra("leftState",leftState);
                startActivity(intent);
                finish();
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

        if (firToJump) {
            dataTimeTv.setText(firstDay);
            firToJump = false;

            String first = firstDay.substring(0, firstDay.indexOf("~"));
            String end = firstDay.substring(0, 5) + firstDay.substring(firstDay.indexOf("~") + 1);
            today = Utils.getLocalDate2(first);

            String firstDay = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
            String lastday = String.valueOf(today.with(TemporalAdjusters.lastDayOfMonth()));
            CommonFilter(firstDay, lastday);

        } else {
            dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());
            /**
             * 计算起始日，来加载recycleView
             */
            String firstDay = String.valueOf(today.with(TemporalAdjusters.firstDayOfMonth()));
            String todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE);
            CommonFilter(firstDay, todayString);
        }


    }


    private void initDate() {

        /**
         * 箭头点击事件
         */
        leftBackImg.setOnClickListener(view -> {
            rightBackImg.setClickable(true);
            rightBackImg.setImageResource(R.mipmap.right_back_img);
            rightState=1;
            //减去一个月后的时间
            today = today.minusMonths(1);

            if (isWhich.equals("前半")) {
                if (today.getMonthValue() == 1) {
                    leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                    leftBackImg.setClickable(false);
                    leftState=-1;
                }
            } else if (isWhich.equals("後半")) {
                if (today.getMonthValue() == 7) {
                    leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                    leftBackImg.setClickable(false);
                    leftState=-1;
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
            leftState=1;

            today = today.plusMonths(1);
            if (today.getYear() == endYear && today.getMonthValue() == endMonth) {
                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                rightBackImg.setClickable(false);
                rightState=-1;
                dataTimeTv.setText(endYear + "." + endMonth + ".1" + "~" + endMonth + "." + endDate.getDayOfMonth());
                String firstday = String.valueOf(endDate.with(TemporalAdjusters.firstDayOfMonth()));
                String lastday = String.valueOf(endDate.with(TemporalAdjusters.lastDayOfMonth()));
                CommonFilter(firstday, lastday);
            } else {
                if (isWhich.equals("前半")) {
                    if (today.getMonthValue() == 6) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
                        rightState=-1;
                    }
                } else if (isWhich.equals("後半")) {
                    if (today.getMonthValue() == 12) {
                        rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                        rightBackImg.setClickable(false);
                        rightState=-1;
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

                JumpPosition = position;
                leftBackImg.setClickable(true);
                leftBackImg.setImageResource(R.mipmap.left_back);
                rightBackImg.setClickable(true);
                rightBackImg.setImageResource(R.mipmap.right_back_img);
                if (state==1){
                    state=0;
                }else{
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
                                rightState=-1;
                            }
                        }
                        i++;
                    } else {
//                        if (leftState==0&& rightState==0){
//                            if (today.getMonthValue() == 1) {
//                                leftBackImg.setClickable(false);
//                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
//                                leftState =-1;
//                            }
//                            rightBackImg.setClickable(false);
//                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
//                            rightState=-1;
//                            i++;
//                        }else {
                            if (leftState==1){
                                leftBackImg.setClickable(true);
                                leftBackImg.setImageResource(R.mipmap.left_back);
                            }if (leftState==-1){
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            }if (rightState==1){
                                rightBackImg.setClickable(true);
                                rightBackImg.setImageResource(R.mipmap.right_back_img);
                            }if (rightState==-1){
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            }
                            i++;
//                        }
                    }
                } else if (isWhich.equals("後半")) {
                    //后半的情况下
                    if (i != 0) {
                        leftBackImg.setClickable(false);
                        leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                        leftState=-1;
                        i++;
                    } else {
//                        if (leftState==0 && rightState==0){
//                            if (today.getMonthValue() == 7) {
//                                leftBackImg.setClickable(false);
//                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
//                                leftState=-1;
//                            }
//                            rightBackImg.setClickable(false);
//                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
//                            rightState=-1;
//                            i++;
//                        }else {
                            if (leftState==1){
                                leftBackImg.setClickable(true);
                                leftBackImg.setImageResource(R.mipmap.left_back);
                            }if (leftState==-1){
                                leftBackImg.setClickable(false);
                                leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            }if (rightState==1){
                                rightBackImg.setClickable(true);
                                rightBackImg.setImageResource(R.mipmap.right_back_img);
                            }if (rightState==-1){
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            }
                            i++;
//                        }
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
                        leftState=-1;
                        if ((isWhich.equals("前半") && Utils.getLocalDate(start).getMonthValue() == 6)||(Utils.getLocalDate(start).getMonthValue()==endDate.getMonthValue())){
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState=-1;
                        } else if (isWhich.equals("後半") && Utils.getLocalDate(end).getMonthValue()>= endDate.getMonthValue()) {
                            rightBackImg.setClickable(false);
                            rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                            rightState=-1;
                        }else {
                            rightBackImg.setClickable(true);
                            rightBackImg.setImageResource(R.mipmap.right_back_img);
                            rightState=1;
                        }
                    } else {
                        if (isWhich.equals("前半")) {
                            leftBackImg.setClickable(false);
                            leftBackImg.setImageResource(R.mipmap.left_back_no_img);
                            leftState=-1;
                            if (Utils.getLocalDate(start).getMonthValue()==6){
                                rightBackImg.setClickable(false);
                                rightBackImg.setImageResource(R.mipmap.right_back_no_img);
                                rightState=-1;
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


    private void CommonFilter(String firstday, String lastday) {
        String a = firstday;
        String b = lastday;
        cardViewEntityList = LitePal.select("*").where("recordDate >=? and recordDate <=?", firstday, lastday).order("recordDateTime desc").find(RecordVideo.class);
        initRecycleView();
        if (cardViewEntityList.isEmpty()) {
            btnSelectAll.setClickable(false);
            btnDelete.setClickable(false);
            emptyRelayout.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.GONE);
        } else {
            emptyRelayout.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);
            btnSelectAll.setClickable(true);
            btnDelete.setClickable(true);
        }
    }


    private void initRecycleView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        gridView.setLayoutManager(layoutManager);
        adapter = new VideoEditAdapter(this, cardViewEntityList);
        gridView.setAdapter(adapter);
    }

    @SuppressLint("WrongConstant")
    @OnClick(R.id.btn_delete)
    public void deleteVideos(View view) {
        if (adapter != null) {

            List<RecordVideo> selectVideos = adapter.getSelectVideos();
            if (selectVideos.size()==0){
                Toast.makeText(getApplicationContext(),"ビデオを選択してください",Constants.Toast_Length).show();
            }else {
                /**
                 * popupwindow
                 */
                View viewPop = View.inflate(gridView.getContext(), R.layout.video_record_popup, null);
                PopupWindow popupWindow = new PopupWindow(viewPop);
                TextView video_record_tv_fir = viewPop.findViewById(R.id.video_record_tv_fir);
                TextView video_record_tv_sec = viewPop.findViewById(R.id.video_record_tv_sec);
                Button cancel_button = viewPop.findViewById(R.id.cancel_button);
                Button determine_button = viewPop.findViewById(R.id.determine_button);
                popupWindow.setFocusable(true);

                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int width = metrics.widthPixels / 16 * 10;
//            int height = metrics.heightPixels / 8 * 5;

                int width = RxImageTool.dp2px(800);
                int height = RxImageTool.dp2px(500);

                popupWindow.setWidth(width);
                popupWindow.setHeight(height);

                popupWindow.showAtLocation(viewPop, Gravity.CENTER, 0, 0);
                darkenBackground(0.2f);

                popupWindow.setOnDismissListener(() -> darkenBackground(1f));

                video_record_tv_fir.setText("選択した動画を");
                video_record_tv_sec.setText("削除してよろしいですか？");
                cancel_button.setOnClickListener(view1 -> {
                    popupWindow.dismiss();
                });

                determine_button.setOnClickListener(view12 -> {
                    String dataId = "";
                    if (selectVideos.size() > 0) {
                        for (int i = 0; i < selectVideos.size(); i++) {
                            dataId = dataId + selectVideos.get(i).getId() + ",";
                            if (selectVideos.get(i).isSaved()) {
                                selectVideos.get(i).delete();
                            }
                        }
//                    dataId=dataId.substring(0,dataId.length()-1);
//                    String token=RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
//                    Call<MsgModel> deleteRecordVideo=HttpHelper.getInstance().create(HomeTrainService.class).deleteRecordVideo(token, dataId);
//                    deleteRecordVideo.enqueue(new Callback<MsgModel>() {
//                        @Override
//                        public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
//                        }
//
//                        @Override
//                        public void onFailure(Call<MsgModel> call, Throwable t) {
//
//                        }
//                    });
                        cardViewEntityList.removeAll(selectVideos);
                        adapter.refresh(false);
                        popupWindow.dismiss();
                        if (cardViewEntityList.size()==selectVideos.size()){
                            btnSelectAll.setClickable(false);
                            btnDelete.setClickable(false);
                            emptyRelayout.setVisibility(View.VISIBLE);
                            gridView.setVisibility(View.GONE);
                        }else {
                            emptyRelayout.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                            btnSelectAll.setClickable(true);
                            btnDelete.setClickable(true);
                        }
                    }
                });
            }
        }
    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }


    @OnClick(R.id.btn_select_all)
    public void selectAll(View view) {
        if (adapter.getSelectVideos().size() == cardViewEntityList.size()) {
            adapter.refresh(false);
        } else {
            adapter.refresh(true);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        if (event.getType().equals("room")) {
            switch (event.getMessage()) {
                case Constants.ROOM_EVENT_DISABLE_START_BUTTON:
                    RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
                    autoDismissPopupWindow.dismiss();
                    stopService(new Intent(this, RingToneService.class));
                    PLog.i("end_calling", LocalDateTime.now().toString());
                    break;
                case Constants.ROOM_EVENT_ENABLE_START_BUTTON:
                    if (RxSPTool.getBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON)) {
                        return;
                    } else {
                        RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, true);
                        if (event.getContent() != null) {
                            showStartMeetingWindow();
                        }
                        PLog.i("start_calling", LocalDateTime.now().toString());
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void showStartMeetingWindow() {
        if (adapter.dialog!=null && adapter.dialog.isShowing()){
            adapter.dialog.dismiss();
            adapter.ijkplayer_video.stop();
            adapter.ijkplayer_video.release();
        }
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();

        }
        if (!AppUtils.isAppForeground()) {
            AppUtils.launchApp("com.example.hometrainng");

        }
        int screenWidth = RxDeviceTool.getScreenWidth(this);
        int screenHeight = RxDeviceTool.getScreenHeight(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_start_meeting, null);
        autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, false);
        autoDismissPopupWindow.setDelayMillis(600000);
        autoDismissPopupWindow.setOutsideTouchable(false);

        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        Button buttonStartMeeting = (Button) contentView.findViewById(R.id.btn_go_to_meeting);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.caller_message_textView);
        messageTextView.setText(getString(R.string.new_calling));
        startService(new Intent(this, RingToneService.class));
        autoDismissPopupWindow.setOnDismissListener(() -> {
            bgAlpha(1.0f);
            stopService(new Intent(getApplicationContext(), RingToneService.class));
        });
        buttonStartMeeting.setOnClickListener(v -> {
            finish();
            autoDismissPopupWindow.dismiss();
            goToMeeting2();
        });
    }

    private void bgAlpha(float f) {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.alpha = f;
        this.getWindow().setAttributes(layoutParams);
    }

    private void goToMeeting2() {
        stopService(new Intent(this, RingToneService.class));
        Intent communicateIntent = new Intent(this, MeetingActivity.class);
        communicateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(communicateIntent);
    }

    @Override
    public void onBackPressed() {
        if (autoDismissPopupWindow !=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }else {
            finish();
        }
    }
}