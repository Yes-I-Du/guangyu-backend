package com.guangyu.guangyubackend.interfaces.controller;/**
 * 空间Controller服务
 *
 * @author Dmz Email:  * @since 2025/06/10 22:04
 */

import com.guangyu.guangyubackend.domain.space.valueobject.SpaceLevel;
import com.guangyu.guangyubackend.infrastructure.config.SpaceLevelConfig;
import com.guangyu.guangyubackend.infrastructure.manager.space.SpaceLevelManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author dmz xxx@163.com
 * @version 2025/6/10 22:04
 * @since JDK17
 */
@Slf4j
@RestController
@RequestMapping("/space")
public class SpaceController {
    @Autowired
    private SpaceLevelManager spaceLevelManager;

    @GetMapping("/debug/config")
    public Map<String, Object> debugConfig() {
        SpaceLevel spaceLevel = spaceLevelManager.getByValue(0);
        System.out.println(spaceLevel.getText());
        System.out.println(spaceLevel.getValue());
        System.out.println(spaceLevel.getMaxCount());
        System.out.println(spaceLevel.getMaxSize());
        return null;
    }
}

