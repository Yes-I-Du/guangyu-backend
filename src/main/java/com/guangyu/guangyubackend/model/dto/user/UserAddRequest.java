package com.guangyu.guangyubackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户管理-追加用户
 *
 * @author dmz xxx@163.com
 * @version 2025/5/19 21:57
 * @since JDK17
 */
@Data
public class UserAddRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /*用户账号*/
    private String userAccount;

    /*用户昵称*/
    private String userName;

    /*用户头像*/
    private String userAvatar;

    /*用户简介*/
    private String userProfile;

    /*用户角色:user/admin*/
    private String userRole;
}

