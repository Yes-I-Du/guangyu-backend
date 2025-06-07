package com.guangyu.guangyubackend.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 批量创建图片请求参数类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/25 22:40
 * @since JDK17
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜索关键词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 图片名称前缀
     */
    private String namePrefix;
}

