package com.guangyu.guangyubackend.model.dto.user;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * 用户登录请求参数类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/18 23:39
 * @since JDK17
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;
}

