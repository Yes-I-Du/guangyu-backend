package com.guangyu.guangyubackend.exception;

import lombok.Getter;

/**
 * 自定义请求响应码
 *
 * @author Dmz Email:  * @since 2025/05/17 22:52
 */
@Getter
public enum RespCode {
    SUCCESS(0, "操作成功"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "用户未登录"),
    NO_AUTH_ERROR(40101, "请求权限错误"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统异常"),
    OPERATION_ERROR(50001, "操作失败，请稍后再试");

    /* 状态码 */
    private final Integer code;
    /* 响应信息 */
    private final String message;

    RespCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
