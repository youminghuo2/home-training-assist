package com.example.hometrainng.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.customview.AutoDismissPopupWindow;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.service.GlideCircleWithBorder;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounselingRoomActivity extends BaseActivity {
    private static final String TAG = "CounselingRoomActivity";
    private final static long ONCE_TIME = 1000;

    @BindView(R.id.back_to_tab4)
    ImageView backImg;
    @BindView(R.id.current_user)
    View currentUserView;
    @BindView(R.id.counseling_date)
    View counselingDate;
    @BindView(R.id.counseling_time)
    TextView counselingTime;
    @BindView(R.id.countdown_hour)
    TextView countdownHour;
    @BindView(R.id.countdown_minute)
    TextView countdownMinute;
    @BindView(R.id.countdown_title1)
    TextView countdownTitle1;
    @BindView(R.id.countdown_title2)
    TextView countdownTitle2;
    @BindView(R.id.countdown_title3)
    TextView countdownTitle3;
    @BindView(R.id.btn_send_message)
    Button btnSendMessage;
    @BindView(R.id.rg_themes)
    RadioGroup rgThemes;
    @BindView(R.id.rb_theme1)
    RadioButton rbTheme1;
    @BindView(R.id.rb_theme2)
    RadioButton rbTheme2;
    @BindView(R.id.rb_theme3)
    RadioButton rbTheme3;
    @BindView(R.id.rb_theme4)
    RadioButton rbTheme4;
    @BindView(R.id.counseling_cancel)
    Button counselingCancelBtn;
    @BindView(R.id.counseling_btn)
    Button counselingBtn;

    Context mContext;
    Button btnConfirmMessage;
    Button btnCancel;
    Button btnOk;
    Button btnCancelCounselingConfirm;
    @BindView(R.id.left_content_label)
    TextView leftContentLabel;

    private String chooseTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_room);

        mContext = getApplicationContext();
        ButterKnife.bind(this);
        LayoutIncluded layoutIncluded = new LayoutIncluded();
        CounselingLayoutIncluded counselingLayoutIncluded = new CounselingLayoutIncluded();
        ButterKnife.bind(layoutIncluded, currentUserView);
        ButterKnife.bind(counselingLayoutIncluded, counselingDate);
        initData(layoutIncluded, counselingLayoutIncluded);

        rgThemes.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId()) {
                case R.id.rb_theme2:
                    chooseTheme = getString(R.string.radio_value_2);
                    break;
                case R.id.rb_theme3:
                    chooseTheme = getString(R.string.radio_value_3);
                    break;
                case R.id.rb_theme4:
                    chooseTheme = getString(R.string.radio_value_4);
                    break;
                default:
                    chooseTheme = getString(R.string.radio_value_1);
                    break;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Intent intent = getIntent();
        if (intent != null && hasFocus) {
            String fromFlag = intent.getStringExtra("fromFlag");
            if (fromFlag != null && fromFlag.equals("Communicate")) {
                showMeetingEndMessage(getString(R.string.tip_meeting_end));
            }
        }
    }

    private void initData(LayoutIncluded layoutIncluded, CounselingLayoutIncluded counselingLayoutIncluded) {
        Therapist therapist = null;
        int therapistId = RxSPTool.getInt(mContext, Constants.SCHEDULE_STATUS_THERAPIST_ID);
        if (therapistId > 0) {
            therapist = LitePal.where("therapistId = ?", String.valueOf(therapistId)).findFirst(Therapist.class);
        }
//        else {
//            therapist = LitePal.where("flag=?", "responsible").findFirst(Therapist.class);
//        }
        if (therapist != null) {
            if (therapist.getPhotoPath() != null) {
                String url = Utils.getPhotoUrl(therapist.getPhotoPath(), RxSPTool.getString(mContext, "token"));
                Glide.with(mContext)
                        .load(url)
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .transform(new GlideCircleWithBorder(this, 3, Color.parseColor("#cccccccc")))
                        .into(layoutIncluded.avatarView);
            }
            layoutIncluded.avatarViewText.setText(therapist.getLastName() + therapist.getFirstName());
        }

        if (RxSPTool.getInt(getApplicationContext(), Constants.SCHEDULE_STATUS) == 1) {
            int year = LocalDate.now().getYear();
            String month = RxSPTool.getString(this, Constants.SCHEDULE_STATUS_MONTH).trim();
            String date = RxSPTool.getString(this, Constants.SCHEDULE_STATUS_DATE).trim();
            String week = Utils.getWeek(year, month, date);
            counselingLayoutIncluded.monthText.setText(Utils.formatMonthString(RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_MONTH)));
            counselingLayoutIncluded.dayText.setText(Utils.formatDayString(RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_DATE)));
            counselingLayoutIncluded.weekText.setText("(" + week + ")");
            counselingTime.setText(RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_TIME));
            String time = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_TIME);
            String[] counseling_time = time.split("~");
            if (counseling_time.length == 0) {
                return;
            }
            String[] counseling_start_time = counseling_time[0].split(":");
            if (counseling_start_time.length == 0) {
                return;
            }
            int hour = Integer.parseInt(counseling_start_time[0]);
            int minute = Integer.parseInt(counseling_start_time[1]);
            LocalDateTime temp_counseling_datetime = LocalDateTime.of(year, Integer.parseInt(month.trim()), Integer.parseInt(date.trim()), hour, minute);
            LocalDateTime now_datetime = LocalDateTime.now();
            long total_time = Duration.between(now_datetime, temp_counseling_datetime).toMinutes() * 60 * 1000;
            new CountDownTimer(total_time, ONCE_TIME) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long totalMinutes = 2 + millisUntilFinished / 1000 / 60;
                    long remainingHours;
                    long remainingMinutes;
                    if (totalMinutes > 60) {
                        remainingHours = totalMinutes / 60;
                        remainingMinutes = totalMinutes - remainingHours * 60;
                    } else if (totalMinutes == 60) {
                        remainingHours = 1;
                        remainingMinutes = 0;
                    } else {
                        remainingHours = 0;
                        remainingMinutes = totalMinutes;
                    }
                    countdownHour.setText(String.valueOf(remainingHours));
                    countdownMinute.setText(String.valueOf(remainingMinutes));
                }

                @Override
                public void onFinish() {
                    countdownHour.setText("0");
                    countdownMinute.setText("0");
                }
            }.start();
        } else {
            counselingTime.setVisibility(View.GONE);
            counselingDate.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.back_to_tab4, R.id.btn_send_message, R.id.counseling_btn, R.id.counseling_cancel})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.btn_send_message) {
            confirmSendMessageDialog(getString(R.string.confirm_send_message));
        } else if (view.getId() == R.id.counseling_cancel) {
            cancelCounseling();
        } else if (view.getId() == R.id.counseling_btn) {
            startActivity(new Intent(CounselingRoomActivity.this, CounselingGridActivity.class));
        } else {
            finish();
        }
    }

    /**
     * 取消预约时间
     */
    private void cancelCounseling() {
        String month = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_MONTH);
        String date = RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_DATE);

        if (TextUtils.isEmpty(month) || TextUtils.isEmpty(date)) {
            return;
        }
        String tempDate = LocalDateTime.now().getYear() + "-" + month + "-" + date;
        LocalDate counselingDate = LocalDate.parse(tempDate.trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String counselingEndDate = counselingDate.minusDays(1).toString() + " 18:00:00";
        LocalDateTime counselingEndDateTime = LocalDateTime.parse(counselingEndDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (counselingEndDateTime.isBefore(LocalDateTime.now())) {
            showAutoDismissPopupWindow(getString(R.string.tip_not_cancel_v2));
        } else {
            showConfirmDialog();
        }
    }

    public void showAutoDismissPopupWindow(String content) {
        int screenWidth = RxDeviceTool.getScreenWidth(this);
        int screenHeight = RxDeviceTool.getScreenHeight(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_start_meeting, null);
        autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, screenWidth / 15 * 8, screenHeight / 15 * 8, false);
        autoDismissPopupWindow.setDelayMillis(10000);
        autoDismissPopupWindow.setOutsideTouchable(false);
        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        darkenBackground(0.5f);
        Button buttonStartMeeting = (Button) contentView.findViewById(R.id.btn_go_to_meeting);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.caller_message_textView);
        messageTextView.setText(content);
        autoDismissPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1.0f);
            }
        });
        buttonStartMeeting.setOnClickListener(v -> {
            autoDismissPopupWindow.dismiss();
        });
    }

    private void requestCancelCounseling() {
        int status = RxSPTool.getInt(mContext, Constants.SCHEDULE_STATUS);
        int scheduleId = RxSPTool.getInt(mContext, Constants.SCHEDULE_ID);
        if (scheduleId > 0 && status == 1) {
            String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
            HttpHelper.getInstance().create(HomeTrainService.class)
                    .cancelCounseling(token, String.valueOf(scheduleId))
                    .enqueue(new Callback<MsgModel>() {
                        @Override
                        public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                            if (response.body().getCode() == 200) {
                                RxSPTool.putInt(mContext, Constants.SCHEDULE_STATUS, 2);
                                showCancelConfirming(getString(R.string.cancel_counseling_confirm_message));
                            }
                        }

                        @Override
                        public void onFailure(Call<MsgModel> call, Throwable t) {
                            PLog.e(TAG, "onFailure:requestCancelCounseling ");
                        }
                    });
        }
    }

    private void sendMessage() {

        int scheduleId = RxSPTool.getInt(mContext, Constants.SCHEDULE_ID);

        if (scheduleId > 0) {

            String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
            RequestBody requestBody = new FormBody.Builder()
                    .add("theme", chooseTheme).build();
            HttpHelper.getInstance().create(HomeTrainService.class)
                    .updateTheme(token, String.valueOf(scheduleId), requestBody)
                    .enqueue(new Callback<MsgModel>() {
                        @Override
                        public void onResponse(Call<MsgModel> call, Response<MsgModel> response) {
                            if (response.body().getCode() == 200) {
//                                finish();
                                popupWindow.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), response.body().getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MsgModel> call, Throwable t) {

                        }
                    });
        }
    }

    static class LayoutIncluded {
        @BindView(R.id.user_avatar_photo)
        ImageView avatarView;
        @BindView(R.id.user_avatar_title)
        TextView avatarViewText;
    }

    static class CounselingLayoutIncluded {
        @BindView(R.id.tab4_month_data_text)
        TextView monthText;
        @BindView(R.id.tab4_day_data_text)
        TextView dayText;
        @BindView(R.id.tab4_week_data_text)
        TextView weekText;
    }

    private void showConfirmDialog() {
        View contentView = LayoutInflater.from(CounselingRoomActivity.this).inflate(R.layout.dialog_cancel_counseling, null);
        int width = RxImageTool.dp2px(800);
        int height = RxImageTool.dp2px(500);
        popupWindow = new AutoDismissPopupWindow(contentView, width, height, false);
        popupWindow.setOutsideTouchable(false);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        btnCancel = (Button) contentView.findViewById(R.id.btn_cancel);
        btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        darkenBackground(0.5f);
        popupWindow.setOnDismissListener(() -> {
            darkenBackground(1f);
        });
        btnCancel.setOnClickListener(v -> {
            popupWindow.dismiss();
            finish();
        });
        btnOk.setOnClickListener(v -> {
            popupWindow.dismiss();
            requestCancelCounseling();
        });
    }

    private void showCancelConfirming(String message) {
        int screenWidth = RxDeviceTool.getScreenWidth(mContext);
        int screenHeight = RxDeviceTool.getScreenHeight(mContext);
        View contentView = LayoutInflater.from(CounselingRoomActivity.this).inflate(R.layout.dialog_cancel_counseling_confirm, null);
        popupWindow = new PopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dip2px(500), true);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        btnCancelCounselingConfirm = (Button) contentView.findViewById(R.id.btn_cancel_counseling_confirm_ok);
        TextView messageTv = (TextView) contentView.findViewById(R.id.cancel_counseling_confirm_message);
        messageTv.setText(message);
        darkenBackground(0.5f);
        popupWindow.setOnDismissListener(() -> {
            darkenBackground(1f);
        });
        btnCancelCounselingConfirm.setOnClickListener(v -> {
            popupWindow.dismiss();
            finish();
        });
    }

    private void showMeetingEndMessage(String message) {
        View contentView = LayoutInflater.from(CounselingRoomActivity.this).inflate(R.layout.dialog_cancel_counseling_confirm_finish, null);
        popupWindow = new AutoDismissPopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dip2px(500), false);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        btnCancelCounselingConfirm = (Button) contentView.findViewById(R.id.btn_cancel_counseling_confirm_ok);
        TextView messageTv = (TextView) contentView.findViewById(R.id.cancel_counseling_confirm_message);
        messageTv.setText(message);
        darkenBackground(0.5f);
        popupWindow.setOnDismissListener(() -> {
            darkenBackground(1f);
            finish();
        });
        btnCancelCounselingConfirm.setOnClickListener(v -> {
            popupWindow.dismiss();
            finish();
        });
    }

    private void confirmSendMessageDialog(String message) {
        if (chooseTheme == null) {
            return;
        }
        View contentView = LayoutInflater.from(CounselingRoomActivity.this).inflate(R.layout.dialog_cancel_counseling_confirm, null);
        popupWindow = new PopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dip2px(500), false);
        View parentView = findViewById(android.R.id.content);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        darkenBackground(0.5f);
        btnConfirmMessage = (Button) contentView.findViewById(R.id.btn_cancel_counseling_confirm_ok);
        TextView messageTv = (TextView) contentView.findViewById(R.id.cancel_counseling_confirm_message);
        messageTv.setText(message);
        popupWindow.setOnDismissListener(() -> {
            darkenBackground(1f);
        });
        btnConfirmMessage.setOnClickListener(v -> {
            sendMessage();
            popupWindow.dismiss();
        });
    }

    private void darkenBackground(float bgColor) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgColor;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }


//    @Subscribe(threadMode = ThreadMode.POSTING, sticky = true)
//    public void onMessageEvent(MessageEvent event) {
//        if (event.getType().equals("schedule")) {
//            if (event.getMessage().equals("schedule_cancel_confirm")) {
//                showPopupWindow(getString(R.string.cancel_counseling_confirm_message_v2));
//            }
//        }
//    }

    public void showPopupWindow(String message) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_main_dialog, null);
        AutoDismissPopupWindow autoDismissPopupWindow = new AutoDismissPopupWindow(contentView, RxImageTool.dp2px(800), RxImageTool.dp2px(500), false);
        autoDismissPopupWindow.setOutsideTouchable(true);
        View parentView = findViewById(android.R.id.content);
        autoDismissPopupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        bgAlpha2(0.618f);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.text_view_message);
        messageTextView.setText(message);
        Button btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        autoDismissPopupWindow.setOnDismissListener(() -> {
            bgAlpha2(1.0f);
            finish();
        });
        btnOk.setOnClickListener(v -> {
            autoDismissPopupWindow.dismiss();
            finish();
        });
    }

    private void bgAlpha2(float f) {
        WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();
        layoutParams.alpha = f;
        this.getWindow().setAttributes(layoutParams);
    }
}