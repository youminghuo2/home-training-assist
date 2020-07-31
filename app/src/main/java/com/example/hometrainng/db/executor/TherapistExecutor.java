package com.example.hometrainng.db.executor;


import android.content.ContentValues;

import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.entity.TherapistBean;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;

import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TherapistExecutor {
    public void executor(String type, String token, String id) {
        PLog.d("TherapistExecutor", "TherapistExecutor=" + type + "," + id);
        if (type.equals("delete")) {
            LitePal.deleteAll(Therapist.class, "therapistId=?", id);
        } else {
            Call<TherapistBean> call = HttpHelper.getInstance().create(HomeTrainService.class).therapistDetailById(token, id);
            call.enqueue(new Callback<TherapistBean>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<TherapistBean> call, Response<TherapistBean> response) {
                    if (response.body().getData() != null) {
                        if (response.body().getCode() == 200) {
                            if (response.body().getData() == null) {
                                return;
                            }
                            PLog.i("therapist executor", response.body().getData().toString());
                            if (type.equals("update")) {
                                ContentValues values = new ContentValues();
                                values.put("lastName", Utils.setBaseAECMsg(response.body().getData().getLastName()));
                                values.put("lastNameKana", Utils.setBaseAECMsg(response.body().getData().getLastNameKana()));
                                values.put("firstName", Utils.setBaseAECMsg(response.body().getData().getFirstName()));
                                values.put("firstNameKana", Utils.setBaseAECMsg(response.body().getData().getFirstNameKana()));
                                values.put("photoPath", response.body().getData().getPhotoPath());
                                LitePal.updateAll(Therapist.class, values, "therapistId = ?", String.valueOf(response.body().getData().getTherapistId()));
                            } else if (type.equals("insert")) {
                                Therapist therapist = new Therapist();
                                therapist.setTherapistId(response.body().getData().getTherapistId());
                                therapist.setLastName(Utils.setBaseAECMsg(response.body().getData().getLastName()));
                                therapist.setLastNameKana(Utils.setBaseAECMsg(response.body().getData().getLastNameKana()));
                                therapist.setFirstName(Utils.setBaseAECMsg(response.body().getData().getFirstName()));
                                therapist.setFirstNameKana(Utils.setBaseAECMsg(response.body().getData().getFirstNameKana()));
                                therapist.setCompanyPhone(response.body().getData().getCompanyPhone());
                                therapist.setPhone(response.body().getData().getPhone());
                                therapist.setLoginName(response.body().getData().getLoginName());
                                therapist.setSex(response.body().getData().getSex());
                                therapist.setPhotoPath(response.body().getData().getPhotoPath());
                                therapist.setCreateTime(response.body().getData().getCreateTime());
                                therapist.setFlag("responsible");
                                therapist.save();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<TherapistBean> call, Throwable t) {

                }
            });
        }
    }
}
