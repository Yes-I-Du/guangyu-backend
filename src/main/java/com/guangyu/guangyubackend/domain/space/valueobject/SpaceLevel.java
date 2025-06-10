package com.guangyu.guangyubackend.domain.space.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 空间等级实体
 *
 * @author dmz xxx@163.com
 * @version 2025/6/10 21:28
 * @since JDK17
 */
@Getter
@AllArgsConstructor
public class SpaceLevel {
    /* 文本 */
    private final String text;
    /* 值 */
    private final int value;
    /* 最大图片总大小 */
    private final long maxCount;
    /* 最大图片总数量 */
    private final long maxSize;
}

