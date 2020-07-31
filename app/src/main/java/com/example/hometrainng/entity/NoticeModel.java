package com.example.hometrainng.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;



@NoArgsConstructor
@Data
public class NoticeModel {


    /**
     * code : 200
     * msg : OK
     * data : [{"id":46,"title":"111111","content":"111111111111","url":"111111","invalidFlag":1,"createTime":"2020-04-29T10:27:52"},{"id":41,"title":"7777","content":"7777","url":"7777","invalidFlag":0,"createTime":"2020-04-24T16:27:24","updateTime":"2020-04-24T16:28:25"},{"id":40,"title":"4321","content":"4321","url":"4321","invalidFlag":0,"createTime":"2020-04-24T16:04:09","updateTime":"2020-04-24T16:27:24"},{"id":39,"title":"666","content":"666","url":"666","invalidFlag":0,"createTime":"2020-04-24T15:39:32","updateTime":"2020-04-24T16:04:09"},{"id":37,"title":"1234","content":"12341234","url":"1234","invalidFlag":0,"createTime":"2020-04-22T17:38:31","updateTime":"2020-04-23T14:06:05"},{"id":32,"title":"111","content":"111","url":"111","invalidFlag":0,"createTime":"2020-04-22T14:58:29","updateTime":"2020-04-22T14:59:06"},{"id":31,"title":"222","content":"222","url":"222","invalidFlag":0,"createTime":"2020-04-22T14:45:18","updateTime":"2020-04-22T14:58:29"},{"id":30,"title":"111","content":"1111","url":"11111","invalidFlag":0,"createTime":"2020-04-22T14:42:05","updateTime":"2020-04-22T14:45:18"},{"id":29,"title":"fe","content":"fefe","url":"feef","invalidFlag":0,"createTime":"2020-04-22T14:23:52","updateTime":"2020-04-22T14:42:05"},{"id":28,"title":"fefe","content":"vddvv","url":"www.baidu.com","invalidFlag":0,"createTime":"2020-04-22T11:22:01","updateTime":"2020-04-22T14:23:52"},{"id":27,"title":"2343tgfdg","content":"21312","url":"22222","invalidFlag":0,"createTime":"2020-04-22T09:16:39","updateTime":"2020-04-22T11:22:01"},{"id":26,"title":"2343tgfdg","content":"21312","invalidFlag":0,"createTime":"2020-04-22T09:15:19","updateTime":"2020-04-22T09:16:38"},{"id":25,"title":"2343tgfdg","content":"21312","invalidFlag":0,"createTime":"2020-04-22T09:13:44","updateTime":"2020-04-22T09:15:19"},{"id":24,"title":"string","content":"string","invalidFlag":0,"createTime":"2020-04-22T09:13:19","updateTime":"2020-04-22T09:13:44"},{"id":23,"title":"string","content":"string","invalidFlag":0,"createTime":"2020-04-22T09:12:30","updateTime":"2020-04-22T09:13:19"},{"id":22,"title":"string","content":"string","invalidFlag":0,"createTime":"2020-04-20T14:58:12","updateTime":"2020-04-22T09:11:48"},{"id":21,"title":"v","content":"a","invalidFlag":0,"createTime":"2020-04-20T14:27:40"},{"id":20,"title":"","content":"","invalidFlag":0,"createTime":"2020-04-20T14:27:31"},{"id":19,"title":"ad","content":"badv","invalidFlag":0,"createTime":"2020-04-20T14:25:50"},{"id":18,"title":"vdv","content":"vd","invalidFlag":0,"createTime":"2020-04-20T14:25:40"},{"id":17,"title":"","content":"","invalidFlag":0,"createTime":"2020-04-20T14:12:54"},{"id":16,"title":"csc","content":"csss","invalidFlag":0,"createTime":"2020-04-20T14:04:13"},{"id":15,"title":"发发啊啊啊","content":"发的发射点方法","invalidFlag":0,"createTime":"2020-04-20T11:37:19"},{"id":14,"title":"zcccz","content":"vdvd","invalidFlag":0,"createTime":"2020-04-20T11:05:18"},{"id":13,"title":"vdv","content":"vvvv","invalidFlag":0,"createTime":"2020-04-20T10:53:33"},{"id":12,"title":"fasd","content":"vdsvdsv","invalidFlag":0,"createTime":"2020-04-20T10:53:18"},{"id":11,"title":"afddsf","content":"vdvasd","invalidFlag":0,"createTime":"2020-04-20T10:52:56"},{"id":10,"title":"fqeef","content":"ewfewfef","invalidFlag":0,"createTime":"2020-04-20T10:45:02"},{"id":9,"title":"qqqqq","content":"qqqqqq","invalidFlag":0,"createTime":"2020-04-20T10:33:56"},{"id":8,"title":"222","content":"2222","invalidFlag":0,"createTime":"2020-04-17T18:24:06"},{"id":7,"title":"111","content":"1111","invalidFlag":0,"createTime":"2020-04-17T18:20:26"},{"id":6,"title":"fqeef","content":"feew","invalidFlag":0,"createTime":"2020-04-17T14:38:58"},{"id":5,"title":"fcasdfds","content":"fasdfsadcv","invalidFlag":0,"createTime":"2020-04-17T14:01:48"},{"id":4,"title":"看戏","content":"智取威虎山","invalidFlag":0,"createTime":"2020-04-17T14:01:13"},{"id":1,"title":"通知","content":"详情","url":"111111","invalidFlag":0,"createTime":"2020-04-17T13:34:11","updateTime":"2020-04-22T14:59:38"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * id : 46
         * title : 111111
         * content : 111111111111
         * url : 111111
         * invalidFlag : 1
         * createTime : 2020-04-29T10:27:52
         * updateTime : 2020-04-24T16:28:25
         */

        private int id;
        private String title;
        private String content;
        private String url;
        private int invalidFlag;
        private String createTime;
        private String updateTime;
    }
}
