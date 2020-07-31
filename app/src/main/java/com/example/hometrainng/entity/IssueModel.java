package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.example.hometrainng.entity
 * @Description java类作用描述
 * @CreateDate: 2020/4/29 11:59 AM
 */
@NoArgsConstructor
@Data
public class IssueModel {


    /**
     * code : 200
     * msg : OK
     * data : [{"id":10,"userId":3,"issue":"课题课题1","invalidFlag":1,"createTime":"2020-04-23 13:34:34","updateTime":"2020-04-23 13:35:36"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * id : 10
         * userId : 3
         * issue : 课题课题1
         * invalidFlag : 1
         * createTime : 2020-04-23 13:34:34
         * updateTime : 2020-04-23 13:35:36
         */

        private int id;
        private int userId;
        private String issue;
        private int invalidFlag;
        private String createTime;
        private String updateTime;
    }
}
