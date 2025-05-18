package com.guangyu.guangyubackend.controller;

import com.guangyu.guangyubackend.common.BaseResponse;
import com.guangyu.guangyubackend.common.ResultUtils;
import com.guangyu.guangyubackend.model.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dmz xxx@163.com
 * @version 2025/5/18 23:24
 * @since JDK17
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册信息
     * @return 新用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userService.userRegister(userRegisterRequest));
    }
}

