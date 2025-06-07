package com.guangyu.guangyubackend.domain.user.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.guangyu.guangyubackend.domain.user.constant.UserConstant;
import com.guangyu.guangyubackend.domain.user.valueobject.UserRoleEnum;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import lombok.Data;
import org.springframework.util.DigestUtils;

/**
 * 用户
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色:user/admin
     */
    private String userRole;

    /**
     * 会员过期时间
     */
    private Date vipExpireTime;

    /**
     * 会员兑换码
     */
    private String vipCode;

    /**
     * 会员编号
     */
    private Long vipNumber;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 邀请用户 id
     */
    private Long inviteUser;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 用户注册信息校验
     *
     * @param userAccount   账号
     * @param userPassword  密码
     * @param checkPassword 确认密码
     */
    public static void validRegister(String userAccount, String userPassword, String checkPassword) {
        // 账号密码校验
        // 账号密码为空的场合
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), RespCode.PARAMS_ERROR,
            "用户名或密码不能为空");

        // 账号长度<6的场合
        ThrowUtils.throwIf(userAccount.length() < 6, RespCode.PARAMS_ERROR, "用户名长度不能少于6");

        // 密码长度<8的场合
        ThrowUtils.throwIf(userPassword.length() < 8 || checkPassword.length() < 8, RespCode.PARAMS_ERROR,
            "密码长度不能少于8");

        // 两次密码不一致的场合
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), RespCode.PARAMS_ERROR, "两次密码不一致");
    }

    /**
     * 用户登录信息校验
     *
     * @param userAccount  账号
     * @param userPassword 密码
     */
    public static void validLogin(String userAccount, String userPassword) {
        // 账号密码参数校验
        // 账号密码为空的场合
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), RespCode.PARAMS_ERROR, "用户名或密码不能为空");

        // 账号长度<6的场合
        ThrowUtils.throwIf(userAccount.length() < 6, RespCode.PARAMS_ERROR, "用户名长度不能少于6");

        // 密码长度<8的场合
        ThrowUtils.throwIf(userPassword.length() < 8, RespCode.PARAMS_ERROR, "密码长度不能少于8");

    }

    /**
     * 是否为管理员
     *
     * @return boolean
     */
    public boolean isAdmin() {
        return UserRoleEnum.ADMIN.getValue().equals(this.getUserRole());
    }
}