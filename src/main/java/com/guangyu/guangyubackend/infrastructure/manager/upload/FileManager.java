package com.guangyu.guangyubackend.infrastructure.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.guangyu.guangyubackend.infrastructure.api.CosManager;
import com.guangyu.guangyubackend.infrastructure.config.CosClientConfig;
import com.guangyu.guangyubackend.domain.picture.constant.FileConstant;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import com.guangyu.guangyubackend.infrastructure.manager.upload.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
@Deprecated
public class FileManager {
    @Autowired
    private CosManager cosManager;

    @Autowired
    private CosClientConfig cosClientConfig;

    /**
     * 图片上传(通过文件上传)
     *
     * @param multipartFile    上传的文件
     * @param uploadPathPrefix 文件存储路径
     * @return 上传图片解析信息
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

    /*
     * 通过Url上传图片
     */

    public UploadPictureResult uploadPictureByUrl(String pictureUrl, String uploadPathPrefix) {
        // 图片校验
        this.vaildPictureByUrl(pictureUrl);

        // 图片上传
        //随机生成图片8位随机Id
        String uuid = RandomUtil.randomString(16);
        //String originFilename = pictureUrl.getOriginalFilename();
        String originFilename = FileUtil.mainName(pictureUrl);
        // Cos文件对象命名
        String uploadFilename =
            String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        // 文件存储路径
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        File file = null;
        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);
            //   pictureUrl.transferTo(file);
            HttpUtil.downloadFile(pictureUrl, file);
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

    /*
     * Url图片校验
     * */

    private void vaildPictureByUrl(String pictureUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(pictureUrl), RespCode.PARAMS_ERROR, "图片Url不能为空");

        // Url格式校验
        try {
            // Url格式合法性校验
            new URL(pictureUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "图片Url格式错误");
        }

        // Url协议格式校验
        ThrowUtils.throwIf(!StrUtil.startWith(pictureUrl, "http://") || !StrUtil.startWith(pictureUrl, "https://"),
            RespCode.PARAMS_ERROR, "图片Url格式错误");

        // Url图片存在校验(发送Head请求)
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpUtil.createRequest(Method.HEAD, pictureUrl).execute();
            // 请求不存在，直接返回，无需其他判断
            if (httpResponse == null) {
                return;
            }
            // 图片类型校验
            String contentType = httpResponse.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                ThrowUtils.throwIf(!FileConstant.ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                    RespCode.PARAMS_ERROR, "文件类型错误");
            }
            //图片大小校验
            String contentLengthStr = httpResponse.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    ThrowUtils.throwIf(contentLength > FileConstant.PICTURE_MAX_SIZE, RespCode.PARAMS_ERROR,
                        "图片大小不能超过2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(RespCode.PARAMS_ERROR, "图片大小格式错误");
                }

            }
        } finally {
            if (httpResponse != null) {
                httpResponse.close();
            }
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

