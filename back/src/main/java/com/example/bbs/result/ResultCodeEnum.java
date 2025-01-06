package com.example.bbs.result;

import lombok.Getter;

/**
 * 功能描述：
 *
 * @Author: lyc
 * @Date: 2022/1/11 9:27 下午
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(true, 20000,"成功"),
    UNKNOWN_REASON(false, 20001, "未知错误");

    private Boolean success;
    private Integer code;
    private String message;

    private ResultCodeEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }
}
