package com.example.hometrainng.db.executor;

import android.content.ContentValues;

import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Notice;
import com.example.hometrainng.entity.GoalEntity;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoalExecutor {

    public void executor(String type, String token, String id) {
        if (type.equals("delete")) {
            LitePal.deleteAll(Goals.class, "goalId=?", id);
        } else {
            Call<GoalEntity> call = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .goalDetailById(token, id);
            call.enqueue(new Callback<GoalEntity>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<GoalEntity> call, Response<GoalEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        GoalEntity.DataBean data = response.body().getData();
                        PLog.i("goal executor", data.toString());
                        if (type.equals("update")) {
                            ContentValues values = new ContentValues();
                            values.put("createTime", data.getUpdateTime());
                            values.put("goal",data.getGoal().length() > 0 ? Utils.setBaseAECMsg(data.getGoal()) : "");
                            LitePal.updateAll(Goals.class, values, "goalId = ?", String.valueOf(data.getId()));
                        } else if (type.equals("insert")) {
                            int count=LitePal.where("goalId = ?",String.valueOf(data.getId())).count(Goals.class);
                            if (count==0){
                                Goals goals = new Goals();
                                goals.setCreateTime(data.getUpdateTime());
                                goals.setGoal(data.getGoal().length() > 0 ? Utils.setBaseAECMsg(data.getGoal()) : "");
                                goals.setGoalId(data.getId());
                                goals.setUserId(data.getUserId());
                                goals.save();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<GoalEntity> call, Throwable t) {

                }
            });

        }
    }
}
