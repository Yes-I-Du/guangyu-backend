package com.guangyu.guangyubackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.domain.spaceuser.entity.SpaceUser;
import com.guangyu.guangyubackend.domain.spaceuser.repository.SpaceUserRepository;
import com.guangyu.guangyubackend.infrastructure.mapper.SpaceUserMapper;
import org.springframework.stereotype.Service;

/**
 * 团队空间仓储接口实现
 *
 * @author dmz xxx@163.com
 * @version 2025/6/23 15:29
 * @since JDK17
 */
@Service
public class SpaceUserRepositoryImpl extends ServiceImpl<SpaceUserMapper, SpaceUser> implements SpaceUserRepository {
}

