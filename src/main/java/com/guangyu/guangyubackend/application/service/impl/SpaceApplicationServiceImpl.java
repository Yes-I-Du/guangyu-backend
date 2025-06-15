package com.guangyu.guangyubackend.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.PictureApplicationService;
import com.guangyu.guangyubackend.application.service.SpaceApplicationService;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.service.SpaceDomainService;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceLevel;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.space.SpaceLevelManager;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceQueryRequest;
import com.guangyu.guangyubackend.interfaces.vo.space.SpaceVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmz
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-06-10 22:53:39
 */
@Service
public class SpaceApplicationServiceImpl implements SpaceApplicationService {
    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private SpaceDomainService spaceDomainService;

    @Override
    public void vaildSpace(Space space, boolean addFlag) {
        ThrowUtils.throwIf(space == null, RespCode.PARAMS_ERROR, "用户空间信息不存在");
        // 空间信息校验
        space.validSpaceInfo(addFlag);
    }

    @Override
    public void setSpaceLimitByLevel(Space space) {
        spaceDomainService.setSpaceLimitByLevel(space);
    }

    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 用户登录
        ThrowUtils.throwIf(loginUser == null, RespCode.NOT_LOGIN_ERROR);

        return spaceDomainService.addSpace(spaceAddRequest, loginUser);
    }

    @Override
    public void updateSpace(Space space) {
        boolean result = spaceDomainService.updateSpaceById(space);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "更新失败");
    }

    @Override
    public void deleteSpace(long spaceId, User loginUser) {
        spaceDomainService.deleteSpace(spaceId, loginUser);
    }

    @Override
    public void editSpace(Space space, User loginUser) {
        spaceDomainService.editSpace(space, loginUser);
    }

    @Override
    public Space getSpaceById(Long id) {
        Space space = spaceDomainService.getById(id);
        ThrowUtils.throwIf(space == null, RespCode.NOT_FOUND_ERROR, "用户私有空间不存在");
        return space;
    }

    @Override
    public SpaceVO getSpaceVO(Space space) {
        // 对象转换
        SpaceVO spaceVO = SpaceVO.SpaceToVo(space);

        //关联用户信息
        Long userId = space.getUserId();
        if (userId != null) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            spaceVO.setUser(userVO);
        }
        return spaceVO;
    }

    @Override
    public SpaceVO getSpaceVOById(Long id) {
        return this.getSpaceVO(this.getSpaceById(id));

    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        List<Space> spaceList = spacePage.getRecords();
        Page<SpaceVO> spaceVOPage = new Page<>(spacePage.getCurrent(), spacePage.getSize(), spacePage.getTotal());
        if (CollUtil.isEmpty(spaceList)) {
            return spaceVOPage;
        }
        // 对象列表 => 封装对象列表
        List<SpaceVO> spaceVOList = spaceList.stream().map(SpaceVO::SpaceToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = spaceList.stream().map(Space::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap =
            userApplicationService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        spaceVOList.forEach(spaceVO -> {
            Long userId = spaceVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            spaceVO.setUser(userApplicationService.getUserVO(user));
        });
        spaceVOPage.setRecords(spaceVOList);
        return spaceVOPage;
    }

    @Override
    public Page<Space> page(Page<Space> spacePage, QueryWrapper<Space> queryWrapper) {
        return spaceDomainService.page(spacePage, queryWrapper);
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        return spaceDomainService.getQueryWrapper(spaceQueryRequest);
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        spaceDomainService.checkSpaceAuth(loginUser, space);
    }
}