package com.guangyu.guangyubackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.annotation.AuthCheck;
import com.guangyu.guangyubackend.common.BaseResponse;
import com.guangyu.guangyubackend.common.DeleteRequest;
import com.guangyu.guangyubackend.common.ResultUtils;
import com.guangyu.guangyubackend.constant.UserConstant;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.model.dto.user.UserAddRequest;
import com.guangyu.guangyubackend.model.dto.user.UserLoginRequest;
import com.guangyu.guangyubackend.model.dto.user.UserQueryRequest;
import com.guangyu.guangyubackend.model.dto.user.UserRegisterRequest;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.vo.LoginUserVO;
import com.guangyu.guangyubackend.model.vo.UserVO;
import com.guangyu.guangyubackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author dmz xxx@163.com
 * @version 2025/5/18 23:24
 * @since JDK17
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /*
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册信息
     * @return 新用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userService.userRegister(userRegisterRequest));
    }

    /*
     * 用户登录接口
     *
     * @param userRegisterRequest 用户登录信息
     * @return 脱敏后的用户信息
     */
    @GetMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(UserLoginRequest userLoginRequest,
        HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(userLoginRequest == null, RespCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogin(userLoginRequest, httpServletRequest));
    }

    /*
     * 获取当前登录用户信息
     *
     * @param httpServletRequest http请求信息
     * @return 脱敏后用户登录信息
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest httpServletRequest) {
        User user = userService.getLoginUser(httpServletRequest);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    /*
     * 退出登录
     *
     * @param httpServletRequest http请求信息
     * @return 退出登录结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(httpServletRequest == null, RespCode.PARAMS_ERROR);
        return ResultUtils.success(userService.userLogout(httpServletRequest));
    }

    /*
     * 管理员权限
     * 用户管理-追加用户
     * @param userAddRequest 用户信息
     * @return 用户 id
     */
    @GetMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, RespCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        // 加密
        String encryptPassword = userService.getEncryptPassword(UserConstant.DEFAULT_PASSWORD);
        // 设置密码
        user.setUserPassword(encryptPassword);
        // 登录
        ThrowUtils.throwIf(!userService.save(user), RespCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /*
     * 管理员权限
     * 用户管理-用户信息查询
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id <= 0, RespCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, RespCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /*
     * 用户信息查询(包装类信息查询)
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/vo/{id}")
    public BaseResponse<UserVO> getUserVO(@PathVariable("id") Long id) {
        ThrowUtils.throwIf(id <= 0, RespCode.PARAMS_ERROR);
        BaseResponse<User> responseUser = getUserById(id);
        return ResultUtils.success(userService.getUserVO(responseUser.getData()));
    }

    /*
     * 管理员权限
     * 用户管理-用户信息删除
     * @param id 用户 id
     * @return 用户信息
     */
    @DeleteMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, RespCode.PARAMS_ERROR);
        return ResultUtils.success(userService.removeById(deleteRequest.getId()));
    }

    /*
     * 管理员权限
     * 用户管理-用户信息修改
     * @param userQueryRequest 用户信息
     * @return true
     */
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, RespCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userQueryRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /*
     * 管理员权限
     * 分页获取用户信息列表
     * @param userQueryRequest 用户信息
     * @return 用户信息
     */
    @GetMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, RespCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage =
            userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }
}

