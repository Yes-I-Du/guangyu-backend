package com.guangyu.guangyubackend.shared.auth.model;

/**
 * 空间成员权限常量
 *
 * @author dmz xxx@163.com
 * @version 2025/6/22 22:37
 * @since JDK17
 */
public interface SpaceUserPermissionConstant {
    /**
     * 空间用户管理权限
     */
    public final String SPACE_USER_MANAGE = "spaceUser:manage";

    /**
     * 图片查看权限
     */
    public final String PICTURE_VIEW = "picture:view";

    /**
     * 图片上传权限
     */
    public final String PICTURE_UPLOAD = "picture:upload";

    /**
     * 图片编辑权限
     */
    public final String PICTURE_EDIT = "picture:edit";

    /**
     * 图片删除权限
     */
    public final String PICTURE_DELETE = "picture:delete";
}

