package com.guangyu.guangyubackend.domain.picture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guangyu.guangyubackend.application.service.SpaceApplicationService;
import com.guangyu.guangyubackend.domain.picture.entity.Picture;
import com.guangyu.guangyubackend.domain.picture.repository.PictureRepository;
import com.guangyu.guangyubackend.domain.picture.service.PictureDomainService;
import com.guangyu.guangyubackend.domain.picture.valueobject.PictureReviewStatusEnum;
import com.guangyu.guangyubackend.domain.space.entity.Space;
import com.guangyu.guangyubackend.domain.space.repository.SpaceRepository;
import com.guangyu.guangyubackend.domain.user.entity.User;
import com.guangyu.guangyubackend.infrastructure.api.CosManager;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import com.guangyu.guangyubackend.interfaces.dto.picture.*;
import com.guangyu.guangyubackend.interfaces.vo.picture.PictureVO;
import com.guangyu.guangyubackend.infrastructure.manager.upload.PictureUploadByFile;
import com.guangyu.guangyubackend.infrastructure.manager.upload.PictureUploadByUrl;
import com.guangyu.guangyubackend.infrastructure.manager.upload.PictureUploadTemplate;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author Dmz
 * @description 针对表【picture(图片)】的数据库操作Service实现
 * @createDate 2025-05-21 23:48:44
 */
@Service
@Slf4j
public class PictureDomainServiceImpl implements PictureDomainService {

    @Resource
    private PictureRepository pictureRepository;

    @Autowired
    private PictureUploadByFile pictureUploadByFile;

    @Autowired
    private PictureUploadByUrl pictureUploadByUrl;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SpaceApplicationService spaceApplicationService;

    @Resource
    private SpaceRepository spaceRepository;

    @Autowired
    private CosManager cosManager;

    @Override
    public PictureVO uploadPicture(Object inputFileSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        // 空间存在Check
        Long spaceId = pictureUploadRequest.getSpaceId();
        if (spaceId != null) {
            // 获取空间信息
            Space existSpace = spaceApplicationService.getSpaceById(spaceId);
            ThrowUtils.throwIf(existSpace == null, RespCode.NOT_FOUND_ERROR, "空间不存在");
            // 空间权限校验(只有用户空间的管理者，即空间的拥有者(创建者)有图片上传权限)
            if (!existSpace.getUserId().equals(loginUser.getId())) {
                ThrowUtils.throwIf(true, RespCode.NO_AUTH_ERROR, "该登录用户暂时无该私有空间操作权限");
            }

            // 空间图片数量额度校验
            ThrowUtils.throwIf(existSpace.getTotalCount() >= existSpace.getMaxCount(), RespCode.PARAMS_ERROR,
                "空间图片数量已达到上限");

            // 空间图片内存大小校验
            ThrowUtils.throwIf(existSpace.getTotalSize() >= existSpace.getMaxSize(), RespCode.PARAMS_ERROR,
                "空间图片内存已达到上限");
        }
        // 图片新增或删除场合校验，PictureUploadRequest不为空时，是图片更新请求，并获取图片Id
        Long pictureId = pictureUploadRequest == null ? null : pictureUploadRequest.getId();
        Picture pictureExist = null;
        // 更新的场合下判断图片是否存在
        if (pictureId != null) {
            pictureExist = pictureRepository.getById(pictureId);
            ThrowUtils.throwIf(pictureExist == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
            // 图片权限校验(只有该图片的拥护者(创建者)以及Admin有图片更新权限或编辑权限)
            if (!pictureExist.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
                ThrowUtils.throwIf(true, RespCode.NO_AUTH_ERROR, "该登录用户暂时无该图片操作权限");
            }
            // 图片空间一致性校验
            if (spaceId == null) {
                // 如果spaceId为空(更新请求中不含SpaceId)，则从图片信息中获取spaceId
                spaceId = pictureExist != null ? pictureExist.getSpaceId() : null;
            } else {
                // 如果spaceId不为空(更新请求中含SpaceId)，则校验图片信息中spaceId是否一致
                ThrowUtils.throwIf(ObjUtil.notEqual(spaceId, pictureExist.getSpaceId()), RespCode.PARAMS_ERROR,
                    "用户空间属性不一致");
            }
        }
        // 图片上传Cos,返回图片信息
        //根据用户Id划分Cos存储目录,如果spaceId为空，则存储至public目录,否则存储至用户私有空间目录
        String uploadPathPrefix =
            spaceId == null ? String.format("public/%s", loginUser.getId()) : String.format("private/%s", spaceId);
        // 图片存储至COs
        // 根据文件上传路径类型选择不同的上传方式
        PictureUploadTemplate uploadTemplate =
            inputFileSource instanceof String ? pictureUploadByUrl : pictureUploadByFile;
        UploadPictureResult uploadPictureResult = uploadTemplate.uploadPicture(inputFileSource, uploadPathPrefix);

        // 图片信息入库
        Picture picture = new Picture();
        picture.setSpaceId(spaceId); // 指定空间 id
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setThumbnailUrl(uploadPictureResult.getThumbnailUrl());
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
        // 转换为标准颜色
        // TODO:后续增加颜色，以便于支持以颜色搜图
        // ...
        // ...

        picture.setUserId(loginUser.getId());
        // 设置审核参数
        this.setReviewStatus(picture, loginUser);

        // 如果是图片更新,则更新图片信息(如果请求参数中包含图片Id，则为图片更新请求)
        if (pictureId != null) {
            // 如果是更新，需要补充 id 和编辑时间
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        // 保存图片信息，图片入库
        // 开启事务
        final Long finalSpaceId = spaceId;
        transactionTemplate.execute(transactionStatus -> {
            // 保存图片信息
            boolean saveResult = pictureRepository.saveOrUpdate(picture);
            ThrowUtils.throwIf(!saveResult, RespCode.OPERATION_ERROR, "图片上传失败");
            // 上传图片后用户空间剩余容量信息更新
            if (finalSpaceId != null) {
                // 更新空间的使用额度
                boolean update = spaceRepository.lambdaUpdate().eq(Space::getId, finalSpaceId)
                    .setSql("totalSize = totalSize + " + picture.getPicSize()).setSql("totalCount = totalCount + 1")
                    .update();
                ThrowUtils.throwIf(!update, RespCode.OPERATION_ERROR, "用户空间剩余容量不足");
            }
            return picture;
        });

        // TODO 图片信息更新的场合下，需要清理图片资源
        this.clearPictureFile(pictureExist);

        // 返回图片脱敏信息
        return PictureVO.PictureToVo(picture);
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
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean spaceIdNull = pictureQueryRequest.isSpaceIdNull();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();

        // TODO: 2025/5/22 从对象中取值,空间属性相关
        // ...
        // ...

        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            // example:and (name like "%xxx%" or introduction like "%xxx%")
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("introduction", searchText));
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId);
        // spaceIdNull: true:仅查询公共图库，查询Space.spaceId 为空的值;
        // 当值为 true 时：为查询添加 spaceId IS NULL 条件;当值为 false 时：忽略此条件，不会添加到 SQL 中
        queryWrapper.isNull(spaceIdNull, "spaceId");
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
                // 标签信息
                // Example: and (tag like "%\"Java\"%" and like "%\"Python\"%")
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 参数校验
        Long pictureId = pictureReviewRequest.getId();
        ThrowUtils.throwIf(pictureReviewRequest == null || pictureId == null || pictureId <= 0, RespCode.PARAMS_ERROR,
            "图片审核信息不存在");
        // 图片审核状态校验
        // 获取审核请求状态
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        PictureReviewStatusEnum pictureReviewStatusEnum =
            PictureReviewStatusEnum.getPictureReviewStatusEnumByValue(reviewStatus);
        if (reviewStatus == null || PictureReviewStatusEnum.REVIEWING.equals(pictureReviewStatusEnum)) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "图片正在审核中，无法重复审核，请稍后查看");
        }

        // 图片校验
        Picture pictureExist = pictureRepository.getById(pictureId);
        ThrowUtils.throwIf(pictureExist == null, RespCode.NOT_FOUND_ERROR, "图片不存在");
        // 业务校验(图片是否重复审核)
        ThrowUtils.throwIf(pictureExist.getReviewStatus() == reviewStatus, RespCode.PARAMS_ERROR, "图片重复审核");

        // 执行审核(数据库操作)
        Picture updatePicture = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewTime(new Date());
        updatePicture.setReviewerId(loginUser.getId());
        boolean result = pictureRepository.updateById(updatePicture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR, "图片审核失败");

    }

    @Override
    public void setReviewStatus(Picture picture, User loginUser) {
        // 用户为管理员则自动过审,并添加审核信息
        // 普通用户创建以及更新图片时均需要待审核
        if (loginUser.isAdmin()) {
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewTime(new Date());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员自动审核通过");
        } else {
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }

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
            pictureUploadRequest.setPicName(namePrefix + (uploadCount + 1));

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

    @Async
    @Override
    public void clearPictureFile(Picture oldPicture) {
        if (oldPicture == null || StrUtil.isBlank(oldPicture.getUrl())) {
            return;
        }
        // 判断改图片是否被多条记录使用
        String pictureUrl = oldPicture.getUrl();
        long count = pictureRepository.lambdaQuery().eq(Picture::getUrl, pictureUrl).count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 1) {
            return;
        }
        // 删除图片
        cosManager.deleteObject(pictureUrl);
        // 删除缩略图
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            cosManager.deleteObject(thumbnailUrl);
        }
    }

    @Override
    public Picture getById(Long id) {
        return pictureRepository.getById(id);
    }

    @Override
    public boolean updatePictureById(Picture picture) {
        return pictureRepository.updateById(picture);
    }

    @Override
    public Page<Picture> page(Page<Picture> picturePage, QueryWrapper<Picture> queryWrapper) {
        return pictureRepository.page(picturePage, queryWrapper);
    }

    @Override
    public void deletePicture(long pictureId, User loginUser) {
        // 参数校验
        ThrowUtils.throwIf(pictureId <= 0, RespCode.PARAMS_ERROR, "图片数据错误");
        ThrowUtils.throwIf(loginUser == null, RespCode.NOT_LOGIN_ERROR, "用户未登录");
        // 图片存在性Check
        Picture existPicture = pictureRepository.getById(pictureId);
        ThrowUtils.throwIf(existPicture == null, RespCode.NOT_FOUND_ERROR, "图片数据不存在");
        // 权限校验
        checkPictureAuth(loginUser, existPicture);

        // 删除图片，数据库操作
        // 需要进行一次事务操作，删除图片时需要进行三步操作：1，删除数据库记录 2，更新空间资源额度 3，异步清理图片文件
        transactionTemplate.execute(transactionStatus -> {
            // 删除图片信息
            boolean saveResult = pictureRepository.removeById(pictureId);
            ThrowUtils.throwIf(!saveResult, RespCode.OPERATION_ERROR, "图片删除失败");
            // 删除图片信息后，用户空间剩余容量信息更新
            // 更新空间的使用额度
            boolean update = spaceRepository.lambdaUpdate().eq(Space::getId, existPicture.getSpaceId())
                .setSql("totalSize = totalSize - " + existPicture.getPicSize()).setSql("totalCount = totalCount - 1")
                .update();
            ThrowUtils.throwIf(!update, RespCode.OPERATION_ERROR, "用户空间剩余容量更新失败");
            return true;
        });
        // 异步清理图片
        this.clearPictureFile(existPicture);
    }

    @Override
    public void editPicture(Picture picture, User loginUser) {
        // 设置编辑时间
        picture.setEditTime(new Date());
        // 数据校验
        picture.vaildPicture();
        // 判断是否存在
        long id = picture.getId();
        Picture existPicture = this.getById(id);
        ThrowUtils.throwIf(existPicture == null, RespCode.NOT_FOUND_ERROR);
        // TODO:权限校验，后续拓展为Sa-Token
        // 仅本人或管理员可编辑
        if (!existPicture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
            throw new BusinessException(RespCode.NO_AUTH_ERROR);
        }
        // 设置审核参数
        this.setReviewStatus(picture, loginUser);
        // 操作数据库
        boolean result = this.updatePictureById(picture);
        ThrowUtils.throwIf(!result, RespCode.OPERATION_ERROR);
    }

    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long loginUserId = loginUser.getId();
        // 用户私有空间模块拓展
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 如果为公共图库，图片拥有者与管理员可以操作
            if (!picture.getUserId().equals(loginUserId) && !loginUser.isAdmin()) {
                throw new BusinessException(RespCode.NO_AUTH_ERROR, "该登录用户无操作权限");
            }
        } else {
            // 如果为私有图库，只有图片拥有者可以操作
            if (!picture.getUserId().equals(loginUserId)) {
                throw new BusinessException(RespCode.NO_AUTH_ERROR, "该登录用户无操作权限");
            }
        }
    }

    @Override
    public List<PictureVO> searchPictureByColor(Long spaceId, String picColor, User loginUser) {
        return null;
    }

    @Override
    public void editPictureByBatch(PictureEditByBatchRequest pictureEditByBatchRequest, User loginUser) {

    }

}
