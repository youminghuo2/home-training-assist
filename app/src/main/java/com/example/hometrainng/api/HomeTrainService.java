package com.example.hometrainng.api;

import com.example.hometrainng.entity.CommentEntity;
import com.example.hometrainng.entity.ConfirmBooKBean;
import com.example.hometrainng.entity.CommentModel;
import com.example.hometrainng.entity.CounselingSchedule;
import com.example.hometrainng.entity.GoalEntity;
import com.example.hometrainng.entity.IssueEntity;
import com.example.hometrainng.entity.MessageEntity;
import com.example.hometrainng.entity.MessageModel;
import com.example.hometrainng.entity.MsgModel;
import com.example.hometrainng.entity.NoticeEntity;
import com.example.hometrainng.entity.NoticeModel;
import com.example.hometrainng.entity.GoalModel;
import com.example.hometrainng.entity.IssueModel;
import com.example.hometrainng.entity.LoginModel;
import com.example.hometrainng.entity.MainHomeModel;
import com.example.hometrainng.entity.ScheduleEntity;
import com.example.hometrainng.entity.Therapist;
import com.example.hometrainng.entity.TherapistBean;
import com.example.hometrainng.entity.TherapistSchedule;
import com.example.hometrainng.entity.VideoEntity;

import java.util.Calendar;

import okhttp3.RequestBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @Package com.example.hometrainng.api
 * @Description HomeTrainService
 * @CreateDate: 2020/4/9 11:42
 */
public interface HomeTrainService {

    //登录
    @POST("app/login")
    Call<LoginModel> login(
            @Query("username") String username
    );


    //主页信息
    @POST("rehabilitationUser/selectAppHomeInfo")
    Call<MainHomeModel> mainHome(
            @Header("Authorization") String Authentication
    );

    //目标详情
    @POST("rehabilitationGoalHistory/list")
    Call<GoalModel> goalDetail(
            @Header("Authorization") String Authentication,
            @Query("userId") Integer userId

    );

    //课题详情
    @POST("rehabilitationIssueHistory/list")
    Call<IssueModel> issueDetail(
            @Header("Authorization") String Authentication,
            @Query("userId") Integer userId
    );

    //通知履历详情
    @POST("noticeHistory/list")
    Call<NoticeModel> noticeDetail(
            @Header("Authorization") String Authentication
    );

    //通知详情
    @POST("noticeHistory/info/{id}")
    Call<NoticeEntity> noticeDetailById(@Header("Authorization") String Authentication, @Path("id") String id);

    @POST("noticeHistory/delete/{id}")
    Call<MsgModel> noticeDeleteById(@Header("Authorization") String Authentication, @Path("id") String id);

    //疗法师履历详情
    @POST("messageFromTherapistHistory/list")
    Call<MessageModel> messageDetail(
            @Header("Authorization") String Authentication,
            @Query("userId") int userId
    );

    //rehabilitationCommentHistory
    @POST("rehabilitationCommentHistory/list")
    Call<CommentModel> commentDetail(
            @Header("Authorization") String Authentication,
            @Query("userId") int userId
    );

    //更新视频记录
    @POST("videoHistory/insert")
    Call<MsgModel> videoInsert(
            @Header("Authorization") String Authentication,
            @Body RequestBody requestBody
    );

    //更新百分比进度
    @POST("achievementRate/update")
    Call<MsgModel> updateRate(
            @Header("Authorization") String Authentication,
            @Body RequestBody requestBody
    );

    @POST("counselingSchedule/appScheduleInfo/{userId}")
    Call<CounselingSchedule> getCounselingSchedule(@Header("Authorization") String Authentication, @Path("userId") String userId);

    @POST("therapistSchedule/list")
    Call<TherapistSchedule> getScheduleList(
            @Header("Authorization") String Authentication,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @POST("counselingBook/book")
    Call<LoginModel> CounselingBook(
            @Header("Authorization") String Authentication,
            @Body ConfirmBooKBean Body
    );

    @POST("counselingSchedule/theme/{id}")
    Call<MsgModel> updateTheme(@Header("Authorization") String Authentication, @Path("id") String id, @Body RequestBody requestBody);

    @POST("counselingSchedule/cancel/{id}")
    Call<MsgModel> cancelCounseling(@Header("Authorization") String Authentication, @Path("id") String id);

    @POST("rehabilitationCommentHistory/info/{id}")
    Call<CommentEntity> commentDetailById(@Header("Authorization") String Authentication,
                                          @Path("id") String id);

    @POST("rehabilitationGoalHistory/info/{id}")
    Call<GoalEntity> goalDetailById(@Header("Authorization") String Authentication,
                                    @Path("id") String id);

    @POST("rehabilitationIssueHistory/info/{id}")
    Call<IssueEntity> issueDetailById(@Header("Authorization") String Authentication,
                                      @Path("id") String id);

    @POST("messageFromTherapistHistory/info/{id}")
    Call<MessageEntity> messageDetailById(@Header("Authorization") String Authentication,
                                          @Path("id") String id);

    @POST("counselingSchedule/info/{id}")
    Call<ScheduleEntity> scheduleDetailById(@Header("Authorization") String Authentication,
                                            @Path("id") String id);

    @POST("userVideo/info/{id}")
    Call<VideoEntity> videoDetailById(@Header("Authorization") String Authentication,
                                      @Path("id") String id);

    @POST("therapist/info/{id}")
    Call<TherapistBean> therapistDetailById(@Header("Authorization") String Authentication,
                                            @Path("id") String id);


    //新增训练录像记录
    @POST("recording/add")
    Call<MsgModel> recordingAdd(
            @Header("Authorization") String Authentication,
            @Body RequestBody requestBody
    );

    //删除训练记录
    @POST("recording/delete")
    Call<MsgModel>deleteRecordVideo(
            @Header("Authorization") String Authentication,
            @Query("recordingIds") String recordingId
    );

}