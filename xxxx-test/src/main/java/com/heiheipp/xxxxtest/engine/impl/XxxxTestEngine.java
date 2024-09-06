package com.heiheipp.xxxxtest.engine.impl;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.db.ds.DSFactory;
import cn.hutool.setting.Setting;
import com.heiheipp.common.config.ConfigModel;
import com.heiheipp.common.executor.ExecutorServiceFactory;
import com.heiheipp.common.executor.ExecutorTask;
import com.heiheipp.xxxxtest.executor.XdsBlobFutureTask;
import com.heiheipp.xxxxtest.executor.XxxxTestTable1FutureTask;
import com.heiheipp.xxxxtest.model.XdsBlobConfigModel;
import com.heiheipp.xxxxtest.model.XxxxTestTable1ConfigModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxTestEngine
 * @desc TODO
 * @date 2022/3/22 23:29
 */
@Slf4j
public class XxxxTestEngine extends AbstractEngine<Boolean> {

    /**
     * instance
     */
    private static XxxxTestEngine xxxxTestEngine = new XxxxTestEngine();

    /**
     * setting
     */
    private Setting setting;

    /**
     * datasource
     */
    @Getter
    private DataSource dataSource;

    /**
     * debug
     */
    private boolean debugEnabled;

    /**
     * concurrency
     */
    private int concurrency;

    /**
     * loop count
     */
    private int loopCount;

    /**
     * batch size
     */
    private int batchSize;

    /**
     * business type
     */
    private String busiType;

    /**
     * config model
     */
    private ConfigModel configModel;

    /**
     * 任务开始时间
     */
    private long startTime;

    /**
     * 多线程计数器
     */
    @Getter
    private static CountDownLatch countDownLatch;

    /**
     * 获取单例
     * @return
     */
    public static XxxxTestEngine getInstance() {
        return xxxxTestEngine;
    }

    /**
     * 初始化
     */
    @Override
    public void beforeProcess() {
        log.info("读取配置文件[config.properties]");
        this.setting = new Setting("config.properties");
        this.debugEnabled = this.setting.getBool("debugEnabled", "common");
        this.concurrency = this.setting.getInt("concurrency", "common");
        this.loopCount = this.setting.getInt("loopCount", "common");
        this.batchSize = this.setting.getInt("batchSize", "common");
        this.busiType = this.setting.getByGroup("busiType", "common");

        switch (this.busiType) {
            case "test_table_1":
                this.configModel = new XxxxTestTable1ConfigModel(this.setting);
                break;
            case "blob_test":
                this.configModel = new XdsBlobConfigModel(this.setting);
                break;
            default:
                break;
        }

        this.dataSource = DSFactory.get();

        if (debugEnabled) {
            ExecutorServiceFactory.getInstance().setExecutorService(this.concurrency);
        }

        // 设置计数器
        startTime = SystemClock.now();
        countDownLatch = new CountDownLatch(this.concurrency);

        log.info("配置加载完毕，业务模型[{}]，操作类型[{}]，并发数[{}]，每并发循环次数[{}]，单事务提交笔数[{}]",
                this.busiType, ((XxxxTestTable1ConfigModel) configModel).getOperateType(),
                ExecutorServiceFactory.getInstance().getPoolSize(), this.loopCount, this.batchSize);
    }

    @Override
    public Boolean process() {
        long parentId = IdUtil.getSnowflakeNextId();
        ExecutorTask[] executorTasks = new ExecutorTask[this.concurrency];

        // 启动多线程任务
        for (int i = 0; i < this.concurrency; i++) {
            switch (this.busiType) {
                case "test_table_1":
                    executorTasks[i] = new XxxxTestTable1FutureTask(parentId, loopCount, batchSize,
                            (XxxxTestTable1ConfigModel) configModel, i, this);
                    break;
                case "blob_test":
                    executorTasks[i] = new XdsBlobFutureTask(parentId, loopCount, batchSize,
                            (XdsBlobConfigModel) configModel, i, this);
                    break;
                default:
                    break;
            }

            if (executorTasks[i] != null) {
                executorTasks[i].execute();
            } else {
                log.error("任务执行器不允许为空，请检查");
                throw new NullPointerException("任务执行器ExecutorTask为空");
            }
        }

        try {
            // 线程等待
            countDownLatch.await();
            log.info("主线程[{}]-多线程分发执行结束, 任务耗时{}", parentId, SystemClock.now() - startTime);
        } catch (Exception e) {
            log.error("主线程[{}]异常结束, 整体任务耗时{}", parentId, SystemClock.now() - startTime);
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void afterProcess() {

    }
}
