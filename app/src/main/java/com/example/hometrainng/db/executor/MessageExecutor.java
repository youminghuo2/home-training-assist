package com.example.hometrainng.db.executor;

import android.content.ContentValues;

import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Messages;
import com.example.hometrainng.entity.MessageEntity;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageExecutor {

    public void executor(String type, String token, String id) {
        if (type.equals("delete")) {
            LitePal.deleteAll(Messages.class, "messageId=?", String.valueOf(id));

        } else {
            Call<MessageEntity> call = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .messageDetailById(token, id);
            call.enqueue(new Callback<MessageEntity>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<MessageEntity> call, Response<MessageEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        MessageEntity.DataBean data = response.body().getData();
                        PLog.i("message executor", data.toString());
                        if (type.equals("update")) {
                            ContentValues values = new ContentValues();
                            values.put("createTime", data.getUpdateTime());
                            values.put("content",data.getContent().length() > 0 ? Utils.setBaseAECMsg(data.getContent()) : "");
                            values.put("title",data.getTitle().length() > 0 ? Utils.setBaseAECMsg(data.getTitle()) : "");
                            LitePal.updateAll(Messages.class, values, "messageId = ?", String.valueOf(data.getId()));

                        } else if (type.equals("insert")) {
                            int count=LitePal.where("messageId = ?",String.valueOf(data.getId())).count(Messages.class);
                            if (count==0){
                                Messages messages = new Messages();
                                messages.setMessageId(data.getId());
                                messages.setUserId(data.getUserId());
                                messages.setTitle(data.getTitle().length() > 0 ? Utils.setBaseAECMsg(data.getTitle()) : "");
                                messages.setContent(data.getContent().length() > 0 ? Utils.setBaseAECMsg(data.getContent()) : "");
                                messages.setCreateTime(data.getUpdateTime());
                                messages.save();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<MessageEntity> call, Throwable t) {

                }
            });
        }
    }
}
