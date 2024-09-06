package com.heiheipp.dataprepare.service.impl;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import com.heiheipp.common.config.ConfigModel;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.ExecutorServiceFactory;
import com.heiheipp.common.executor.ExecutorTask;
import com.heiheipp.common.mbg.model.CdmTransDetail;
import com.heiheipp.common.mbg.model.SzTable1;
import com.heiheipp.common.mbg.model.TestHtap2Insert001;
import com.heiheipp.common.mbg.model.TestTable1;
import com.heiheipp.common.util.CsvWriterNew;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.common.util.FileMergeUtil;
import com.heiheipp.dataprepare.executor.*;
import com.heiheipp.dataprepare.model.*;
import com.heiheipp.dataprepare.service.DataPrepareService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxDataPrepareServiceImpl
 * @desc TODO
 * @date 2022/3/1 14:29
 */
@Service("xxxxDataPrepareServiceImpl")
@Slf4j
public class XxxxDataPrepareServiceImpl implements DataPrepareService<Boolean> {

    /**
     * 初始化的数据表类型，1-交易基础信息表，2-批量相关表
     */
    @Value("${xxxx.model.type}")
    private int modelType;

    /**
     * 目标存储类型，1-数据库，2-CSV文件（文件名默认为表名.csv）
     */
    @Value("${xxxx.common.targetType}")
    @Getter
    private int targetType;

    /**
     * 文件存放位置，default-执行目录
     */
    @Value("${xxxx.common.fileLocation}")
    @Getter
    private String fileLocation;

    /**
     * 文件拼接标志
     */
    @Value("${xxxx.common.fileMerge}")
    @Getter
    private boolean fileMerge;

    /**
     * 是否记录DB日志
     */
    @Value("${xxxx.common.isRegisterDBLog}")
    @Getter
    private boolean isRegisterDBLog;

    /**
     * xxxx项目配置模型
     */
    private ConfigModel xxxxConfigModel;

    /**
     * debug模式是否开启标志
     */
    @Value("${debug.enable:false}")
    private boolean debugEnable;

    /**
     * debug模式并发数
     */
    @Value("${debug.concurrency}")
    private int debugConcurrency;

    /**
     * 多线程计数器
     */
    private static CountDownLatch countDownLatch;

    /**
     * 初始化标志
     */
    private boolean init = false;

    /**
     * 任务开始时间
     */
    private long startTime;

    /**
     * 线程数量
     */
    private int threadNums;

    /**
     * 临时文件
     */
    @Getter
    private List<String> tmpFileNameList = Collections.synchronizedList(new ArrayList<>());

    /**
     * 最终文件路径
     */
    private String targetFilePath;

    /**
     * 最终文件名
     */
    private String targetFileName;

    @Override
    public void init() {
        log.info("主任务公共配置模型信息：目标数据类型[{}]{}", targetType,
                targetType == 2 ? "，目标存放位置" + fileLocation : "");

        // 设置配置参数模型类
        switch (modelType) {
            case 1:
                xxxxConfigModel = SpringContextUtil.getBean(XxxxTranDetailConfigModel.class);
                log.info("主任务初始化配置模型[{}]，客户数量[{}]，每客户每日交易笔数[{}]，交易日期跨度[{}]天",
                        xxxxConfigModel.getConfigModelDesc(),
                        ((XxxxTranDetailConfigModel) xxxxConfigModel).getCustNum(),
                        ((XxxxTranDetailConfigModel) xxxxConfigModel).getCustTransNumEveryday(),
                        DateTimeUtil.daysBetween(((XxxxTranDetailConfigModel) xxxxConfigModel).getStartDay(), ((XxxxTranDetailConfigModel) xxxxConfigModel).getEndDay()));
                break;
            case 2:
                this.xxxxConfigModel = SpringContextUtil.getBean(XxxxTransMainTableConfigModel.class);
                log.info("主任务初始化配置模型[{}]，客户数量[{}]，每客户每日交易笔数[{}]，交易日期跨度[{}]天",
                        this.xxxxConfigModel.getConfigModelDesc(),
                        ((XxxxTransMainTableConfigModel) this.xxxxConfigModel).getCustNum(),
                        ((XxxxTransMainTableConfigModel) this.xxxxConfigModel).getCustTransNumEveryday(),
                        DateTimeUtil.daysBetween(((XxxxTransMainTableConfigModel) this.xxxxConfigModel).getStartDay(),
                                ((XxxxTransMainTableConfigModel) this.xxxxConfigModel).getEndDay()));
                break;
            case 3:
                this.xxxxConfigModel = SpringContextUtil.getBean(XxxxBatchSceneConfigModel.class);
                log.info("主任务初始化配置模型[{}]，客户数量[{}]", this.xxxxConfigModel.getConfigModelDesc(),
                        ((XxxxBatchSceneConfigModel) xxxxConfigModel).getCustNum());
                break;
            case 4:
                this.xxxxConfigModel = SpringContextUtil.getBean(KuaijiTableConfigModel.class);
                log.info("主任务初始化配置模型[{}]，客户数量[{}]，每客户每日会计分录笔数[{}]，交易日期跨度[{}]天",
                        this.xxxxConfigModel.getConfigModelDesc(),
                        ((KuaijiTableConfigModel) this.xxxxConfigModel).getCustNum(),
                        ((KuaijiTableConfigModel) this.xxxxConfigModel).getPerCustWithTranSeqNums() *
                                ((KuaijiTableConfigModel) this.xxxxConfigModel).getPerTranSeqWithFenluNums(),
                        DateTimeUtil.daysBetween(((KuaijiTableConfigModel) this.xxxxConfigModel).getStartDay(),
                                ((KuaijiTableConfigModel) this.xxxxConfigModel).getEndDay()));
                break;
            case 5:
                this.xxxxConfigModel = SpringContextUtil.getBean(ZdjConfigModel.class);
                log.info("主任务初始化配置模型[{}]，总客户数量[{}]，总账户数量[{}], 总交易笔数[{}]，交易日期跨度[{}]天",
                        this.xxxxConfigModel.getConfigModelDesc(),
                        ((ZdjConfigModel) this.xxxxConfigModel).getCustNum(),
                        ((ZdjConfigModel) this.xxxxConfigModel).getCustNum() * ((ZdjConfigModel) this.xxxxConfigModel).getPerCustWithCardNums(),
                        ((ZdjConfigModel) this.xxxxConfigModel).getCustNum() * ((ZdjConfigModel) this.xxxxConfigModel).getPerCustWithCardNums() * ((ZdjConfigModel) this.xxxxConfigModel).getPerCardWithTransNums(),
                        DateTimeUtil.daysBetween(((ZdjConfigModel) this.xxxxConfigModel).getStartDay(),
                                ((ZdjConfigModel) this.xxxxConfigModel).getEndDay()));
                break;
            case 6:
                this.xxxxConfigModel = SpringContextUtil.getBean(MergeModelConfigModel.class);
                ((MergeModelConfigModel) this.xxxxConfigModel).setTableMask(
                        ((MergeModelConfigModel) this.xxxxConfigModel).getTableMask().substring(1)
                );
                log.info("主任务初始化配置模型[{}]，客户数量[{}]，每客户每日交易笔数[{}]，交易日期跨度[{}]天，表掩码[{}]",
                        this.xxxxConfigModel.getConfigModelDesc(),
                        ((MergeModelConfigModel) this.xxxxConfigModel).getCustNum(),
                        ((MergeModelConfigModel) this.xxxxConfigModel).getCustTransNumEveryday(),
                        DateTimeUtil.daysBetween(((MergeModelConfigModel) this.xxxxConfigModel).getStartDay(),
                                ((MergeModelConfigModel) this.xxxxConfigModel).getEndDay()),
                        ((MergeModelConfigModel) this.xxxxConfigModel).getTableMask());
                break;
            case 7:
                this.xxxxConfigModel = SpringContextUtil.getBean(RegulatoryModelConfigModel.class);
                log.info("主任务初始化配置模型[{}]，客户数量[{}]，每客户每日交易笔数[{}]，交易日期跨度[{}]天，表类型[{}]",
                        this.xxxxConfigModel.getConfigModelDesc(),
                        ((RegulatoryModelConfigModel) this.xxxxConfigModel).getCustNum(),
                        ((RegulatoryModelConfigModel) this.xxxxConfigModel).getCustTransNumEveryday(),
                        DateTimeUtil.daysBetween(((RegulatoryModelConfigModel) this.xxxxConfigModel).getStartDay(),
                                ((RegulatoryModelConfigModel) this.xxxxConfigModel).getEndDay()),
                        ((RegulatoryModelConfigModel) this.xxxxConfigModel).getTableType());
                break;
            case 8:
                this.xxxxConfigModel = SpringContextUtil.getBean(SQLConvertConfigModel.class);
                log.info("主任务初始化配置模型[{}]", this.xxxxConfigModel.getConfigModelDesc());
            default:
                break;
        }

        // 手动模式
        if (debugEnable) {
            ExecutorServiceFactory.getInstance().setExecutorService(debugConcurrency);
        }

        // 设置计数器
        startTime = SystemClock.now();
        threadNums = ExecutorServiceFactory.getInstance().getPoolSize();
        countDownLatch = new CountDownLatch(threadNums);

        // 设置目标文件，仅针对modelType不为3和5的情况
        if (targetType == 2 && modelType != 3 && modelType != 5 && modelType != 6) {
            targetFileName = xxxxConfigModel.getTargetFileName();

            this.targetFilePath = this.fileLocation.equalsIgnoreCase("default") ?
                    new File(".").getAbsolutePath() : this.fileLocation;
            this.targetFilePath = this.targetFilePath + File.separator;

            // 拼接文件提前生成表头文件
            String fileHeaderName = null;
            if (fileMerge) {
                // 生成表头文件
                fileHeaderName = xxxxConfigModel.getFileHeaderName();

                // 如果文件已存在，则删除
                if (FileUtil.exist(this.targetFilePath + fileHeaderName)) {
                    log.debug("文件[{}]已存在，进行删除", this.targetFilePath + fileHeaderName);
                    FileUtil.del(this.targetFilePath + fileHeaderName);
                }

                CsvWriterNew csvWriter = new CsvWriterNew(this.targetFilePath + fileHeaderName,
                        CharsetUtil.CHARSET_UTF_8);

                boolean flag = false;
                switch (modelType) {
                    case 1:
                        csvWriter.writeHeaderLine(CdmTransDetail.generateFileHeader().split(","));
                        flag = true;
                        break;
                    case 2:
//                        csvWriter.writeHeaderLine(TestTable1.generateFileHeader().split(","));
                        flag = true;
                        break;
                    case 4:
                        csvWriter.writeHeaderLine(SzTable1.generateFileHeader().split(","));
                        flag = true;
                        break;
//                    case 7:
//                        if (((RegulatoryModelConfigModel) xxxxConfigModel).getTableType().equals("1")) {
//                            csvWriter.writeHeaderLine(TestHtap2Insert001.generateFileHeader().split(","));
//                        }
//                        break;
                    default:
                        break;
                }

                if (flag) {
                    csvWriter.writeLine();
                    csvWriter.flush();
                }

                tmpFileNameList.add(this.targetFilePath + fileHeaderName);
            }
        }

        init = true;
    }

    @Override
    public Boolean initialData() {
        if (!init) {
            init();
        }

        // 分发多线程（以客户维度为主）
        int totalNums = xxxxConfigModel.getTotalNums();
        boolean flag = (totalNums % threadNums == 0) ? true : false;
        long parentId = IdUtil.getSnowflakeNextId();
        ExecutorTask executorTask = null;

        switch (modelType) {
            case 1:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums * ((XxxxTranDetailConfigModel) xxxxConfigModel).getCustTransNumEveryday() *
                                DateTimeUtil.daysBetween(((XxxxTranDetailConfigModel) xxxxConfigModel).getStartDay(),
                                        ((XxxxTranDetailConfigModel) xxxxConfigModel).getEndDay()),
                        threadNums, startTime);
                break;
            case 2:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums * ((XxxxTransMainTableConfigModel) xxxxConfigModel).getCustTransNumEveryday() *
                                DateTimeUtil.daysBetween(((XxxxTransMainTableConfigModel) xxxxConfigModel).getStartDay(),
                                        ((XxxxTransMainTableConfigModel) xxxxConfigModel).getEndDay()),
                        threadNums, startTime);
                break;
            case 3:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums, threadNums, startTime);
                break;
            case 4:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums * ((KuaijiTableConfigModel) xxxxConfigModel).getPerCustWithTranSeqNums() *
                                ((KuaijiTableConfigModel) xxxxConfigModel).getPerTranSeqWithFenluNums() *
                                DateTimeUtil.daysBetween(((KuaijiTableConfigModel) xxxxConfigModel).getStartDay(),
                                        ((KuaijiTableConfigModel) xxxxConfigModel).getEndDay()),
                        threadNums, startTime);
                break;
            case 5:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums * ((ZdjConfigModel) this.xxxxConfigModel).getPerCustWithCardNums() *
                                ((ZdjConfigModel) xxxxConfigModel).getPerCardWithTransNums() *
                                DateTimeUtil.daysBetween(((ZdjConfigModel) xxxxConfigModel).getStartDay(),
                                        ((ZdjConfigModel) xxxxConfigModel).getEndDay()),
                        threadNums, startTime);
                break;
            case 6:
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        totalNums * ((MergeModelConfigModel) xxxxConfigModel).getCustTransNumEveryday() *
                                DateTimeUtil.daysBetween(((MergeModelConfigModel) xxxxConfigModel).getStartDay(),
                                        ((MergeModelConfigModel) xxxxConfigModel).getEndDay()),
                        threadNums, startTime);
                break;
            case 7:
                int total;
                if (((RegulatoryModelConfigModel) xxxxConfigModel).getTableType().equals("1")) {
                    // TEST_HTAP2_INSERT_001
                    total = totalNums * ((RegulatoryModelConfigModel) xxxxConfigModel).getCustTransNumEveryday() *
                            DateTimeUtil.daysBetween(((RegulatoryModelConfigModel) xxxxConfigModel).getStartDay(),
                                    ((RegulatoryModelConfigModel) xxxxConfigModel).getEndDay());
                } else {
                    // TEST_HTAP2_SORDATA_001 or TEST_HTAP2_SORDATA_002
                    total = totalNums * DateTimeUtil.daysBetween(
                            ((RegulatoryModelConfigModel) xxxxConfigModel).getStartDay(),
                            ((RegulatoryModelConfigModel) xxxxConfigModel).getEndDay());
                }
                log.info("XxxxDataPrepareService以[{}]模式运行[{}]造数任务，主线程[{}], 数据总量[{}]，并发数[{}]， 开始时间[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        total, threadNums, startTime);
                break;
            case 8:
                log.info("SQL转换以[{}]模式运行[{}]任务，主线程[{}]，输入目录为[{}]，输出目录为[{}]",
                        debugEnable ? "手动" : "正常", xxxxConfigModel.getConfigModelDesc(), parentId,
                        ((SQLConvertConfigModel) xxxxConfigModel).getSourceFileLocation(),
                        ((SQLConvertConfigModel) xxxxConfigModel).getTargetFileLocation());
                // todo: 增加参数校验逻辑，包括sourceType必须是mybatis、sourceFileLocation与targetFileLocation不能相同、
                //  targetFileLocation必须是空目录(以免错误覆盖)
            default:
                break;
        }

        if (threadNums > 0) {
            for (int i = 0; i < threadNums; i++) {
                int threadRows;
                if (flag) {
                    threadRows = totalNums / threadNums;
                } else {
                    threadRows = (i < threadNums - 1) ? totalNums / threadNums + 1 : totalNums / threadNums;
                }

                // 按dataType分发不同的处理任务
                switch (modelType) {
                    case 1:
                        executorTask =
                                new XxxxDataPrepareFutureTask(parentId, threadRows, i,
                                        (XxxxTranDetailConfigModel) xxxxConfigModel);
                        break;
                    case 2:
//                        executorTask =
//                                new XxxxTransMainTableFutureTask(parentId, threadRows, i,
//                                        (XxxxTransMainTableConfigModel) xxxxConfigModel);
                        break;
                    case 3:
                        executorTask =
                                new XxxxBatchSceneFutureTask(parentId, threadRows, i,
                                        (XxxxBatchSceneConfigModel) xxxxConfigModel);
                        break;
                    case 4:
                        executorTask =
                                new KuaijiDataPrepareFutureTask(parentId, threadRows, i,
                                        (KuaijiTableConfigModel) xxxxConfigModel);
                        break;
                    case 5:
                        executorTask =
                                new ZdjDataPrepareFutureTask(parentId, threadRows, i,
                                        (ZdjConfigModel) xxxxConfigModel);
                        break;
                    case 6:
                        executorTask =
                                new MergeModelDataFutureTask(parentId, threadRows, i,
                                        (MergeModelConfigModel) xxxxConfigModel);
                        break;
                    case 7:
                        executorTask =
                                new RegulatoryModelDataFutureTask(parentId, threadRows, i, xxxxConfigModel);
                    case 8:
                        executorTask =
                                new SQLConvertFutureTask(parentId, i, xxxxConfigModel);
                    default:
                        break;
                }

                if (executorTask != null) {
                    executorTask.execute();
                } else {
                    log.error("任务执行器不允许为空，请检查");
                    throw new NullPointerException("任务执行器ExecutorTask为空");
                }
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

        // 执行后置操作
        long tmpTime = SystemClock.now();
        if (targetType == 2 && modelType != 3 && fileMerge) {
            try {
                doFilesMerge();
                log.info("主线程[{}]-文件合并子任务执行结束, 任务耗时{}", parentId, SystemClock.now() - tmpTime);
            } catch (Exception e) {
                log.error("主线程[{}]-文件合并子任务执行失败, 任务耗时{}", parentId, SystemClock.now() - tmpTime);
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * 获取计数器
     *
     * @return
     */
    public static CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * 文件合并
     */
    private void doFilesMerge() throws Exception {
        // 生成合并后目标文件
        if (FileUtil.exist(this.targetFilePath + this.targetFileName)) {
            FileUtil.del(this.targetFilePath + this.targetFileName);
        }
        FileUtil.touch(this.targetFilePath + this.targetFileName);

        FileMergeUtil.mergeFiles(tmpFileNameList, this.targetFilePath + this.targetFileName);
    }
}
