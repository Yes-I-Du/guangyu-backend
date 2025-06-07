package com.guangyu.guangyubackend.interfaces.controller;

import com.guangyu.guangyubackend.infrastructure.common.BaseResponse;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dmz xxx@163.com
 * @version 2025/5/18 0:26
 * @since JDK17
 */
@RestController
@RequestMapping("/")
public class MainController {

    /*
    * 健康检查接口
    * */
    @GetMapping("/health")
    public BaseResponse<String> health(){
        return ResultUtils.success(RespCode.SUCCESS.getMessage());
    }
}

