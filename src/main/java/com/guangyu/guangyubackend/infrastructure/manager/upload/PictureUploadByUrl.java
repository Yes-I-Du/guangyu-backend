package com.guangyu.guangyubackend.infrastructure.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.guangyu.guangyubackend.domain.picture.constant.FileConstant;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import com.guangyu.guangyubackend.infrastructure.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 通过Url上传图片
 *
 * @author dmz xxx@163.com
 * @version 2025/5/25 21:11
 * @since JDK17
 */
@Service
public class PictureUploadByUrl extends PictureUploadTemplate {
    @Override
    protected void vaildPicture(Object inputFileSource) {
        String pictureUrl = (String)inputFileSource;
        ThrowUtils.throwIf(StrUtil.isBlank(pictureUrl), RespCode.PARAMS_ERROR, "图片Url不能为空");

        // Url格式校验
        try {
            // Url格式合法性校验
            new URL(pictureUrl);
        } catch (MalformedURLException e) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "图片Url格式错误");
        }

        // Url协议格式校验
        ThrowUtils.throwIf(!StrUtil.startWith(pictureUrl, "http://") && !StrUtil.startWith(pictureUrl, "https://"),
            RespCode.PARAMS_ERROR, "仅支持http以及https协议图片地址");

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

    @Override
    protected String getOriginFileName(Object inputFileSource) {
        return FileUtil.getName((String)inputFileSource);
    }

    @Override
    protected void processFile(File file, Object inputFileSource) throws IOException {
        String pictureUrl = (String)inputFileSource;
        HttpUtil.downloadFile(pictureUrl, file);
    }
}

