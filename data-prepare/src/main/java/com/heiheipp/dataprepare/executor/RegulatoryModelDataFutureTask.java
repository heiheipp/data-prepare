package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.config.ConfigModel;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.*;
import com.heiheipp.common.service.MergeModelService;
import com.heiheipp.common.service.RegulatoryService;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.dataprepare.model.MergeModelConfigModel;
import com.heiheipp.dataprepare.model.RegulatoryModelConfigModel;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className RegulatoryModelDataFutureTask
 * @desc TODO
 * @date 2022/3/16 16:58
 */
@Slf4j
public class RegulatoryModelDataFutureTask extends AbstractFutureTask<String> {

    private int targetType;

    private String fileLocation;

    private int threadCustNums;

    private long threadTotalNums;

    private int batchNums;

    private TaskLogService taskLogService;

    private RegulatoryService regulatoryService;

    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    private String subThreadId;

    private AtomicLong processedNums = new AtomicLong(0L);

    private int committedRows = 0;

    private int startOffset;

    private RegulatoryModelConfigModel configModel;

    private Faker faker = new Faker(Locale.CHINA);

    private boolean isFirstLine;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date start;

    private Date end;

    private long dayBetween = 1L;

    private boolean isRegisterDBLog;

    private TestHtap2Insert001 testHtap2Insert001;

    private TestHtap2Sordata001 testHtap2Sordata001;

    private TestHtap2Sordata002 testHtap2Sordata002;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public RegulatoryModelDataFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                         ConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.regulatoryService = SpringContextUtil.getBean(RegulatoryService.class);
        this.configModel = (RegulatoryModelConfigModel) configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 参数计算
        if (this.configModel.getTableType().equals("1")) {
            // TEST_HTAP2_INSERT_001 = 客户数 * 交易数
            this.startOffset = this.threadCustNums * this.configModel.getCustTransNumEveryday() * threadOrder + 1 +
                    this.configModel.getSerialNoOffset();
            this.threadTotalNums = (threadCustNums * this.configModel.getCustTransNumEveryday()) * this.dayBetween;
        } else {
            // TEST_HTAP2_SORDATA_001 or TEST_HTAP2_SORDATA_002 = 客户数
            this.startOffset = this.threadCustNums * threadOrder + 1 + this.configModel.getSerialNoOffset();
            this.threadTotalNums = threadCustNums * this.dayBetween;
        }
        this.targetType = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTargetType();
        this.fileLocation = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getFileLocation();
        this.isFirstLine = !SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isFileMerge();
        this.isRegisterDBLog = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isRegisterDBLog();

        // 以startDay和endDay计算要循环的天数
        if (!StrUtil.isEmpty(this.configModel.getStartDay()) && !StrUtil.isEmpty(this.configModel.getEndDay())) {
            try {
                this.start = this.dateFormat.parse(this.configModel.getStartDay());
                this.end = this.dateFormat.parse(this.configModel.getEndDay());

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
        } else if (this.configModel.getDays() > 0) {
            this.dayBetween = this.configModel.getDays();
        }
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

        // 处理文件信息
        String fileName = null;
        if (this.targetType == 2) {
            if (configModel.getTableType().equals("1")) {
                fileName = "TEST_HTAP2_INSERT_001.0" + this.subThreadId + ".csv";
                this.fileLocation = this.fileLocation.equalsIgnoreCase("default") ?
                        new File(".").getAbsolutePath() : this.fileLocation;
                this.fileLocation += File.separator + fileName;
                log.info("Sub thread[{}] write data to file[{}].", this.subThreadId, this.fileLocation);
            }

            // 将文件名写入主线程
            SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTmpFileNameList().add(this.fileLocation);
        }

        // 处理业务公共信息
        int custIdFillLength;
        String custIdPrefix = null;
        switch (this.configModel.getCustType()) {
            case "Personal":
            case "Corporate":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getCustIdPrefix().length() -
                        (this.configModel.isCustWithProvinceFlag() ? DataModelConstant.PROVINCE_LENGTH : 0);
                custIdPrefix = this.configModel.getCustIdPrefix();
                break;
            default:
                custIdFillLength = 0;
                custIdPrefix = "";
                break;
        }

        int cardNoFillLength = this.configModel.getCardLength() - this.configModel.getCardBin().length();
        String cardNoPrefix = this.configModel.getCardBin();
        int accountFillLength = this.configModel.getAccountLength() - this.configModel.getAccountPrefix().length();
        String accountPrefix = this.configModel.getAccountPrefix();

        // 登记任务开始日志
        if (isRegisterDBLog) {
            recordTask(ConfigConstant.TaskStatusEnum.START);
        }

        // 制造数据
        List<BaseModel> testTables = new ArrayList<>();
        BaseModel testTable = null;
        int currentPercentage = 0, prevPercentage = 0;

        try {
            for (int i = 0; i < this.threadCustNums; i++) {
                // 按单客户每次交易笔数制造数据
                buildMultiDatas(testTables, testTable, custIdPrefix, cardNoPrefix, accountPrefix, i,
                        custIdFillLength, cardNoFillLength, accountFillLength);

                // 数据写入操作
                if (this.processedNums.get() < this.threadTotalNums) {
                    writeData(testTables, false);
                } else {
                    writeData(testTables, true);
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
            writeData(testTables, true);
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
        return "TEST_TABLE_1表数据准备";
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
     * 构建多表对象
     *
     * @param testTable
     * @param custId
     * @param cardNoArray
     * @param accountArray
     * @param custName
     * @param dateStr
     * @param tranSeqStr
     */
    private void buildTestTable(BaseModel testTable, String custId,
                                String[] cardNoArray, String[] accountArray, String custName,
                                String dateStr, String tranSeqStr, String branchNo) {
        // 公共信息
        int num = DataBuildUtil.getIntegerRandom(this.configModel.getPerCustCardNums());
        String zdyZj = IdUtil.getSnowflakeNextIdStr();

        // 根据类型处理不同的表
        switch (configModel.getTableType()) {
            case "1":
                // TEST_HTAP2_INSERT_001
                ((TestHtap2Insert001) testTable).setZdyZj(zdyZj);
                ((TestHtap2Insert001) testTable).setDte(dateStr);
                ((TestHtap2Insert001) testTable).setJyxlh(tranSeqStr);
                ((TestHtap2Insert001) testTable).setNbjgh(branchNo);
                ((TestHtap2Insert001) testTable).setKhtybh(Long.parseLong(custId) % 10000L == 1 ? "" : custId);
                ((TestHtap2Insert001) testTable).setZhmc(custName);
                ((TestHtap2Insert001) testTable).setGrckzh(accountArray[num]);
                ((TestHtap2Insert001) testTable).setWbzh(cardNoArray[num]);
                ((TestHtap2Insert001) testTable).setHxjyrq(dateStr);
                ((TestHtap2Insert001) testTable).setBz("CNY");
                ((TestHtap2Insert001) testTable).setJyje(DataBuildUtil.getRandomBigDecimal(10000, 2));
                ((TestHtap2Insert001) testTable).setZhye(
                        ((TestHtap2Insert001) testTable).getJyje().add(new BigDecimal(100)));
                ((TestHtap2Insert001) testTable).setCjrq(dateStr);
                ((TestHtap2Insert001) testTable).setGsfzjg(branchNo);
                break;
            case "2":
                // TEST_HTAP2_SORDATA_001
                ((TestHtap2Sordata001) testTable).setZdyZj(zdyZj);
                ((TestHtap2Sordata001) testTable).setDte(dateStr);
                ((TestHtap2Sordata001) testTable).setNbjgh(branchNo);
                ((TestHtap2Sordata001) testTable).setKhtybh(custId);
                ((TestHtap2Sordata001) testTable).setKhxm(custName);
                ((TestHtap2Sordata001) testTable).setCjrq(dateStr);
                ((TestHtap2Sordata001) testTable).setGsfzjg(branchNo);
                break;
            case "3":
                // TEST_HTAP2_SORDATA_002
                ((TestHtap2Sordata002) testTable).setZdyZj(zdyZj);
                ((TestHtap2Sordata002) testTable).setDte(dateStr);
                ((TestHtap2Sordata002) testTable).setNbjgh(branchNo);
                ((TestHtap2Sordata002) testTable).setKhtybh(custId);
                ((TestHtap2Sordata002) testTable).setKhmc(custName);
                ((TestHtap2Sordata002) testTable).setJbckzh(accountArray[num]);
                ((TestHtap2Sordata002) testTable).setCjrq(dateStr);
                ((TestHtap2Sordata002) testTable).setGsfzjg(branchNo);
                break;
            default:
                break;
        }
    }

    /**
     * 构建多表数据
     *
     * @param testTables
     * @param testTable
     * @param custIdPrefix
     * @param cardNoPrefix
     * @param accountPrefix
     * @param currentOffset
     * @param custIdFillLength
     * @param cardNoFillLength
     * @param accountFillLength
     */
    private void buildMultiDatas(List<BaseModel> testTables, BaseModel testTable,
                                      String custIdPrefix, String cardNoPrefix, String accountPrefix, int currentOffset,
                                      int custIdFillLength, int cardNoFillLength, int accountFillLength) {
        // 公共信息
        int offset;
        if (this.configModel.getTableType().equals("1")) {
            // TEST_HTAP2_INSERT_001
            offset = currentOffset * this.configModel.getCustTransNumEveryday();
        } else {
            // TEST_HTAP2_SORDATA_001 or TEST_HTAP2_SORDATA_002
            offset = currentOffset;
        }

        // 客户id
        String custId = DataBuildUtil.generateCustId(custIdPrefix +
                        (configModel.isCustWithProvinceFlag() ? configModel.getProvinceCode() : ""),
                        this.startOffset, offset, custIdFillLength);
        String custName = this.faker.name().fullName();
        String branchNo = DataBuildUtil.generateBranchNo(configModel.getProvinceCode(), this.startOffset,
                offset, configModel.getBranchNoLength() - configModel.getProvinceCode().length());

        // 账号/卡号
        String[] cardNoArray = new String[this.configModel.getPerCustCardNums()];
        String[] accountArray = new String[this.configModel.getPerCustCardNums()];
        for (int k = 0; k < this.configModel.getPerCustCardNums(); k++) {
            cardNoArray[k] = DataBuildUtil.generateCardNo(cardNoPrefix, this.startOffset,
                    offset + k, cardNoFillLength);
            accountArray[k] = DataBuildUtil.generateAccount(accountPrefix, this.startOffset,
                    offset + k, accountFillLength);
        }

        String tranSeqStr = null;

        // 外层按照日期循环
        for (int i = 0; i < (new Long(this.dayBetween)).intValue(); i++) {
            String dateStr = DateUtil.offsetDay(this.start, i).toDateStr().replaceAll("-", "");

            if (!this.configModel.getTableType().equals("1")) {
                // TEST_HTAP2_SORDATA_001 or TEST_HTAP2_SORDATA_002
                tranSeqStr = DataBuildUtil.generateSeqStr("", this.startOffset,
                        offset + i, this.configModel.getTranSeqLength());

                // 初始化对象
                switch (configModel.getTableType()) {
                    case "1":
                        testTable = new TestHtap2Insert001();
                        break;
                    case "2":
                        testTable = new TestHtap2Sordata001();
                        break;
                    case "3":
                        testTable = new TestHtap2Sordata002();
                        break;
                    default:
                        break;
                }

                buildTestTable(testTable, custId, cardNoArray, accountArray, custName, dateStr, tranSeqStr, branchNo);

                testTables.add(testTable);

                // 递增总生成数量和待提交数量
                this.processedNums.incrementAndGet();
                this.committedRows++;
            } else {
                // TEST_HTAP2_INSERT_001
                // 内层按照每日交易数循环
                for (int j = 0; j < this.configModel.getCustTransNumEveryday(); j++) {
                    // 初始化对象
                    switch (configModel.getTableType()) {
                        case "1":
                            testTable = new TestHtap2Insert001();
                            break;
                        case "2":
                            testTable = new TestHtap2Sordata001();
                            break;
                        case "3":
                            testTable = new TestHtap2Sordata002();
                            break;
                        default:
                            break;
                    }

                    // 交易流水号
                    tranSeqStr = DataBuildUtil.generateSeqStr("", this.startOffset,
                            offset + j, this.configModel.getTranSeqLength());

                    buildTestTable(testTable, custId, cardNoArray, accountArray, custName, dateStr,
                            tranSeqStr, branchNo);

                    testTables.add(testTable);

                    // 递增总生成数量和待提交数量
                    this.processedNums.incrementAndGet();
                    this.committedRows++;
                }
            }
        }
    }

    /**
     * 写数据操作
     *
     * @param testTables
     * @param flag
     */
    private void writeData(List<BaseModel> testTables, boolean flag) {
        if (testTables.size() > 0) {
            if (this.targetType == 1) {
                //写入数据库
                if (this.committedRows >= this.batchNums) {
                    this.regulatoryService.batchInsert(configModel.getTableType(), testTables);
                    testTables.clear();
                    this.committedRows = 0;
                } else if (flag) {
                    this.regulatoryService.batchInsert(configModel.getTableType(), testTables);
                    testTables.clear();
                    this.committedRows = 0;
                }
            } else if (this.targetType == 2) {
                //写入csv文件
                this.isFirstLine = false;
                this.regulatoryService.writeCsv(this.fileLocation, testTables, false);
                testTables.clear();

                //末尾追加空行
                if (flag) {
                    this.regulatoryService.writeCsv();
                }
            }
        }
    }
}
