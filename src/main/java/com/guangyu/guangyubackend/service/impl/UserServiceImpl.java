package com.guangyu.guangyubackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.model.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.enums.UserRoleEnum;
import com.guangyu.guangyubackend.service.UserService;
import com.guangyu.guangyubackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * @author Dmz
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-05-18 19:04:32
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        // 账号密码校验
        // 账号密码为空的场合
        ThrowUtils.throwIf(StrUtil.hasBlank(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(),
            userRegisterRequest.getCheckPassword()), RespCode.PARAMS_ERROR, "用户名或密码不能为空");

        // 账号长度<6的场合
        ThrowUtils.throwIf(userRegisterRequest.getUserAccount().length() < 6, RespCode.PARAMS_ERROR,
            "用户名长度不能少于6");

        // 密码长度<8的场合
        ThrowUtils.throwIf(
            userRegisterRequest.getUserPassword().length() < 8 || userRegisterRequest.getCheckPassword().length() < 8,
            RespCode.PARAMS_ERROR, "密码长度不能少于8");

        // 两次密码不一致的场合
        ThrowUtils.throwIf(!userRegisterRequest.getUserPassword().equals(userRegisterRequest.getCheckPassword()),
            RespCode.PARAMS_ERROR, "两次密码不一致");

        // 账号重复检查
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userRegisterRequest.getUserAccount());
        ThrowUtils.throwIf(this.baseMapper.selectCount(queryWrapper) > 0, RespCode.PARAMS_ERROR, "账号已存在");

        // 加密
        String encryptPassword = this.getEncryptPassword(userRegisterRequest.getUserPassword());

        // 数据入库
        User user = new User();
        user.setUserAccount(userRegisterRequest.getUserAccount());
        user.setUserPassword(encryptPassword);
        user.setUserName("用户" + userRegisterRequest.getUserAccount());
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, RespCode.SYSTEM_ERROR, "注册失败,请稍后再试！！！");

        return user.getId();
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "guangYuUserPasswordSalt";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

}