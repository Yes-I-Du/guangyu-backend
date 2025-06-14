package com.guangyu.guangyubackend.domain.picture.entity;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.guangyu.guangyubackend.domain.picture.constant.FileConstant;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import lombok.Data;

/**
 * 图片
 *
 * @TableName picture
 */
@TableName(value = "picture")
@Data
public class Picture implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private String tags;

    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 图片所属空间（为空表示公共空间）
     */
    private Long spaceId;

    /**
     * 状态：0-待审核; 1-通过; 2-拒绝
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    /**
     * 审核人 id
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 图片信息校验
     */
    public void vaildPicture() {
        Long id = this.getId();
        String url = this.getUrl();
        String introduction = this.getIntroduction();
        // Id校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), RespCode.PARAMS_ERROR, "图片Id不能为空");
        // Url校验
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > FileConstant.URL_LENGTH, RespCode.PARAMS_ERROR, "图片Url过长");
        }

        // 简介校验
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > FileConstant.INTRODUCTION_LENGTH, RespCode.PARAMS_ERROR,
                "图片简介过长");
        }

    }
}