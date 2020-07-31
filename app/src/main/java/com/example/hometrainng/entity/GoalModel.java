package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.example.hometrainng.entity
 * @Description GoalModel entity
 * @CreateDate: 2020/4/29 10:21 AM
 */
@NoArgsConstructor
@Data
public class GoalModel {

    /**
     * code : 200
     * msg : OK
     * data : [{"id":6,"userId":3,"goal":"目标目标1","invalidFlag":1,"createTime":"2020-04-23 13:34:34","updateTime":"2020-04-23 13:35:36"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * id : 6
         * userId : 3
         * goal : 目标目标1
         * invalidFlag : 1
         * createTime : 2020-04-23 13:34:34
         * updateTime : 2020-04-23 13:35:36
         */

        private int id;
        private int userId;
        private String goal;
        private int invalidFlag;
        private String createTime;
        private String updateTime;
    }
}
