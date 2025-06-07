package com.guangyu.guangyubackend.infrastructure.exception;

import com.guangyu.guangyubackend.infrastructure.common.BaseResponse;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author dmz xxx@163.com
 * @version 2025/5/17 23:55
 * @since JDK17
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(RespCode.SYSTEM_ERROR);
    }
}

