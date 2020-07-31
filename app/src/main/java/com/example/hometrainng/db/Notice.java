package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Notice extends LitePalSupport {

    /**
     * id : 5
     * title : fcasdfds
     * content : fasdfsadcv
     * createTime : 2020-04-17T14:01:48
     */

    private int noticeId;
    private String title;
    private String content;
    private String createTime;
    private String url;
}
