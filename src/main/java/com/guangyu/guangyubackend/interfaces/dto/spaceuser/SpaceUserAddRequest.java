package com.guangyu.guangyubackend.interfaces.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间成员创建请求
 *
 * @author dmz xxx@163.com
 * @version 2025/6/23 16:37
 * @since JDK17
 */
@Data
public class SpaceUserAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;
}

