package com.guangyu.guangyubackend.domain.space.valueobject;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 用户空间类型枚举类
 *
 * @author Dmz Email:  * @since 2025/06/11 22:30
 */
@Getter
public enum SpaceTypeEnum {
    PRIVATE("私有空间", 0), TEAM("团队空间", 1);

    private final String text;

    private final int value;

    SpaceTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value 枚举值
     * @return 枚举
     */
    public static SpaceTypeEnum getSpaceTypeEnumByValue(Integer value) {
        // 如果value为空或者value==null,返回null
        if (ObjUtil.isNull(value) || ObjUtil.isEmpty(value)) {
            return null;
        }
        for (SpaceTypeEnum spaceTypeEnum : SpaceTypeEnum.values()) {
            if (spaceTypeEnum.value == value) {
                return spaceTypeEnum;
            }
        }
        return null;
    }
}
