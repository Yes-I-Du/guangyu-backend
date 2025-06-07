package com.guangyu.guangyubackend.infrastructure.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.guangyu.guangyubackend.domain.picture.constant.FileConstant;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 通过文件方式上传图片
 *
 * @author dmz xxx@163.com
 * @version 2025/5/25 20:58
 * @since JDK17
 */
@Service
public class PictureUploadByFile extends PictureUploadTemplate {
    @Override
    protected void vaildPicture(Object inputFileSource) {
        MultipartFile multipartFile = (MultipartFile)inputFileSource;
        // 空图校验
        ThrowUtils.throwIf(multipartFile == null || multipartFile.isEmpty(), RespCode.PARAMS_ERROR, "图片不能为空");
        // 图片大小校验
        long fileSize = multipartFile.getSize();
        ThrowUtils.throwIf(fileSize > FileConstant.PICTURE_MAX_SIZE, RespCode.PARAMS_ERROR, "图片大小不能超过2M");
        // 图片后缀校验
        // 获取文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        ThrowUtils.throwIf(!FileConstant.ALLOW_FORMAT_LIST.contains(fileSuffix), RespCode.PARAMS_ERROR, "图片格式错误");

    }

    @Override
    protected String getOriginFileName(Object inputFileSource) {
        return ((MultipartFile)inputFileSource).getOriginalFilename();
    }

    @Override
    protected void processFile(File file, Object inputFileSource) throws IOException {
        MultipartFile multipartFile = (MultipartFile)inputFileSource;
        multipartFile.transferTo(file);
    }
}

