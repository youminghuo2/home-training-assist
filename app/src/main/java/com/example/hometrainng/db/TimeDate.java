package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import retrofit2.http.GET;

/**
 * @Package com.example.hometrainng.db
 * @Description Time
 * @CreateDate: 2020/5/6 10:17 AM
 */


@Data
public class TimeDate extends LitePalSupport {

    private String beginDate;
    private String endDate;

    private String videoDate;
}
