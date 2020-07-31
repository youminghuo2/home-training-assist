package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Package com.example.hometrainng.db
 * @Description Completion完成度
 * @CreateDate: 2020/5/6 6:25 PM
 */

@NoArgsConstructor
@Data
public class Completion extends LitePalSupport {

    private int goals;
    private String week;
    private String localDate;


}
