package com.guangyu.guangyubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.model.dto.picture.PictureQueryRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureUploadRequest;
import com.guangyu.guangyubackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dmz
 * @description 针对表【picture(图片)】的数据库操作Service
 * @createDate 2025-05-21 23:48:44
 */
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest 上传图片参数
     * @param loginUser                 当前登录用户
     * @return 图片脱敏信息
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

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
     * 图片信息校验
     *
     * @param picture 图片信息
     */
    void vaildPicture(Picture picture);
}
