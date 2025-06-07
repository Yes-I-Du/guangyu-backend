package com.guangyu.guangyubackend.interfaces.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * 用户登录脱敏信息
 *
 * @author dmz xxx@163.com
 * @version 2025/5/19 20:20
 * @since JDK17
 */
@Data
public class LoginUserVO implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;
    /*用户ID*/
    private Long id;

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

    /*创建时间*/
    private Date createTime;

    /*更新时间*/
    private Date updateTime;
}

