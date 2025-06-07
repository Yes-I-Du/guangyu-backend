package com.guangyu.guangyubackend.domain.picture.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 文件上传下载相关常量接口
 *
 * @author Dmz Email:  * @since 2025/05/22 20:31
 */
public interface FileConstant {

    /* 允许上传图片url长度 */
    public static final int URL_LENGTH = 1024;

    /* 允许上传图片简介长度 */
    public static final int INTRODUCTION_LENGTH = 800;

    /* 允许上传图片最大大小：2M */
    public static final long PICTURE_MAX_SIZE = 2 * 1024 * 1024L;

    /* 允许上传图片格式 */
    public static final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp");

    /* 允许上传图片格式(Url方式上传) */
    public static final List<String> ALLOW_CONTENT_TYPES =
        Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
}
