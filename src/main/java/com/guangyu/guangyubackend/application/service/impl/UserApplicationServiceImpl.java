package com.guangyu.guangyubackend.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.domain.user.service.UserDomainService;
import com.guangyu.guangyubackend.infrastructure.common.DeleteRequest;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.interfaces.dto.user.UserLoginRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.interfaces.vo.user.LoginUserVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.infrastructure.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

/**
 * @author Dmz
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-05-18 19:04:32
 */
@Service
@Slf4j
public class UserApplicationServiceImpl extends ServiceImpl<UserMapper, User> implements UserApplicationService {

    @Resource
    private UserDomainService userDomainService;

    @Override
    @Transactional
    public long userRegister(UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, RespCode.PARAMS_ERROR, "参数错误");
        // 账号
        String userAccount = userRegisterRequest.getUserAccount();
        // 密码
        String userPassword = userRegisterRequest.getUserPassword();
        // 确认密码
        String checkPassword = userRegisterRequest.getCheckPassword();
        // 用户注册信息校验
        User.validRegister(userAccount, userPassword, checkPassword);

        return userDomainService.userRegister(userAccount, userPassword);
    }

    @Override
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(userLoginRequest == null, RespCode.PARAMS_ERROR, "参数错误");
        // 账号
        String userAccount = userLoginRequest.getUserAccount();
        // 密码
        String userPassword = userLoginRequest.getUserPassword();
        // 用户登录信息校验
        User.validLogin(userAccount, userPassword);

        return userDomainService.userLogin(userAccount, userPassword, httpServletRequest);
    }

    @Override
    public User getLoginUser(HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(httpServletRequest == null, RespCode.PARAMS_ERROR, "参数错误");
        return userDomainService.getLoginUser(httpServletRequest);
    }

    @Override
    public boolean userLogout(HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(httpServletRequest == null, RespCode.PARAMS_ERROR, "参数错误");
        return userDomainService.userLogout(httpServletRequest);
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        return userDomainService.getLoginUserVO(user);
    }

    @Override
    public UserVO getUserVO(User user) {
        return userDomainService.getUserVO(user);
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userlist) {
        return userDomainService.getUserVOList(userlist);
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        return userDomainService.getQueryWrapper(userQueryRequest);
    }

    @Override
    public User getUserById(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, RespCode.PARAMS_ERROR, "用户id错误");
        User user = userDomainService.getById(id);
        ThrowUtils.throwIf(user == null, RespCode.NOT_FOUND_ERROR, "用户不存在");
        return user;
    }

    @Override
    public UserVO getUserVOById(Long id) {
        return this.getUserVO(this.getUserById(id));

    }

    @Override
    public long addUser(User user) {
        return userDomainService.addUser(user);
    }

    @Override
    public boolean deleteUser(DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "参数错误");
        }
        return userDomainService.removeUserById(deleteRequest.getId());
    }

    @Override
    public void updateUser(User user) {
        boolean result = userDomainService.updateUserById(user);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "更新失败");
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, RespCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage =
            userDomainService.page(new Page<>(current, pageSize), userDomainService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userDomainService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return userVOPage;
    }

    @Override
    public List<User> listByIds(Set<Long> userIdSet) {
        return userDomainService.listByIds(userIdSet);
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        return userDomainService.getEncryptPassword(userPassword);
    }

}