package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TherapistBean {
    private int code;
    private String msg;
    private Therapist data;
}
