package com.guangyu.guangyubackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guangyu.guangyubackend.exception.BusinessException;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.manager.upload.PictureUploadByFile;
import com.guangyu.guangyubackend.manager.upload.PictureUploadByUrl;
import com.guangyu.guangyubackend.manager.upload.PictureUploadTemplate;
import com.guangyu.guangyubackend.model.dto.file.UploadPictureResult;
import com.guangyu.guangyubackend.model.dto.picture.PictureQueryRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureReviewRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureUploadByBatchRequest;
import com.guangyu.guangyubackend.model.dto.picture.PictureUploadRequest;
import com.guangyu.guangyubackend.model.entity.Picture;
import com.guangyu.guangyubackend.model.entity.User;
import com.guangyu.guangyubackend.model.enums.PictureReviewStatusEnum;
import com.guangyu.guangyubackend.model.vo.PictureVO;
import com.guangyu.guangyubackend.model.vo.UserVO;
import com.guangyu.guangyubackend.service.PictureService;
import com.guangyu.guangyubackend.mapper.PictureMapper;
import com.guangyu.guangyubackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmz
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-05-21 23:48:44
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {
    @Autowired
    private UserService userService;

    @Autowired
    private PictureUploadByFile pictureUploadByFile;

    @Autowired
    private PictureUploadByUrl pictureUploadByUrl;

    @Override
    public PictureVO uploadPicture(Object inputFileSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 图片校验
        ThrowUtils.throwIf(inputFileSource == null, RespCode.PARAMS_ERROR);
        // 用户登录
        ThrowUtils.throwIf(loginUser == null, RespCode.NOT_LOGIN_ERROR);

        // 图片上传Cos,返回图片信息
        //根据用户Id划分Cos存储目录
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        // 图片存储至COs
        // 根据文件上传路径类型选择不同的上传方式
        PictureUploadTemplate uploadTemplate =
            inputFileSource instanceof String ? pictureUploadByUrl : pictureUploadByFile;
        UploadPictureResult uploadPictureResult = uploadTemplate.uploadPicture(inputFileSource, uploadPathPrefix);

        // 图片信息入库
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        // 支持外层传递图片名称
        String picName = uploadPictureResult.getPicName();
        if (pictureUploadRequest != null && StrUtil.isNotBlank(pictureUploadRequest.getPicName())) {
            picName = pictureUploadRequest.getPicName();
        }
        picture.setName(picName);
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());
        // 设置审核参数
        this.setReviewStatus(picture, loginUser);
        // 如果是图片更新,则更新图片信息
        Long pictureId = this.updatePicture(pictureUploadRequest, loginUser);
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }
        boolean saveResult = this.save(picture);
        ThrowUtils.throwIf(!saveResult, RespCode.OPERATION_ERROR, "图片入库失败");

        // 返回图片脱敏信息
        return PictureVO.PictureToVo(picture);
    }

    @Override
    public Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {
        // 校验参数
        String searchText = pictureUploadByBatchRequest.getSearchText();
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtils.throwIf(count > 30, RespCode.PARAMS_ERROR, "一次最多抓取30张图片");
        // 名称前缀默认等于搜索关键词
        String namePrefix = pictureUploadByBatchRequest.getNamePrefix();
        if (StrUtil.isBlank(namePrefix)) {
            namePrefix = searchText;
        }
        // 抓取内容
        String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document;
        try {
            document = Jsoup.connect(fetchUrl).get();
        } catch (IOException e) {
            log.error("获取页面失败", e);
            throw new BusinessException(RespCode.OPERATION_ERROR, "获取页面失败");
        }
        // 解析内容
        Element div = document.getElementsByClass("dgControl").first();
        if (ObjUtil.isEmpty(div)) {
            throw new BusinessException(RespCode.OPERATION_ERROR, "获取元素失败");
        }
        Elements imgElementList = div.select("img.mimg");
        // 遍历元素，依次处理上传图片
        int uploadCount = 0;
        for (Element imgElement : imgElementList) {
            String fileUrl = imgElement.attr("src");
            if (StrUtil.isBlank(fileUrl)) {
                log.info("当前链接为空，已跳过：{}", fileUrl);
                continue;
            }
            // 处理图片的地址，防止转义或者和对象存储冲突的问题
            // xxxxxxx.cn?picture=dog，应该只保留 xxxxxx.cn
            int questionMarkIndex = fileUrl.indexOf("?");
            if (questionMarkIndex > -1) {
                fileUrl = fileUrl.substring(0, questionMarkIndex);
            }
            // 上传图片
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            pictureUploadRequest.setFileUrl(fileUrl);
            if (StrUtil.isBlank(namePrefix)) {
                pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));
            }
            try {
                PictureVO pictureVO = this.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
                log.info("图片上传成功，id = {}", pictureVO.getId());
                uploadCount++;
            } catch (Exception e) {
                log.error("图片上传失败", e);
                continue;
            }
            if (uploadCount >= count) {
                break;
            }
        }
        return uploadCount;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 对象转换
        PictureVO pictureVO = PictureVO.PictureToVo(picture);

        //关联用户信息
        Long userId = picture.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage =
            new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表 => 封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::PictureToVo).collect(Collectors.toList());
        // 1. 关联查询用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap =
            userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 2. 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void vaildPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, RespCode.PARAMS_ERROR, "图片不存在");
        // Id校验
        ThrowUtils.throwIf(ObjUtil.isNull(picture.getId()), RespCode.PARAMS_ERROR, "图片Id不能为空");
        // Url校验
        ThrowUtils.throwIf(StrUtil.isNotBlank(picture.getUrl()) && picture.getUrl().length() > 1024,
            RespCode.PARAMS_ERROR, "图片Url过长");
        // 简介校验
        ThrowUtils.throwIf(StrUtil.isNotBlank(picture.getIntroduction()) && picture.getIntroduction().length() > 800,
            RespCode.PARAMS_ERROR, "图片简介过长");
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(
            pictureReviewRequest == null || pictureReviewRequest.getId() == null || pictureReviewRequest.getId() <= 0,
            RespCode.PARAMS_ERROR, "图片审核信息不存在");
        // 是否已审核
        // 获取审核请求状态
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        ThrowUtils.throwIf(ObjUtil.isNull(reviewStatus) || PictureReviewStatusEnum.REVIEWING.equals(
                PictureReviewStatusEnum.getPictureReviewStatusEnumByValue(reviewStatus)), RespCode.PARAMS_ERROR,
            "图片审核状态不能为空或者图片正在审核中");

        // 图片校验
        Picture pictureExist = this.getById(pictureReviewRequest.getId());
        ThrowUtils.throwIf(pictureExist == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
        // 业务校验(图片是否重复审核)
        ThrowUtils.throwIf(pictureExist.getReviewStatus() == reviewStatus, RespCode.PARAMS_ERROR, "图片重复审核");

        // 执行审核(数据库操作)
        Picture updatePicture = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewTime(new Date());
        updatePicture.setReviewerId(loginUser.getId());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "图片审核失败");

    }

    @Override
    public void setReviewStatus(Picture picture, User loginUser) {
        // 用户为管理员则自动过审,并添加审核信息
        // 普通用户创建以及更新图片时均需要待审核
        if (userService.isAdmin(loginUser)) {
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewTime(new Date());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动审核通过");
            return;
        } else {
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }

    }

    /**
     * 图片更新
     *
     * @param pictureUploadRequest
     * @return Long pictureId 图片id
     */
    private Long updatePicture(PictureUploadRequest pictureUploadRequest, User user) {
        // 获取图片id(只有图片更新请求才会有id)
        Long pictureId = pictureUploadRequest == null ? null : pictureUploadRequest.getId();

        // 校验图片是否存在
        if (pictureId != null) {
            Picture pictureExist = this.getById(pictureId);
            ThrowUtils.throwIf(pictureExist == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
            // 更新权限校验
            ThrowUtils.throwIf(!pictureExist.getUserId().equals(user.getId()) && !userService.isAdmin(user),
                RespCode.NO_AUTH_ERROR);
        }

        return pictureId;
    }

}




