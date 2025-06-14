package com.guangyu.guangyubackend.interfaces.vo.picture;

/**
 * 图片视图包装类
 *
 * @author Dmz Email:  * @since 2025/05/21 23:59
 */

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.guangyu.guangyubackend.interfaces.vo.user.UserVO;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

/**
 * 图片视图包装类 被上传图片信息需要关联用户信息
 *
 * @author dmz xxx@163.com
 * @version 2025/5/21 23:59
 * @since JDK17
 */
@Data
public class PictureVO implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    /* 图片Id */
    private Long id;

    /* 图片名称 */
    private String name;

    /* 图片地址 */
    private String url;

    /* 图片简介 */
    private String introduction;

    /* 图片标签 */
    private List<String> tags;

    /* 图片分类 */
    private String category;

    /* 图片体积 */
    private Long picSize;

    /* 图片宽度 */
    private Integer picWidth;

    /* 图片高度 */
    private Integer picHeight;

    /* 图片比例 */
    private Double picScale;

    /* 图片格式 */
    private String picFormat;

    /**
     * 缩略图 url
     */
    private String thumbnailUrl;

    /* 用户Id */
    private Long userId;

    /* 图片所属空间（为空表示公共空间） */
    private Long spaceId;

    /* 图片创建时间 */
    private Date createTime;

    /* 图片编辑时间 */
    private Date editTime;

    /* 图片更新时间 */
    private Date updateTime;

    /* 用户信息 */
    private UserVO user;

    /**
     * 封装类 -> 图片对象
     */
    public static Picture voToPicture(PictureVO pictureVO) {
        if (pictureVO == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureVO, picture);
        // 将标签转为List
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }

    /**
     * 图片对象 -> 封装类
     */
    public static PictureVO PictureToVo(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtil.copyProperties(picture, pictureVO);
        //将标签转为String
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }

}

