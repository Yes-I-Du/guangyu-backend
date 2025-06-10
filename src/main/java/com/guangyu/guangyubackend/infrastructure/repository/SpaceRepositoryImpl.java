package com.guangyu.guangyubackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.repository.SpaceRepository;
import com.guangyu.guangyubackend.infrastructure.mapper.SpaceMapper;
import org.springframework.stereotype.Service;

/**
 * 用户空间仓储接口实现
 *
 * @author dmz xxx@163.com
 * @version 2025/6/10 23:06
 * @since JDK17
 */
@Service
public class SpaceRepositoryImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceRepository {
}

