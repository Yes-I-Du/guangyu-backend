package com.guangyu.guangyubackend.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.PictureApplicationService;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.picture.service.PictureDomainService;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.interfaces.dto.picture.*;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmz
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-05-21 23:48:44
 */
@Service
@Slf4j
public class PictureApplicationServiceImpl implements PictureApplicationService {
    @Resource
    private UserApplicationService userApplicationService;

    @Resource
    private PictureDomainService pictureDomainService;

    @Override
    public void vaildPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, RespCode.PARAMS_ERROR, "图片不存在");
        // 图片校验
        picture.vaildPicture();
    }

    @Override
    public PictureVO uploadPicture(Object inputFileSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 图片校验
        ThrowUtils.throwIf(inputFileSource == null, RespCode.PARAMS_ERROR);
        // 用户登录
        ThrowUtils.throwIf(loginUser == null, RespCode.NOT_LOGIN_ERROR);

        return pictureDomainService.uploadPicture(inputFileSource, pictureUploadRequest, loginUser);
    }

    @Override
    public PictureVO getPictureVO(Picture picture,HttpServletRequest request) {
        // 对象转换
        PictureVO pictureVO = PictureVO.PictureToVo(picture);

        //关联用户信息
        Long userId = picture.getUserId();
        if (userId != null) {
            User user = userApplicationService.getUserById(userId);
            UserVO userVO = userApplicationService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    @Override
    public Picture getPictureById(Long id) {
        Picture picture = pictureDomainService.getById(id);
        ThrowUtils.throwIf(picture == null, RespCode.NOT_FOUND_ERROR, "图片信息不存在");
        return picture;
    }

    @Override
    public PictureVO getPictureVOById(Picture picture, HttpServletRequest request) {
        return this.getPictureVO(picture,request);

    }

    @Override
    public Page<Picture> page(Page<Picture> picturePage, QueryWrapper<Picture> queryWrapper) {
        return pictureDomainService.page(picturePage, queryWrapper);
    }

    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage =
            new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::PictureToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap =
            userApplicationService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userApplicationService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        return pictureDomainService.getQueryWrapper(pictureQueryRequest);
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        pictureDomainService.doPictureReview(pictureReviewRequest, loginUser);
    }

    @Override
    public void setReviewStatus(Picture picture, User loginUser) {
        pictureDomainService.setReviewStatus(picture, loginUser);
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        return pictureDomainService.uploadPictureByBatch(pictureUploadByBatchRequest, loginUser);
    }

    @Override
    public void updatePicture(Picture picture) {
        boolean result = pictureDomainService.updatePictureById(picture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "更新失败");
    }

    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        pictureDomainService.clearPictureFile(oldPicture);
    }

    @Override
    public void deletePicture(long pictureId, User loginUser) {
        pictureDomainService.deletePicture(pictureId, loginUser);
    }

    @Override
    public void editPicture(Picture picture, User loginUser) {
        pictureDomainService.editPicture(picture, loginUser);
    }

    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        pictureDomainService.checkPictureAuth(loginUser, picture);
    }

    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        return pictureDomainService.searchPictureByColor(spaceId, picColor, loginUser);
    }

    @Override
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {
        pictureDomainService.editPictureByBatch(pictureEditByBatchRequest, loginUser);
    }

}
