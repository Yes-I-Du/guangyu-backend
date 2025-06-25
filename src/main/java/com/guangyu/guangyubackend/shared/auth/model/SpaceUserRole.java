package com.guangyu.guangyubackend.shared.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 空间成员角色
 *
 * @author dmz xxx@163.com
 * @version 2025/6/22 22:40
 * @since JDK17
 */
@Data
public class SpaceUserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色键
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 权限键列表
     */
    private List<String> permissions;

    /**
     * 角色描述
     */
    private String description;
}

