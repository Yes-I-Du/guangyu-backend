package com.guangyu.guangyubackend.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传下载相关常量接口
 *
 * @author Dmz Email:  * @since 2025/05/22 20:31
 */
public interface FileConstant {
    /* 允许上传图片最大大小：1M */ long PICTURE_MAX_SIZE = 2 * 1024 * 1024L;

    /* 允许上传图片格式 */
    final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");
}
