package com.guangyu.guangyubackend.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据图片搜索图片请求(以图搜图)
 *
 * @author dmz xxx@163.com
 * @version 2025/6/7 20:37
 * @since JDK17
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 图片Id */
    private Long pictureId;
}

