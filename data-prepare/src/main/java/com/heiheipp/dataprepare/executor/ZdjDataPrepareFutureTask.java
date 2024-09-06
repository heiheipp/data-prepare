package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.*;
import com.heiheipp.common.service.SzTable1Service;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.service.ZdjModelService;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.dataprepare.model.KuaijiTableConfigModel;
import com.heiheipp.dataprepare.model.ZdjConfigModel;
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
 * @className ZdjDataPrepareFutureTask
 * @desc TODO
 * @date 2022/9/11 16:58
 */
@Slf4j
public class ZdjDataPrepareFutureTask extends AbstractFutureTask<String> {

    private int targetType;

    private String fileLocation;

    private int threadCustNums;

    private long threadTotalNums;

    private int batchNums;

    private TaskLogService taskLogService;

    private ZdjModelService zdjModelService;

    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    private String subThreadId;

    private AtomicLong processedNums = new AtomicLong(0L);

    private int committedRows = 0;

    private int startOffset;

    private ZdjConfigModel configModel;

    private Faker faker = new Faker(Locale.CHINA);

    private boolean isFirstLine;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date start;

    private Date end;

    private long dayBetween = 1L;

    private boolean isRegisterDBLog;

    private ZdjTable1 zdjTable1;

    private ZdjTable2 zdjTable2;

    private ZdjTable3 zdjTable3;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public ZdjDataPrepareFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                    ZdjConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.zdjModelService = SpringContextUtil.getBean(ZdjModelService.class);
        this.configModel = configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 参数计算，交易笔数 = 客户数量 * 每客户账户数 * 每账户交易笔数
        this.startOffset = this.threadCustNums * configModel.getPerCustWithCardNums() *
                configModel.getPerCardWithTransNums() * threadOrder + 1;
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
        this.threadTotalNums = (threadCustNums * configModel.getPerCustWithCardNums() *
                configModel.getPerCardWithTransNums()) * this.dayBetween;
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
//        String fileName = null;
//        if (this.targetType == 2) {
//            fileName = "boc_poc.sz_table_1.0" + this.subThreadId + ".csv";
//            this.fileLocation = this.fileLocation.equalsIgnoreCase("default") ?
//                    new File(".").getAbsolutePath() : this.fileLocation;
//            this.fileLocation += File.separator + fileName;
//            log.info("Sub thread[{}] write data to file[{}].", this.subThreadId, this.fileLocation);
//
//            // 将文件名写入主线程
//            SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTmpFileNameList().add(this.fileLocation);
//        }

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
        Map<String, List<BaseModel>> batchListDatas = new HashMap<>();
        List<BaseModel> zdjTable1s = new ArrayList<>();
        List<BaseModel> zdjTable2s = new ArrayList<>();
        List<BaseModel> zdjTable3s = new ArrayList<>();
        batchListDatas.put(DataModelConstant.ZDJ_TABLE1_KEY, zdjTable1s);
        batchListDatas.put(DataModelConstant.ZDJ_TABLE2_KEY, zdjTable2s);
        batchListDatas.put(DataModelConstant.ZDJ_TABLE3_KEY, zdjTable3s);

        Map<String, BaseModel> batchDatas = new HashMap<>();
        boolean flag = false;

        int currentPercentage = 0, prevPercentage = 0;
        try {
            for (int i = 0; i < this.threadCustNums; i++) {
                // 按单客户每次交易笔数制造数据
                buildMultiBatchModel(batchListDatas, batchDatas, custIdPrefix, accountPrefix, i,
                        custIdFillLength, accountFillLength, flag);

                // 数据写入操作
                if (this.processedNums.get() < this.threadTotalNums) {
                    writeData(batchListDatas, false);
                } else {
                    writeData(batchListDatas, true);
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
            writeData(batchListDatas, true);
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
     *
     * @param batchDatas
     * @param custId
     * @param account
     * @param branch
     * @param dateStr
     */
    private void buildBatchModel(Map<String, BaseModel> batchDatas, String custId, String account,
                                 String branch, String dateStr, String tranSeqStr,
                                 long startLongTime, long endLongTime, boolean flag) {
        // 初始化对象
        zdjTable1 = new ZdjTable1();
        zdjTable2 = new ZdjTable2();
        zdjTable3 = new ZdjTable3();

        // 公共变量
        String productNo = "000000" + String.format("%03d", RandomUtil.randomInt(1, 100));
        String currency = DataBuildUtil.currencyArray[DataBuildUtil.getIntegerRandom(
                DataBuildUtil.currencyArray.length)];
        String term = DataBuildUtil.termArray[DataBuildUtil.getIntegerRandom(DataBuildUtil.termArray.length)];
        String transCode = DataBuildUtil.transCodeArray[DataBuildUtil.getIntegerRandom(
                DataBuildUtil.transCodeArray.length)];
        String teller = "000000" + String.format("%d", RandomUtil.randomInt(1, 6));
        String timeStr = Long.toString(new Timestamp(
                DateTimeUtil.randomLongTime(startLongTime, endLongTime)).getTime());

        // 对象赋值
        zdjTable1.setBranch(branch);
        zdjTable1.setTranSeq(tranSeqStr);
        zdjTable1.setAcctNo(account);
        zdjTable1.setAmount(DataBuildUtil.getRandomBigDecimal(50000, 2));
        zdjTable1.setTranCode(transCode);
        zdjTable1.setTellerNo(teller);
        zdjTable1.setTellerNo1(teller);
        zdjTable1.setCustNo(custId);
        zdjTable1.setTranDate(dateStr);
        zdjTable1.setTranTime(timeStr.substring(timeStr.length() - 8));

        // table2和table3每个客户每个账户只处理一次
        if (flag) {
            zdjTable2.setBranch(branch);
            zdjTable2.setAcctNo(account);
            zdjTable2.setCustNo(custId);
            zdjTable2.setProductNo(productNo);
            zdjTable2.setCurrency(currency);
            zdjTable2.setTerm(term);
            zdjTable2.setEndDate(dateStr);
            zdjTable2.setAmount(DataBuildUtil.getRandomBigDecimal(50000, 2));

            zdjTable3.setBranch(branch);
            zdjTable3.setAcctNo(account);
            zdjTable3.setProductNo(productNo);
            zdjTable3.setCurrency(currency);
            zdjTable3.setTerm(term);
            zdjTable3.setInst(DataBuildUtil.getRandomBigDecimal(100, 3));
            zdjTable3.setSignDate(dateStr);
            zdjTable3.setStatus("002081");
        }

        // 组装返回值
        batchDatas.put(DataModelConstant.ZDJ_TABLE1_KEY, zdjTable1);
        batchDatas.put(DataModelConstant.ZDJ_TABLE2_KEY, zdjTable2);
        batchDatas.put(DataModelConstant.ZDJ_TABLE3_KEY, zdjTable3);
    }

    /**
     * 构建多表数据
     *
     * @param batchListDatas
     * @param batchDatas
     * @param custIdPrefix
     * @param accountPrefix
     * @param currentOffset
     * @param custIdFillLength
     * @param accountFillLength
     */
    private void buildMultiBatchModel(Map<String, List<BaseModel>> batchListDatas, Map<String, BaseModel> batchDatas,
                                      String custIdPrefix, String accountPrefix, int currentOffset,
                                      int custIdFillLength, int accountFillLength, boolean flag) {
        // 公共信息
        int offset = currentOffset * this.configModel.getPerCustWithCardNums() *
                this.configModel.getPerCardWithTransNums();
        // 基础业务信息
        String custId = DataBuildUtil.generateCustId(custIdPrefix, this.startOffset, offset, custIdFillLength);
        String branch = "513" + String.format("%02d", RandomUtil.randomInt(1, 99));
        String[] accountArray = new String[this.configModel.getPerCustWithCardNums()];
        for (int k = 0; k < this.configModel.getPerCustWithCardNums(); k++) {
            accountArray[k] = DataBuildUtil.generateAccount(accountPrefix, this.startOffset,
                    offset + k, accountFillLength);
        }

        // 1-外层按照日期循环
        for (int i = 0; i < (new Long(this.dayBetween)).intValue(); i++) {
            long startLongTime = DateUtil.offsetDay(this.start, i).getTime();
            long endLongTime = DateUtil.offsetDay(this.start, i + 1).getTime() - 1L;

            String dateStr = DateUtil.offsetDay(this.start, i).toDateStr().replaceAll("-", "");

            // 2-二层嵌套按照账户数循环
            for (int j = 0; j < this.configModel.getPerCustWithCardNums(); j++) {
                // 设置flag
                flag = (i == 0) ? true : false;

                // 3-三层嵌套按照交易数循环
                for (int x = 0; x < this.configModel.getPerCardWithTransNums(); x++) {
                    // 交易流水号
                    String tranSeqStr = DataBuildUtil.generateSeqStr("", this.startOffset,
                            offset + x, this.configModel.getTranSeqLength());

                    buildBatchModel(batchDatas, custId, accountArray[j], branch, dateStr, tranSeqStr,
                            startLongTime, endLongTime, flag);

                    // 填充结果
                    for (Map.Entry<String, BaseModel> entry : batchDatas.entrySet()) {
                        switch (entry.getKey()) {
                            case DataModelConstant.ZDJ_TABLE1_KEY:
                                batchListDatas.get(DataModelConstant.ZDJ_TABLE1_KEY).add(entry.getValue());
                                break;
                            case DataModelConstant.ZDJ_TABLE2_KEY:
                                if (((ZdjTable2) entry.getValue()).getBranch() != null) {
                                    batchListDatas.get(DataModelConstant.ZDJ_TABLE2_KEY).add(entry.getValue());
                                }
                                break;
                            case DataModelConstant.ZDJ_TABLE3_KEY:
                                if (((ZdjTable3) entry.getValue()).getBranch() != null) {
                                    batchListDatas.get(DataModelConstant.ZDJ_TABLE3_KEY).add(entry.getValue());
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    // 递增总生成数量和待提交数量
                    this.processedNums.incrementAndGet();
                    this.committedRows++;

                    flag = false;
                }
            }
        }
    }

    /**
     * 写文件操作
     *
     * @param batchListDatas
     * @param flag
     */
    private void writeData(Map<String, List<BaseModel>> batchListDatas, boolean flag) {
        if (batchListDatas.get(DataModelConstant.ZDJ_TABLE1_KEY).size() > 0) {
            if (this.targetType == 1) {
                //写入数据库
                if (this.committedRows >= this.batchNums) {
                    this.zdjModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                } else if (flag) {
                    this.zdjModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                }
            }
        }
    }

    /**
     * clear map
     *
     * @param batchListDatas
     */
    private void clearBatchMap(Map<String, List<BaseModel>> batchListDatas) {
        for (List<BaseModel> value : batchListDatas.values()) {
            value.clear();
        }
    }
}
