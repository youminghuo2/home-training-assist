package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MessageModel {

    /**
     * code : 200
     * msg : OK
     * data : [{"id":13,"userId":3,"title":"饮食tip","content":"禁止辛辣饮食","invalidFlag":1,"createTime":"2020-04-26 09:20:45"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * id : 13
         * userId : 3
         * title : 饮食tip
         * content : 禁止辛辣饮食
         * invalidFlag : 1
         * createTime : 2020-04-26 09:20:45
         */

        private int id;
        private int userId;
        private String title;
        private String content;
        private int invalidFlag;
        private String createTime;
        private String updateTime;
    }
}
