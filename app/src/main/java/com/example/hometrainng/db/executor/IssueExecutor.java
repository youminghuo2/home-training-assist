package com.example.hometrainng.db.executor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.widget.Toast;

import com.example.hometrainng.MyApplication;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Goals;
import com.example.hometrainng.db.Issues;
import com.example.hometrainng.entity.IssueEntity;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueExecutor {

    public void executor(String type, String token, String id) {
        if (type.equals("delete")) {
            LitePal.deleteAll(Issues.class, "issueId=?", id);

        } else {
            Call<IssueEntity> call = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .issueDetailById(token, id);
            call.enqueue(new Callback<IssueEntity>() {
                @SuppressLint("WrongConstant")
                @SneakyThrows
                @Override
                public void onResponse(Call<IssueEntity> call, Response<IssueEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        IssueEntity.DataBean data = response.body().getData();
                        PLog.i("issue executor", data.toString());
                        if (type.equals("update")) {
                            ContentValues values = new ContentValues();
                            values.put("createTime", data.getUpdateTime());
                            values.put("issue",data.getIssue().length() > 0 ? Utils.setBaseAECMsg(data.getIssue()) : "");
                            LitePal.updateAll(Issues.class, values, "issueId = ?", String.valueOf(data.getId()));
                        } else if (type.equals("insert")) {
                            int count=LitePal.where("issueId = ?",String.valueOf(data.getId())).count(Issues.class);
                            if (count ==0){
                                Issues issues = new Issues();
                                issues.setUserId(data.getUserId());
                                issues.setIssueId(data.getId());
                                issues.setIssue(data.getIssue().length() > 0 ? Utils.setBaseAECMsg(data.getIssue()) : "");
                                issues.setCreateTime(data.getUpdateTime());
                                issues.save();
                            }
                        }


                    } else {
                        if (response.body().getMsg() != null) {
                            Toast.makeText(MyApplication.getContext(), response.body().getMsg(), Constants.Toast_Length).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<IssueEntity> call, Throwable t) {

                }
            });
        }
    }
}
