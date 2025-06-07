package com.guangyu.guangyubackend.infrastructure.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.guangyu.guangyubackend.infrastructure.config.CosClientConfig;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.api.CosManager;
import com.guangyu.guangyubackend.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 图片上传模板方法
 *
 * @author dmz xxx@163.com
 * @version 2025/5/25 20:28
 * @since JDK17
 */
@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    public UploadPictureResult uploadPicture(Object inputFileSource, String uploadPathPrefix) {
        // 文件信息校验
        this.vaildPicture(inputFileSource);

        // 文件上传路径
        //随机生成8位文件随机Id
        String uuid = RandomUtil.randomString(16);
        // 文件Name取得
        String originFilename = this.getOriginFileName(inputFileSource);
        // Cos文件对象命名
        String uploadFilename =
            String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        // 文件存储路径
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            // 源文件处理(生成临时文件)
            this.processFile(file, inputFileSource);
            // 上传图片以及图片信息封装
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollUtil.isNotEmpty(objectList)) {
                CIObject compressedCiObject = objectList.get(0);
                // 如果没有生成缩略图，则将原图作为缩略图
                CIObject thumbnailCiObject = objectList.size() == 1 ? compressedCiObject : objectList.get(1);
                // 封装压缩图以及缩略图返回结果
                return getUploadPictureResult(originFilename, compressedCiObject, thumbnailCiObject);
            }
            // 封装原图返回结果
            return getUploadPictureResult(originFilename, uploadPath, file, imageInfo);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new BusinessException(RespCode.OPERATION_ERROR, "图片上传失败");
        } finally {
            this.deleteTempFile(file);
        }

    }

    /**
     * 源文件信息校验
     *
     * @param inputFileSource 源文件
     */
    protected abstract void vaildPicture(Object inputFileSource);

    /**
     * 源文件Name取得
     *
     * @param inputFileSource 源文件
     * @return 源文件Name
     */
    protected abstract String getOriginFileName(Object inputFileSource);

    /**
     * 源文件处理
     *
     * @param file
     * @param inputFileSource
     */
    protected abstract void processFile(File file, Object inputFileSource) throws IOException;

    /**
     * 图片信息封装(原图)
     *
     * @param originFilename web文件name
     * @param uploadPath     文件存储路径
     * @param file           文件
     * @param imageInfo      图片信息
     * @return 图片存储信息
     */
    private UploadPictureResult getUploadPictureResult(String originFilename, String uploadPath, File file,
        ImageInfo imageInfo) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

    /**
     * 图片信息封(压缩图片)
     *
     * @param originFilename    web文件name * @param compressedCiObject 压缩图片信息
     * @param thumbnailCiObject 缩略图信息
     * @return 图片存储信息
     */
    private UploadPictureResult getUploadPictureResult(String originFilename, CIObject compressedCiObject,
        CIObject thumbnailCiObject) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        return uploadPictureResult;
    }

    /**
     * 临时文件删除
     *
     * @param file
     */
    private void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.delete()) {
            log.error("file delete error, filepath = {}", file.getAbsoluteFile());
        }
    }
}

