package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hometrainng.R;
import com.example.hometrainng.adapter.CounselingGridAdapter;
import com.example.hometrainng.adapter.DateListAdapter;
import com.example.hometrainng.adapter.TimeListAdapter;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.entity.ConfirmBooKBean;
import com.example.hometrainng.entity.TherapistSchedule;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.DateUtils;
import com.example.hometrainng.tools.DividerItemDecoration;
import com.tamsiree.rxkit.RxSPTool;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CounselingGridActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView back_img;

    @BindView(R.id.ll_previous)
    LinearLayout ll_previous;

    @BindView(R.id.ll_next)
    LinearLayout ll_next;

    @BindView(R.id.list_date)
    RecyclerView list_date;

    @BindView(R.id.list_time)
    RecyclerView list_time;

    @BindView(R.id.grid_view)
    RecyclerView grid_view;

    @BindView(R.id.btn_confirm)
    Button btn_confirm;

    @BindView(R.id.tv_next)
    TextView tv_next;

    @BindView(R.id.tv_previous)
    TextView tv_previous;

    @BindView(R.id.firstCandidate)
    TextView firstCandidate;

    @BindView(R.id.secondCandidate)
    TextView secondCandidate;

    @BindView(R.id.thirdCandidate)
    TextView thirdCandidate;

    private String currentDate;
    private DateListAdapter dateListAdapter;
    private ArrayList<Integer> positions = new ArrayList<>();
    private ArrayList<TherapistSchedule.DataBean> beans = new ArrayList<>();
    private CounselingGridAdapter counselingGridAdapter;
    private TimeListAdapter timeListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counseling_grid);
        ButterKnife.bind(this);
        initTimeList();
        initDateList();
        initGridView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxSPTool.remove(getApplicationContext(), Constants.FIRST_BOOK_DATE);
        RxSPTool.remove(getApplicationContext(), Constants.FIRST_BOOK_TIME);
        RxSPTool.remove(getApplicationContext(), Constants.SECOND_BOOK_DATE);
        RxSPTool.remove(getApplicationContext(), Constants.SECOND_BOOK_TIME);
        RxSPTool.remove(getApplicationContext(), Constants.THIRD_BOOK_DATE);
        RxSPTool.remove(getApplicationContext(), Constants.THIRD_BOOK_TIME);
    }

    //加载日期列表
    private void initDateList() {
        currentDate = DateUtils.getNowDate(DateUtils.DATE_YMD);
        ArrayList<String> dates = DateUtils.getWeek(currentDate, DateUtils.DATE_MD);
        list_date.setLayoutManager(new LinearLayoutManager(this));
        dateListAdapter = new DateListAdapter(this, dates);
        list_date.setAdapter(dateListAdapter);
    }

    //加载日期列表
    @SuppressLint("WrongConstant")
    private void initTimeList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 12);
        gridLayoutManager.setOrientation(GridLayout.VERTICAL);
        DividerItemDecoration divider = new DividerItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.dividerSize)
                .setVerticalSpan(R.dimen.dividerSize)
                .setColorResource(R.color.grid_divider_gray)
                .setShowLastLine(false)
                .build();
        list_time.addItemDecoration(divider);
        list_time.setLayoutManager(gridLayoutManager);
        timeListAdapter = new TimeListAdapter(CounselingGridActivity.this);
        list_time.setAdapter(timeListAdapter);
    }

    private void initGridView() {
        String token = RxSPTool.getString(getApplicationContext(), Constants.TOKEN);
        Call<TherapistSchedule> getSchedule = HttpHelper.getInstance().create(HomeTrainService.class).getScheduleList(token, DateUtils.getMonday(currentDate, DateUtils.DATE_YMD).compareTo(DateUtils.getNowDate(DateUtils.DATE_YMD)) > 0 ? DateUtils.getMonday(currentDate, DateUtils.DATE_YMD) : DateUtils.getNowDate(DateUtils.DATE_YMD), DateUtils.getSunday(currentDate, DateUtils.DATE_YMD));
        getSchedule.enqueue(new Callback<TherapistSchedule>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<TherapistSchedule> call, Response<TherapistSchedule> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        positions.clear();
                        beans.clear();
                        List<TherapistSchedule.DataBean> list = response.body().getData();
                        beans.addAll(list);
                        for (int i = 0; i < list.size(); i++) {
                            positions.add(DateUtils.getSchedulePosition(list.get(i).getScheduleTime()));
                        }
                        if (counselingGridAdapter != null) {
                            counselingGridAdapter.refresh(positions, beans, currentDate);
                        } else {
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(CounselingGridActivity.this, 12);
                            gridLayoutManager.setOrientation(GridLayout.VERTICAL);
                            DividerItemDecoration divider = new DividerItemDecoration.Builder(CounselingGridActivity.this)
                                    .setHorizontalSpan(R.dimen.dividerSize)
                                    .setVerticalSpan(R.dimen.dividerSize)
                                    .setColorResource(R.color.grid_divider_gray)
                                    .setShowLastLine(true)
                                    .build();
                            grid_view.addItemDecoration(divider);
                            grid_view.setLayoutManager(gridLayoutManager);
                            counselingGridAdapter = new CounselingGridAdapter(CounselingGridActivity.this, positions, beans, currentDate);
                            grid_view.setAdapter(counselingGridAdapter);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TherapistSchedule> call, Throwable t) {

            }
        });
    }

    @OnClick({R.id.ll_next, R.id.ll_previous})
    public void refreshDate(View view) {
        boolean isNext = view.getId() == R.id.ll_next;
        if (!isNext && DateUtils.getMonday(currentDate, DateUtils.DATE_YMD).compareTo(DateUtils.getNowDate(DateUtils.DATE_YMD)) <= 0) {
            return;
        }
        currentDate = DateUtils.getDelayDay(currentDate, isNext ? 7 : -7, DateUtils.DATE_YMD);
        ArrayList<String> dates = DateUtils.getWeek(currentDate, DateUtils.DATE_MD);
        dateListAdapter.setDates(dates);
        if (DateUtils.getMonday(currentDate, DateUtils.DATE_YMD).compareTo(DateUtils.getNowDate(DateUtils.DATE_YMD)) <= 0) {
            tv_previous.setTextColor(getResources().getColor(R.color.linear_frame));
            ll_previous.setBackgroundResource(R.drawable.round_previous_buttom);
        } else {
            tv_previous.setTextColor(getResources().getColor(R.color.textColor));
            ll_previous.setBackgroundResource(R.drawable.round_next_buttom);
        }
        initGridView();
    }

    @OnClick(R.id.back_img)
    public void back(View view) {
        finish();
    }

    @OnClick(R.id.btn_confirm)
    public void confirmCounseling(View view) {
        ConfirmBooKBean bean = new ConfirmBooKBean();
        ArrayList<TherapistSchedule.DataBean> selectIds = counselingGridAdapter.getSelectIds();
        if (selectIds.get(0) == null) {
            return;
        }

        bean.setFirstCandidate(selectIds.get(0).getId());
        bean.setFirstCandidateDatetime(selectIds.get(0).getScheduleTime());
        if (selectIds.get(1) != null) {

            bean.setSecondCandidate(selectIds.get(1).getId());
            bean.setSecondCandidateDatetime(selectIds.get(1).getScheduleTime());
        }
        if (selectIds.get(2) != null) {

            bean.setThirdCandidate(selectIds.get(2).getId());
            bean.setThirdCandidateDatetime(selectIds.get(2).getScheduleTime());
        }
        int userId = RxSPTool.getInt(this, Constants.USER_ID);
        bean.setUserId(userId);

        Intent counselingIntent = new Intent(CounselingGridActivity.this, CounselingDateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("ConfirmBooKBean", bean);
        counselingIntent.putExtras(bundle);
        startActivity(counselingIntent);
//        finish();
    }

    public void setCounselingDate(int position, String date) {
        String text = null;
        if (date != null) {
            text = date.substring(0, 19) + "\n" + date.substring(19, date.length());
        }
        switch (position) {
            case 1:
                if (date != null) {
                    firstCandidate.setText(text);
                    firstCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.second_title_size));
                } else {
                    firstCandidate.setText("第一候補日時");
                    firstCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.title_size));
                }
                break;
            case 2:
                if (date != null) {
                    secondCandidate.setText(text);
                    secondCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.second_title_size));
                } else {
                    secondCandidate.setText("第二候補日時");
                    secondCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.title_size));
                }
                break;
            case 3:
                if (date != null) {
                    thirdCandidate.setText(text);
                    thirdCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.second_title_size));
                } else {
                    thirdCandidate.setText("第三候補日時");
                    thirdCandidate.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelOffset(R.dimen.title_size));
                }
                break;
            default:
                break;
        }
    }
}
