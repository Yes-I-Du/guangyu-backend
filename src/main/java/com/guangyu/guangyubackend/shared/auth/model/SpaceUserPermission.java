package com.guangyu.guangyubackend.shared.auth.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间成员权限
 *
 * @author dmz xxx@163.com
 * @version 2025/6/22 22:30
 * @since JDK17
 */
@Data
public class SpaceUserPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 权限键
     */
    private String key;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限描述
     */
    private String description;
}

