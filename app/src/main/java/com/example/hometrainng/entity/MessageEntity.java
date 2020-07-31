package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MessageEntity {
    private int code;
    private String msg;
    private DataBean data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        private int id;
        private int userId;
        private String title;
        private String content;
        private String createTime;
        private String updateTime;
    }
}
