package com.guangyu.guangyubackend.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户空间创建请求
 *
 * @author dmz xxx@163.com
 * @version 2025/5/26 21:39
 * @since JDK17
 */
@Data
public class SpaceAddRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;
}

