package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TherapistSchedule {

    /**
     * code : 200
     * msg : OK
     * data : [{ "id": 399,"therapistId": 39,"scheduleTime": "2020-05-08 15:30:00~2020-05-08 16:00:00","repeatBookNum": 0}]
     */
    private int code;
    private String msg;
    private List<TherapistSchedule.DataBean> data;

//    public int getCode() {
//        return code;
//    }
//
//    public String getMsg() {
//        return msg;
//    }
//
//    public List<DataBean> getData() {
//        return data;
//    }

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * "id": 399,
         * "therapistId": 39,
         * "scheduleTime": "2020-05-08 15:30:00~2020-05-08 16:00:00",
         * "repeatBookNum": 0
         */

        private int id;
        private int therapistId;
        private String scheduleTime;
        private int repeatBookNum;
        private String therapistFirstName;
        private String therapistLastName;

    }
}
