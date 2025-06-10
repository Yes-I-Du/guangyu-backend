package com.guangyu.guangyubackend.application.service.impl;

import com.guangyu.guangyubackend.application.service.PictureApplicationService;
import com.guangyu.guangyubackend.application.service.SpaceApplicationService;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.service.SpaceDomainService;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    private PictureApplicationService pictureApplicationService;

    @Resource
    private SpaceDomainService spaceDomainService;

    @Override
    public void vaildSpace(Space space,boolean addFlag) {
        ThrowUtils.throwIf(space == null, RespCode.PARAMS_ERROR, "用户空间信息不存在");
        // 空间信息校验
        space.validSpaceInfo(addFlag);
    }
}




