package com.guangyu.guangyubackend.application.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.interfaces.dto.picture.*;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureVO;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Dmz
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-05-21 23:48:44
 */
public interface PictureApplicationService {

    /**
     * 图片信息校验
     *
     * @param picture 图片信息
     */
    void vaildPicture(Picture picture);

    /**
     * 上传图片
     *
     * @param inputFileSource      图片上传方式(文件/Url)
     * @param pictureUploadRequest 上传图片参数
     * @param loginUser            当前登录用户
     * @return 图片脱敏信息
     */
    PictureVO uploadPicture(Object inputFileSource, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 获取图片脱敏信息
     *
     * @param picture 图片信息
     * @param request 请求
     * @return 图片脱敏信息
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片信息
     *
     * @param picturePage 图片分页信息
     * @param request     请求
     * @return 图片分页信息
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 构造QueryWrapper对象生成Sql查询
     *
     * @param pictureQueryRequest 图片信息查询对象
     * @return QueryWrapper QueryWrapper对象
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 图片信息审核
     *
     * @param pictureReviewRequest 审核信息
     * @param loginUser            当前登录用户
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 设置审核状态
     *
     * @param picture   图片信息
     * @param loginUser 当前登录用户
     */
    void setReviewStatus(Picture picture, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest 批量创建图片参数
     * @param loginUser                   当前登录用户
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);

    /**
     * 清理图片文件
     *
     * @param oldPicture 图片信息
     */
    @Async
    void clearPictureFile(Picture oldPicture);

    /**
     * 删除图片
     *
     * @param pictureId 图片Id
     * @param loginUser 当前登录用户
     */
    void deletePicture(long pictureId, User loginUser);

    /**
     * 编辑图片信息
     *
     * @param picture   图片信息
     * @param loginUser 当前登录用户
     */
    void editPicture(Picture picture, User loginUser);

    /**
     * 空间图片信息校验
     *
     * @param picture   图片信息
     * @param loginUser 当前登录用户
     */
    void checkPictureAuth(User loginUser, Picture picture);

    /**
     * 根据图片颜色搜索图片
     *
     * @param spaceId   空间Id
     * @param picColor  图片颜色
     * @param loginUser 当前登录用户
     * @return 图片列表
     */
    List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser);

    /**
     * 批量编辑图片
     *
     * @param pictureEditByBatchRequest 图片批量编辑请求
     * @param loginUser                 当前登录用户
     */
    void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser);
}
