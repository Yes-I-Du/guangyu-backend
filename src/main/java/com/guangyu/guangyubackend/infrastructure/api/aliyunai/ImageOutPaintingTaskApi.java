package com.guangyu.guangyubackend.infrastructure.api.aliyunai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.guangyu.guangyubackend.infrastructure.api.aliyunai.model.CreateImageOutPaintingTaskResponse;
import com.guangyu.guangyubackend.infrastructure.api.aliyunai.model.GetImageOutPaintingTaskResponse;
import com.guangyu.guangyubackend.infrastructure.api.aliyunai.model.ImageOutPaintingRequest;
import com.guangyu.guangyubackend.infrastructure.exception.BusinessException;
import com.guangyu.guangyubackend.infrastructure.exception.RespCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;

/**
 * AI图像服务扩展API
 *
 * @author dmz xxx@163.com
 * @version 2025/6/19 21:13
 * @since JDK17
 */
@Component
@Slf4j
public class ImageOutPaintingTaskApi {
    // 读取Aliyun配置文件
    @Value("${aliyun.ai.apiKey}")
    private String apiKey;
    // 创建扩图任务请求
    private static final String CREATE_IMAGE_OUT_PAINTING_TASK_URL =
        "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";
    // 查询扩图任务请求
    private static final String GET_MAGE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";
    // 默认时间间隔
    private static final int DEFAULT_POLL_INTERVAL_MS = 30000; // 30秒
    // 默认超时时间
    private static final int DEFAULT_TIMEOUT_MINUTES = 60; // 60分钟超时

    /**
     * 创建图像扩展任务
     *
     * @param request 扩展任务请求参数
     * @return 扩展任务响应
     */
    public CreateImageOutPaintingTaskResponse createImageOutPaintingTask(ImageOutPaintingRequest request) {
        // 请求信息校验
        this.validateCreateRequest(request);

        // 构建Http请求
        HttpRequest httpRequest =
            HttpRequest.post(CREATE_IMAGE_OUT_PAINTING_TASK_URL)
                .header(Header.AUTHORIZATION, "Bearer " + apiKey)
                // 必须开启异步处理，设置为enable。
                .header("X-DashScope-Async", "enable")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(request));

        try (HttpResponse httpResponse = httpRequest.execute()) {
            if (!httpResponse.isOk()) {
                log.error("请求失败" + httpResponse.body() + ", API call failed. Code: " + httpResponse.getStatus());
                throw new BusinessException(RespCode.OPERATION_ERROR, "AI 扩图失败");
            }
            CreateImageOutPaintingTaskResponse response =
                JSONUtil.toBean(httpResponse.body(), CreateImageOutPaintingTaskResponse.class);
            String errorCode = response.getCode();
            if (StrUtil.isNotBlank(errorCode)) {
                String errorMessage = response.getMessage();
                log.error("AI 扩图失败，errorCode:{}, errorMessage:{}", errorCode, errorMessage);
                throw new BusinessException(RespCode.OPERATION_ERROR, "AI 扩图任务响应异常");
            }
//            String str = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(httpRequest);
//            String str2 = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
//            System.out.println("------Start----------");
//            System.out.println(str);
//            System.out.println(str2);
//            System.out.println("---------END---------");
            return response;
        } catch (Exception e) {
            log.error("AI 扩图失败", e);
            throw new BusinessException(RespCode.OPERATION_ERROR, "API call failed: " + e.getMessage());
        }
    }

    public GetImageOutPaintingTaskResponse getImageOutPaintingTask(String taskId) {
        // 任务Id参数校验
        this.validateTaskId(taskId);

        try (HttpResponse httpResponse = HttpRequest.get(String.format(GET_MAGE_OUT_PAINTING_TASK_URL, taskId))
            .header(Header.AUTHORIZATION, "Bearer " + apiKey).execute()) {
            if (!httpResponse.isOk()) {
                throw new BusinessException(RespCode.OPERATION_ERROR, "获取任务失败");
            }

            return JSONUtil.toBean(httpResponse.body(), GetImageOutPaintingTaskResponse.class);
        } catch (Exception e) {
            log.error("获取任务失败", e);
            throw new BusinessException(RespCode.OPERATION_ERROR, "API call failed:" + e.getMessage());
        }
    }

    /**
     * 请求参数校验
     *
     * @param request
     */
    private void validateCreateRequest(ImageOutPaintingRequest request) {
        if (request == null) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "请求错误");
        }

        if (request.getInput() == null || request.getInput().getImageUrl() == null) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "图片信息不能为空");
        }

        if (request.getParameters() == null) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "扩图参数不能为空");
        }
    }

    /**
     * 任务ID参数校验
     *
     * @param taskId
     */
    private void validateTaskId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "任务ID不能为空");
        }
        // UUID格式验证 (32字符的16进制数，包含连字符)
        if (!taskId.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new BusinessException(RespCode.PARAMS_ERROR, "任务ID格式不正确");
        }
    }

}

