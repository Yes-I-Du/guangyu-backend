package com.guangyu.guangyubackend.infrastructure.config;

import com.guangyu.guangyubackend.infrastructure.manager.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

/**
 * 空间等级配置类
 *
 * @author dmz xxx@163.com
 * @version 2025/6/10 21:20
 * @since JDK17
 */
@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "space")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application-space.yml")
public class SpaceLevelConfig {
    private Map<String, SpaceConfig> levels = new HashMap<>();

    @Data
    public static class SpaceConfig {
        /* 文本 */
        private String text = "";
        /* 值 */
        private int value = -1;
        /* 最大图片总大小 */
        private long maxCount = 0;
        /* 最大图片总数量 */
        private long maxSize = 0;
    }
}

