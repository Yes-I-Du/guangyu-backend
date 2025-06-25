package com.guangyu.guangyubackend.shared.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * Sa-Token多账号认证体系(Kit模式)
 * StpLogic 门面类，管理项目中所有的 StpLogic 账号体系
 *
 * @author dmz xxx@163.com
 * @version 2025/6/23 17:22
 * @since JDK17
 */
@Component
public class StpKit {
    public static final String SPACE_TYPE = "space";

    public static final String USER_TYPE = "user";
    /**
     * 默认原生会话对象
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space 会话对象，管理 Space 表所有账号的登录、权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);

    /**
     * User 会话对象，管理 user 表所有账号的登录、权限认证
     */
    public static final StpLogic USER = new StpLogic(USER_TYPE);
}

