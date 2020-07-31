package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Comments extends LitePalSupport {
    private int userId;
    private int therapistId;
    private String rehabilitationComment;
    private String createTime;
    private String updateTime;
    private int commentId;
}
