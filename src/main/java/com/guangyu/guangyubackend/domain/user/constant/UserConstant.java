package com.guangyu.guangyubackend.domain.user.constant;

/**
 * 用户相关状态常量接口
 *
 * @author dmz xxx@163.com
 * @version 2025/5/19 20:14
 * @since JDK17
 */
public interface UserConstant {

    /*用户登陆状态*/
    public static final String USER_LOGIN_STATUS = "user_login";

    /* 盐值，混淆密码 */
    public final String SALT = "guangYuUserPasswordSalt";

    /* */
    public static final String DEFAULT_USER_PASSWORD = "012345678";

    /*region 用户角色权限*/
    /*默认用户角色权限*/
    public static final String DEFAULT_ROLE = "user";

    /*管理员角色权限*/
    public static final String ADMIN_ROLE = "admin";

    /*用户默认密码*/
    public static final String DEFAULT_PASSWORD = "1234@abc";

    /*endregion*/
}