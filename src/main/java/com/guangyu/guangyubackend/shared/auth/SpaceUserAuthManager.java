package com.guangyu.guangyubackend.shared.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.guangyu.guangyubackend.application.service.SpaceUserApplicationService;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceRoleEnum;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceTypeEnum;
import com.guangyu.guangyubackend.domain.spaceuser.entity.SpaceUser;
import com.guangyu.guangyubackend.domain.spaceuser.repository.SpaceUserRepository;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.shared.auth.model.SpaceUserAuthConfig;
import com.guangyu.guangyubackend.shared.auth.model.SpaceUserPermissionConstant;
import com.guangyu.guangyubackend.shared.auth.model.SpaceUserRole;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 空间用户角色权限管理器
 *
 * @author dmz xxx@163.com
 * @version 2025/6/22 22:50
 * @since JDK17
 */
@Component
public class SpaceUserAuthManager {
    @Resource
    private SpaceUserApplicationService spaceUserApplicationService;

    @Resource
    private SpaceUserRepository spaceUserRepository;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/spaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     *
     * @param spaceUserRole
     * @return
     */
    public List<String> getPermissionsByRole(String spaceUserRole) {
        if (StrUtil.isBlank(spaceUserRole)) {
            return new ArrayList<>();
        }
        SpaceUserRole role =
            SPACE_USER_AUTH_CONFIG.getRoles().stream().filter(r -> r.getKey().equals(spaceUserRole)).findFirst()
                .orElse(null);
        if (role == null) {
            return new ArrayList<>();
        }
        return role.getPermissions();
    }

    /**
     * 获取权限列表
     *
     * @param space
     * @param loginUser
     * @return
     */
    public List<String> getPermissionList(Space space, User loginUser) {
        if (loginUser == null) {
            return new ArrayList<>();
        }
        // 管理员权限
        List<String> ADMIN_PERMISSIONS = getPermissionsByRole(SpaceRoleEnum.ADMIN.getValue());
        // 公共图库
        if (space == null) {
            if (loginUser.isAdmin()) {
                return ADMIN_PERMISSIONS;
            }
            return Collections.singletonList(SpaceUserPermissionConstant.PICTURE_VIEW);
        }
        SpaceTypeEnum spaceTypeEnum = SpaceTypeEnum.getSpaceTypeEnumByValue(space.getSpaceType());
        if (spaceTypeEnum == null) {
            return new ArrayList<>();
        }
        // 根据空间获取对应的权限
        switch (spaceTypeEnum) {
            case PRIVATE:
                // 私有空间，仅本人或管理员有所有权限
                if (space.getUserId().equals(loginUser.getId()) || loginUser.isAdmin()) {
                    return ADMIN_PERMISSIONS;
                } else {
                    return new ArrayList<>();
                }
            case TEAM:
                // 团队空间，查询 SpaceUser 并获取角色和权限
                SpaceUser spaceUser = spaceUserRepository.lambdaQuery().eq(SpaceUser::getSpaceId, space.getId())
                    .eq(SpaceUser::getUserId, loginUser.getId()).one();
                if (spaceUser == null) {
                    return new ArrayList<>();
                } else {
                    return getPermissionsByRole(spaceUser.getSpaceRole());
                }
        }
        return new ArrayList<>();
    }
}

