package com.guangyu.guangyubackend.interfaces.vo.space;

import cn.hutool.core.bean.BeanUtil;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户空间信息试图包装类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/26 22:00
 * @since JDK17
 */
@Data
public class SpaceVO implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * id
     */
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
     * 创建空间用户信息
     */
    private UserVO user;

    /**
     * 用户空间信息视图封装类 -> 空间对象
     *
     * @param spaceVO 用户空间信息视图封装类
     * @return 空间对象
     */
    public static Space voToSpace(SpaceVO spaceVO) {
        if (spaceVO == null) {
            return null;
        }
        Space space = new Space();
        BeanUtil.copyProperties(spaceVO, space);
        return space;
    }

    /**
     * 空间对象 -> 用户空间信息视图封装类
     *
     * @param space 空间对象
     * @return 用户空间信息视图封装类
     */
    public static SpaceVO SpaceToVo(Space space) {
        if (space == null) {
            return null;
        }
        SpaceVO spaceVO = new SpaceVO();
        BeanUtil.copyProperties(space, spaceVO);
        return spaceVO;
    }
}

