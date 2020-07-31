package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package com.example.hometrainng.db
 * @Description java类作用描述
 * @CreateDate: 2020/5/12 6:38 PM
 */
@Data
@NoArgsConstructor
public class VideoHistory extends LitePalSupport {
    private int videoId;
    private int userId;
    private String playEndTime;
}
