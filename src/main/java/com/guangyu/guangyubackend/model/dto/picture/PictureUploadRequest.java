package com.guangyu.guangyubackend.model.dto.picture;

import lombok.Data;
import java.io.Serializable;
/**
 * 图片上传请求参数类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/21 23:54
 * @since JDK17
 */
@Data
public class PictureUploadRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /*
     * 图片ID(用于修改图片)
     * 图片需要支持重复上传，不改变图片基础信息，只改变图片文件
     * */
    private Long id;

    /**
     * 文件上传Url
     */
    private String fileUrl;

    /**
     * 图片名称
     */
    private String picName;
}

