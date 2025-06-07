package com.guangyu.guangyubackend.interfaces.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.PictureApplicationService;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.picture.valueobject.PictureReviewStatusEnum;
import com.guangyu.guangyubackend.domain.user.constant.UserConstant;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.annotation.AuthCheck;
import com.guangyu.guangyubackend.infrastructure.common.BaseResponse;
import com.guangyu.guangyubackend.infrastructure.common.DeleteRequest;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.interfaces.dto.picture.*;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureTagCategory;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureVO;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 图片功能模块服务Controller
 *
 * @author dmz xxx@163.com
 * @version 2025/5/21 23:09
 * @since JDK17
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private PictureApplicationService pictureApplicationService;

   // TODO: 2025/5/22 未完成 本地缓存Caffeine
     // ...
    // ...

    /**
     * 用户图片上传(文件方式)
     *
     * @param multipartFile        上传的文件
     * @param pictureUploadRequest 图片上传信息
     * @param httpServletRequest   http请求信息
     * @return 图片信息
     */
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
        PictureUploadRequest pictureUploadRequest, HttpServletRequest httpServletRequest) {
        User loginUser = userApplicationService.getLoginUser(httpServletRequest);
        PictureVO pictureVO = pictureApplicationService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 用户图片上传(Url方式)
     *
     * @param pictureUploadRequest 图片上传信息
     * @param httpServletRequest   http请求信息
     * @return 图片信息
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(@RequestBody PictureUploadRequest pictureUploadRequest,
        HttpServletRequest httpServletRequest) {
        User loginUser = userApplicationService.getLoginUser(httpServletRequest);
        String pictureUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureApplicationService.uploadPicture(pictureUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest 批量创建图片参数
     * @param request                     http请求
     * @return 成功创建的图片数
     */
    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPictureByBatch(
        @RequestBody PictureUploadByBatchRequest pictureUploadByBatchRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUploadByBatchRequest == null, RespCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(request);
        int uploadCount = pictureService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
        return ResultUtils.success(uploadCount);
    }

    /**
     * 图片删除 该图片只有用户本人或者管理员可以删除
     *
     * @param deleteRequest      图片删除信息
     * @param httpServletRequest http请求信息
     * @return 删除结果
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest,
        HttpServletRequest httpServletRequest) {
        // 请求校验
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, RespCode.PARAMS_ERROR);
        User loginUser = userApplicationService.getLoginUser(httpServletRequest);
        pictureApplicationService.deletePicture(deleteRequest.getId(), loginUser);
        return ResultUtils.success(true);
    }

    /*
     * 图片更新 仅管理员权限可用
     *
     * @param pictureUpdateRequest 图片更新信息
     * @param request              http请求信息
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
        HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0, RespCode.PARAMS_ERROR);
        // 实体类与DTO类转换 TODO:参照USER生成转换方法assembler
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 图片数据校验
        pictureApplicationService.vaildPicture(picture);
        // 图片存在信息校验
        Long pictureId = pictureUpdateRequest.getId();
        Picture existPicture = pictureApplicationService.getById(pictureId);
        ThrowUtils.throwIf(existPicture == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
        // 设置审核参数
        pictureApplicationService.setReviewStatus(existPicture, userApplicationService.getLoginUser(request));
        // 更新图片信息
        boolean result = pictureApplicationService.updateById(picture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "图片更新失败");
        return ResultUtils.success(result);
    }

    /**
     * 根据id获取图片 仅管理员权限可用
     *
     * @param id 图片id
     * @return 图片信息
     */
    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(@RequestParam("id") Long id) {
        ThrowUtils.throwIf(id <= 0, RespCode.PARAMS_ERROR);
        Picture picture = pictureApplicationService.getById(id);
        ThrowUtils.throwIf(picture == null, RespCode.NOT_FOUND_ERROR);
        return ResultUtils.success(picture);
    }

    /**
     * 根据Id获取图片数据
     *
     * @param id                 图片id
     * @param httpServletRequest http请求信息
     * @return 图片信息
     */
    @GetMapping("/get/vo/{id}")
    public BaseResponse<PictureVO> getPictureVOById(@PathVariable("id") Long id,
        HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(id <= 0, RespCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        ThrowUtils.throwIf(picture == null, RespCode.NOT_FOUND_ERROR);
        return ResultUtils.success(pictureService.getPictureVO(picture, httpServletRequest));
    }

    /*
     * 管理员权限
     * 分页获取图片信息列表
     * @param pictureQueryRequest 图片信息查询对象
     * @return 图片信息
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        ThrowUtils.throwIf(pictureQueryRequest == null, RespCode.PARAMS_ERROR);
        long current = pictureQueryRequest.getCurrent();
        long pageSize = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage =
            pictureApplicationService.page(new Page<>(current, pageSize), pictureApplicationService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取图片列表（封装类）
     *
     * @param pictureQueryRequest 图片信息查询对象
     * @param request             http请求信息
     * @return 图片信息
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
        HttpServletRequest request) {
        long current = pictureQueryRequest.getCurrent();
        long size = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, RespCode.PARAMS_ERROR);
        // 限制用户查看条件(默认普通用户只能查看已过审的图片)
        pictureQueryRequest.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
        // 查询数据库
        Page<Picture> picturePage =
            pictureService.page(new Page<>(current, size), pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    /**
     * 编辑图片（给用户使用）
     *
     * @param pictureEditRequest 图片编辑信息 * @param request            http请求信息
     * @return 编辑结果
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
        HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() <= 0, RespCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        pictureService.vaildPicture(picture);
        User loginUser = userApplicationService.getLoginUser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, RespCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userApplicationService.isAdmin(loginUser)) {
            throw new BusinessException(RespCode.NO_AUTH_ERROR);
        }
        // 设置审核参数
        pictureService.setReviewStatus(picture, loginUser);
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * 获取图片标签分类列表
     *
     * @return
     */
    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }

    /**
     * 图片审核 (管理员权限)
     *
     * @param pictureReviewRequest 图片审核请求
     * @param request              http请求
     * @return 审核结果
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/review")
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest,
        HttpServletRequest request) {
        ThrowUtils.throwIf(
            pictureReviewRequest == null || pictureReviewRequest.getId() == null || pictureReviewRequest.getId() <= 0,
            RespCode.PARAMS_ERROR);
        pictureService.doPictureReview(pictureReviewRequest, userApplicationService.getLoginUser(request));
        return ResultUtils.success(true);
    }

}

