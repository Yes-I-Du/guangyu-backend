package com.guangyu.guangyubackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除通用请求类
 * @author dmz xxx@163.com
 * @version 2025/5/18 0:21
 * @since JDK17
 */
@Data
public class DeleteRequest implements Serializable {

    /* 主键ID */
    private Long id;

    private static final long serialVersionUID = 1L;
}

