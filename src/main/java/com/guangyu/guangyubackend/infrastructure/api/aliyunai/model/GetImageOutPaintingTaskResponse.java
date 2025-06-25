package com.guangyu.guangyubackend.infrastructure.api.aliyunai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询创建任务结果请求响应实体
 *
 * @author dmz xxx@163.com
 * @version 2025/6/19 0:12
 * @since JDK17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetImageOutPaintingTaskResponse {

    /**
     * 请求唯一标识
     */
    private String requestId;

    /**
     * 任务输出信息
     */
    private Output output;

    /**
     * 资源使用统计
     */
    private Usage usage;

    @Data
    public static class Output {
        /**
         * 任务ID
         */
        private String taskId;

        /**
         * 任务状态 PENDING:排队中 | RUNNING:处理中 | SUCCEEDED:成功 FAILED:失败 | CANCELED:已取消 | UNKNOWN:未知状态
         */
        private String taskStatus;

        /**
         * 任务提交时间（格式：yyyy-MM-dd HH:mm:ss.SSS）
         */
        private String submitTime;

        /**
         * 任务调度时间（格式：yyyy-MM-dd HH:mm:ss.SSS）
         */
        private String scheduledTime;

        /**
         * 任务完成时间（格式：yyyy-MM-dd HH:mm:ss.SSS）
         */
        private String endTime;

        /**
         * 输出图片URL地址（任务成功时返回）
         */
        private String outputImageUrl;

        /**
         * 任务结果统计
         */
        private TaskMetrics taskMetrics;

        /**
         * 错误码（失败时返回）
         */
        private String code;

        /**
         * 错误信息（失败时返回）
         */
        private String message;
    }

    @Data
    public static class TaskMetrics {
        /**
         * 总任务数
         */
        private Integer total;

        /**
         * 成功任务数
         */
        private Integer succeeded;

        /**
         * 失败任务数
         */
        private Integer failed;
    }

    @Data
    public static class Usage {
        /**
         * 生成图片数量
         */
        private Integer imageCount;
    }
}