package com.guangyu.guangyubackend.shared.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 空间成员角色权限配置
 *
 * @author dmz xxx@163.com
 * @version 2025/6/22 22:28
 * @since JDK17
 */
@Data
public class SpaceUserAuthConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 权限列表
     */
    private List<SpaceUserPermission> permissions;

    /**
     * 角色列表
     */
    private List<SpaceUserRole> roles;
}

