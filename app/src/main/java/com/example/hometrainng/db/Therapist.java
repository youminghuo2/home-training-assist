package com.example.hometrainng.db;

import org.litepal.crud.LitePalSupport;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Therapist extends LitePalSupport {

    private int therapistId;
    private String lastName;
    private String lastNameKana;
    private String firstName;
    private String firstNameKana;
    private String companyPhone;
    private String phone;
    private String loginName;
    private int sex;
    private String photoPath;
    private String createTime;
    private String flag;

}
