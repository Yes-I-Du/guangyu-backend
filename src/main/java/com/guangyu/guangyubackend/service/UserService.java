package com.guangyu.guangyubackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guangyu.guangyubackend.model.dto.user.UserLoginRequest;
import com.guangyu.guangyubackend.model.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.model.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guangyu.guangyubackend.model.vo.LoginUserVO;
import com.guangyu.guangyubackend.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Dmz
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-05-18 19:04:32
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册信息
     * @return 新用户 id
     */
    long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest   用户登录信息
     * @param httpServletRequest http请求信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest);

    /**
     * 获取当前登录用户信息
     *
     * @param httpServletRequest http请求信息
     * @return 当前登录用户信息
     */
    User getLoginUser(HttpServletRequest httpServletRequest);

    /**
     * 获取登录用户脱敏信息
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 退出登录
     *
     * @param httpServletRequest http请求信息
     * @return 退出登录结果
     */
    boolean userLogout(HttpServletRequest httpServletRequest);

    /**
     * 单一用户信息脱敏
     *
     * @param user 用户信息
     * @return 脱敏后的用户信息
     */
    UserVO getUserVO(User user);

    /**
     * 多用户信息脱敏
     *
     * @param userlist 用户信息列表
     * @return 脱敏后的用户信息列表
     */
    List<UserVO> getUserVOList(List<User> userlist);

    /**
     * 构造QueryWrapper对象生成Sql查询
     *
     * @param userQueryRequest 用户信息查询对象
     * @return QueryWrapper QueryWrapper对象
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 获取加密密码
     *
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);

    /*
     *登录用户角色判断(是否为Admin)
     */
    boolean isAdmin(User user);
}
