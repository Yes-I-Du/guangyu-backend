package com.guangyu.guangyubackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页通用请求类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/18 0:18
 * @since JDK17
 */
@Data
public class PageRequest implements Serializable {

    /* 当前页号 */
    private int current = 1;

    /* 页面大小 */
    private int pageSize = 10;

    /* 排序字段 */
    private String sortField;

    /* 排序顺序（默认降序） */
    private String sortOrder = "descend";
}

