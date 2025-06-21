package com.guangyu.guangyubackend.infrastructure.api.aliyunai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图像外绘任务创建响应实体
 *
 * @author dmz xxx@163.com
 * @version 2025/6/18 23:53
 * @since JDK17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateImageOutPaintingTaskResponse {
    /**
     * 任务输出信息
     */
    private Output output;

    /**
     * 请求唯一标识（成功/失败均返回）
     */
    private String requestId;

    /**
     * 错误码（仅失败时返回）
     */
    private String code;

    /**
     * 错误信息（仅失败时返回）
     */
    private String message;

    /**
     * 任务输出信息实体
     */
    @Data
    public static class Output {

        /**
         * 任务ID
         */
        private String taskId;

        /**
         * 任务状态 PENDING:排队中 | RUNNING:处理中 | SUCCEEDED:成功 FAILED:失败 | CANCELED:已取消 | UNKNOWN:未知状态
         */
        private TaskStatus taskStatus;
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING,    // 排队中
        RUNNING,    // 处理中
        SUCCEEDED,  // 成功
        FAILED,     // 失败
        CANCELED,   // 已取消
        UNKNOWN;    // 未知状态
    }
}