package com.guangyu.guangyubackend.domain.space.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceQueryRequest;
import com.guangyu.guangyubackend.interfaces.vo.space.SpaceVO;

/**
* @author Dmz
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-06-10 22:53:39
*/
public interface SpaceDomainService {

    /**
     * 根据用户空间Id更新空间信息
     *
     * @param space 用户空间信息
     * @return 更新结果
     */
    boolean updateSpaceById(Space space);

    /**
     * 用户创建私有空间
     *
     * @param spaceAddRequest 用户空间信息
     * @param loginUser       当前登录用户
     * @return 脱敏后的用户空间信息
     */
    Long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 删除图片
     *
     * @param spaceId   图片Id
     * @param loginUser 当前登录用户
     */
    void deleteSpace(long spaceId, User loginUser);

    /**
     * 用户私有空间信息编辑
     *
     * @param space   用户私有空间
     * @param loginUser 当前登录用户
     */
    void editSpace(Space space, User loginUser);

    /**
     * 根据空间ID获取用户空间信息
     *
     * @param spaceId 空间Id
     */
    Space getById(Long spaceId);

    /**
     * 获取空间分页信息
     *
     * @param spacePage    空间分页信息
     * @param queryWrapper 查询条件
     * @return 该页信息
     */
    Page<Space> page(Page<Space> spacePage, QueryWrapper<Space> queryWrapper);

    /**
     * 构造QueryWrapper对象生成Sql查询
     *
     * @param spaceQueryRequest 空间信息查询对象
     * @return QueryWrapper QueryWrapper对象
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间级别设置空间限制
     *
     * @param space 用户空间信息
     */
    void setSpaceLimitByLevel(Space space);

    /**
     * 空间信息权限校验
     *
     * @param space   空间信息
     * @param loginUser 当前登录用户
     */
    void checkSpaceAuth(User loginUser, Space space);
}
