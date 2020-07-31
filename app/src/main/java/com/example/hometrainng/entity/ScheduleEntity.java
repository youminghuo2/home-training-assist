package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class ScheduleEntity {
    private int code;
    private String msg;
    private DataBean data;

    @Data
    @NoArgsConstructor
    public static class DataBean {
        private int id;
        private String startTime;
        private String endTime;
        private int therapistId;
        private TherapistBean therapist;

        @NoArgsConstructor
        @Data
        @ToString
        public static class TherapistBean {

            private int therapistId;
            private String lastName;
            private String lastNameKana;
            private String firstName;
            private String firstNameKana;
            private String companyPhone;
            private String phone;
            private String loginName;
            private int sex;
            private String password;
            private String salt;
            private int qualificationId;
            private int roleId;
            private String photoPath;
            private int delFlag;
            private String createTime;
            private String updateTime;
        }
    }
}
