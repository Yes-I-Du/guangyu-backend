package com.guangyu.guangyubackend.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户图片编辑请求类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/22 22:10
 * @since JDK17
 */
@Data
public class PictureEditRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /*
     * 图片ID(用于修改图片)
     * 图片需要支持重复上传，不改变图片基础信息，只改变图片文件
     * */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;
}

