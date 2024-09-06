package com.heiheipp.common.constant;

/**
 * @author zhangxi
 * @version 1.0
 * @className ConfigConstant
 * @desc TODO
 * @date 2022/3/1 18:21
 */
public class ConfigConstant {

    /**
     * 默认并发数
     */
    public static final int DEFAULT_CONCURRENCY = 20;

    /**
     * 默认队列深度
     */
    public static final int DEFAULT_QUEUE_SIZE = 200;

    /**
     * 默认队列链接超时时间
     */
    public static final int DEFAULT_KEEP_ALIVE_TIME = 5;

    /**
     * 父线程id key
     */
    public static final String PARENT_THREAD_ID_KEY = "parentThreadId";

    /**
     * 子线程id key
     */
    public static final String SUB_THREAD_ID_KEY = "subThreadId";

    /**
     * 任务类型key
     */
    public static final String TASK_TYPE_KEY = "taskType";

    /**
     * 任务备注key
     */
    public static final String TASK_CONTENT_KEY = "taskContent";

    /**
     * 任务状态key
     */
    public static final String TASK_STATUS_KEY = "status";

    /**
     * 任务待处理数据总数key
     */
    public static final String TASK_TOTAL_NUMS_KEY = "totalNums";

    /**
     * 任务已处理数据key
     */
    public static final String TASK_PROCESSED_NUMS_KEY = "processedNums";

    /**
     * 任务创建时间key
     */
    public static final String TASK_CREATE_TIME_KEY = "createTime";

    /**
     * 任务创建时间key
     */
    public static final String TASK_UPDATE_TIME_KEY = "updateTime";

    /**
     * 任务创建时间key
     */
    public static final String TASK_STATUS_ENUM_KEY = "taskStatusEnum";

    /**
     * 默认交易金额基数
     */
    public static final int DEFAULT_ACCOUNT_MONEY = 10000;

    /**
     * 默认交易金额精度
     */
    public static final int DEFAULT_ACCOUNT_MONEY_PRECISION = 2;

    /**
     * 任务状态枚举类
     */
    public enum TaskStatusEnum {
        /**
         * 启动
         */
        START("1"),

        /**
         * 运行中
         */
        PROCESSING("2"),

        /**
         * 成功结束
         */
        SUCCESS("3"),

        /**
         * 异常结束
         */
        ERROR("9");

        private String status;

        TaskStatusEnum(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }

    /**
     * 任务状态枚举类
     */
    public enum TaskTypeEnum {
        /**
         * 数据准备
         */
        DATA_PREPARE(1, "数据准备");

        private int typeId;

        private String typeDesc;

        TaskTypeEnum(int typeId, String typeDesc) {
            this.typeId = typeId;
            this.typeDesc = typeDesc;
        }

        public int getTypeId() {
            return this.typeId;
        }

        public String getTypeDesc() {
            return this.typeDesc;
        }
    }
}
