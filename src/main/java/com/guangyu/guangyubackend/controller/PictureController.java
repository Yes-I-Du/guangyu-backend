package com.guangyu.guangyubackend.controller;/**
 * 文件对象服务Controller
 *
 * @author Dmz Email:  * @since 2025/05/21 23:09
 */

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.annotation.AuthCheck;
import com.guangyu.guangyubackend.common.BaseResponse;
import com.guangyu.guangyubackend.common.DeleteRequest;
import com.guangyu.guangyubackend.common.ResultUtils;
import com.guangyu.guangyubackend.constant.UserConstant;
import com.guangyu.guangyubackend.exception.BusinessException;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.manager.CosManager;
import com.guangyu.guangyubackend.model.dto.picture.PictureEditRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureQueryRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureUpdateRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureUploadRequest;
import com.guangyu.guangyubackend.model.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.model.entity.Picture;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.vo.PictureTagCategory;
import com.guangyu.guangyubackend.model.vo.PictureVO;
import com.guangyu.guangyubackend.model.vo.UserVO;
import com.guangyu.guangyubackend.service.PictureService;
import com.guangyu.guangyubackend.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author dmz xxx@163.com
 * @version 2025/5/21 23:09
 * @since JDK17
 */
@RestController
@RequestMapping("/picture")
@Log4j2
public class PictureController {
    @Autowired
    private CosManager cosManager;
    @Autowired
    private PictureService pictureService;
    @Autowired
    private UserService userService;

    /**
     * 图片上传
     *
     * @param multipartFile        上传的文件
     * @param pictureUploadRequest 图片上传信息
     * @param httpServletRequest   http请求信息
     * @return 图片信息
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
        PictureUploadRequest pictureUploadRequest, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
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
        // 图片信息校验
        Long pictureId = deleteRequest.getId();
        Picture picture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(picture == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
        // 权限校验
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(loginUser == null, RespCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser),
            RespCode.NO_AUTH_ERROR, "无权限删除");
        // 删除图片(数据库信息删除)
        boolean result = pictureService.removeById(pictureId);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "图片删除失败");
        return ResultUtils.success(result);
    }

    /*
     * 图片更新 仅管理员权限可用
     *
     * @param pictureUpdateRequest 图片更新信息
     * @return 更新结果
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest) {
        ThrowUtils.throwIf(pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0, RespCode.PARAMS_ERROR);
        // 实体类与DTO类转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 图片更新信息校验
        pictureService.vaildPicture(picture);
        // 图片存在信息校验
        Long pictureId = pictureUpdateRequest.getId();
        Picture existPicture = pictureService.getById(pictureId);
        ThrowUtils.throwIf(existPicture == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
        // 更新图片信息
        boolean result = pictureService.updateById(picture);
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
        Picture picture = pictureService.getById(id);
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
            pictureService.page(new Page<>(current, pageSize), pictureService.getQueryWrapper(pictureQueryRequest));
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
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtils.throwIf(oldPicture == null, RespCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(RespCode.NO_AUTH_ERROR);
        }
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

}

