package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Video extends LitePalSupport {
    private int videoId;
    private int userVideoHistoryId;
    private String title;
    private String duration;
    private String thumbnailPath;
    private String videoCommonComment;
    private String individualComment;
    private String specifyStartTime;
    private String specifyEndTime;
    private String videoFileName;


    //判断是否观看了
    private String watched;

}
