package com.example.hometrainng.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Package com.example.hometrainng.entity
 * @Description java类作用描述
 * @CreateDate: 2020/5/8 2:22 PM
 */
@NoArgsConstructor
@Data
public class MsgModel {

    /**
     * code : 200
     * msg : OK
     */

    private int code;
    private String msg;
}

