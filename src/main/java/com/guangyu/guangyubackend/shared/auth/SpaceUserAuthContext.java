package com.guangyu.guangyubackend.shared.auth;

import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.spaceuser.entity.SpaceUser;
import lombok.Data;

/**
 * 空间用户权限上下文 表示用户在特定空间内的授权上下文，包括关联的图片、空间和用户信息
 *
 * @author dmz xxx@163.com
 * @version 2025/6/23 18:56
 * @since JDK17
 */
@Data
public class SpaceUserAuthContext {
    /**
     * 临时参数，不同请求对应的 id 可能不同
     */
    private Long id;

    /**
     * 图片 ID
     */
    private Long pictureId;

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 空间用户 ID
     */
    private Long spaceUserId;

    /**
     * 图片信息
     */
    private Picture picture;

    /**
     * 空间信息
     */
    private Space space;

    /**
     * 空间用户信息
     */
    private SpaceUser spaceUser;
}
