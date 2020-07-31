package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Messages extends LitePalSupport {
    private int userId;
    private String title;
    private String content;
    private String createTime;
    private int messageId;

}
