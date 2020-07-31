package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class Issues extends LitePalSupport {

    private int userId;
    private int issueId;
    private String issue;
    private String createTime;




}
