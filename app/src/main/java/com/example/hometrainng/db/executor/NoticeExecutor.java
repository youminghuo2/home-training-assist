package com.example.hometrainng.db.executor;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.widget.Toast;

import com.example.hometrainng.MyApplication;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Notice;
import com.example.hometrainng.entity.NoticeEntity;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;

import org.litepal.LitePal;

import lombok.Data;
import lombok.NoArgsConstructor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@NoArgsConstructor
public class NoticeExecutor {

    public void deleteFromSqlite(String id) {
        LitePal.deleteAll(Notice.class, "noticeId=?", id);
    }

    public void executor(String type, String token, String id) {

        PLog.d("NoticeExecutor", "executor=" + id + "," + type);

        if (type.equals("delete")) {
            deleteFromSqlite(id);
        } else {
            Call<NoticeEntity> noticeModelCall = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .noticeDetailById(token, String.valueOf(id));
            noticeModelCall.enqueue(new Callback<NoticeEntity>() {
                @SuppressLint("WrongConstant")
                @Override
                public void onResponse(Call<NoticeEntity> call, Response<NoticeEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        NoticeEntity.DataBean data = response.body().getData();
                        PLog.i("notice executor", data.toString());
                        if (type.equals("update")) {
                            ContentValues values = new ContentValues();
                            values.put("createTime", data.getUpdateTime());
                            values.put("content", data.getContent());
                            values.put("title", data.getTitle());
                            values.put("url", data.getUrl());
                            LitePal.updateAll(Notice.class, values, "noticeId = ?", String.valueOf(data.getId()));
                        } else if (type.equals("insert")) {
                            int count=LitePal.where("noticeId = ?",String.valueOf(data.getId())).count(Notice.class);
                            if (count==0){
                                Notice notice = new Notice();
                                notice.setUrl(data.getUrl());
                                notice.setTitle(data.getTitle());
                                notice.setNoticeId(Integer.parseInt(id));
                                notice.setContent(data.getContent());
                                notice.setCreateTime(data.getUpdateTime());
                                notice.save();
                            }
                        }

                    } else {
                        if (response.body().getMsg() != null) {
                            Toast.makeText(MyApplication.getContext(), response.body().getMsg(), Constants.Toast_Length).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<NoticeEntity> call, Throwable t) {

                }
            });
        }
    }
}
