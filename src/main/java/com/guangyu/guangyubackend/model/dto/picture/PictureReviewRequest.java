package com.guangyu.guangyubackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * 审核请求参数类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/24 12:44
 * @since JDK17
 */
@Data
public class PictureReviewRequest  implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
    private Long id;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;
}

