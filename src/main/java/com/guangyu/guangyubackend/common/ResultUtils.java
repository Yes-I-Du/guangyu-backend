package com.guangyu.guangyubackend.common;

import com.guangyu.guangyubackend.exception.RespCode;

/**
 * 响应结果工具类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/17 23:40
 * @since JDK17
 */
public class ResultUtils {

    /*
     * 操作成功
     *
     * @param data
     * @param <T> 数据类型
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "操作成功！！！", data);
    }

    /*
     * 操作失败
     *
     * @param respcode 响应码
     * @param <T> 数据类型
     * @return
     */
    public static BaseResponse<?> error(RespCode respCode) {
        return new BaseResponse<>(respCode);
    }

    /*
     * 操作失败
     *
     * @param code 响应码
     * @param message 响应信息
     * @return
     */
    public static BaseResponse<?> error(Integer code, String message) {
        return new BaseResponse<>(code, message, null);
    }

    /*
     * 操作失败
     *
     * @param respCode 响应码
     * @return
     */
    public static BaseResponse<?> error(RespCode respCode, String message) {
        return new BaseResponse<>(respCode.getCode(), message, null);
    }

}

