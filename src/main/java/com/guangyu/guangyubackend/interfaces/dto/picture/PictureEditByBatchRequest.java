package com.guangyu.guangyubackend.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片批量编辑请求
 *
 * @author dmz xxx@163.com
 * @version 2025/6/7 20:29
 * @since JDK17
 */
@Data
public class PictureEditByBatchRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /* 图片Id 列表 */
    private List<Long> pictureIdList;

    /* 空间Id */
    private Long spaceId;

    /* 分类 */
    private String category;

    /* 标签 */
    private List<String> tagList;

    /* 命名规则 */
    private String nameRule;
}

