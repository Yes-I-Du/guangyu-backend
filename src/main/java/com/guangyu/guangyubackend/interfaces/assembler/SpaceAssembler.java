package com.guangyu.guangyubackend.interfaces.assembler;

import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceEditRequest;
import com.guangyu.guangyubackend.interfaces.dto.space.SpaceUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * 用户私有空间相关请求DTO转换
 *
 * @author dmz xxx@163.com
 * @version 2025/6/12 22:55
 * @since JDK17
 */
public class SpaceAssembler {
    public static Space toSpaceEntity(SpaceUpdateRequest spaceUpdateRequest) {
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest, space);
        return space;
    }

    public static Space toSpaceEntity(SpaceEditRequest spaceEditRequest) {
        Space space = new Space();
        BeanUtils.copyProperties(spaceEditRequest, space);
        return space;
    }
}

