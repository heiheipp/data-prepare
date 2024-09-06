package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.SzTable1;
import com.heiheipp.common.service.SzTable1Service;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.dataprepare.model.KuaijiTableConfigModel;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className SQLPlanPrepareFutureTask
 * @desc TODO
 * @date 2022/9/11 16:58
 */
@Slf4j
public class SQLPlanPrepareFutureTask extends AbstractFutureTask<String> {

    private int targetType;

    private String fileLocation;

    private int threadCustNums;

    private long threadTotalNums;

    private int batchNums;

    private TaskLogService taskLogService;

    private SzTable1Service szTable1Service;

    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    private String subThreadId;

    private AtomicLong processedNums = new AtomicLong(0L);

    private int committedRows = 0;

    private int startOffset;

    private KuaijiTableConfigModel configModel;

    private Faker faker = new Faker(Locale.CHINA);

    private boolean isFirstLine;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date start;

    private Date end;

    private long dayBetween = 1L;

    private boolean isRegisterDBLog;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public SQLPlanPrepareFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                    KuaijiTableConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.szTable1Service = SpringContextUtil.getBean(SzTable1Service.class);
        this.configModel = configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 参数计算，交易笔数 = 客户数量 * 每客户每日主交易笔数 * 每笔主交易分录数
        this.startOffset = this.threadCustNums * configModel.getPerCustWithTranSeqNums() *
                                    configModel.getPerTranSeqWithFenluNums() * threadOrder + 1;
        this.targetType = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTargetType();
        this.fileLocation = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getFileLocation();
        this.isFirstLine = !SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isFileMerge();
        this.isRegisterDBLog = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isRegisterDBLog();

        // 以startDay和endDay计算要循环的天数
        if (!StrUtil.isEmpty(configModel.getStartDay()) && !StrUtil.isEmpty(configModel.getEndDay())) {
            try {
                this.start = this.dateFormat.parse(configModel.getStartDay());
                this.end = this.dateFormat.parse(configModel.getEndDay());

                // 日期校验
                if (this.start.compareTo(this.end) > 0) {
                    log.error("起始日期不能大于终止日期");
                    throw new RuntimeException("起始日期不能大于终止日期");
                }

                // 计算时间差
                this.dayBetween = DateUtil.between(this.start, this.end, DateUnit.DAY) + 1L;
            } catch (ParseException e) {
                log.error("子线程[{}]解析起始、终止时间异常", this.subThreadId);
                e.printStackTrace();
                throw new RuntimeException("解析起始、终止时间异常");
            }
        } else if (configModel.getDays() > 0) {
            this.dayBetween = configModel.getDays();
        }

        // 计算当前子线程要处理的总记录数
        this.threadTotalNums = (threadCustNums * configModel.getPerCustWithTranSeqNums() *
                configModel.getPerTranSeqWithFenluNums()) * this.dayBetween;
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

        log.info("Sub thread[{}] start, threadTotalNums is {}, custNums is {}, duration is {} days, batchNums is {}, startOffset is {}, isRegisterDBLog is {}.",
                this.subThreadId, this.threadTotalNums, this.threadCustNums, this.dayBetween,
                this.batchNums, this.startOffset, this.isRegisterDBLog);

        // 计算公共变量
        String fileName = null;
        if (this.targetType == 2) {
            fileName = "boc_poc.sz_table_1.0" + this.subThreadId + ".csv";
            this.fileLocation = this.fileLocation.equalsIgnoreCase("default") ?
                    new File(".").getAbsolutePath() : this.fileLocation;
            this.fileLocation += File.separator + fileName;
            log.info("Sub thread[{}] write data to file[{}].", this.subThreadId, this.fileLocation);

            // 将文件名写入主线程
            SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTmpFileNameList().add(this.fileLocation);
        }

        // 处理业务公共信息
        int custIdFillLength;
        String custIdPrefix = null;
        switch (this.configModel.getCustType()) {
            case "Personal":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getPersonalCustIdPrefix().length();
                custIdPrefix = this.configModel.getPersonalCustIdPrefix();
                break;
            default:
                custIdFillLength = 0;
                custIdPrefix = "";
                break;
        }

        int accountFillLength = this.configModel.getAccountLength() - this.configModel.getAccountPrefix().length();
        String accountPrefix = this.configModel.getAccountPrefix();

        // 登记任务开始日志
        if (isRegisterDBLog) {
            recordTask(ConfigConstant.TaskStatusEnum.START);
        }

        // 制造数据
        List<SzTable1> szTable1s = new ArrayList<>();
        SzTable1 szTable1 = null;
        int currentPercentage = 0, prevPercentage = 0;
        try {
            for (int i = 0; i < this.threadCustNums; i++) {
                // 按单客户每次交易笔数制造数据
                buildMultiSzTable1(szTable1s, szTable1, custIdPrefix, accountPrefix, i,
                        custIdFillLength, accountFillLength);

                // 数据写入操作
                if (this.processedNums.get() < this.threadTotalNums) {
                    writeData(szTable1s, false);
                } else {
                    writeData(szTable1s, true);
                }

                // 登记任务处理中日志
                prevPercentage = currentPercentage;
                currentPercentage = DataBuildUtil.getPercentge(this.processedNums.get(), this.threadTotalNums);
                if (DataBuildUtil.getPercentageWtihTens(this.processedNums.get(), this.threadTotalNums) &&
                        prevPercentage < currentPercentage) {
                    if (isRegisterDBLog) {
                        recordTask(ConfigConstant.TaskStatusEnum.PROCESSING);
                    }
                    log.info("Sub thread {} has been processing {}%, processedNums is {}, totalNums is {}, execution time is {}.",
                            this.subThreadId, currentPercentage, this.processedNums.get(), this.threadTotalNums,
                            SystemClock.now() - startTime);
                }
            }

            // 补提交剩余部分
            writeData(szTable1s, true);
        } catch (Exception e) {
            log.error("Sub thread {} error, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);
            e.printStackTrace();

            // 登记任务失败信息
            if (isRegisterDBLog) {
                recordTask(ConfigConstant.TaskStatusEnum.ERROR);
            }
        } finally {
            log.info("Sub thread {} finish, and execution time is {}ms.", this.subThreadId,
                    System.currentTimeMillis() - startTime);

            // 登记任务结束信息
            if (isRegisterDBLog) {
                recordTask(ConfigConstant.TaskStatusEnum.SUCCESS);
            }

            // 清理线程上下文
            RuntimeContext.clearRuntimeData();

            // 计数器操作
            XxxxDataPrepareServiceImpl.getCountDownLatch().countDown();
        }
        return result;
    }

    /**
     * 构建任务日志数据
     */
    private void buildRuntimeDatas(ConfigConstant.TaskStatusEnum taskStatusEnum, Object... args) {
        switch (taskStatusEnum) {
            case START:
                // 登记开始任务
                runtimeDatas.put(ConfigConstant.PARENT_THREAD_ID_KEY, this.parentThreadId);
                runtimeDatas.put(ConfigConstant.SUB_THREAD_ID_KEY, subThreadId);
                runtimeDatas.put(ConfigConstant.TASK_TYPE_KEY, ConfigConstant.TaskTypeEnum.DATA_PREPARE.getTypeDesc());
                runtimeDatas.put(ConfigConstant.TASK_CONTENT_KEY, getTaskContent());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.START.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_TOTAL_NUMS_KEY, this.threadTotalNums);
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, 0L);
                runtimeDatas.put(ConfigConstant.TASK_CREATE_TIME_KEY, SystemClock.now());
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.START);
                break;
            case PROCESSING:
                // 更新处理中任务
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.PROCESSING.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums.get());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.PROCESSING);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            case ERROR:
                // 任务异常
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.ERROR.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums.get());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.ERROR);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            case SUCCESS:
                // 任务成功结束
                runtimeDatas.put(ConfigConstant.TASK_STATUS_KEY, ConfigConstant.TaskStatusEnum.SUCCESS.getStatus());
                runtimeDatas.put(ConfigConstant.TASK_PROCESSED_NUMS_KEY, processedNums.get());
                runtimeDatas.put(ConfigConstant.TASK_STATUS_ENUM_KEY, ConfigConstant.TaskStatusEnum.SUCCESS);
                runtimeDatas.put(ConfigConstant.TASK_UPDATE_TIME_KEY, SystemClock.now());
                break;
            default:
                break;
        }
    }

    /**
     * 获取任务类型
     *
     * @return
     */
    private String getTaskContent() {
        return "SZ_TABLE_1表数据准备";
    }

    /**
     * 登记任务日志
     *
     * @param taskStatusEnum
     */
    private void recordTask(ConfigConstant.TaskStatusEnum taskStatusEnum) {
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
            taskLogService.recordTaskLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建SzTable1对象
     * @param szTable1
     * @param custId
     * @param accountArray
     * @param startLongTime
     * @param endLongTime
     */
    private void buildSzTable1(SzTable1 szTable1, String custId, String[] accountArray,
                               long startLongTime, long endLongTime, String dateStr,
                               String tranSeqStr, String[] fenluArray, String branch, int pos) {
        szTable1.setBankNo("001");
        szTable1.setJizhangDate(dateStr);
        szTable1.setTranDate(dateStr);

        String timeStr = Long.toString(new Timestamp(
                DateTimeUtil.randomLongTime(startLongTime, endLongTime)).getTime());
        szTable1.setTranTime(timeStr.substring(timeStr.length() - 8));

        // 交易流水、分录
        szTable1.setTranSeq(tranSeqStr);
        szTable1.setFenluSeq(fenluArray[pos]);

        // 机构信息
        szTable1.setJizhangBranch(branch);
        szTable1.setJizhangCurrency("CNY");

        // 核算的三个字段有对应关系
        int num = DataBuildUtil.getIntegerRandom(10);
        szTable1.setHesuanCode(DataBuildUtil.hesuanCodeArray[num]);
        szTable1.setHesuanObjCode(DataBuildUtil.hesuanObjArray[num]);
        szTable1.setSourceCode(DataBuildUtil.sourceCodeArray[num]);

        szTable1.setCustNo(custId);
        szTable1.setJizhangAmount(DataBuildUtil.getRandomBigDecimal(10000, 2));
        szTable1.setAcctNo(accountArray[pos]);
        szTable1.setTranCode("0000");
        szTable1.setTellerNo("aaaa001");
    }

    /**
     * 构建SzTable1列表
     * @param szTable1s
     * @param szTable1
     * @param custIdPrefix
     * @param accountPrefix
     * @param currentOffset
     * @param custIdFillLength
     * @param accountFillLength
     */
    private void buildMultiSzTable1(List<SzTable1> szTable1s, SzTable1 szTable1, String custIdPrefix,
                                      String accountPrefix, int currentOffset,
                                      int custIdFillLength, int accountFillLength) {
        // 公共信息
        int offset = currentOffset * this.configModel.getPerCustWithTranSeqNums() *
                            this.configModel.getPerTranSeqWithFenluNums();
        // 客户id
        String custId = DataBuildUtil.generateCustId(custIdPrefix, this.startOffset, offset, custIdFillLength);
        String branch = String.format("%05d", RandomUtil.randomInt(1, 50000));

        // 1-外层按照日期循环
        for (int i = 0; i < (new Long(this.dayBetween)).intValue(); i++) {
            long startLongTime = DateUtil.offsetDay(this.start, i).getTime();
            long endLongTime = DateUtil.offsetDay(this.start, i + 1).getTime() - 1L;

            String dateStr = DateUtil.offsetDay(this.start, i).toDateStr().replaceAll("-", "");

            // 2-内层首先按照每日主交易数循环
            for (int j = 0; j < this.configModel.getPerCustWithTranSeqNums(); j++) {
                // 主交易流水后
                String tranSeqStr = DataBuildUtil.generateSeqStr("", this.startOffset,
                        offset + j, this.configModel.getTranSeqLength());
                // 分录序号
                String[] fenluArray = new String[]{"01", "02", "03"};
                // 账号
                String[] accountArray = new String[this.configModel.getPerTranSeqWithFenluNums()];
                for (int k = 0; k < this.configModel.getPerTranSeqWithFenluNums(); k++) {
                    accountArray[k] = DataBuildUtil.generateAccount(accountPrefix, this.startOffset +
                                    (j * this.configModel.getPerTranSeqWithFenluNums()),
                            offset + k, accountFillLength);
                }

                // 3-内层再按照每笔主交易的分录数循环
                for (int x = 0; x < this.configModel.getPerTranSeqWithFenluNums(); x++) {
                    szTable1 = new SzTable1();
                    buildSzTable1(szTable1, custId, accountArray, startLongTime, endLongTime,
                            dateStr, tranSeqStr, fenluArray, branch, x);

                    szTable1s.add(szTable1);

                    // 递增总生成数量和待提交数量
                    this.processedNums.incrementAndGet();
                    this.committedRows++;
                }
            }
        }
    }

    /**
     * 写文件操作
     * @param szTable1s
     * @param flag
     */
    private void writeData(List<SzTable1> szTable1s, boolean flag) {
        if (szTable1s.size() > 0) {
            if (this.targetType == 1) {
                //写入数据库
                if (this.committedRows >= this.batchNums) {
                    this.szTable1Service.batchInsert(szTable1s);
                    szTable1s.clear();
                    this.committedRows = 0;
                } else if (flag) {
                    this.szTable1Service.batchInsert(szTable1s);
                    szTable1s.clear();
                    this.committedRows = 0;
                }
            } else if (this.targetType == 2) {
                //写入csv文件
                this.szTable1Service.writeCsv(this.fileLocation, szTable1s, this.isFirstLine);
                szTable1s.clear();
                this.isFirstLine = false;

                //末尾追加空行
                if (flag) {
                    this.szTable1Service.writeCsv();
                }
            }
        }
    }
}
