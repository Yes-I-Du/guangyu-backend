package com.guangyu.guangyubackend.domain.space.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.guangyu.guangyubackend.domain.space.constant.SpaceConstant;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.space.SpaceLevelManager;
import lombok.Data;

/**
 * 空间
 *
 * @TableName space
 */
@TableName(value = "space")
@Data
public class Space implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间级别：0-普通版 1-专业版 2-旗舰版
     */
    private Integer spaceLevel;

    /**
     * 空间图片的最大总大小
     */
    private Long maxSize;

    /**
     * 空间图片的最大数量
     */
    private Long maxCount;

    /**
     * 当前空间下图片的总大小
     */
    private Long totalSize;

    /**
     * 当前空间下的图片数量
     */
    private Long totalCount;

    /**
     * 创建用户 id
     */
    private Long userId;

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
     * 用户空间信息校验
     *
     * @param addFlag 是否为新增用户空间
     */
    public void validSpaceInfo(boolean addFlag) {
        // 空间信息取得
        Long spaceId = this.getId();
        String spaceName = this.getSpaceName();
        Integer spaceLevel = this.getSpaceLevel();
        Integer space = SpaceLevelManager.getByValue(spaceLevel).getValue();
        // 创建用户空间的场合
        if (addFlag) {
            // 用户空间名称为空的场合
            ThrowUtils.throwIf(StrUtil.isBlank(spaceName), RespCode.PARAMS_ERROR, "空间名称不能为空");

            // 用户空间级别为空的场合
            ThrowUtils.throwIf(spaceLevel == null, RespCode.PARAMS_ERROR, "空间级别不能为空");

        }
        // 更新用户空间信息的场合
        // 用户空间名称长>20的场合
        ThrowUtils.throwIf(spaceName.length() > SpaceConstant.SPACE_NAME_LENGTH, RespCode.PARAMS_ERROR,
            "用户空间名称过长");
        // 用户空间级别不存在的场合
        ThrowUtils.throwIf(space == null, RespCode.PARAMS_ERROR, "用户空间级别不存在");

    }
}