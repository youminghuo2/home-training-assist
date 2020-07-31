package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MqMessage {
    private String type;
    private String entity;
    private Integer id;
    private String message;
}
