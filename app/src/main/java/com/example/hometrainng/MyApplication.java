package com.example.hometrainng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hometrainng.activity.MeetingActivity;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.service.RoomService;
import com.example.hometrainng.tools.CheckNetwork;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxSPTool;
import com.tamsiree.rxkit.RxTool;

import org.litepal.LitePal;

/**
 * @Package com.example.hometrainng
 * @Description MyApplication启动类
 * @CreateDate: 2020/4/9 11:17
 */
public class MyApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private AppCompatActivity currentActivity;
    public PopupWindow popupWindow;
    private static final String TAG = "MyApplication";
    WindowManager windowManager;

    public AppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    public static void setContext(Context context) {
        MyApplication.context = context;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
        RxTool.init(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                currentActivity = (AppCompatActivity) activity;
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                currentActivity = null;
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

        CheckNetwork checkNetwork = new CheckNetwork(getContext());
        checkNetwork.registerNetworkCallback();
    }

    // 弹窗后背景暗淡
    private void bgAlpha(float f) {
        WindowManager.LayoutParams layoutParams = currentActivity.getWindow().getAttributes();
        layoutParams.alpha = f;
        currentActivity.getWindow().setAttributes(layoutParams);
    }

    public void showPopupWindow(String message) {
        int screenWidth = RxDeviceTool.getScreenWidth(context);
        int screenHeight = RxDeviceTool.getScreenHeight(context);
        View contentView = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.activity_main_dialog, null);
        popupWindow = new PopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            popupWindow.dismiss();
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha(1.0f);
            }
        });
    }

    public void canToMeetingDialog(String content) {
        int screenWidth = RxDeviceTool.getScreenWidth(context);
        int screenHeight = RxDeviceTool.getScreenHeight(context);
        View contentView = LayoutInflater.from(getCurrentActivity()).inflate(R.layout.dialog_start_meeting, null);
        popupWindow = new PopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(contentView, Gravity.CENTER, 0, 0);
        bgAlpha(0.618f);
        Button buttonStartMeeting = (Button) contentView.findViewById(R.id.btn_go_to_meeting);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.caller_message_textView);
        messageTextView.setText(content);
        startService(new Intent(this, RingToneService.class));
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                bgAlpha(1.0f);
//                goToMeeting();
            }
        });
        buttonStartMeeting.setOnClickListener(v -> {
            popupWindow.dismiss();
            goToMeeting();
        });
    }

    private void goToMeeting() {
        RoomService.getInstance().emitCalled();
        RxSPTool.putBoolean(getApplicationContext(), Constants.ENABLE_MEETING_BUTTON, false);
        stopService(new Intent(this, RingToneService.class));
        Intent meetingIntent = new Intent(this, MeetingActivity.class);
        meetingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(meetingIntent);
    }

    public static int getResourcesId(String name, Context context) {
        Resources resources = context.getResources();
        return resources.getIdentifier(name, "string", context.getPackageName());
    }

}
