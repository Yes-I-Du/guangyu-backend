package com.guangyu.guangyubackend.infrastructure.api.aliyunai.model;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 图像外绘API请求实体
 *
 * @author dmz xxx@163.com
 * @version 2025/6/18 23:29
 * @since JDK17
 */
@Data
public class ImageOutPaintingRequest implements Serializable {

    /**
     * 模型名称（必填） 示例："image-out-painting"
     */
    private String model = "image-out-painting";

    /**
     * 输入图像信息（必填）
     */
    private Input input;

    /**
     * 图像处理参数（必填）
     */
    private Parameters parameters;

    /**
     * 输入图像信息
     */
    @Data
    public static class Input {
        /**
         * 图像URL/base64数据（必填） 格式：JPG/JPEG/PNG/HEIF/WEBP 大小：≤10MB 分辨率：512×512 ~ 4096×4096
         */
        @Alias("image_url")
        private String imageUrl;
    }

    /**
     * 图像处理参数
     */
    @Data
    public static class Parameters {
        /**
         * 逆时针旋转角度（0-359度） 默认：0
         */
        @Alias("angle")
        private Integer angle = 0;

        /**
         * 输出图像宽高比 可选值：["", "1:1", "3:4", "4:3", "9:16", "16:9"] 默认：""（不设置）
         */
        @Alias("output_ratio")
        private String outputRatio = "";

        /**
         * 水平扩展比例（1.0-3.0） 示例：1000×1000 → x_scale=2.0 → 2000×1000
         */
        @Alias("x_scale")
        private Float xScale = 1.0f;

        /**
         * 垂直扩展比例（1.0-3.0） 示例：1000×1000 → y_scale=2.0 → 1000×2000
         */
        @Alias("y_scale")
        private Float yScale = 1.0f;

        /**
         * 上方添加像素数 约束：top_offset + bottom_offset < 3×原图高度
         */
        @Alias("top_offset")
        private Integer topOffset = 1;

        /**
         * 下方添加像素数 约束：top_offset + bottom_offset < 3×原图高度
         */
        @Alias("bottom_offset")
        private Integer bottomOffset = 1;

        /**
         * 左侧添加像素数 约束：left_offset + right_offset < 3×原图宽度
         */
        @Alias("left_offset")
        private Integer leftOffset = 1;

        /**
         * 右侧添加像素数 约束：left_offset + right_offset < 3×原图宽度
         */
        @Alias("right_offset")
        private Integer rightOffset = 1;

        /**
         * 开启最佳质量模式 默认：false（减少生成时间）
         */
        @Alias("best_quality")
        private Boolean bestQuality = false;

        /**
         * 限制输出图像大小 默认：true（输出图像≤5MB）
         */
        @Alias("limit_image_size")
        private Boolean limitImageSize = true;

        /**
         * 添加AI水印 默认：true（左下角添加水印）
         */
        @Alias("add_watermark")
        private Boolean addWatermark = true;
    }
}

