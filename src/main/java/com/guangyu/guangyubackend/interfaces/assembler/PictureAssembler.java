package com.guangyu.guangyubackend.interfaces.assembler;

import cn.hutool.json.JSONUtil;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.interfaces.dto.picture.PictureEditRequest;
import com.guangyu.guangyubackend.interfaces.dto.picture.PictureUpdateRequest;
import com.guangyu.guangyubackend.interfaces.dto.picture.PictureUploadRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * 图片请求转换类
 *
 * @author dmz xxx@163.com
 * @version 2025/6/8 23:45
 * @since JDK17
 */
public class PictureAssembler {
    public static Picture toPictureEntity(PictureEditRequest pictureEditRequest) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        return picture;
    }

    public static Picture toPictureEntity(PictureUpdateRequest pictureUpdateRequest) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        return picture;
    }
}

