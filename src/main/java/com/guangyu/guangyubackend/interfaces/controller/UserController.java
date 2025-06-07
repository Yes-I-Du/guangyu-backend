package com.guangyu.guangyubackend.interfaces.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.UserApplicationService;
import com.guangyu.guangyubackend.domain.user.constant.UserConstant;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.annotation.AuthCheck;
import com.guangyu.guangyubackend.infrastructure.common.BaseResponse;
import com.guangyu.guangyubackend.infrastructure.common.DeleteRequest;
import com.guangyu.guangyubackend.infrastructure.common.ResultUtils;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.interfaces.assembler.UserAssembler;
import com.guangyu.guangyubackend.interfaces.dto.user.*;
import com.guangyu.guangyubackend.interfaces.vo.user.LoginUserVO;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务Controller
 *
 * @author dmz xxx@163.com
 * @version 2025/5/18 23:24
 * @since JDK17
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserApplicationService userApplicationService;

    // region 用户登录相关
    /*
     * 用户注册接口
     *
     * @param userRegisterRequest 用户注册信息
     * @return 新用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(UserRegisterRequest userRegisterRequest) {
        return ResultUtils.success(userApplicationService.userRegister(userRegisterRequest));
    }

    /*
     * 用户登录接口
     *
     * @param userRegisterRequest 用户登录信息
     * @return 脱敏后的用户信息
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(UserLoginRequest userLoginRequest,
        HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userApplicationService.userLogin(userLoginRequest, httpServletRequest));
    }

    /*
     * 退出登录
     *
     * @param httpServletRequest http请求信息
     * @return 退出登录结果
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest httpServletRequest) {
        return ResultUtils.success(userApplicationService.userLogout(httpServletRequest));
    }

    /*
     * 获取当前登录用户信息
     *
     * @param httpServletRequest http请求信息
     * @return 脱敏后用户登录信息
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest httpServletRequest) {
        User user = userApplicationService.getLoginUser(httpServletRequest);
        return ResultUtils.success(userApplicationService.getLoginUserVO(user));
    }

    // endregion

    // region 用户信息管理相关(增删改查操作)

    /*
     * 管理员权限
     * 用户管理-追加用户
     * @param userAddRequest 用户信息
     * @return 用户 id
     */
    @GetMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, RespCode.PARAMS_ERROR, "请求参数错误");
        return ResultUtils.success(userApplicationService.addUser(UserAssembler.toUserEntity(userAddRequest)));
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
        return ResultUtils.success(userApplicationService.getUserById(id));
    }

    /*
     * 用户信息查询(包装类信息查询)
     * @param id 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/vo/{id}")
    public BaseResponse<UserVO> getUserVOById(@PathVariable("id") Long id) {
        return ResultUtils.success(userApplicationService.getUserVOById(id));
    }

    /*
     * 管理员权限
     * 用户管理-用户信息删除
     * @param id 用户 id
     * @return 用户信息
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(userApplicationService.deleteUser(deleteRequest));
    }

    /*
     * 管理员权限
     * 用户管理-用户信息修改
     * @param userQueryRequest 用户信息
     * @return true
     */
    @PutMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        ThrowUtils.throwIf(userUpdateRequest == null, RespCode.PARAMS_ERROR, "请求参数错误");
        userApplicationService.updateUser(UserAssembler.toUserEntity(userUpdateRequest));
        return ResultUtils.success(true);
    }

    /*
     * 管理员权限
     * 分页获取用户信息列表
     * @param userQueryRequest 用户信息
     * @return 用户信息
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, RespCode.PARAMS_ERROR);
        Page<UserVO> userVOPage = userApplicationService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(userVOPage);
    }

    // endregion
}

