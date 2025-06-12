package com.guangyu.guangyubackend.domain.space.service.impl;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.domain.space.constant.SpaceConstant;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.repository.SpaceRepository;
import com.guangyu.guangyubackend.domain.space.service.SpaceDomainService;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceLevel;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceTypeEnum;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.space.SpaceLevelManager;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceQueryRequest;
import com.guangyu.guangyubackend.interfaces.vo.space.SpaceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * @author Dmz
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-06-10 22:53:39
 */
@Service
@Slf4j
public class SpaceDomainServiceImpl implements SpaceDomainService {
    @Resource
    private SpaceRepository spaceRepository;

    @Resource
    private SpaceLevelManager spaceLevelManager;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 用户空间信息初始化设置
        // 实体类进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        Collection<SpaceLevel> spaceLevel = spaceLevelManager.getAllLevels();
        // 用户空间名称初始化
        if (StrUtil.isBlank(space.getSpaceName())) {
            space.setSpaceName(loginUser.getUserName() + "的空间");
        }
        // TODO 用户私有空间简介
        //        // 用户空间简介初始化
        //        if (StrUtil.isBlank(space.getSpaceIntro())){
        //            space.setSpaceIntro("欢迎来到我的空间");
        //        }
        // 用户空间等级初始化
        if (space.getSpaceLevel() == null) {
            space.setSpaceLevel(spaceLevelManager.getValueByName(SpaceConstant.SPACE_LEVEL_COMMON));
        }
        // TODO　团队空间功能拓展：用户空间类型初始化
        //        if (space.getSpaceType() == null) {
        //            space.setSpaceType(SpaceTypeEnum.PRIVATE.getValue());
        //        }

        // 用户空间容量填充
        this.setSpaceLimitByLevel(space);
        // 用户空间信息校验
        space.validSpaceInfo(true);

        // 权限校验，默认普通用户只能创建普通空间
        Long userId = loginUser.getId();
        space.setUserId(userId);
        if (space.getSpaceLevel()!=spaceLevelManager.getValueByName(SpaceConstant.SPACE_LEVEL_COMMON) && !loginUser.isAdmin()){
            throw new BusinessException(RespCode.NO_AUTH_ERROR,"权限不足，无法创建高级空间，该用户只能创建普通版空间");
        }

        // 同一用户只能创建一个私有空间
        String userIdLock = String.valueOf(userId).intern();
        synchronized (userIdLock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                // 判断是否已有空间
                boolean exists = spaceRepository.lambdaQuery()
                    .eq(Space::getUserId, userId)
                    .exists();
                // 如果已有空间，就不能再创建
                ThrowUtils.throwIf(exists, RespCode.OPERATION_ERROR, "每个用户只能创建一个私有空间");
                // 创建
                boolean result = spaceRepository.save(space);
                ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "用户私有空间创建失败");
                return space.getId();
            });
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }

    @Override
    public boolean updateSpaceById(Space space) {
        return spaceRepository.updateById(space);
    }

    @Override
    public void deleteSpace(long spaceId, User loginUser) {
        // 判断是否存在
        Space existSpace = this.getById(spaceId);
        ThrowUtils.throwIf(existSpace == null, RespCode.NOT_FOUND_ERROR, "用户空间不存在");
        // 仅本人或者管理员可删除
        this.checkSpaceAuth(loginUser, existSpace);
        // 操作数据库
        ThrowUtils.throwIf(!spaceRepository.removeById(spaceId), RespCode.OPERATION_ERROR,"删除失败");
    }

    @Override
    public void editSpace(Space space, User loginUser) {
        // 判断是否存在
        Space existSpace = this.getById(space.getId());
        ThrowUtils.throwIf(existSpace == null, RespCode.NOT_FOUND_ERROR, "用户空间不存在");
        // 仅本人或者管理员可编辑
        this.checkSpaceAuth(loginUser, existSpace);
        // 数据填充
        this.setSpaceLimitByLevel(space);
        space.setEditTime(new Date());

        // 数据校验
        space.validSpaceInfo(false);
        // 操作数据库
        ThrowUtils.throwIf(!spaceRepository.updateById(space), RespCode.OPERATION_ERROR,"编辑失败");
    }

    @Override
    public Space getById(Long spaceId) {
        return spaceRepository.getById(spaceId);
    }

    @Override
    public Page<Space> page(Page<Space> spacePage, QueryWrapper<Space> queryWrapper) {
        return spaceRepository.page(spacePage, queryWrapper);
    }

    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
        QueryWrapper<Space> queryWrapper = new QueryWrapper<>();
        if (spaceQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = spaceQueryRequest.getId();
        Long userId = spaceQueryRequest.getUserId();
        String spaceName = spaceQueryRequest.getSpaceName();
        Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
        String sortField = spaceQueryRequest.getSortField();
        String sortOrder = spaceQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(spaceName), "spaceName", spaceName);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceLevel), "spaceLevel", spaceLevel);
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void setSpaceLimitByLevel(Space space) {
        SpaceLevel spaceLevel = spaceLevelManager.getByValue(space.getSpaceLevel());
        // 在创建或者更新空间信息时，根据空间级别自动填充空间限额
        // 只有在没有设置空间限额的场合下，才会填充数据，保证空间数据的灵活性
        if (spaceLevel != null) {
            if (space.getMaxSize() == null) {
                space.setMaxSize(spaceLevel.getMaxSize());
            }
            if (space.getMaxCount() == null) {
                space.setMaxCount(spaceLevel.getMaxCount());
            }
        }
    }

    @Override
    public void checkSpaceAuth(User loginUser, Space space) {
        Long loginUserId = loginUser.getId();
        // 仅管理员与空间拥有者可以操作
        if (!space.getUserId().equals(loginUserId) && !loginUser.isAdmin()) {
            throw new BusinessException(RespCode.NO_AUTH_ERROR, "该登录用户无操作权限");
        }
    }

}




