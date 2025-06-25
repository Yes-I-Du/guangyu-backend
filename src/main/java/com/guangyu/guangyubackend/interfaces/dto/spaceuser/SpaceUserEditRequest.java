package com.guangyu.guangyubackend.interfaces.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间成员编辑请求
 *
 * @author dmz xxx@163.com
 * @version 2025/6/23 16:39
 * @since JDK17
 */
@Data
public class SpaceUserEditRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间角色：viewer/editor/admin
     */
    private String spaceRole;

}

