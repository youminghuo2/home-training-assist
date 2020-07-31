package com.example.hometrainng.db.executor;


import com.example.hometrainng.MyApplication;
import com.example.hometrainng.api.HomeTrainService;
import com.example.hometrainng.db.Therapist;
import com.example.hometrainng.entity.ScheduleEntity;
import com.example.hometrainng.events.MessageEvent;
import com.example.hometrainng.retrofit.Constants;
import com.example.hometrainng.retrofit.HttpHelper;
import com.example.hometrainng.tools.PLog;
import com.example.hometrainng.tools.Utils;
import com.tamsiree.rxkit.RxSPTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.litepal.LitePal;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleExecutor {
    public void executor(String type, String token, String id) {
        PLog.w("ScheduleExecutor", type + "," + id);
        int scheduleId = RxSPTool.getInt(MyApplication.getContext(), Constants.SCHEDULE_ID);
        if (type.equals("finish") || type.equals("cancelConfirm") || type.equals("delete")) {
            if(!type.equals("delete") && Integer.parseInt(id) != scheduleId){
                return;
            }
            RxSPTool.putInt(MyApplication.getContext(), Constants.SCHEDULE_STATUS, -1);
            RxSPTool.remove(MyApplication.getContext(), Constants.SCHEDULE_ID);
            RxSPTool.remove(MyApplication.getContext(), Constants.SCHEDULE_STATUS_MONTH);
            RxSPTool.remove(MyApplication.getContext(), Constants.SCHEDULE_STATUS_DATE);
            RxSPTool.remove(MyApplication.getContext(), Constants.SCHEDULE_STATUS_TIME);
            if (type.equals("cancelConfirm")) {
                EventBus.getDefault().post(new MessageEvent("schedule_cancel_confirm", "schedule"));
            }
            if (type.equals("delete")) {
                EventBus.getDefault().post(new MessageEvent("schedule_web_delete", "schedule"));
            }
        } else {
            String lastMonth = RxSPTool.getString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_MONTH).trim();
            String lastDate = RxSPTool.getString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_DATE).trim();
            String lastTimes = RxSPTool.getString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_TIME);
            String lastDateTime = Utils.counselingDate(lastMonth, lastDate, lastTimes);
            Call<ScheduleEntity> call = HttpHelper.getInstance()
                    .create(HomeTrainService.class)
                    .scheduleDetailById(token, id);
            call.enqueue(new Callback<ScheduleEntity>() {
                @SneakyThrows
                @Override
                public void onResponse(Call<ScheduleEntity> call, Response<ScheduleEntity> response) {
                    if (response.body().getData() != null && response.body().getCode() == 200) {
                        ScheduleEntity.DataBean data = response.body().getData();
                        PLog.i("schedule executor", data.toString());
                        if(type.equals("update") && Integer.parseInt(id) != scheduleId){
                            return;
                        }
                        RxSPTool.putInt(MyApplication.getContext(), Constants.SCHEDULE_ID, data.getId());
                        String month = data.getStartTime().substring(5, 7);
                        String date = data.getStartTime().substring(8, 11);
                        String createTime = data.getStartTime().substring(11, 16);
                        String endTime = data.getEndTime().substring(11, 16);
                        String time = createTime + "~" + endTime;
                        RxSPTool.putString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_MONTH, month);
                        RxSPTool.putString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_DATE, date);
                        RxSPTool.putString(MyApplication.getContext(), Constants.SCHEDULE_STATUS_TIME, time);
                        RxSPTool.putInt(MyApplication.getContext(), Constants.SCHEDULE_STATUS, 1);
                        String newDateTime = Utils.counselingDate(month, date, time);

                        LitePal.deleteAll(Therapist.class, "therapistId = ?", String.valueOf(data.getTherapistId()));

                        RxSPTool.putInt(MyApplication.getContext(), Constants.SCHEDULE_STATUS_THERAPIST_ID, data.getTherapistId());
                        Therapist therapist = new Therapist();
                        therapist.setTherapistId(data.getTherapist().getTherapistId());
                        therapist.setLastName(Utils.setBaseAECMsg(data.getTherapist().getLastName()));
                        therapist.setLastNameKana(Utils.setBaseAECMsg(data.getTherapist().getLastNameKana()));
                        therapist.setFirstName(Utils.setBaseAECMsg(data.getTherapist().getFirstName()));
                        therapist.setFirstNameKana(Utils.setBaseAECMsg(data.getTherapist().getFirstNameKana()));
                        therapist.setCompanyPhone(Utils.setBaseAECMsg(data.getTherapist().getCompanyPhone()));
                        therapist.setPhone(Utils.setBaseAECMsg(data.getTherapist().getPhone()));
                        therapist.setLoginName(data.getTherapist().getLoginName());
                        therapist.setSex(data.getTherapist().getSex());
                        therapist.setPhotoPath(data.getTherapist().getPhotoPath());
                        therapist.setCreateTime(data.getTherapist().getCreateTime());
                        therapist.setFlag("temporary");
                        therapist.save();

                        if (type.equals("approve")) {
                            EventBus.getDefault().post(new MessageEvent("schedule_approve", "schedule"));
                        } else {
                            JSONObject dateTime = new JSONObject();

                            if (lastMonth.length() == 0) {
                                dateTime.put("lastDateTime", "");
                            } else {
                                dateTime.put("lastDateTime", lastDateTime);
                            }
                            dateTime.put("newDateTime", newDateTime);
                            EventBus.getDefault().post(new MessageEvent("schedule_update", "schedule", dateTime));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ScheduleEntity> call, Throwable t) {

                }
            });
        }
    }
}
