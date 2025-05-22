package com.guangyu.guangyubackend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.guangyu.guangyubackend.config.CosClientConfig;
import com.guangyu.guangyubackend.constant.FileConstant;
import com.guangyu.guangyubackend.exception.BusinessException;
import com.guangyu.guangyubackend.exception.RespCode;
import com.guangyu.guangyubackend.exception.ThrowUtils;
import com.guangyu.guangyubackend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 文件上传通用服务
 *
 * @author dmz xxx@163.com
 * @version 2025/5/22 19:59
 * @since JDK17
 */
@Service
@Slf4j
public class FileManager {
    @Autowired
    private CosManager cosManager;

    @Autowired
    private CosClientConfig cosClientConfig;

    /**
     * 图片上传
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 图片校验
        this.vaildPicture(multipartFile);

        // 图片上传
        //随机生成图片8位随机Id
        String uuid = RandomUtil.randomString(16);
        String originFilename = multipartFile.getOriginalFilename();
        // Cos文件对象命名
        String uploadFilename =
            String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        // 文件存储路径
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            // 上传图片
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 文件信息封装
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
        } catch (IOException e) {
            log.error("图片上传失败", e);
            throw new BusinessException(RespCode.OPERATION_ERROR, "图片上传失败");
        } finally {
            this.deleteTempFile(file);
        }

    }

    /**
     * 文件校验
     *
     * @param martipartFile
     */
    private void vaildPicture(MultipartFile martipartFile) {
        // 空图校验
        ThrowUtils.throwIf(martipartFile == null || martipartFile.isEmpty(), RespCode.PARAMS_ERROR, "图片不能为空");
        // 图片大小校验
        long fileSize = martipartFile.getSize();
        ThrowUtils.throwIf(fileSize > FileConstant.PICTURE_MAX_SIZE, RespCode.PARAMS_ERROR, "图片大小不能超过2M");
        // 图片后缀校验
        // 获取文件后缀
        String fileSuffix = FileUtil.getSuffix(martipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!FileConstant.ALLOW_FORMAT_LIST.contains(fileSuffix), RespCode.PARAMS_ERROR, "图片格式错误");
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

