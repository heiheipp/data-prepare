package com.heiheipp.common.service;

import cn.hutool.core.date.SystemClock;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.mbg.mapper.TaskLogMapper;
import com.heiheipp.common.mbg.model.TaskLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangxi
 * @version 1.0
 * @className TaskLogService
 * @desc TODO
 * @date 2022/3/5 23:01
 */
@Service
@Scope("prototype")
@Slf4j
public class TaskLogService {

    /**
     * 事务管理器
     */
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 事务定义
     */
    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * TaskLogMapper对象
     */
    @Autowired
    private TaskLogMapper taskLogMapper;

    /**
     * TaskLog对象
     */
    private TaskLog taskLog;

    /**
     * context
     */
    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    /**
     * 父日志ID
     */
    protected String parentThreadId;

    /**
     * sub thread id
     */
    private String subThreadId;

    /**
     * total nums
     */
    private int totalNums;

    /**
     * processed nums
     */
    private int processedNums;

    /**
     * task msg
     */
    private String taskContent;

    /**
     * 登记任务日志
     */
    public void recordTaskLog() throws Exception {
        Map<String, Object> runtimeDatas = RuntimeContext.getRuntimeDatas();
        ConfigConstant.TaskStatusEnum taskStatusEnum =
                (ConfigConstant.TaskStatusEnum) runtimeDatas.get(ConfigConstant.TASK_STATUS_ENUM_KEY);

        // 数据检查
        if (!String.valueOf(Thread.currentThread().getId()).equalsIgnoreCase(
                (String) runtimeDatas.get(ConfigConstant.SUB_THREAD_ID_KEY))) {
            log.error("TaskLogService所在线程[{}]与任务执行线程[{}]不一致", Thread.currentThread().getId(),
                    (String) runtimeDatas.get(ConfigConstant.SUB_THREAD_ID_KEY));
            throw new RuntimeException("TaskLogService所在线程与任务执行线程不一致");
        }

        // 数据准备
        buildTaskLogModel(runtimeDatas);

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            switch (taskStatusEnum) {
                case START:
                    taskLogMapper.insert(taskLog);
                    break;
                default:
                    taskLogMapper.updateByPrimaryKey(taskLog);
                    break;
            }
            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            log.error("子线程[{}]记录任务日志失败!!!", runtimeDatas.get(ConfigConstant.SUB_THREAD_ID_KEY));
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }
    }

    /**
     * 构建TaskLog对象
     * @param  datas
     */
    private void buildTaskLogModel(Map datas) {
        switch ((ConfigConstant.TaskStatusEnum)datas.get(ConfigConstant.TASK_STATUS_ENUM_KEY)) {
            case START:
                taskLog = new TaskLog();
                taskLog.setParentId((String) datas.get(ConfigConstant.PARENT_THREAD_ID_KEY));
                taskLog.setSubId((String) datas.get(ConfigConstant.SUB_THREAD_ID_KEY));
                taskLog.setTaskType((String) datas.get(ConfigConstant.TASK_TYPE_KEY));
                taskLog.setTaskContent((String) datas.get(ConfigConstant.TASK_CONTENT_KEY));
                taskLog.setStatus((String) datas.get(ConfigConstant.TASK_STATUS_KEY));
                taskLog.setTotalNums((long) datas.get(ConfigConstant.TASK_TOTAL_NUMS_KEY));
                taskLog.setProcessedNums((long) datas.get(ConfigConstant.TASK_PROCESSED_NUMS_KEY));
                taskLog.setCreateTime(new Timestamp((long) datas.get(ConfigConstant.TASK_CREATE_TIME_KEY)));
                taskLog.setUpdateTime(new Timestamp((long) datas.get(ConfigConstant.TASK_UPDATE_TIME_KEY)));
                break;
            default:
                taskLog.setStatus((String) datas.get(ConfigConstant.TASK_STATUS_KEY));
                taskLog.setProcessedNums(((long) datas.get(ConfigConstant.TASK_PROCESSED_NUMS_KEY)));
                taskLog.setUpdateTime(new Timestamp((long) datas.get(ConfigConstant.TASK_UPDATE_TIME_KEY)));
                break;
        }
    }

    /**
     * 登记任务日志
     *
     * @param taskStatusEnum
     */
    private void recordTask(ConfigConstant.TaskStatusEnum taskStatusEnum,
                            String taskContent, String parentThreadId, String subThreadId,
                            int totalNums, int processedNums) {
        if (this.parentThreadId == null || this.subThreadId == null) {
            this.parentThreadId = parentThreadId;
            this.subThreadId = subThreadId;
            this.totalNums = totalNums;
            this.taskContent = taskContent;
        }

        this.processedNums = processedNums;

        switch (taskStatusEnum) {
            case START:
                // 登记任务开始日志
                buildRuntimeDatas(ConfigConstant.TaskStatusEnum.START);
                break;
            case PROCESSING:
                // 登记任务处理中日志
                buildRuntimeDatas(ConfigConstant.TaskStatusEnum.PROCESSING);
                break;
            case ERROR:
                // 登记任务失败信息
                buildRuntimeDatas(ConfigConstant.TaskStatusEnum.ERROR);
                break;
            case SUCCESS:
                // 登记任务结束信息
                buildRuntimeDatas(ConfigConstant.TaskStatusEnum.SUCCESS);
                break;
            default:
                break;
        }

        RuntimeContext.setRuntimeDatas(runtimeDatas);

        try {
            recordTaskLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建任务日志数据
     */
    private void buildRuntimeDatas(ConfigConstant.TaskStatusEnum taskStatusEnum, Object... args) {
        switch (taskStatusEnum) {
            case START:
                // 登记开始任务
                runtimeDatas.put(ConfigConstant.PARENT_THREAD_ID_KEY, this.parentThreadId);
                runtimeDatas.put(ConfigConstant.SUB_THREAD_ID_KEY, this.subThreadId);
                runtimeDatas.put(ConfigConstant.TASK_TYPE_KEY, ConfigConstant.TaskTypeEnum.DATA_PREPARE.getTypeDesc());
                runtimeDatas.put(ConfigConstant.TASK_CONTENT_KEY, this.taskContent);
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.START.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_TOTAL_NUMS_KEY, this.totalNums);
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, 0L);
                runtimeDatas.put(ConfigConstant.TASK_CREATE_TIME_KEY, SystemClock.now());
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.START);
                break;
            case PROCESSING:
                // 更新处理中任务
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.PROCESSING.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums);
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.PROCESSING);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            case ERROR:
                // 任务异常
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.ERROR.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums);
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.ERROR);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            case SUCCESS:
                // 任务成功结束
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.SUCCESS.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums);
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.SUCCESS);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            default:
                break;
        }
    }
}
