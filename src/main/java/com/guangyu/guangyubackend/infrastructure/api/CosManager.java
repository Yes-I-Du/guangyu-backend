package com.guangyu.guangyubackend.infrastructure.api;

import cn.hutool.core.io.FileUtil;
import com.guangyu.guangyubackend.infrastructure.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * COS对象存储服务通用方法类
 *
 * @author dmz xxx@163.com
 * @version 2025/5/21 22:53
 * @since JDK17
 */
@Component
public class CosManager {

    /* COS服务配置 */
    @Resource
    private CosClientConfig cosClientConfig;

    /* COS客户端 */
    @Resource
    private COSClient cosClient;

    /**
     * 上传对象文件
     *
     * @param key  唯一键
     * @param file 本地文件对象
     * @return PutObjectResult
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 下载对象文件
     *
     * @param key 唯一键
     * @return COSObject
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传并解析图片信息
     *
     * @param key  唯一键
     * @param file 图片
     * @return PutObjectResult
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 解析图片信息
        PicOperations picOperations = new PicOperations();
        // 获取原图信息
        picOperations.setIsPicInfo(1);
        // 添加图片处理规则
        List<PicOperations.Rule> ruleList = new LinkedList<>();
        PicOperations.Rule rule1 = new PicOperations.Rule();
        // 图片压缩(图片转为Webp格式)
        String webpKey = FileUtil.mainName(key) + ".webp";
        rule1.setBucket(cosClientConfig.getBucket());
        rule1.setFileId(webpKey);
        rule1.setRule("imageMogr2/rotate/webp");
        ruleList.add(rule1);
        // 缩略图处理,仅对 >= 50KB图片生成缩略图
        if (file.length() >= 50 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            // 缩放规则 /thumbnail/<Width>x<Height>>（如果大于原图宽高，则不处理）
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128));
            ruleList.add(thumbnailRule);
        }

        picOperations.setRules(ruleList);
        putObjectRequest.setPicOperations(picOperations);
        return cosClient.putObject(putObjectRequest);
    }

}

