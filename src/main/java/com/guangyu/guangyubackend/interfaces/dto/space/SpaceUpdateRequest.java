package com.guangyu.guangyubackend.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户空间更新请求
 *
 * @author dmz xxx@163.com
 * @version 2025/5/26 21:46
 * @since JDK17
 */
@Data
public class SpaceUpdateRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;
}

