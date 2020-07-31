package com.example.hometrainng.entity;


import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class VideoEntity {


    /**
     * code : 200
     * msg : OK
     * data : [{"id":687,"title":"前後への体重移動_共通_5回","videoCategoryId":84,"duration":"00:04:10","thumbnailPath":"0002_01.png","videoCommonComment":"da's'd","videoCategoryDto":{"id":84,"typeId":1,"typeName":"身体","name":"体幹"},"individualComment":"你哄好","specifyStartTime":"2020-05-26","userVideoHistoryId":258,"videoFileName":"0002_お尻での前後への体重移動_共通_20n_10_170322.477p.mpeg4.aac.mp4"},{"id":714,"title":"前後への体重移動_共通_5回","videoCategoryId":87,"duration":"00:03:57","thumbnailPath":"0019_01.png","videoCommonComment":"faf","videoCategoryDto":{"id":87,"typeId":1,"typeName":"身体","name":"下肢・体幹"},"individualComment":"fa","specifyStartTime":"2020-05-26","userVideoHistoryId":258,"videoFileName":"0019_前後への体重移動_20n_10_0316.477p.mpeg4.aac.mp4"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    @NoArgsConstructor
    @Data
    public static class DataBean {
        /**
         * id : 687
         * title : 前後への体重移動_共通_5回
         * videoCategoryId : 84
         * duration : 00:04:10
         * thumbnailPath : 0002_01.png
         * videoCommonComment : da's'd
         * videoCategoryDto : {"id":84,"typeId":1,"typeName":"身体","name":"体幹"}
         * individualComment : 你哄好
         * specifyStartTime : 2020-05-26
         * userVideoHistoryId : 258
         * videoFileName : 0002_お尻での前後への体重移動_共通_20n_10_170322.477p.mpeg4.aac.mp4
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
        private int userVideoHistoryId;
        private String videoFileName;
        private String specifyEndTime;

        @NoArgsConstructor
        @Data
        public static class VideoCategoryDtoBean {
            /**
             * id : 84
             * typeId : 1
             * typeName : 身体
             * name : 体幹
             */

            private int id;
            private int typeId;
            private String typeName;
            private String name;
        }
    }
}
