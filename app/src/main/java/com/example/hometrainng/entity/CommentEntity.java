package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentEntity {
    private int code;
    private String msg;
    private DataBean data;

    @Data
    @NoArgsConstructor
    public static class DataBean {
        private int id;
        private int userId;
        private int therapistId;
        private String rehabilitationComment;
        private String createTime;
        private String updateTime;
    }
}
