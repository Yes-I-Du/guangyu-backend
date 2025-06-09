package com.guangyu.guangyubackend.domain.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.common.DeleteRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserLoginRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.interfaces.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.interfaces.vo.user.LoginUserVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author Dmz
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-05-18 19:04:32
 */
public interface UserDomainService {

    /**
     * 用户注册
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword);

    /**
     * 用户登录
     *
     * @param userAccount        用户账号
     * @param userPassword       用户密码
     * @param httpServletRequest http请求信息
     * @return 脱敏后的用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

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

    /*
     * 根据id获取用户信息
     *
     * @param id 用户id
     * @return 用户信息
     */
    User getById(Long id);

    /**
     * 添加用户
     *
     * @param user 用户信息
     * @return 新用户 id
     */
    long addUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户Id
     * @return 删除结果
     */
    boolean removeUserById(Long id);

    /**
     * 更新用户
     *
     * @param user 用户信息
     * @return 更新结果
     */
    boolean updateUserById(User user);

    /**
     * 分页获取用户信息列表
     *
     * @param userPage 用户信息
     * @return 用户信息列表
     */
    Page<User> page(Page<User> userPage, QueryWrapper<User> queryWrapper);

    /**
     * 根据id集合获取用户信息
     *
     * @param userIdSet 用户id集合
     * @return 用户信息列表
     */
    List<User> listByIds(Set<Long> userIdSet);

    /**
     * 获取加密密码
     *
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);
}
