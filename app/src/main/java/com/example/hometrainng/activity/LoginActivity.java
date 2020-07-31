package com.example.hometrainng.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Comments;
import com.example.hometrainng.db.Completion;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.db.TimeDate;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.db.VideoHistory;
import com.example.hometrainng.entity.CommentModel;
import com.example.hometrainng.entity.LoginModel;
import com.example.hometrainng.entity.MainHomeModel;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxSPTool;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.login_title)
    TextView loginTitle;
    @BindView(R.id.login_id)
    EditText loginId;
    @BindView(R.id.login_nickName)
    EditText loginNickName;
    @BindView(R.id.rules_button)
    Button rulesButton;
    private Context mContext;
    private String userName, nickName;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
    }

    @SuppressLint("WrongConstant")
    @OnClick(R.id.rules_button)
    public void onViewClicked() {
        userName = loginId.getText().toString();
        nickName = loginNickName.getText().toString();
        if (!Utils.checkNet(mContext)) {
            Toast.makeText(getApplicationContext(), "ネットワークに接続できません", Constants.Toast_Length).show();
        } else {
            if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(nickName)) {
                Toast.makeText(mContext, "IDと名前を入力してください。", Constants.Toast_Length).show();
            } else {
                progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage(getString(R.string.tip_login_process));
                progressDialog.setCancelable(true);
                progressDialog.show();
                login(userName);
            }
        }
    }


    private void login(String userName) {
        Call<LoginModel> loginModelCall = HttpHelper.getInstance().create(HomeTrainService.class).login(userName);
        loginModelCall.enqueue(new Callback<LoginModel>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                if (response.body() != null) {
                    if (response.body().getCode() == 200) {
                        String token = response.body().getData();
                        RxSPTool.putString(mContext, Constants.TOKEN, token);
                        RxSPTool.putString(getApplicationContext(), Constants.NICK_NAME, nickName);
                        selectAppHomeInfo(token);
                    } else {
                        Toast.makeText(mContext, "ログイン失敗しました。", Constants.Toast_Length).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        loginNickName.setText("");
                        loginId.setText("");
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
            }
        });

    }


//    //新曾Timedate数据库
//    private void storeTimeDate() {
//        TimeDate timeDate = new TimeDate();
////        LocalDate localDate = LocalDate.now();
////        String date = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
//
////        timeDate.setBeginDate(date);
//        timeDate.setBeginDate();
//        timeDate.save();
//    }

    private void selectAppHomeInfo(String token) {
        Call<MainHomeModel> mainHomeModelCall = HttpHelper.getInstance().create(HomeTrainService.class).mainHome(token);
        mainHomeModelCall.enqueue(new Callback<MainHomeModel>() {
            @SuppressLint("WrongConstant")
            @SneakyThrows
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<MainHomeModel> call, Response<MainHomeModel> response) {
                if (response.body() != null && response.body().getCode() == 200) {

                    MainHomeModel mainHomeModel = new MainHomeModel();
                    mainHomeModel.setData(response.body().getData());
                    int userId = response.body().getData().getUserId();
                    RxSPTool.putInt(mContext, Constants.USER_ID, userId);
                    //存储预约信息
                    storeCounseling(response.body().getData().getScheduleInfo());
                    //新增Video数据库数据
                    storeVideoData(mainHomeModel.getData().getVideoDtoList());
//                    storeTimeDate();

                    TimeDate timeDate=new TimeDate();
                    timeDate.setBeginDate(mainHomeModel.getData().getLoginDate());

                    LocalDate local = LocalDate.now();
                    String date = local.format(DateTimeFormatter.ISO_LOCAL_DATE);
                    timeDate.setVideoDate(date);

                    timeDate.save();

                    storeCommentData(token, userId);

                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    //创建一个文件夹
                    File dir2 = new File(externalStorageDirectory, "_MyVideo");
                    if (!dir2.exists()) {
                        dir2.mkdir();
                    }

                    //创建文件夹
                    File file = Environment.getExternalStorageDirectory();
                    File dir = new File(file, "_MyPhoto");
                    if (!dir.exists()) {
                        dir.mkdir();
                    }


                    //传入首次跳转编辑
                    RxSPTool.putBoolean(getApplicationContext(), "JumpState", false);

                    //达成度
//                    LocalDate localDate = LocalDate.now();
//                    List<Completion> completionList = new ArrayList<>();
//                    for (int i = 0; i < 7; i++) {
//                        Completion completion = new Completion();
//                        completion.setGoals(0);
//                        LocalDate days = localDate.minus(i, ChronoUnit.DAYS);
//                        completion.setLocalDate(days.format(DateTimeFormatter.ISO_LOCAL_DATE));
//                        String week = String.valueOf(days.getDayOfWeek());
//                        completion.setWeek(Utils.weekOf(week));
//                        completionList.add(completion);
//                    }
//                    LitePal.saveAll(completionList);

                    //videoHistoryList同步
                    List<VideoHistory> videoHistoryList = new ArrayList<>();
                    for (int i = 0; i < mainHomeModel.getData().getVideoHistoryList().size(); i++) {
                        VideoHistory videoHistory = new VideoHistory();
                        videoHistory.setUserId(mainHomeModel.getData().getVideoHistoryList().get(i).getUserId());
                        videoHistory.setPlayEndTime(mainHomeModel.getData().getVideoHistoryList().get(i).getPlayEndTime().substring(0, 10));
                        videoHistory.setVideoId(mainHomeModel.getData().getVideoHistoryList().get(i).getVideoId());
                        videoHistoryList.add(videoHistory);
                    }
                    LitePal.saveAll(videoHistoryList);

                    //rate同步
                    List<Completion> completionList1 = new ArrayList<>();
                    for (int i = 0; i < mainHomeModel.getData().getAchievementRateList().size(); i++) {
                        Completion completion = new Completion();
                        completion.setGoals(mainHomeModel.getData().getAchievementRateList().get(i).getRate());
                        completion.setWeek(mainHomeModel.getData().getAchievementRateList().get(i).getWeek());
                        completion.setLocalDate(mainHomeModel.getData().getAchievementRateList().get(i).getTrainingDay());
                        completionList1.add(completion);
                    }
                    LitePal.saveAll(completionList1);


                    //增加达成度
                    LocalDate localDate = LocalDate.now();
                    List<Completion> completionList = new ArrayList<>();
                    for (int i = 0; i < 7; i++) {
                        String time = localDate.minus(i, ChronoUnit.DAYS).format(DateTimeFormatter.ISO_LOCAL_DATE);
                        int count = LitePal.where("localDate = ?", time).count(Completion.class);
                        if (count == 0) {
                            Completion completion = new Completion();
                            completion.setGoals(0);
                            LocalDate days = localDate.minus(i, ChronoUnit.DAYS);
                            completion.setLocalDate(days.format(DateTimeFormatter.ISO_LOCAL_DATE));
                            String week = String.valueOf(days.getDayOfWeek());
                            completion.setWeek(Utils.weekOf(week));
                            completionList.add(completion);
                        }
                    }
                    LitePal.saveAll(completionList);


                    //担当者
                    storeResponsibleTherapist(mainHomeModel.getData().getResponsibleTherapist());

                    startActivity(new Intent(LoginActivity.this, MainHomeActivity.class));

                } else {
                    Toast.makeText(mContext, getString(R.string.no_data_from_server), Constants.Toast_Length).show();
                }
            }

            @Override
            public void onFailure(Call<MainHomeModel> call, Throwable t) {
                PLog.e(TAG + "onFailure", t.getMessage());
            }
        });
    }

    private void storeResponsibleTherapist(MainHomeModel.DataBean.ResponsibleTherapistBean therapistBean) throws Exception {
        if (therapistBean.getTherapistId() == 0) {
            return;
        }
        Therapist therapist = new Therapist();
        therapist.setTherapistId(therapistBean.getTherapistId());
        therapist.setLastName(Utils.setBaseAECMsg(therapistBean.getLastName()));
        therapist.setLastNameKana(Utils.setBaseAECMsg(therapistBean.getLastNameKana()));
        therapist.setFirstName(Utils.setBaseAECMsg(therapistBean.getFirstName()));
        therapist.setFirstNameKana(Utils.setBaseAECMsg(therapistBean.getFirstNameKana()));
        therapist.setCompanyPhone(Utils.setBaseAECMsg(therapistBean.getCompanyPhone()));
        therapist.setPhone(Utils.setBaseAECMsg(therapistBean.getPhone()));
        therapist.setLoginName(therapistBean.getLoginName());
        therapist.setSex(therapistBean.getSex());
        therapist.setPhotoPath(therapistBean.getPhotoPath());
        therapist.setCreateTime(therapistBean.getCreateTime());
        therapist.setFlag("responsible");
        therapist.save();
    }


    //新增Video数据库数据
    private void storeVideoData(List<MainHomeModel.DataBean.VideoDtoListBean> videoDtoList) {
        List<Video> videoList = new ArrayList<>();
        List<MainHomeModel.DataBean.VideoDtoListBean> videoDtoListBeanList = new ArrayList<>();
        videoDtoListBeanList.addAll(videoDtoList);
        for (int i = 0; i < videoDtoListBeanList.size(); i++) {
            Video video = new Video();
            video.setVideoId(videoDtoListBeanList.get(i).getId());
            video.setUserVideoHistoryId(videoDtoListBeanList.get(i).getUserVideoHistoryId());
            video.setTitle(videoDtoListBeanList.get(i).getTitle());
            video.setDuration(videoDtoListBeanList.get(i).getDuration());
            video.setThumbnailPath(videoDtoListBeanList.get(i).getThumbnailPath());
            video.setIndividualComment(videoDtoListBeanList.get(i).getIndividualComment());
            video.setVideoCommonComment(videoDtoListBeanList.get(i).getVideoCommonComment());
            video.setSpecifyStartTime(videoDtoListBeanList.get(i).getSpecifyStartTime());
            video.setSpecifyEndTime(videoDtoListBeanList.get(i).getSpecifyEndTime());
            video.setVideoFileName(videoDtoListBeanList.get(i).getVideoFileName());
            videoList.add(video);
        }
        LitePal.saveAll(videoList);
    }

    //新增comments
    private void storeCommentData(String token, int userId) {

        Call<CommentModel> commentModelCall = HttpHelper.getInstance()
                .create(HomeTrainService.class)
                .commentDetail(token, userId);

        commentModelCall.enqueue(new Callback<CommentModel>() {
            @SneakyThrows
            @Override
            public void onResponse(@NotNull Call<CommentModel> call, @NotNull Response<CommentModel> response) {
                if (response.body() != null && response.body().getCode() == 200) {
                    List<CommentModel.DataBean> lists = response.body().getData();
                    List<Comments> commentsList = new ArrayList<>();

                    for (int i = 0; i < lists.size(); i++) {
                        Comments comments = new Comments();
                        comments.setUserId(lists.get(i).getUserId());
                        comments.setTherapistId(lists.get(i).getTherapistId());
                        comments.setRehabilitationComment(TextUtils.isEmpty(lists.get(i).getRehabilitationComment()) ? "" : Utils.setBaseAECMsg(lists.get(i).getRehabilitationComment()));
                        comments.setCreateTime(lists.get(i).getCreateTime());
                        comments.setUpdateTime(lists.get(i).getUpdateTime());
                        comments.setCommentId(lists.get(i).getId());
                        commentsList.add(comments);
                    }
                    LitePal.saveAll(commentsList);
                }
            }

            @Override
            public void onFailure(@NotNull Call<CommentModel> call, @NotNull Throwable t) {

            }
        });
    }

    private void storeCounseling(MainHomeModel.DataBean.ScheduleInfoBean scheduleInfoBean) throws Exception {
        int status = scheduleInfoBean.getStatus();
        RxSPTool.putInt(mContext, Constants.SCHEDULE_STATUS, status);
        if (status == 1) {
            String month = scheduleInfoBean.getInfo().getStartTime().substring(5, 7);
            String date = scheduleInfoBean.getInfo().getStartTime().substring(8, 11);
            String createTime = scheduleInfoBean.getInfo().getStartTime().substring(11, 16);
            String endTime = scheduleInfoBean.getInfo().getEndTime().substring(11, 16);
            String time = createTime + "~" + endTime;

            RxSPTool.putInt(mContext, Constants.SCHEDULE_ID, scheduleInfoBean.getInfo().getId());
            RxSPTool.putString(mContext, Constants.SCHEDULE_STATUS_MONTH, month);
            RxSPTool.putString(mContext, Constants.SCHEDULE_STATUS_DATE, date);
            RxSPTool.putString(mContext, Constants.SCHEDULE_STATUS_TIME, time);

            // therapist
            storeTherapist(scheduleInfoBean.getInfo().getTherapist());
        }
    }

    private void storeTherapist(MainHomeModel.DataBean.ScheduleInfoBean.InfoBean.TherapistBean therapistBean) throws Exception {
        RxSPTool.putInt(mContext, Constants.SCHEDULE_STATUS_THERAPIST_ID, therapistBean.getTherapistId());
        Therapist therapist = new Therapist();
        therapist.setTherapistId(therapistBean.getTherapistId());
        therapist.setLastName(Utils.setBaseAECMsg(therapistBean.getLastName()));
        therapist.setLastNameKana(Utils.setBaseAECMsg(therapistBean.getLastNameKana()));
        therapist.setFirstName(Utils.setBaseAECMsg(therapistBean.getFirstName()));
        therapist.setFirstNameKana(Utils.setBaseAECMsg(therapistBean.getFirstNameKana()));
        therapist.setCompanyPhone(Utils.setBaseAECMsg(therapistBean.getCompanyPhone()));
        therapist.setPhone(Utils.setBaseAECMsg(therapistBean.getPhone()));
        therapist.setLoginName(therapistBean.getLoginName());
        therapist.setSex(therapistBean.getSex());
        therapist.setPhotoPath(therapistBean.getPhotoPath());
        therapist.setCreateTime(therapistBean.getCreateTime());
        therapist.setFlag("temporary");
        therapist.save();
    }

    private void computeCounselingDate() {
        if (RxSPTool.getInt(getApplicationContext(), Constants.SCHEDULE_STATUS) == 1) {
            String month = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_MONTH);
            String date = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_DATE);
            String time = RxSPTool.getString(getApplicationContext(), Constants.SCHEDULE_STATUS_TIME);
            if (TextUtils.isEmpty(month) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                RxSPTool.putInt(getApplicationContext(), Constants.SCHEDULE_STATUS, -1);
                return;
            }
            int year = LocalDateTime.now().getYear();
            String[] counseling_time = time.split("~");
            String[] counseling_start_time = counseling_time[0].split(":");
            int hour = Integer.parseInt(counseling_start_time[0]);
            int minute = Integer.parseInt(counseling_start_time[1]);
            LocalDateTime counselingDatetime = LocalDateTime.of(year, Integer.parseInt(month.trim()), Integer.parseInt(date.trim()), hour, minute);
            if (counselingDatetime.isBefore(LocalDateTime.now())) {
                RxSPTool.putInt(getApplicationContext(), Constants.SCHEDULE_STATUS, -1);
                return;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

}


