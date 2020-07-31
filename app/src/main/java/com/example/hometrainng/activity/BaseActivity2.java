package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.example.hometrainng.MyApplication;
import com.example.hometrainng.R;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.model.HomeKeyListener;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.service.RoomService;
import com.example.hometrainng.tools.HomeReceiverUtil;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxActivityTool;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

@SuppressLint("Registered")
public class BaseActivity2 extends AppCompatActivity {
    private final static String TAG = "BaseActivity";
    PopupWindow popupWindow;
    AutoDismissPopupWindow autoDismissPopupWindow;

    private boolean flagCreate = true;
    private boolean flagDestroy = false;
    private boolean flagHome = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        flagCreate = false;
        flagDestroy = true;

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        flagHome = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if (flagCreate) {
            EventBus.getDefault().register(this);
            flagCreate = false;
        }
        HomeReceiverUtil.registerHomeKeyReceiver(this, new HomeKeyListener() {
            @Override
            public void homeKey() {
                flagHome = true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (autoDismissPopupWindow!=null && autoDismissPopupWindow.isShowing()){
            autoDismissPopupWindow.dismiss();
        }else {
            super.onBackPressed();
        }

    }

    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//       if (flagDestroy){
//           EventBus.getDefault().unregister(this);
//           flagDestroy=false;
//       }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        //flagHome为true，不销毁，flagHome为false，销毁
        if (!flagHome) {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
                flagDestroy = false;
                flagCreate = true;
            }
        } else {
            //不执行任何操作
            Log.d(TAG, "onPause: ");
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        Log.d(TAG, "getType: " + event.getType());

        if (event.getType().equals("network")) {
            if (event.getMessage().equals("onAvailable")) {

                RoomService.getInstance().close();
                int userId = RxSPTool.getInt(getApplicationContext(), Constants.USER_ID);
                RoomService.getInstance().init(String.valueOf(userId));


                showPopupWindow(getResources().getString(R.string.network_resume));
            }
            if (event.getMessage().equals("onLost")) {
                RxActivityTool.skipActivityAndFinishAll(this, MainHomeActivity.class);
            }
        }
        if (event.getType().equals("alarm")) {

            if (RxSPTool.getInt(getApplicationContext(), Constants.SCHEDULE_STATUS) != 1) {
                return;
            }
            String month = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_MONTH);
            String date = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_DATE);
            String time = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_TIME);
            if (TextUtils.isEmpty(month) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                return;
            }

            int year = LocalDateTime.now().getYear();
            String[] counseling_time = time.split("~");
            String[] counseling_start_time = counseling_time[0].split(":");
            int hour = Integer.parseInt(counseling_start_time[0]);
            int minute = Integer.parseInt(counseling_start_time[1]);
            LocalDateTime counselingDatetime = LocalDateTime.of(year, Integer.parseInt(month.trim()), Integer.parseInt(date.trim()), hour, minute);

            if (counselingDatetime.getDayOfYear() == LocalDateTime.now().getDayOfYear()) {
                String showDataTime = RxSPTool.getString(getApplicationContext(), Constants.SHOW_TODAY_COUNSELING);
                String counselingDateMsg = getString(R.string.tip_today_meeting) + counselingDatetime.toLocalTime() + "!";


                if (TextUtils.isEmpty(showDataTime)) {

                    showPopupWindow(counselingDateMsg);
                    Utils.playNotificationRing(getApplicationContext());
                    RxSPTool.putString(getApplicationContext(), Constants.SHOW_TODAY_COUNSELING, LocalDateTime.now().toString());
                } else {
                    LocalDateTime showDataTime2 = LocalDateTime.parse(showDataTime);
                    if (showDataTime2.getDayOfYear() != LocalDateTime.now().getDayOfYear()) {
                        showPopupWindow(counselingDateMsg);
                        Utils.playNotificationRing(getApplicationContext());
                        RxSPTool.putString(getApplicationContext(), Constants.SHOW_TODAY_COUNSELING, LocalDateTime.now().toString());
                    }
                }
            }
        }
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
        if (event.getType().equals("counseling")) {
            ((MyApplication) getApplication()).showPopupWindow(event.getMessage());
            Utils.playNotificationRing(getApplicationContext());
        }
        if (event.getType().equals("go_to_meeting")) {
            ((MyApplication) getApplication()).canToMeetingDialog(event.getMessage());
        }
        if (event.getType().equals("schedule")) {
            if (event.getMessage().equals("schedule_update")) {
                JSONObject jsonObject = event.getData();
                try {
                    String message = "";

                    if (jsonObject.getString("lastDateTime").length() != 0) {
                        message += jsonObject.getString("lastDateTime") + "の";
                    }
                    message += "カウンセリングは";
                    message += jsonObject.getString("newDateTime") + "に変更されました。ご確認お願いたします。";
                    showPopupWindow(message);
                } catch (JSONException e) {
                    PLog.e(TAG, "onMessageEvent: " + e.toString());
                }
            }
            if (event.getMessage().equals("schedule_cancel_confirm")) {
                showPopupWindow2(getString(R.string.cancel_counseling_confirm_message_v2));
            }
            if (event.getMessage().equals("schedule_web_delete")) {
                showPopupWindow(getString(R.string.delete_counseling_message));
            }
            if (event.getMessage().equals("schedule_approve")) {
                showPopupWindow(getString(R.string.MSG0005_v2));
            }
        }
        if (event.getType().equals("video_update")) {
            PLog.i("video_update", LocalDateTime.now().toString());
            showVideoPopupWindow(event.getMessage());
        }
    }

    public void showVideoPopupWindow(String message) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_main_dialog, null);
        AutoDismissPopupWindow videoUpdatePopupWindow = new AutoDismissPopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dp2px(500), false);
        videoUpdatePopupWindow.setOutsideTouchable(true);
        View parentView = findViewById(android.R.id.content);
        videoUpdatePopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        videoUpdatePopupWindow.setOnDismissListener(() -> bgAlpha(1.0f));

        btnOk.setOnClickListener(v -> {
            videoUpdatePopupWindow.dismiss();
        });
    }


    public void showPopupWindow2(String message) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_main_dialog, null);
        AutoDismissPopupWindow autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dp2px(500), false);
        autoDismissPopupWindow.setOutsideTouchable(true);
        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        autoDismissPopupWindow.setOnDismissListener(() -> {
            bgAlpha(1.0f);
            finish();
        });
        btnOk.setOnClickListener(v -> {
            autoDismissPopupWindow.dismiss();
            finish();
        });
    }

    public void showPopupWindow(String message) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_main_dialog, null);
        autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dp2px(500), false);
        autoDismissPopupWindow.setOutsideTouchable(true);
        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        autoDismissPopupWindow.setOnDismissListener(() -> bgAlpha(1.0f));

        btnOk.setOnClickListener(v -> {
            autoDismissPopupWindow.dismiss();
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return false;
        }
        if (autoDismissPopupWindow != null && autoDismissPopupWindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void showStartMeetingWindow() {
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
}
