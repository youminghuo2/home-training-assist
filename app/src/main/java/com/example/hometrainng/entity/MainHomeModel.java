package com.example.hometrainng.entity;


import com.example.hometrainng.db.VideoHistory;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.example.hometrainng.entity
 * @Description java类作用描述
 * @CreateDate: 2020/4/28 3:14 PM
 */


@NoArgsConstructor
@Data
public class MainHomeModel {


    /**
     * code : 200
     * msg : OK
     * data : {"userId":3,"noticeHistory":{"id":163,"title":"dddd","content":"bbbb","invalidFlag":1,"createTime":"2020-05-12 11:43:42"},"messageFromTherapistHistory":{"id":13,"userId":3,"title":"YwHl8M3vWqBn8nXxQzr3Gg==","content":"MSxJXcYqBYlt1cFxZV4Q6Q==","invalidFlag":1,"createTime":"2020-04-26 09:20:45","updateTime":"2020-05-12 15:41:14"},"rehabilitationGoalHistory":{"id":1117,"userId":3,"goal":"2yv9AoH3hqNPmT+YyL0qcfDHNNqfsDg4hQir7oLY5hU=","invalidFlag":1,"createTime":"2020-05-12 12:00:01","updateTime":"2020-05-12 15:41:14"},"rehabilitationIssueHistory":{"id":1115,"userId":3,"issue":"iuR0jLXyt+DxE2ts6lSOOL4smOTUefeZ9k7I5yPaNAM=","invalidFlag":1,"createTime":"2020-05-12 11:52:04","updateTime":"2020-05-12 15:41:14"},"scheduleInfo":{"status":1,"info":{"id":61,"startTime":"2020-05-15 09:00:00","endTime":"2020-05-15 09:30:00","therapistId":8718,"therapist":{"therapistId":8718,"lastName":"jOiumczSMAygcFa1YAdb0Q==","lastNameKana":"Vlt3AqPGfvtV0KOpNNHFbw==","firstName":"cwc1jRZLynC2AuPamNntlg==","firstNameKana":"S0O8YPtMCucIXd2r6ACOTQ==","companyPhone":"s5EAuZC/ggRJ/uFI+Kudyw==","phone":"s5EAuZC/ggRJ/uFI+Kudyw==","loginName":"neko1","sex":2,"password":"$2a$10$vajFYb.3lbChkEu0SAy.DOQWRzMwBzRYS7qo7Yh6KVQUoSuCw4Duq","salt":"$2a$10$vajFYb.3lbChkEu0SAy.DO","qualificationId":3,"roleId":1,"photoPath":"default.jpg","delFlag":1,"createTime":"2020-05-12 10:14:30","updateTime":"2020-05-12 17:13:16"}}},"cumulativeDays":1,"rateList":[],"videoDtoList":[{"id":6,"title":"ステップ位での前後体重移動_左","videoCategoryId":7,"duration":"00:05:43","thumbnailPath":"5.png","videoCommonComment":"23423432","videoCategoryDto":{"id":7,"typeId":1,"typeName":"身体","name":"脚"},"individualComment":"234324234","specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"0047_ステップ位での前後体重移動_左_15n_15_0316.477p.mpeg4.aac.mp4"},{"id":13,"title":"ま行_語頭","videoCategoryId":9,"duration":"00:02:38","thumbnailPath":"11.png","videoCommonComment":"注意动作规范","videoCategoryDto":{"id":9,"typeId":1,"typeName":"身体","name":"骨盤"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L42_ま行_語頭.477p.mpeg4.aac.mp4"},{"id":12,"title":"こ_語中","videoCategoryId":26,"duration":"00:02:20","thumbnailPath":"10.png","videoCategoryDto":{"id":26,"typeId":2,"typeName":"構音","name":"こ"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L11_こ_語中.477p.mpeg4.aac.mp4"},{"id":11,"title":"あ行_語中","videoCategoryId":3,"duration":"00:02:22","thumbnailPath":"9.png","videoCategoryDto":{"id":3,"typeId":2,"typeName":"構音","name":"あ"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L06_あ行_語中.477p.mpeg4.aac.mp4"}],"responsibleTherapist":{"therapistId":2,"lastName":"NtMXd1sMkieLgIA5SmG8qw==","lastNameKana":"BamwAcyz04Ln7AUznJS6oA==","firstName":"CSdWFoRxvEeg4XnIu+wihg==","firstNameKana":"RUckh0rDkWXyDXP3sj+Ueg==","companyPhone":"a7JnF4LHH2qxl0FUym8fdA==","phone":"17315526431","loginName":"ymh666","sex":2,"password":"$2a$10$HYt2lU6VUF5GB0tnUHUzuOY.QVmcD5mfbnr7HfMBzLtEuNWBTjNoq","salt":"$2a$10$HYt2lU6VUF5GB0tnUHUzuO","qualificationId":2,"roleId":2,"photoPath":"default.jpg","delFlag":1},"ip":"47.94.175.111:8001","connectionKey":"123456789","cryptoMap":{"iv":"2624750004598718","key":"751f621ea5c8f930"}}
     */

    private int code;
    private String msg;
    private DataBean data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * userId : 3
         * noticeHistory : {"id":163,"title":"dddd","content":"bbbb","invalidFlag":1,"createTime":"2020-05-12 11:43:42"}
         * messageFromTherapistHistory : {"id":13,"userId":3,"title":"YwHl8M3vWqBn8nXxQzr3Gg==","content":"MSxJXcYqBYlt1cFxZV4Q6Q==","invalidFlag":1,"createTime":"2020-04-26 09:20:45","updateTime":"2020-05-12 15:41:14"}
         * rehabilitationGoalHistory : {"id":1117,"userId":3,"goal":"2yv9AoH3hqNPmT+YyL0qcfDHNNqfsDg4hQir7oLY5hU=","invalidFlag":1,"createTime":"2020-05-12 12:00:01","updateTime":"2020-05-12 15:41:14"}
         * rehabilitationIssueHistory : {"id":1115,"userId":3,"issue":"iuR0jLXyt+DxE2ts6lSOOL4smOTUefeZ9k7I5yPaNAM=","invalidFlag":1,"createTime":"2020-05-12 11:52:04","updateTime":"2020-05-12 15:41:14"}
         * scheduleInfo : {"status":1,"info":{"id":61,"startTime":"2020-05-15 09:00:00","endTime":"2020-05-15 09:30:00","therapistId":8718,"therapist":{"therapistId":8718,"lastName":"jOiumczSMAygcFa1YAdb0Q==","lastNameKana":"Vlt3AqPGfvtV0KOpNNHFbw==","firstName":"cwc1jRZLynC2AuPamNntlg==","firstNameKana":"S0O8YPtMCucIXd2r6ACOTQ==","companyPhone":"s5EAuZC/ggRJ/uFI+Kudyw==","phone":"s5EAuZC/ggRJ/uFI+Kudyw==","loginName":"neko1","sex":2,"password":"$2a$10$vajFYb.3lbChkEu0SAy.DOQWRzMwBzRYS7qo7Yh6KVQUoSuCw4Duq","salt":"$2a$10$vajFYb.3lbChkEu0SAy.DO","qualificationId":3,"roleId":1,"photoPath":"default.jpg","delFlag":1,"createTime":"2020-05-12 10:14:30","updateTime":"2020-05-12 17:13:16"}}}
         * cumulativeDays : 1
         * rateList : []
         * videoDtoList : [{"id":6,"title":"ステップ位での前後体重移動_左","videoCategoryId":7,"duration":"00:05:43","thumbnailPath":"5.png","videoCommonComment":"23423432","videoCategoryDto":{"id":7,"typeId":1,"typeName":"身体","name":"脚"},"individualComment":"234324234","specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"0047_ステップ位での前後体重移動_左_15n_15_0316.477p.mpeg4.aac.mp4"},{"id":13,"title":"ま行_語頭","videoCategoryId":9,"duration":"00:02:38","thumbnailPath":"11.png","videoCommonComment":"注意动作规范","videoCategoryDto":{"id":9,"typeId":1,"typeName":"身体","name":"骨盤"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L42_ま行_語頭.477p.mpeg4.aac.mp4"},{"id":12,"title":"こ_語中","videoCategoryId":26,"duration":"00:02:20","thumbnailPath":"10.png","videoCategoryDto":{"id":26,"typeId":2,"typeName":"構音","name":"こ"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L11_こ_語中.477p.mpeg4.aac.mp4"},{"id":11,"title":"あ行_語中","videoCategoryId":3,"duration":"00:02:22","thumbnailPath":"9.png","videoCategoryDto":{"id":3,"typeId":2,"typeName":"構音","name":"あ"},"specifyStartTime":"2020-05-01","specifyEndTime":"2020-06-17","userVideoHistoryId":62,"watched":"no","videoFileName":"L06_あ行_語中.477p.mpeg4.aac.mp4"}]
         * responsibleTherapist : {"therapistId":2,"lastName":"NtMXd1sMkieLgIA5SmG8qw==","lastNameKana":"BamwAcyz04Ln7AUznJS6oA==","firstName":"CSdWFoRxvEeg4XnIu+wihg==","firstNameKana":"RUckh0rDkWXyDXP3sj+Ueg==","companyPhone":"a7JnF4LHH2qxl0FUym8fdA==","phone":"17315526431","loginName":"ymh666","sex":2,"password":"$2a$10$HYt2lU6VUF5GB0tnUHUzuOY.QVmcD5mfbnr7HfMBzLtEuNWBTjNoq","salt":"$2a$10$HYt2lU6VUF5GB0tnUHUzuO","qualificationId":2,"roleId":2,"photoPath":"default.jpg","delFlag":1}
         * ip : 47.94.175.111:8001
         * connectionKey : 123456789
         * cryptoMap : {"iv":"2624750004598718","key":"751f621ea5c8f930"}
         */

        private int userId;
        private NoticeHistoryBean noticeHistory;
        private MessageFromTherapistHistoryBean messageFromTherapistHistory;
        private RehabilitationGoalHistoryBean rehabilitationGoalHistory;
        private RehabilitationIssueHistoryBean rehabilitationIssueHistory;
        private ScheduleInfoBean scheduleInfo;
        private int cumulativeDays;
        private ResponsibleTherapistBean responsibleTherapist;
        private String ip;
        private String connectionKey;
        private CryptoMapBean cryptoMap;
        private List<?> rateList;
        private List<VideoDtoListBean> videoDtoList;
        private List<AchieveMentRateBean> achievementRateList;
        private List<VideoHistory> videoHistoryList;
        private String loginDate;



        @NoArgsConstructor
        @Data
        public static class AchieveMentRateBean {
            private int id;
            private int userId;
            private String trainingDay;
            private int rate;
            private String week;
        }

        @NoArgsConstructor
        @Data
        public static class NoticeHistoryBean {
            /**
             * id : 163
             * title : dddd
             * content : bbbb
             * invalidFlag : 1
             * createTime : 2020-05-12 11:43:42
             */

            private int id;
            private String title;
            private String content;
            private int invalidFlag;
            private String createTime;
        }

        @NoArgsConstructor
        @Data
        public static class MessageFromTherapistHistoryBean {
            /**
             * id : 13
             * userId : 3
             * title : YwHl8M3vWqBn8nXxQzr3Gg==
             * content : MSxJXcYqBYlt1cFxZV4Q6Q==
             * invalidFlag : 1
             * createTime : 2020-04-26 09:20:45
             * updateTime : 2020-05-12 15:41:14
             */

            private int id;
            private int userId;
            private String title;
            private String content;
            private int invalidFlag;
            private String createTime;
            private String updateTime;
        }

        @NoArgsConstructor
        @Data
        public static class RehabilitationGoalHistoryBean {
            /**
             * id : 1117
             * userId : 3
             * goal : 2yv9AoH3hqNPmT+YyL0qcfDHNNqfsDg4hQir7oLY5hU=
             * invalidFlag : 1
             * createTime : 2020-05-12 12:00:01
             * updateTime : 2020-05-12 15:41:14
             */

            private int id;
            private int userId;
            private String goal;
            private int invalidFlag;
            private String createTime;
            private String updateTime;
        }

        @NoArgsConstructor
        @Data
        public static class RehabilitationIssueHistoryBean {
            /**
             * id : 1115
             * userId : 3
             * issue : iuR0jLXyt+DxE2ts6lSOOL4smOTUefeZ9k7I5yPaNAM=
             * invalidFlag : 1
             * createTime : 2020-05-12 11:52:04
             * updateTime : 2020-05-12 15:41:14
             */

            private int id;
            private int userId;
            private String issue;
            private int invalidFlag;
            private String createTime;
            private String updateTime;
        }

        @NoArgsConstructor
        @Data
        public static class ScheduleInfoBean {
            /**
             * status : 1
             * info : {"id":61,"startTime":"2020-05-15 09:00:00","endTime":"2020-05-15 09:30:00","therapistId":8718,"therapist":{"therapistId":8718,"lastName":"jOiumczSMAygcFa1YAdb0Q==","lastNameKana":"Vlt3AqPGfvtV0KOpNNHFbw==","firstName":"cwc1jRZLynC2AuPamNntlg==","firstNameKana":"S0O8YPtMCucIXd2r6ACOTQ==","companyPhone":"s5EAuZC/ggRJ/uFI+Kudyw==","phone":"s5EAuZC/ggRJ/uFI+Kudyw==","loginName":"neko1","sex":2,"password":"$2a$10$vajFYb.3lbChkEu0SAy.DOQWRzMwBzRYS7qo7Yh6KVQUoSuCw4Duq","salt":"$2a$10$vajFYb.3lbChkEu0SAy.DO","qualificationId":3,"roleId":1,"photoPath":"default.jpg","delFlag":1,"createTime":"2020-05-12 10:14:30","updateTime":"2020-05-12 17:13:16"}}
             */

            private int status;
            private InfoBean info;

            @NoArgsConstructor
            @Data
            public static class InfoBean {
                /**
                 * id : 61
                 * startTime : 2020-05-15 09:00:00
                 * endTime : 2020-05-15 09:30:00
                 * therapistId : 8718
                 * therapist : {"therapistId":8718,"lastName":"jOiumczSMAygcFa1YAdb0Q==","lastNameKana":"Vlt3AqPGfvtV0KOpNNHFbw==","firstName":"cwc1jRZLynC2AuPamNntlg==","firstNameKana":"S0O8YPtMCucIXd2r6ACOTQ==","companyPhone":"s5EAuZC/ggRJ/uFI+Kudyw==","phone":"s5EAuZC/ggRJ/uFI+Kudyw==","loginName":"neko1","sex":2,"password":"$2a$10$vajFYb.3lbChkEu0SAy.DOQWRzMwBzRYS7qo7Yh6KVQUoSuCw4Duq","salt":"$2a$10$vajFYb.3lbChkEu0SAy.DO","qualificationId":3,"roleId":1,"photoPath":"default.jpg","delFlag":1,"createTime":"2020-05-12 10:14:30","updateTime":"2020-05-12 17:13:16"}
                 */

                private int id;
                private String startTime;
                private String endTime;
                private int therapistId;
                private TherapistBean therapist;

                @NoArgsConstructor
                @Data
                public static class TherapistBean {
                    /**
                     * therapistId : 8718
                     * lastName : jOiumczSMAygcFa1YAdb0Q==
                     * lastNameKana : Vlt3AqPGfvtV0KOpNNHFbw==
                     * firstName : cwc1jRZLynC2AuPamNntlg==
                     * firstNameKana : S0O8YPtMCucIXd2r6ACOTQ==
                     * companyPhone : s5EAuZC/ggRJ/uFI+Kudyw==
                     * phone : s5EAuZC/ggRJ/uFI+Kudyw==
                     * loginName : neko1
                     * sex : 2
                     * password : $2a$10$vajFYb.3lbChkEu0SAy.DOQWRzMwBzRYS7qo7Yh6KVQUoSuCw4Duq
                     * salt : $2a$10$vajFYb.3lbChkEu0SAy.DO
                     * qualificationId : 3
                     * roleId : 1
                     * photoPath : default.jpg
                     * delFlag : 1
                     * createTime : 2020-05-12 10:14:30
                     * updateTime : 2020-05-12 17:13:16
                     */

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
                    private String flag;
                }
            }
        }

        @NoArgsConstructor
        @Data
        public static class ResponsibleTherapistBean {
            /**
             * therapistId : 2
             * lastName : NtMXd1sMkieLgIA5SmG8qw==
             * lastNameKana : BamwAcyz04Ln7AUznJS6oA==
             * firstName : CSdWFoRxvEeg4XnIu+wihg==
             * firstNameKana : RUckh0rDkWXyDXP3sj+Ueg==
             * companyPhone : a7JnF4LHH2qxl0FUym8fdA==
             * phone : 17315526431
             * loginName : ymh666
             * sex : 2
             * password : $2a$10$HYt2lU6VUF5GB0tnUHUzuOY.QVmcD5mfbnr7HfMBzLtEuNWBTjNoq
             * salt : $2a$10$HYt2lU6VUF5GB0tnUHUzuO
             * qualificationId : 2
             * roleId : 2
             * photoPath : default.jpg
             * delFlag : 1
             */

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
            private String flag;
            private String createTime;
            private String updateTime;
        }

        @NoArgsConstructor
        @Data
        public static class CryptoMapBean {
            /**
             * iv : 2624750004598718
             * key : 751f621ea5c8f930
             */

            private String iv;
            private String key;
        }

        @NoArgsConstructor
        @Data
        public static class VideoDtoListBean {
            /**
             * id : 6
             * title : ステップ位での前後体重移動_左
             * videoCategoryId : 7
             * duration : 00:05:43
             * thumbnailPath : 5.png
             * videoCommonComment : 23423432
             * videoCategoryDto : {"id":7,"typeId":1,"typeName":"身体","name":"脚"}
             * individualComment : 234324234
             * specifyStartTime : 2020-05-01
             * specifyEndTime : 2020-06-17
             * userVideoHistoryId : 62
             * watched : no
             * videoFileName : 0047_ステップ位での前後体重移動_左_15n_15_0316.477p.mpeg4.aac.mp4
             */

            private int id;
            private String title;
            private int videoCategoryId;
            private String duration;
            private String thumbnailPath;
            private String videoCommonComment;
            private VideoCategoryDtoBean videoCategoryDto;
            private String individualComment;
            private String specifyStartTime;
            private String specifyEndTime;
            private int userVideoHistoryId;
            private String watched;
            private String videoFileName;

            @NoArgsConstructor
            @Data
            public static class VideoCategoryDtoBean {
                /**
                 * id : 7
                 * typeId : 1
                 * typeName : 身体
                 * name : 脚
                 */

                private int id;
                private int typeId;
                private String typeName;
                private String name;
            }
        }
    }
}
