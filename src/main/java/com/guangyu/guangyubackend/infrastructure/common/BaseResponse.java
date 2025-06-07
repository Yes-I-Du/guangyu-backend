package com.guangyu.guangyubackend.infrastructure.common;

import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dmz xxx@163.com
 * @version 2025/5/17 23:28
 * @since JDK17
 */

@Data
public class BaseResponse<T> implements Serializable {

    /*状态码*/
    private Integer code;

    /*响应信息*/
    private String message;

    /*响应结果数据集*/
    private T data;

    public BaseResponse() {
    }

    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public BaseResponse(Integer code, T data) {
        this(code, "", data);
    }

    public BaseResponse(RespCode respCode) {
        this(respCode.getCode(), respCode.getMessage(), null);
    }

    public BaseResponse(RespCode respCode, T data) {
        this(respCode.getCode(), respCode.getMessage(), data);
    }
}
