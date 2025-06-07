package com.guangyu.guangyubackend.interfaces.assembler;

import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.interfaces.dto.user.UserAddRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserUpdateRequest;
import org.springframework.beans.BeanUtils;

/**
 * 用户请求转换类
 *
 * @author dmz xxx@163.com
 * @version 2025/6/7 17:19
 * @since JDK17
 */
public class UserAssembler {
    public static User toUserEntity(UserAddRequest userAddRequest) {
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        return user;
    }

    public static User toUserEntity(UserUpdateRequest userUpdateRequest) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        return user;
    }
}

