package com.guangyu.guangyubackend.domain.space.valueobject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户空间级别信息枚举类
 *
 * @author Dmz Email:  * @since 2025/05/26 22:11
 */
@Getter
public enum SpaceLevelEnum {
    COMMON("普通版", 0, 100, 100L * 1024 * 1024), PROFESSIONAL("专业版", 1, 1000, 1000L * 1024 * 1024),
    FLAGSHIP("旗舰版", 2, 10000, 10000L * 1024 * 1024);

    private final String text;

    private final int value;

    private final long maxCount;

    private final long maxSize;

    /**
     * @param text     文本
     * @param value    值
     * @param maxSize  最大图片总大小
     * @param maxCount 最大图片总数量
     */
    SpaceLevelEnum(String text, int value, long maxCount, long maxSize) {
        this.text = text;
        this.value = value;
        this.maxCount = maxCount;
        this.maxSize = maxSize;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static SpaceLevelEnum getSpaceLevelEnumByValue(Integer value) {
        // 如果value为空或者value==null,返回null
        if (ObjUtil.isNull(value) || ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceLevelEnum SpaceLevelEnum : SpaceLevelEnum.values()) {
            if (SpaceLevelEnum.getValue() == value) {
                return SpaceLevelEnum;
            }
        }
        return null;
    }
}
