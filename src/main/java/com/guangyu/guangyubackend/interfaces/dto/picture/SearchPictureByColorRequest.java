package com.guangyu.guangyubackend.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据颜色搜索图片请求
 *
 * @author dmz xxx@163.com
 * @version 2025/6/7 20:35
 * @since JDK17
 */
@Data
public class SearchPictureByColorRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 图片主色 */
    private String color;

    /* 空间Id */
    private Long spaceId;
}


