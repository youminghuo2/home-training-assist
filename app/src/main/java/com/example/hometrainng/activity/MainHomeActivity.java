package com.example.hometrainng.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.hometrainng.R;
import com.example.hometrainng.db.Comments;
import com.example.hometrainng.db.Completion;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Issues;
import com.example.hometrainng.db.Messages;
import com.example.hometrainng.db.Notice;
import com.example.hometrainng.db.RecordVideo;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.AlarmBroadcastReceiver;
import com.example.hometrainng.service.RabbitMqService;
import com.example.hometrainng.service.RingToneService;
import com.example.hometrainng.service.RoomService;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tamsiree.rxkit.RxFileTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;


public class MainHomeActivity extends BaseActivity {

    @BindView(R.id.custom_toolbar)
    Toolbar customToolbar;
    @BindView(R.id.tool_text)
    TextView toolText;

    final static int COUNTS = 10;  //规定次数
    final static long DURATION = 5000; //规定有效时间
    long[] mHits = new long[COUNTS];
    AlarmManager alarmManager;
    private int i;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        setSupportActionBar(customToolbar);
        ButterKnife.bind(this);
        i=0;
        startMqService();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        checkCounselingDate();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setItemIconTintList(null);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_tab4, R.id.navigation_tab5)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
        int userId = RxSPTool.getInt(getApplicationContext(), Constants.USER_ID);
        RoomService.getInstance().init(String.valueOf(userId));

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        if (id == 4) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_notifications);
        } else if (id == 5) {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_tab4);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            if (i==0){
                if (!Utils.checkNet2(getApplicationContext())){
                    PopWindow();
                    stopService(new Intent(this, RabbitMqService.class));
                }
                i++;
            }

        }
    }



    private void PopWindow() {
        View view =getLayoutInflater().inflate(R.layout.activity_main_dialog, null);
        PopupWindow popupWindow = new PopupWindow(view);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        TextView messageTextView = (TextView) view.findViewById(R.id.text_view_message);
        messageTextView.setText(getString(R.string.no_network));
        popupWindow.setFocusable(true);
        int width = RxImageTool.dp2px(800);
        int height = RxImageTool.dp2px(500);
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.showAtLocation(toolText, Gravity.CENTER, 0, 0);
        darkenBackground(0.2f);
        popupWindow.setOnDismissListener(() -> darkenBackground(1f));
        btnOk.setOnClickListener(view1 -> {
            popupWindow.dismiss();
        });

    }

    private void darkenBackground(float bgcolor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }



    @Override
    protected void onResume() {
        super.onResume();

        customToolbar.setOnClickListener(view -> {
            continuousClick(COUNTS, DURATION);
        });

        String nickName = RxSPTool.getString(getApplicationContext(), Constants.NICK_NAME);
//        LocalDateTime localDateTime = LocalDateTime.now();
//        int hour = localDateTime.getHour();
//        if (hour < 9 && hour >= 5) {
//            toolText.setText(nickName + "さん、" + "おはようございます！");
//        } else if (hour >= 9 && hour < 18) {
//            toolText.setText(nickName + "さん、" + "こんにちは！");
//        } else {
//            toolText.setText(nickName + "さん、" + "こんばんは!");
//        }
        int number=(int)(Math.random()*5+1);
        String titleToolbar = "";

        switch (number){
            case 1:
                titleToolbar = String.format(getString(R.string.toolbar_title_1) , nickName);
                break;
            case 2:
                titleToolbar = String.format(getString(R.string.toolbar_title_2) , nickName);
                break;
            case 3:
                titleToolbar = String.format(getString(R.string.toolbar_title_3) , nickName);
                break;
            case 4:
                titleToolbar = String.format(getString(R.string.toolbar_title_4) , nickName);
                break;
            case 5:
                titleToolbar = String.format(getString(R.string.toolbar_title_5) , nickName);
                break;
        }
        toolText.setText(titleToolbar);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        PLog.w("mainhome", "destroy");
        stopService(new Intent(this, RingToneService.class));
        stopService(new Intent(this, RabbitMqService.class));
    }

    private void startMqService() {
        PLog.i("startMqService", "start mq service");
        int userId = RxSPTool.getInt(getApplicationContext(), Constants.USER_ID);
        Intent rabbitMqIntent = new Intent(this, RabbitMqService.class);
        rabbitMqIntent.putExtra("USER_ID", userId);
        startService(rabbitMqIntent);
    }

    private void checkCounselingDate() {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, 8);
        currentTime.set(Calendar.MINUTE, 00);
        currentTime.set(Calendar.MILLISECOND, 00);
        currentTime.set(Calendar.SECOND, 00);
        Intent intent = new Intent(this, AlarmBroadcastReceiver.class);
        intent.setAction("check_counseling");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 9, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, currentTime.getTimeInMillis(), 24 * 60 * 60 * 1000L, pendingIntent);
    }

    private void continuousClick(int count, long time) {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];

            LitePal.deleteAll(VideoHistory.class);
            LitePal.deleteAll(Video.class);
            LitePal.deleteAll(TimeDate.class);
            LitePal.deleteAll(Therapist.class);
            LitePal.deleteAll(RecordVideo.class);
            LitePal.deleteAll(Notice.class);
            LitePal.deleteAll(Messages.class);
            LitePal.deleteAll(Issues.class);
            LitePal.deleteAll(Goals.class);
            LitePal.deleteAll(Completion.class);
            LitePal.deleteAll(Comments.class);

            RxFileTool.cleanCustomCache("/storage/emulated/0/_MyPhoto");
            RxFileTool.cleanCustomCache("/storage/emulated/0/_MyVideo");

            RxFileTool.cleanInternalSP(getApplicationContext());
            RxFileTool.cleanInternalCache(getApplicationContext());
            RxFileTool.cleanExternalCache(getApplicationContext());

            RxSPTool.clearPreference(getApplicationContext(), "token", "token");
            RxSPTool.clearPreference(getApplicationContext(), null, null);

            RxSPTool.putBoolean(getApplicationContext(), Constants.FIRST_LOGIN, true);

            RoomService.getInstance().close();
            stopService(new Intent(this, RabbitMqService.class));

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}
