package com.guangyu.guangyubackend.infrastructure.manager.upload.model.dto.file;

import lombok.Data;

/**
 * 图片解析信息参数接受类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/22 20:06
 * @since JDK17
 */
@Data
public class UploadPictureResult {
    /* 图片url */
    private String url;

    /* 图片名称 */
    private String picName;

    /* 图片体积 */
    private Long picSize;

    /* 图片宽度 */
    private Integer picWidth;

    /* 图片高度 */
    private Integer picHeight;

    /* 图片比例 */
    private Double picScale;

    /* 图片格式 */
    private String picFormat;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

}

