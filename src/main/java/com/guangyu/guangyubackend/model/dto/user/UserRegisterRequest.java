package com.guangyu.guangyubackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求参数类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/18 21:23
 * @since JDK17
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}

