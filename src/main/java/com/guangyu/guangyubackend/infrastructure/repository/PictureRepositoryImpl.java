package com.guangyu.guangyubackend.infrastructure.repository;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.picture.repository.PictureRepository;
import com.guangyu.guangyubackend.infrastructure.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
 * 图片仓储接口实现
 *
 * @author dmz xxx@163.com
 * @version 2025/6/7 18:19
 * @since JDK17
 */
@Service
public class PictureRepositoryImpl extends ServiceImpl<PictureMapper, Picture> implements PictureRepository {

}

