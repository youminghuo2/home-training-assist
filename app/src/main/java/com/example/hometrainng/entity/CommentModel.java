package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentModel {
    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        private int id;
        private int userId;
        private int therapistId;
        private String rehabilitationComment;
        private String createTime;
        private String updateTime;
    }

}
