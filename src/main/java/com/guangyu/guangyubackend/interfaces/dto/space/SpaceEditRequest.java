package com.guangyu.guangyubackend.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户空间编辑请求
 *
 * @author dmz xxx@163.com
 * @version 2025/5/26 21:41
 * @since JDK17
 */
@Data
public class SpaceEditRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;
}

