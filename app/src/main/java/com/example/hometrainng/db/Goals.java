package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Goals extends LitePalSupport {
    private int userId;
    private int goalId;
    private String goal;
    private String createTime;

}
