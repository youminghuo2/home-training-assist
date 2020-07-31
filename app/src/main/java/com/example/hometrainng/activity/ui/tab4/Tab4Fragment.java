package com.example.hometrainng.activity.ui.tab4;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.hometrainng.R;
import com.example.hometrainng.activity.CounselingCommentActivity;
import com.example.hometrainng.activity.CounselingDateActivity;
import com.example.hometrainng.activity.CounselingGridActivity;
import com.example.hometrainng.activity.CounselingRoomActivity;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.service.GlideCircleWithBorder;
import com.example.hometrainng.tools.Utils;
import com.example.hometrainng.tools.ViewUtils;
import com.tamsiree.rxkit.RxImageTool;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Tab4Fragment extends Fragment {
    private final String TAG = "Tab4Fragment-test";
    @BindView(R.id.current_user)
    View currentUserView;
    @BindView(R.id.counseling_date)
    View counselingDate;
    @BindView(R.id.counseling_btn)
    Button counselingBtn;
    @BindView(R.id.tab4_time_data_text)
    TextView timeText;
    @BindView(R.id.button1)
    Button button1;
    @BindView(R.id.tab4_title)
    TextView tab4_title;
    @BindView(R.id.counseling_tv)
    TextView counselingTv;
    @BindView(R.id.counseling_tips)
    TextView counselingTips;

    private Unbinder unbinder;
    private Tab4ViewModel tab4ViewModel;
    private Context mContext;

    CounselingLayoutIncluded counselingLayoutIncluded;
    LayoutIncluded layoutIncluded;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        tab4ViewModel = ViewModelProviders.of(this).get(Tab4ViewModel.class);
        View root = inflater.inflate(R.layout.tab4_fragment, container, false);
        unbinder = ButterKnife.bind(this, root);
        mContext = getActivity();
        Drawable drawable = getActivity().getDrawable(R.mipmap.note);
        drawable.setBounds(ViewUtils.dp2px(getContext(), 20), 0, ViewUtils.dp2px(getContext(), 58), ViewUtils.dp2px(getContext(), 38));
        tab4_title.setCompoundDrawables(drawable, null, null, null);

        Drawable drawable1 = getActivity().getDrawable(R.mipmap.times_clock2);
        drawable1.setBounds(10, 0, RxImageTool.dp2px(50), RxImageTool.dp2px(40));
        timeText.setCompoundDrawables(drawable1, null, null, null);
        layoutIncluded = new LayoutIncluded();
        counselingLayoutIncluded = new CounselingLayoutIncluded();
        ButterKnife.bind(layoutIncluded, currentUserView);
        ButterKnife.bind(counselingLayoutIncluded, counselingDate);

        initData(layoutIncluded, counselingLayoutIncluded);

        return root;
    }

    private void setButton1(int status) {
        if (status == 1) {
            button1.setEnabled(true);
            button1.setBackgroundResource(R.drawable.button_bg_rounded_corners);
            button1.setTextColor(ContextCompat.getColor(mContext, R.color.button_color));
        } else {
            button1.setEnabled(false);
            button1.setBackgroundResource(R.drawable.button_disable_bg_rounded_corners);
            button1.setTextColor(ContextCompat.getColor(mContext, R.color.button_disable_color));
        }
    }

    private void computeSchedule() {
        int status = RxSPTool.getInt(getContext(), Constants.SCHEDULE_STATUS);
        Log.d(TAG, "computeSchedule: status=" + status);
        setButton1(status);
        initData(layoutIncluded, counselingLayoutIncluded);
        if (status == 1) {
            currentUserView.setVisibility(View.VISIBLE);
            counselingBtn.setVisibility(View.GONE);
            counselingTips.setVisibility(View.GONE);
            counselingTv.setVisibility(View.GONE);
            counselingDate.setVisibility(View.VISIBLE);
            timeText.setVisibility(View.VISIBLE);
            timeText.setText(RxSPTool.getString(mContext, Constants.SCHEDULE_STATUS_TIME));
            counselingLayoutIncluded.monthText.setText(Utils.formatMonthString(RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_MONTH)));
            counselingLayoutIncluded.dayText.setText(Utils.formatDayString(RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_DATE)));
        } else if (status == -1) {
            currentUserView.setVisibility(View.GONE);
            timeText.setVisibility(View.GONE);
            counselingDate.setVisibility(View.GONE);
            counselingTv.setVisibility(View.GONE);
            counselingTips.setVisibility(View.GONE);
            counselingBtn.setVisibility(View.VISIBLE);
            counselingBtn.setText(getString(R.string.counseling));
            counselingBtn.setEnabled(true);
            counselingBtn.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), CounselingGridActivity.class));
            });
        } else if (status == 0) {
            currentUserView.setVisibility(View.GONE);
            timeText.setVisibility(View.GONE);
            counselingBtn.setVisibility(View.GONE);
            counselingDate.setVisibility(View.GONE);
            counselingTv.setVisibility(View.VISIBLE);
            counselingTv.setText(getString(R.string.counseling_confirm));
            counselingTv.setEnabled(true);
            counselingTips.setVisibility(View.VISIBLE);
            counselingTv.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), CounselingDateActivity.class));
            });
        } else if (status == 2) {
            currentUserView.setVisibility(View.GONE);
            timeText.setVisibility(View.GONE);
            counselingBtn.setVisibility(View.GONE);
            counselingDate.setVisibility(View.GONE);
            counselingTips.setVisibility(View.GONE);
            counselingTv.setVisibility(View.VISIBLE);
            counselingTv.setText(getString(R.string.counseling_cancel_confirm));
            counselingTv.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        computeSchedule();
        checkCurrentCounselingDateTime();
    }

    private void initData(LayoutIncluded layoutIncluded, CounselingLayoutIncluded counselingLayoutIncluded) {
        int status = RxSPTool.getInt(getContext(), Constants.SCHEDULE_STATUS);
        Log.d(TAG, "initData: status=" + status);
        if (status == 1) {
            Therapist therapist = null;
            int therapistId = RxSPTool.getInt(mContext, Constants.SCHEDULE_STATUS_THERAPIST_ID);
            Log.d(TAG, "therapistId: therapistId=" + therapistId);
            if (therapistId > 0) {
                therapist = LitePal.where("therapistId = ?", String.valueOf(therapistId)).findFirst(Therapist.class);
                Log.d(TAG, "therapist: therapist=" + therapist.getPhotoPath());
                Log.d(TAG, "therapist: therapist=" + therapist.getLastName() + "-" + therapist.getFirstName());
                if (therapist != null) {
                    if (therapist.getPhotoPath() != null) {
                        String url = Utils.getPhotoUrl(therapist.getPhotoPath(), RxSPTool.getString(mContext, "token"));
                        Glide.with(mContext)
                                .load(url)
                                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                                .transform(new GlideCircleWithBorder(getContext(), 3, Color.parseColor("#cccccccc")))
                                .into(layoutIncluded.avatarView);

                    }
                    layoutIncluded.avatarViewText.setText(therapist.getLastName() + therapist.getFirstName());
                }
            }

            int year = LocalDate.now().getYear();
            String month = RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_MONTH).trim();
            String date = RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_DATE).trim();
            String week = Utils.getWeek(year, month, date);
            counselingLayoutIncluded.monthText.setText(Utils.formatMonthString(month));
            counselingLayoutIncluded.dayText.setText(Utils.formatDayString(date));
            counselingLayoutIncluded.weekText.setText("(" + week + ")");
            timeText.setText(RxSPTool.getString(getContext(), Constants.SCHEDULE_STATUS_TIME));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.button1, R.id.button2, R.id.counseling_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button1:
                startActivity(new Intent(mContext, CounselingRoomActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(mContext, CounselingCommentActivity.class));
                break;
            case R.id.counseling_btn:
                startActivity(new Intent(mContext, CounselingGridActivity.class));
                break;
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(MessageEvent event) {
        String message = event.getMessage();
        if (Arrays.asList("schedule_update", "schedule_approve", "schedule_cancel_confirm", "schedule_web_delete").contains(message)) {
            getActivity().runOnUiThread(() -> {
                computeSchedule();
            });
        }
    }
}
