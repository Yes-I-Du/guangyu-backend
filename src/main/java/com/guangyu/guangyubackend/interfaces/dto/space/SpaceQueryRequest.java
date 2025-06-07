package com.guangyu.guangyubackend.interfaces.dto.space;

import java.io.Serializable;

/**
 * 用户空间查询请求
 *
 * @author dmz xxx@163.com
 * @version 2025/5/26 21:49
 * @since JDK17
 */
public class SpaceQueryRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;
}

