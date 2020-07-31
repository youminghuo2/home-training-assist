package com.example.hometrainng.retrofit;

import androidx.annotation.NonNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Package com.example.hometrainng.retrofit
 * @Description java类作用描述
 * @CreateDate: 2020/4/9 11:43
 */
public class ApiResult<T> {
    @Getter
    @Setter
    private int code;
    @Getter
    @Setter
    private String msg;
    @Getter
    @Setter
    private T data;

    public ApiResult() {

    }

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @NonNull
    @Override
    public String toString() {
        return "ApiResult{" + "code=" + code + ",msg=" + msg + '\'' + ",data" + data + '}';
    }
}
