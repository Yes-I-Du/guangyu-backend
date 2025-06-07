package com.guangyu.guangyubackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.domain.user.repository.UserRepository;
import com.guangyu.guangyubackend.infrastructure.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 * 用户仓储接口实现
 *
 * @author dmz xxx@163.com
 * @version 2025/5/29 22:35
 * @since JDK17
 */
@Service
public class UserRepositoryImpl extends ServiceImpl<UserMapper, User> implements UserRepository {
}

