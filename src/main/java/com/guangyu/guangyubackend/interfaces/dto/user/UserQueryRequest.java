package com.guangyu.guangyubackend.interfaces.dto.user;

import com.guangyu.guangyubackend.infrastructure.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户管理-查询用户
 *
 * @author dmz xxx@163.com
 * @version 2025/5/19 22:02
 * @since JDK17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

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

    /*用户角色*/
    private String userRole;
}

