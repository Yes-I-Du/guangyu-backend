package com.guangyu.guangyubackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.exception.BusinessException;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.model.dto.user.UserLoginRequest;
import com.guangyu.guangyubackend.model.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.model.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.enums.UserRoleEnum;
import com.guangyu.guangyubackend.model.vo.LoginUserVO;
import com.guangyu.guangyubackend.model.vo.UserVO;
import com.guangyu.guangyubackend.service.UserService;
import com.guangyu.guangyubackend.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.guangyu.guangyubackend.constant.UserConstant.USER_LOGIN_STATUS;

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
    public LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        // 账号密码参数校验
        // 账号密码为空的场合
        ThrowUtils.throwIf(StrUtil.hasBlank(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword()),
            RespCode.PARAMS_ERROR, "用户名或密码不能为空");

        // 账号长度<6的场合
        ThrowUtils.throwIf(userLoginRequest.getUserAccount().length() < 6, RespCode.PARAMS_ERROR,
            "用户名长度不能少于6");

        // 密码长度<8的场合
        ThrowUtils.throwIf(userLoginRequest.getUserPassword().length() < 8, RespCode.PARAMS_ERROR, "密码长度不能少于8");

        // 加密
        String encryptPassword = this.getEncryptPassword(userLoginRequest.getUserPassword());

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userLoginRequest.getUserAccount());
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.baseMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, RespCode.PARAMS_ERROR, "用户不存在或密码错误");

        // 记录用户登陆状态
        httpServletRequest.getSession().setAttribute(USER_LOGIN_STATUS, user);

        // 返回用户脱敏信息
        return this.getLoginUserVO(user);
    }

    @Override
    public User getLoginUser(HttpServletRequest httpServletRequest) {
        // 判断当前用户是否登录
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User)userObj;
        ThrowUtils.throwIf(user == null || user.getId() == null || user.getId() <= 0, RespCode.NOT_LOGIN_ERROR);

        long userId = user.getId();
        user = this.getById(userId);
        ThrowUtils.throwIf(user == null, RespCode.NOT_LOGIN_ERROR, "用户不存在");

        return user;
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public boolean userLogout(HttpServletRequest httpServletRequest) {
        // 判断用户登录状态
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATUS);
        User user = (User)userObj;
        ThrowUtils.throwIf(user == null || user.getId() == null, RespCode.NOT_LOGIN_ERROR);
        // 退出登录
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATUS);
        return true;
    }

    @Override
    public UserVO getUserVO(User user) {
        // 用户信息为空
        if (user == null) {
            return null;
        }

        // 用户信息脱敏
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<User> userlist) {
        // 用户信息列表为空
        if (CollUtil.isEmpty(userlist)) {
            return new ArrayList<>();
        }
        return userlist.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "guangYuUserPasswordSalt";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

}