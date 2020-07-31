package com.example.hometrainng.db.executor;

import android.annotation.SuppressLint;

import com.example.hometrainng.MyApplication;
import com.example.hometrainng.R;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Video;
import com.example.hometrainng.entity.VideoEntity;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoExecutor {

    public void executor(String type, String token, String id) {
        Call<VideoEntity> call = HttpHelper.getInstance()
                .create(HomeTrainService.class)
                .videoDetailById(token, id);
        call.enqueue(new Callback<VideoEntity>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<VideoEntity> call, Response<VideoEntity> response) {
                if (response.body().getData() != null) {
                    if (response.body().getCode() == 200) {
                        if (response.body().getData() == null) {
                            return;
                        }
                        PLog.i("video executor", response.body().getData().toString());
                        if (type.equals("update")) {
                            LitePal.deleteAll(Video.class, "userVideoHistoryId= ?", id);

                            String message = MyApplication.getContext().getResources().getString(R.string.video_update);
                            PLog.w("VideoExecutor,video_update", LocalDateTime.now().toString());
                            EventBus.getDefault().post(new MessageEvent(message, "video_update"));
//                        Toast.makeText(MyApplication.getContext(), MyApplication.getContext().getResources().getString(R.string.video_update), Constants.Toast_Length).show();
                        }

                        List<Video> videoList = new ArrayList<>();
                        for (int i = 0; i < response.body().getData().size(); i++) {
                            Video video = new Video();
                            video.setVideoId(response.body().getData().get(i).getId());
                            video.setTitle(response.body().getData().get(i).getTitle());
                            video.setDuration(response.body().getData().get(i).getDuration());
                            video.setThumbnailPath(response.body().getData().get(i).getThumbnailPath());
                            video.setVideoCommonComment(response.body().getData().get(i).getVideoCommonComment());
                            video.setUserVideoHistoryId(response.body().getData().get(i).getUserVideoHistoryId());
                            video.setIndividualComment(response.body().getData().get(i).getIndividualComment());
                            video.setSpecifyStartTime(response.body().getData().get(i).getSpecifyStartTime());
                            video.setSpecifyEndTime(response.body().getData().get(i).getSpecifyEndTime());
                            video.setVideoFileName(response.body().getData().get(i).getVideoFileName());
                            videoList.add(video);
                        }
                        LitePal.saveAll(videoList);

                    }
                }


            }

            @Override
            public void onFailure(Call<VideoEntity> call, Throwable t) {

            }
        });
    }
}
