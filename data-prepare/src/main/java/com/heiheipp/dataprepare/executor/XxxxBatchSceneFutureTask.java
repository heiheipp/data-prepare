package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.SystemClock;
import com.github.javafaker.*;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.*;
import com.heiheipp.common.service.BatchModelService;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.util.RatioUtil;
import com.heiheipp.dataprepare.model.XxxxBatchSceneConfigModel;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxBatchSceneFutureTask
 * @desc TODO
 * @date 2022/3/16 21:00
 */
@Slf4j
public class XxxxBatchSceneFutureTask extends AbstractFutureTask<String> {

    /**
     * 目标数据类型
     */
    private int targetType;

    /**
     * 文件位置
     */
    private String baseFileLocation;

    /**
     * target file location
     */
    private String[] targetFileLocations = new String[6];

    /**
     * 当前线程处理客户数
     */
    private int threadCustNums;

    /**
     * 当前线程处理总数
     */
    private long threadTotalNums;

    /**
     * 每一批次执行行数
     */
    private int batchNums;

    /**
     * 任务执行状态记录服务
     */
    private TaskLogService taskLogService;

    /**
     * batch service
     */
    private BatchModelService batchModelService;

    /**
     * 运行时上下文
     */
    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    /**
     * 子线程id
     */
    private String subThreadId;

    /**
     * 已处理记录数
     */
    private AtomicLong processedNums = new AtomicLong(0L);

    /**
     * 已提交或写入文件记录数
     */
    private int committedRows = 0;

    /**
     * 起始序号偏移量
     */
    private int startOffset;

    /**
     * 配置模型
     */
    private XxxxBatchSceneConfigModel configModel;

    /**
     * faker
     */
    private Faker faker = new Faker(Locale.CHINA);

    /**
     * 首行标志
     */
    private boolean isFirstLine;

    /**
     * test_table_3
     */
    private TestTable3 testTable3 = null;

    /**
     * test_table_4
     */
    private TestTable4 testTable4 = null;

    /**
     * test_table_5
     */
    private TestTable5 testTable5 = null;

    /**
     * test_table_6
     */
    private TestTable6 testTable6 = null;

    /**
     * test_table_7
     */
    private TestTable7 testTable7 = null;

    /**
     * test_table_8
     */
    private TestTable8 testTable8 = null;

    /**
     * dateFormat
     */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * start date
     */
    private Date fromDate, toDate, endDate;

    /**
     * is register flag
     */
    private boolean isRegisterDBLog;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public XxxxBatchSceneFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                    XxxxBatchSceneConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.batchModelService = SpringContextUtil.getBean(BatchModelService.class);
        this.configModel = configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 参数计算
        this.startOffset = this.threadCustNums * configModel.getPerCustCardNums() * threadOrder + 1 +
                            configModel.getBeginOffset();
        this.targetType = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTargetType();
        this.baseFileLocation = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getFileLocation();
        this.isFirstLine = !SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isFileMerge();
        this.isRegisterDBLog = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isRegisterDBLog();

        // 计算当前子线程要处理的总记录数
        this.threadTotalNums = threadCustNums;

        // 初始化日期
        try {
            fromDate = dateFormat.parse("2018-01-01");
            toDate = dateFormat.parse("2022-03-01");
            endDate = dateFormat.parse("2099-01-01");
        } catch (ParseException e) {
            log.error("Generate from date error!!!");
            e.printStackTrace();
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

        log.info("Sub thread[{}] start, threadTotalNums is {}, custNums is {}, batchNums is {}, startOffset is {}.",
                this.subThreadId, this.threadTotalNums, this.threadCustNums,
                this.batchNums, this.startOffset);

        // 计算公共变量
        String fileName = null;
        if (this.targetType == 2) {
            for (int i = 0; i < this.targetFileLocations.length; i++) {
                fileName = "boc_poc.test_table_" + (i + 3) + ".0" + this.subThreadId + ".csv";
                this.targetFileLocations[i] = this.baseFileLocation.equalsIgnoreCase("default") ?
                        new File(".").getAbsolutePath() : this.baseFileLocation;

                if (fileName.contains("test_table_3")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable3() +
                            File.separator + fileName;
                } else if (fileName.contains("test_table_4")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable4() +
                            File.separator + fileName;
                } else if (fileName.contains("test_table_5")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable5() +
                            File.separator + fileName;
                } else if (fileName.contains("test_table_6")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable6() +
                            File.separator + fileName;
                } else if (fileName.contains("test_table_7")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable7() +
                            File.separator + fileName;
                } else if (fileName.contains("test_table_8")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithTestTable8() +
                            File.separator + fileName;
                }
            }
            log.info("Sub thread[{}] write data to file[{}] / [{}] / [{}] / [{}] / [{}] / [{}].",
                    this.subThreadId,
                    this.targetFileLocations[0], this.targetFileLocations[1], this.targetFileLocations[2],
                    this.targetFileLocations[3], this.targetFileLocations[4], this.targetFileLocations[5]);
        }

        // 处理业务公共信息
        int custIdFillLength;
        String custIdPrefix = null;
        switch (this.configModel.getCustType()) {
            case "Personal":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getPersonalCustIdPrefix().length() - DataModelConstant.PROVINCE_LENGTH;
                custIdPrefix = this.configModel.getPersonalCustIdPrefix();
                break;
            case "Company":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getCompanyCustIdPrefix().length() - DataModelConstant.PROVINCE_LENGTH;
                custIdPrefix = this.configModel.getCompanyCustIdPrefix();
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
        Map<String, List<BaseModel>> batchListDatas = new HashMap<>();
        List<BaseModel> testTable3s = new ArrayList<>();
        List<BaseModel> testTable4s = new ArrayList<>();
        List<BaseModel> testTable5s = new ArrayList<>();
        List<BaseModel> testTable6s = new ArrayList<>();
        List<BaseModel> testTable7s = new ArrayList<>();
        List<BaseModel> testTable8s = new ArrayList<>();
        batchListDatas.put(DataModelConstant.TEST_TABLE_3_KEY, testTable3s);
        batchListDatas.put(DataModelConstant.TEST_TABLE_4_KEY, testTable4s);
        batchListDatas.put(DataModelConstant.TEST_TABLE_5_KEY, testTable5s);
        batchListDatas.put(DataModelConstant.TEST_TABLE_6_KEY, testTable6s);
        batchListDatas.put(DataModelConstant.TEST_TABLE_7_KEY, testTable7s);
        batchListDatas.put(DataModelConstant.TEST_TABLE_8_KEY, testTable8s);

        Map<String, BaseModel> batchDatas = new HashMap<>();

        int currentPercentage = 0, prevPercentage = 0;
        try {
            for (int i = 0; i < this.threadCustNums; i++) {
                // 按单客户每次交易笔数制造数据
                buildMultiBatchModel(batchListDatas, batchDatas, custIdPrefix, cardNoPrefix, accountPrefix, i,
                        custIdFillLength, cardNoFillLength, accountFillLength);

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
     * 构建批量单条数据
     * @param batchDatas
     * @param custId
     * @param cardNoArray
     * @param accountArray
     */
    private void buildBatchModel(Map<String, BaseModel> batchDatas, String custId, String[] cardNoArray,
                                 String[] accountArray) {
        // 初始化对象
        testTable3 = new TestTable3();
        testTable4 = new TestTable4();
        testTable5 = new TestTable5();
        testTable6 = new TestTable6();
        testTable7 = new TestTable7();
        testTable8 = new TestTable8();

        // 公共变量
        String instCode = DataBuildUtil.getRandomWithLength(6);
        Name name = faker.name();
        Address address = faker.address();
        PhoneNumber phoneNumber = faker.phoneNumber();
        DateAndTime dateAndTime = faker.date();

        // TEST_TABLE_3
        testTable3.setvCustId(custId);
        testTable3.setvCustTypeCd(DataModelConstant.DEFAULT_CUST_TYPE_CD);
        testTable3.setvInstCode(instCode);
        testTable3.setvSrcCustRefCode(custId + DataModelConstant.CUST_REF_CODE_SUFFIX);
        testTable3.setvFirstName(name.firstName());
        testTable3.setvLastName(name.lastName());
        testTable3.setvSexCd(RatioUtil.pickIndex(configModel.getSexRatio()) == 0 ? "男性" : "女性");
        testTable3.setvAddress(address.streetAddress());
        testTable3.setvCity(address.city());
        testTable3.setvCountry(address.city());
        testTable3.setvPostcode(address.zipCode());
        testTable3.setvPhone(phoneNumber.phoneNumber());
        testTable3.setvMobile(phoneNumber.cellPhone());
        testTable3.setvEmail(faker.internet().emailAddress());
        testTable3.setvMaritalStatusCd(RatioUtil.pickIndex(configModel.getMaritialStatusRatio()) == 0 ?
                "已婚" : "未婚");
        testTable3.setdBirthday(new java.sql.Date(faker.date().birthday().getTime()));

        // TEST_TABLE_5
        testTable5.setvSrcCustRefCode(testTable3.getvSrcCustRefCode());
        testTable5.setvRelationId(testTable5.getvSrcCustRefCode() + DataModelConstant.RELATION_ID_SUFFIX);
        testTable5.setnRelationSeqNo(0);
        testTable5.setdStartDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable5.setdEndDate(new java.sql.Date(endDate.getTime()));
        testTable5.setfDelFlag("0");
        testTable5.setnNextNo(99);
        testTable5.setvInstCode(instCode);
        testTable5.setvCustAddressId("111111");

        // TEST_TABLE_4
        testTable4.setvCardMailerContactId(testTable5.getvRelationId());
        testTable4.setvCardId(cardNoArray[0]);
        testTable4.setvCardIssueId("1");
        testTable4.setvInstCode(instCode);

        // TEST_TABLE_6
        testTable6.setvCardId(cardNoArray[0]);
        testTable6.setvCardAppId(cardNoArray[0]);
        testTable6.setnCardAppIssueId(99);
        testTable6.setvAccountNumber(accountArray[0]);
        testTable6.setvInstCode(instCode);
        testTable6.setnCardIssueId(99);
        testTable6.setvCardNumber(cardNoArray[0]);
        testTable6.setdStartDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable6.setdEndDate(new java.sql.Date(endDate.getTime()));

        // TEST_TABLE_7
        testTable7.setvAccountNumber(accountArray[0]);
        testTable7.setvCorporateAcctFlag(String.valueOf(RatioUtil.pickIndex(configModel.getPrivateCorporateRatio())));
        testTable7.setnCurCycleNo(DataBuildUtil.getIntegerRandom(36) + 1);
        testTable7.setvInstCode(instCode);
        testTable7.setvCardProductCode(DataBuildUtil.getRandomWithLength(8));
        testTable7.setvAcctTypeCd(DataBuildUtil.getRandomWithLength(8));
        testTable7.setvAggreementNumber(DataBuildUtil.getRandomWithLength(20));
        testTable7.setvCustRefCode(DataBuildUtil.getRandomWithLength(10));
        testTable7.setvCcyCode("156");
        testTable7.setvManualStatusCd("1");
        testTable7.setdManualStatusSetDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable7.setvAutoStatusCd("2");
        testTable7.setdAutoStatusSetDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable7.setdOpenDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable7.setvExtAccountNumber(accountArray[0]);
        testTable7.setvExtAccountName(name.name());
        testTable7.setnCardNumber(150);
        testTable7.setnCurBookBal(DataBuildUtil.getRandomBigDecimal(1000, 2));
        testTable7.setnHighestBal(testTable7.getnCurBookBal().add(new BigDecimal(100)));
        testTable7.setnCurDebitInterest(DataBuildUtil.getRandomBigDecimal(20, 2));
        testTable7.setnCurCreditInterest(DataBuildUtil.getRandomBigDecimal(20, 2));
        testTable7.setnPreDelinquencyDays(DataBuildUtil.getIntegerRandom(20) + 1);
        testTable7.setnCurStatementNo(2);
        testTable7.setvAcctRefNo(accountArray[0]);
        testTable7.setdLastAutopaymentDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));

        // TEST_TABLE_8
        testTable8.setvAccountNumber(accountArray[0]);
        testTable8.setnCycleNo(testTable7.getnCurCycleNo());
        testTable8.setdCycleStartDate(testTable7.getdOpenDate());
        testTable8.setdCycleEndDate(testTable7.getdLastAutopaymentDate());
        testTable8.setdCycleDate(testTable7.getdOpenDate());
        testTable8.setvStatementProductionCd(DataBuildUtil.getRandomWithLength(4));
        testTable8.setnOpeningBalance(DataBuildUtil.getRandomBigDecimal(10000, 2));
        testTable8.setnOpeningDue(testTable8.getnOpeningBalance().add(new BigDecimal(500)));
        testTable8.setdOpeningDueDate(new java.sql.Date(dateAndTime.between(fromDate, toDate).getTime()));
        testTable8.setnDirectDebitAmount(DataBuildUtil.getRandomBigDecimal(10000, 2));

        // 组装
        batchDatas.put(DataModelConstant.TEST_TABLE_3_KEY, testTable3);
        batchDatas.put(DataModelConstant.TEST_TABLE_4_KEY, testTable4);
        batchDatas.put(DataModelConstant.TEST_TABLE_5_KEY, testTable5);
        batchDatas.put(DataModelConstant.TEST_TABLE_6_KEY, testTable6);
        batchDatas.put(DataModelConstant.TEST_TABLE_7_KEY, testTable7);
        batchDatas.put(DataModelConstant.TEST_TABLE_8_KEY, testTable8);
    }

    /**
     * 构造批量数据
     *
     * @param batchListDatas
     * @param batchDatas
     * @param custIdPrefix
     * @param cardNoPrefix
     * @param accountPrefix
     * @param currentOffset
     * @param custIdFillLength
     * @param cardNoFillLength
     * @param accountFillLength
     */
    private void buildMultiBatchModel(Map<String, List<BaseModel>> batchListDatas, Map<String, BaseModel> batchDatas,
                                      String custIdPrefix, String cardNoPrefix, String accountPrefix,
                                      int currentOffset, int custIdFillLength,
                                      int cardNoFillLength, int accountFillLength) {
        // 公共信息
        int offset = currentOffset * this.configModel.getPerCustCardNums();
        String custId = DataBuildUtil.generateCustId(custIdPrefix, this.startOffset, offset, custIdFillLength);
        String[] cardNoArray = new String[this.configModel.getPerCustCardNums()];
        String[] accountArray = new String[this.configModel.getPerCustCardNums()];
        for (int k = 0; k < this.configModel.getPerCustCardNums(); k++) {
            cardNoArray[k] = DataBuildUtil.generateCardNo(cardNoPrefix, this.startOffset,
                    offset + k, cardNoFillLength);
            accountArray[k] = DataBuildUtil.generateAccount(accountPrefix, this.startOffset,
                    offset + k, accountFillLength);
        }

        // 按客户造数
        buildBatchModel(batchDatas, custId, cardNoArray, accountArray);

        // 填充结果
        for (Map.Entry<String, BaseModel> entry : batchDatas.entrySet()) {
            switch (entry.getKey()) {
                case DataModelConstant.TEST_TABLE_3_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_3_KEY).add(entry.getValue());
                    break;
                case DataModelConstant.TEST_TABLE_4_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_4_KEY).add(entry.getValue());
                    break;
                case DataModelConstant.TEST_TABLE_5_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_5_KEY).add(entry.getValue());
                    break;
                case DataModelConstant.TEST_TABLE_6_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_6_KEY).add(entry.getValue());
                    break;
                case DataModelConstant.TEST_TABLE_7_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_7_KEY).add(entry.getValue());
                    break;
                case DataModelConstant.TEST_TABLE_8_KEY:
                    batchListDatas.get(DataModelConstant.TEST_TABLE_8_KEY).add(entry.getValue());
                    break;
                default:
                    break;
            }
        }

        // 递增总生成数量和待提交数量
        this.processedNums.incrementAndGet();
        this.committedRows++;
    }

    /**
     * 写数据操作
     *
     * @param batchListDatas
     * @param flag
     */
    private void writeData(Map<String, List<BaseModel>> batchListDatas, boolean flag) {
        if (batchListDatas.get(DataModelConstant.TEST_TABLE_3_KEY).size() > 0) {
            if (this.targetType == 1) {
                //写入数据库
                if (this.committedRows >= this.batchNums) {
                    this.batchModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                } else if (flag) {
                    this.batchModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                }
            } else if (this.targetType == 2) {
                //写入csv文件
                this.batchModelService.writeCsv(this.targetFileLocations, batchListDatas, this.isFirstLine);
                clearBatchMap(batchListDatas);
                this.isFirstLine = false;

                //末尾追加空行
                if (flag) {
                    this.batchModelService.writeCsv();
                }
            }
        }
    }

    /**
     * clear map
     * @param batchListDatas
     */
    private void clearBatchMap(Map<String, List<BaseModel>> batchListDatas) {
        for (List<BaseModel> value : batchListDatas.values()) {
            value.clear();
        }
    }
}
