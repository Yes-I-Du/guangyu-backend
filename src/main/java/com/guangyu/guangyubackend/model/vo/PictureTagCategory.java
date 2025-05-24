package com.guangyu.guangyubackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 图片标签分类列表信息类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/23 21:50
 * @since JDK17
 */
@Data
public class PictureTagCategory {
    /* 标签列表 */
    List<String> TagList;

    /* 分类列表 */
    List<String> CategoryList;
}

