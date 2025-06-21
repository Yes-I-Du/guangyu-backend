package com.guangyu.guangyubackend.interfaces.dto.picture;

import com.guangyu.guangyubackend.infrastructure.api.aliyunai.model.ImageOutPaintingRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * Ai扩图请求实体
 *
 * @author dmz xxx@163.com
 * @version 2025/6/19 22:19
 * @since JDK17
 */
@Data
public class CreatePictureOutPaintingRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 图片ID
     * */
    private Long pictureId;

    /*
     * 扩图参数
     * */
    private ImageOutPaintingRequest.Parameters parameters;
}

