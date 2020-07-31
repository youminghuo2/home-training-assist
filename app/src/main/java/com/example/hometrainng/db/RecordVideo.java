package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.example.hometrainng.db
 * @Description RecordVideo录制视频
 * @CreateDate: 2020/5/11 9:33 AM
 */
@NoArgsConstructor
@Data
public class RecordVideo extends LitePalSupport {
    private int videoId;
    private int id;
    private String recordPath;

    private String recordImg;
    //录制视频时间，年月日
    private String recordDate;
    //视频长短
    private String duration;

    //录制视频时间，年月日时分秒
    private String recordDateTime;
}
