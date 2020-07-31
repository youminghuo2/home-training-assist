package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Therapist {
    private int therapistId;
    private String lastName;
    private String lastNameKana;
    private String firstName;
    private String firstNameKana;
    private String companyPhone;
    private String phone;
    private String loginName;
    private int sex;
    private int qualificationId;
    private int roleId;
    private String photoPath;
    private String createTime;
}
