package com.heiheipp.xxxxtest.executor;

import cn.hutool.core.date.SystemClock;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.Test1;
import com.heiheipp.common.mbg.model.TestTable1;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.xxxxtest.engine.impl.XxxxTestEngine;
import com.heiheipp.xxxxtest.model.XdsBlobConfigModel;
import com.heiheipp.xxxxtest.service.SimpleXdsBlobService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className CdsBlobFutureTask
 * @desc TODO
 * @date 2022/4/5 13:31
 */
@Slf4j
public class XdsBlobFutureTask extends AbstractFutureTask<String> {

    /**
     * loop count
     */
    private int loopCount;

    /**
     * batch size
     */
    private int batchSize;

    /**
     * sub thread id
     */
    private String subThreadId;

    /**
     * processed nums
     */
    private AtomicLong processedNums = new AtomicLong(0L);

    /**
     * committed rows
     */
    private int committedRows = 0;

    /**
     * start offset
     */
    private int startOffset;

    /**
     * config model
     */
    private XdsBlobConfigModel configModel;

    /**
     * XxxxTestEngine
     */
    private XxxxTestEngine xxxxTestEngine;

    /**
     * Connection
     */
    private Connection connection;

    /**
     * SimpleCdsBlobService
     */
    private SimpleXdsBlobService simpleCdsBlobService;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param loopCount
     * @param batchSize
     * @param threadOrder
     * @param configModel
     */
    public XdsBlobFutureTask(long parentThreadId, int loopCount, int batchSize,
                             XdsBlobConfigModel configModel, int threadOrder,
                             XxxxTestEngine xxxxTestEngine) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.loopCount = loopCount;
        this.batchSize = batchSize;
        this.configModel = configModel;
        this.simpleCdsBlobService = new SimpleXdsBlobService();
        this.xxxxTestEngine = xxxxTestEngine;

        // 参数检查
        if (this.loopCount > configModel.getStepSize()) {
            log.error("参数非法! loopCount应小于等于业务模型的stepSize");
            throw new RuntimeException("参数非法! loopCount应小于等于业务模型的stepSize");
        }

        // 参数计算
        this.startOffset = configModel.getStepSize() * threadOrder + 1;
    }

    /**
     * 任务执行方法
     *
     * @return
     */
    @Override
    protected String submit(long subThreadId) {
        String result = "";
        this.subThreadId = String.valueOf(subThreadId);
        long startTime = System.currentTimeMillis();

        log.info("Sub thread[{}] start, startOffset is {}.", this.subThreadId, this.startOffset);

        // 制造数据
        List<Test1> test1s = new ArrayList<>();
        Test1 test1 = null;
        int currentPercentage = 0, prevPercentage = 0;
        int totalNums = this.loopCount;

        try {
            // get connection
            this.connection = xxxxTestEngine.getDataSource().getConnection();

            String value;
            Map<String, Object> datas = new HashMap<>();
            for (int i = 0; i < this.loopCount; i++) {
                // 模拟每个循环生成xml
                for (int j = 0; j < 100 * 2000; j++) {
                    value = DataBuildUtil.getRandomWithLength((int)(1 + Math.random()*(10 - 1 + 1)));
                    datas.put("key" + (i + 1), value);
                }

                test1 = new Test1();
                buildTest1(test1, i);

                test1s.add(test1);

                // 递增总生成数量和待提交数量
                this.processedNums.incrementAndGet();
                this.committedRows++;

                // 数据写入操作
                //writeData(test1s, false);

                // 登记任务处理中日志
                prevPercentage = currentPercentage;
                currentPercentage = DataBuildUtil.getPercentge(this.processedNums.get(), totalNums);
                if (DataBuildUtil.getPercentageWtihTens(this.processedNums.get(), totalNums) &&
                        prevPercentage < currentPercentage) {
                    log.info("Sub thread {} has been processing {}%, processedNums is {}, totalNums is {}, execution time is {}.",
                            this.subThreadId, currentPercentage, this.processedNums.get(), totalNums,
                            SystemClock.now() - startTime);
                }
            }

            // 补提交剩余部分
            //writeData(test1s, true);
        } catch (Exception e) {
            log.error("Sub thread {} error, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);
            e.printStackTrace();
        } finally {
            log.info("Sub thread {} finish, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);

            // 清理线程上下文
            RuntimeContext.clearRuntimeData();

            // 计数器操作
            XxxxTestEngine.getCountDownLatch().countDown();

            try {
                if (this.connection != null) {
                    this.connection.close();
                }
            } catch (Exception e) {
                log.warn("Close connection failed!");
            }
        }
        return result;
    }

    /**
     * 构建Test1表数据
     * @param test1
     */
    private void buildTest1(Test1 test1, int pos) {
        test1.setId(this.startOffset + pos);

        // 生成xml报文



        // 生成json字符串
//        testTable1.setColumn151(DataModelConstant.DEFAULT_JSON_STRING);
    }

    /**
     * 写文件操作
     *
     * @param testTable1s
     * @param flag
     */
    private void writeData(List<TestTable1> testTable1s, boolean flag) {
        if (testTable1s.size() > 0) {
            //写入数据库
            if (this.committedRows >= this.batchSize) {
                //this.simpleTestTable1Service.batchInsert(testTable1s, this.connection, this.configModel);
                testTable1s.clear();
                this.committedRows = 0;
            } else if (flag) {
                //this.simpleTestTable1Service.batchInsert(testTable1s, this.connection, this.configModel);
                testTable1s.clear();
                this.committedRows = 0;
            }

        }
    }
}
