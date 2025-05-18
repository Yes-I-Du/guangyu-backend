package com.guangyu.guangyubackend.exception;

import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @author Dmz Email:  * @since 2025/05/17 23:09
 */

@Getter
public class BusinessException extends RuntimeException{

    /* 状态码 */
    private final Integer code;

    public BusinessException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(RespCode respCode) {
        super(respCode.getMessage());
        this.code = respCode.getCode();
    }

    public BusinessException(RespCode respCode,String message) {
        super(message);
        this.code = respCode.getCode();
    }
}

