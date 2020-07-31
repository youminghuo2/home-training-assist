package com.example.hometrainng.db.executor;

import android.content.ContentValues;

import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Comments;
import com.example.hometrainng.db.Issues;
import com.example.hometrainng.entity.CommentEntity;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentExecutor {

    public void executor(String type, String token, String id) {
        if (type.equals("delete")) {
            LitePal.deleteAll(Comments.class, "commentId=?", id);
        } else {
            Call<CommentEntity> call = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .commentDetailById(token, id);
            call.enqueue(new Callback<CommentEntity>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<CommentEntity> call, Response<CommentEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        CommentEntity.DataBean data = response.body().getData();
                        PLog.i("comment executor", data.toString());
                        if (type.equals("update")) {
                            ContentValues values = new ContentValues();
                            values.put("updateTime", data.getUpdateTime());
                            values.put("rehabilitationComment", data.getRehabilitationComment().length() > 0 ? Utils.setBaseAECMsg(data.getRehabilitationComment()) : "");
                            LitePal.updateAll(Comments.class, values, "commentId = ?", String.valueOf(data.getId()));
                        } else if (type.equals("insert")) {
                            PLog.i("comment", "插入了comment：" + data.getId());
                            int count=LitePal.where("commentId = ?",String.valueOf(data.getId())).count(Comments.class);
                            if (count==0){
                                Comments comments = new Comments();
                                comments.setTherapistId(data.getTherapistId());
                                comments.setUserId(data.getUserId());
                                comments.setCommentId(data.getId());
                                comments.setCreateTime(data.getCreateTime());
                                comments.setUpdateTime(data.getUpdateTime());
                                comments.setRehabilitationComment(data.getRehabilitationComment().length() > 0 ? Utils.setBaseAECMsg(data.getRehabilitationComment()) : "");
                                comments.save();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<CommentEntity> call, Throwable t) {

                }
            });
        }
    }
}
