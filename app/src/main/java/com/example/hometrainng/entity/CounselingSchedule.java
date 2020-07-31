package com.example.hometrainng.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CounselingSchedule {
    private int code;
    private String msg;
    private DataBean data;

    @Data
    @NoArgsConstructor
    public static class DataBean {
        private int status;
        private ScheduleInfo info;
    }

    @Data
    @NoArgsConstructor
    public static class ScheduleInfo {
        private int id;
        private String startTime;
        private String endTime;
        private int therapistId;
        private Therapist therapist;
    }
}
