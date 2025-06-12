package com.guangyu.guangyubackend.interfaces.controller;/**
 * 空间Controller服务
 *
 * @author Dmz Email:  * @since 2025/06/10 22:04
 */

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.PictureApplicationService;
import com.guangyu.guangyubackend.application.service.SpaceApplicationService;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.picture.valueobject.PictureReviewStatusEnum;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.valueobject.SpaceLevel;
import com.guangyu.guangyubackend.domain.user.constant.UserConstant;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.annotation.AuthCheck;
import com.guangyu.guangyubackend.infrastructure.common.BaseResponse;
import com.guangyu.guangyubackend.infrastructure.common.DeleteRequest;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.space.SpaceLevelManager;
import com.guangyu.guangyubackend.interfaces.assembler.PictureAssembler;
import com.guangyu.guangyubackend.interfaces.assembler.SpaceAssembler;
import com.guangyu.guangyubackend.interfaces.dto.picture.PictureEditRequest;
import com.guangyu.guangyubackend.interfaces.dto.picture.PictureQueryRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceEditRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceQueryRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceUpdateRequest;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureVO;
import com.guangyu.guangyubackend.interfaces.vo.space.SpaceVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dmz xxx@163.com
 * @version 2025/6/10 22:04
 * @since JDK17
 */
@Slf4j
@RestController
@RequestMapping("/space")
public class SpaceController {
    @Autowired
    private SpaceLevelManager spaceLevelManager;

    @Autowired
    private UserApplicationService userApplicationService;

    @Autowired
    private PictureApplicationService pictureApplicationService;

    @Autowired
    private SpaceApplicationService spaceApplicationService;

    // region 增删查改
    /*
     * 创建用户私有空间
     *
     * @param spaceCreateRequest 用户空间创建信息
     * @return 用户空间 id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addSpace(SpaceAddRequest spaceAddRequest, HttpServletRequest httpServletRequest) {
        // 请求参数
        ThrowUtils.throwIf(spaceAddRequest == null, RespCode.PARAMS_ERROR, "请求失败");
        User loginUser = userApplicationService.getLoginUser(httpServletRequest);
        return ResultUtils.success(spaceApplicationService.addSpace(spaceAddRequest, loginUser));
    }

    /**
     * 管理员——用户私有空间更新
     *
     * @param spaceUpdateRequest 用户空间更新信息
     * @param httpServletRequest 请求信息
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(SpaceUpdateRequest spaceUpdateRequest,
        HttpServletRequest httpServletRequest) {
        // 请求参数校验
        ThrowUtils.throwIf(spaceUpdateRequest == null || spaceUpdateRequest.getId() <= 0, RespCode.PARAMS_ERROR,
            "请求失败");
        // 空间存在校验
        Long spaceId = spaceUpdateRequest.getId();
        spaceApplicationService.getSpaceById(spaceId);
        // 数据填充
        Space space = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
        spaceApplicationService.setSpaceLimitByLevel(space);
        // 用户空间数据校验
        space.validSpaceInfo(false);
        // 用户空间数据更新
        spaceApplicationService.updateSpace(space);

        return ResultUtils.success(true);
    }

    /**
     * 用户私有空间删除
     *
     * @param deleteRequest      用户空间删除信息
     * @param httpServletRequest 请求信息
     * @return 删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest,
        HttpServletRequest httpServletRequest) {
        // 请求参数校验
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, RespCode.PARAMS_ERROR, "请求失败");
        // 空间存在校验
        Long spaceId = deleteRequest.getId();
        Space existSpace = spaceApplicationService.getSpaceById(spaceId);
        // 权限校验（仅空间拥有者与管理员可操作）
        spaceApplicationService.checkSpaceAuth(userApplicationService.getLoginUser(httpServletRequest), existSpace);
        // 用户空间数据删除
        spaceApplicationService.deleteSpace(spaceId, userApplicationService.getLoginUser(httpServletRequest));

        return ResultUtils.success(true);
    }

    /*
     * 管理员权限
     * 空间——根据Id查询用户私有空间
     * @param id 用户私有空间id
     * @return 用户私有空间信息
     */
    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Space> getSpaceById(@PathVariable("id") Long id) {
        return ResultUtils.success(spaceApplicationService.getSpaceById(id));
    }

    /*
     * 用户私有空间信息查询(包装类信息查询)
     *
     * @param id 用户私有空间id
     * @return 用户私有空间脱敏信息
     */
    @GetMapping("/get/vo/{id}")
    public BaseResponse<SpaceVO> getSpaceVOById(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, RespCode.PARAMS_ERROR, "请求参数错误");

        return ResultUtils.success(spaceApplicationService.getSpaceVOById(id));
    }

    /*
     * 管理员权限
     * 分页获取用户私有空间信息列表
     *
     * @param spaceQueryRequest 用户空间信息查询对象
     * @return 私有空间信息
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Space>> listSpaceByPage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        ThrowUtils.throwIf(spaceQueryRequest == null, RespCode.PARAMS_ERROR, "请求参数错误");
        long current = spaceQueryRequest.getCurrent();
        long pageSize = spaceQueryRequest.getPageSize();
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, pageSize),
            spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        return ResultUtils.success(spacePage);
    }

    /**
     * 分页获取私有空间信息列表（封装类）
     *
     * @param spaceQueryRequest 用户空间信息查询对象
     * @param request           http请求信息
     * @return 图片信息
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<SpaceVO>> listSpaceVOByPage(@RequestBody SpaceQueryRequest spaceQueryRequest,
        HttpServletRequest request) {
        long current = spaceQueryRequest.getCurrent();
        long pageSize = spaceQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, RespCode.PARAMS_ERROR);

        // 查询数据库
        Page<Space> spacePage = spaceApplicationService.page(new Page<>(current, pageSize),
            spaceApplicationService.getQueryWrapper(spaceQueryRequest));
        // 获取封装类
        return ResultUtils.success(spaceApplicationService.getSpaceVOPage(spacePage, request));
    }

    /**
     * 用户——用户私有空间信息编辑
     *
     * @param spaceEditRequest 用户空间编辑信息 * @param request            http请求信息
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest, HttpServletRequest request) {
        // 请求参数校验
        ThrowUtils.throwIf(spaceEditRequest == null || spaceEditRequest.getId() <= 0, RespCode.PARAMS_ERROR);

        // 在此处将实体类和 DTO 进行转换
        Space space = SpaceAssembler.toSpaceEntity(spaceEditRequest);
        spaceApplicationService.editSpace(space, userApplicationService.getLoginUser(request));
        return ResultUtils.success(true);
    }

    // endregion

    /**
     * 获取空间级别列表，便于前端展示
     *
     * @return
     */
    @GetMapping("/list/level")
    public BaseResponse<Collection<SpaceLevel>> listSpaceLevel() {
        Collection<SpaceLevel> spaceLevelList = spaceLevelManager.getAllLevels();
        return ResultUtils.success(spaceLevelList);
    }
}

