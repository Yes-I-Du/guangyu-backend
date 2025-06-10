package com.guangyu.guangyubackend.infrastructure.manager.space;

import com.guangyu.guangyubackend.domain.space.valueobject.SpaceLevel;
import com.guangyu.guangyubackend.infrastructure.config.SpaceLevelConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 空间等级管理类
 *
 * @author dmz xxx@163.com
 * @version 2025/6/10 21:25
 * @since JDK17
 */
@Component
public class SpaceLevelManager implements InitializingBean {
    private static final Map<Integer, SpaceLevel> LEVEL_MAP = new ConcurrentHashMap<>();

    private final SpaceLevelConfig properties;

    public SpaceLevelManager(SpaceLevelConfig properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        properties.getLevels().forEach((key, config) -> {
            LEVEL_MAP.put(config.getValue(), new SpaceLevel(
                config.getText(),
                config.getValue(),
                config.getMaxCount(),
                config.getMaxSize()
            ));
        });
    }

    // 根据value获取空间等级
    public static SpaceLevel getByValue(Integer value) {
        if (value == null) return null;
        return LEVEL_MAP.get(value);
    }

    // 获取所有等级
    public static Collection<SpaceLevel> getAllLevels() {
        return LEVEL_MAP.values();
    }
}

