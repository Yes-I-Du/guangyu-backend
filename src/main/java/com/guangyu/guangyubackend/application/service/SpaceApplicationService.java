package com.guangyu.guangyubackend.application.service;

import com.guangyu.guangyubackend.domain.space.entity.Space;

/**
* @author Dmz
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-06-10 22:53:39
*/
public interface SpaceApplicationService {

    /**
     * 用户空间信息校验
     *
     * @param space   用户空间信息
     * @param addFlag 是否为新增用户空间
     */
    void vaildSpace(Space space, boolean addFlag);
}
