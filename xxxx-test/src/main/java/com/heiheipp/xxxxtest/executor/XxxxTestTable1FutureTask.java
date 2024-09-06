package com.heiheipp.xxxxtest.executor;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.TestTable1;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.xxxxtest.engine.impl.XxxxTestEngine;
import com.heiheipp.xxxxtest.model.XxxxTestTable1ConfigModel;
import com.heiheipp.xxxxtest.service.SimpleTestTable1Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxTestTable1FutureTask
 * @desc TODO
 * @date 2022/3/16 16:58
 */
@Slf4j
public class XxxxTestTable1FutureTask extends AbstractFutureTask<String> {

    /**
     * loop count
     */
    private int loopCount;

    /**
     * batch size
     */
    private int batchSize;

    /**
     * SimpleTestTable1Service
     */
    private SimpleTestTable1Service simpleTestTable1Service;

    /**
     * context
     */
    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

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
     * end offset
     */
    private int endOffset;

    /**
     * config model
     */
    private XxxxTestTable1ConfigModel configModel;

    /**
     * faker
     */
    private Faker faker = new Faker(Locale.CHINA);

    /**
     * date format
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * start date
     */
    private Date currDate;

    /**
     * count
     */
    private int count = 1000000;

    /**
     * column6
     */
    private int column6;

    /**
     * XxxxTestEngine
     */
    private XxxxTestEngine xxxxTestEngine;

    /**
     * Connection
     */
    private Connection connection;

    /**
     * PreparedStatement
     */
    private PreparedStatement preparedStatement = null;

    /**
     * sql header
     */
    private String sqlHeader = "insert into test_table_1";

    /**
     * sql columns
     */
    private String sqlColumns = "(column_1, column_2, column_3, column_4, column_5, column_6,\n" +
            "column_7, column_8, column_9, column_10, column_11, column_12, column_13, column_14,\n" +
            "column_15, column_16, column_17, column_18, column_19, column_20, column_21, column_22,\n" +
            "column_23, column_24, column_25, column_26, column_27, column_28, column_29, column_30,\n" +
            "column_31, column_32, column_33, column_34, column_35, column_36, column_37, column_38,\n" +
            "column_39, column_40, column_41, column_42, column_43, column_44, column_45, column_46,\n" +
            "column_47, column_48, column_49, column_50, column_51, column_52, column_53, column_54,\n" +
            "column_55, column_56, column_57, column_58, column_59, column_60, column_61, column_62,\n" +
            "column_63, column_64, column_65, column_66, column_67, column_68, column_69, column_70,\n" +
            "column_71, column_72, column_73, column_74, column_75, column_76, column_77, column_78,\n" +
            "column_79, column_80, column_81, column_82, column_83, column_84, column_85, column_86,\n" +
            "column_87, column_88, column_89, column_90, column_91, column_92, column_93, column_94,\n" +
            "column_95, column_96, column_97, column_98, column_99, column_100, column_101, column_102,\n" +
            "column_103, column_104, column_105, column_106, column_107, column_108, column_109, column_110,\n" +
            "column_111, column_112, column_113, column_114, column_115, column_116, column_117, column_118,\n" +
            "column_119, column_120, column_121, column_122, column_123, column_124, column_125, column_126,\n" +
            "column_127, column_128, column_129, column_130, column_131, column_132, column_133, column_134,\n" +
            "column_135, column_136, column_137, column_138, column_139, column_140, column_141, column_142,\n" +
            "column_143, column_144, column_145, column_146, column_147, column_148, column_149, column_150,\n" +
            "column_151)";

    /**
     * values
     */
    private String value = " values ";

    /**
     * sql body
     */
    private String sqlBody = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param loopCount
     * @param batchSize
     * @param threadOrder
     * @param configModel
     */
    public XxxxTestTable1FutureTask(long parentThreadId, int loopCount, int batchSize,
                                    XxxxTestTable1ConfigModel configModel, int threadOrder,
                                    XxxxTestEngine xxxxTestEngine) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.loopCount = loopCount;
        this.batchSize = batchSize;
        this.configModel = configModel;
        this.simpleTestTable1Service = new SimpleTestTable1Service();
        this.xxxxTestEngine = xxxxTestEngine;

        // 参数计算
        this.startOffset = configModel.getStepSize() * threadOrder + 1;
        this.endOffset = configModel.getStepSize() * (threadOrder + 1);
        this.column6 = this.count + this.startOffset;

        try {
            this.currDate = this.dateFormat.parse(configModel.getCurrentDate());
        } catch (ParseException e) {
            log.error("子线程[{}]解析起始、终止时间异常", this.subThreadId);
            e.printStackTrace();
            throw new RuntimeException("解析起始、终止时间异常");
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

        log.info("Sub thread[{}] start, startOffset is {}.", this.subThreadId, this.startOffset);

        // 处理业务公共信息
        int custIdFillLength;
        String custIdPrefix = null;
        switch (this.configModel.getCustType()) {
            case "Personal":
            case "Company":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getCustIdPrefix().length();
                custIdPrefix = this.configModel.getCustIdPrefix();
                break;
            default:
                custIdFillLength = 0;
                custIdPrefix = "";
                break;
        }

        int cardNumberFillLength = this.configModel.getCardNumberLength() -
                this.configModel.getCardNumberPrefix().length();
        String cardNumberPrefix = this.configModel.getCardNumberPrefix();
        int accountNumberFillLength = this.configModel.getAccountNumberLength() -
                this.configModel.getAccountNumberPrefix().length();
        String accountNumberPrefix = this.configModel.getAccountNumberPrefix();

        // 制造数据
        List<TestTable1> testTable1s = new ArrayList<>();
        TestTable1 testTable1 = null;
        int currentPercentage = 0, prevPercentage = 0;

        // 日期计算
        long startLongTime = DateUtil.offsetDay(this.currDate, 0).getTime();
        long endLongTime = DateUtil.offsetDay(this.currDate, 1).getTime() - 1L;

        int totalNums = this.loopCount * this.configModel.getCustTransNumEveryday();

        try {
            // get connection
            this.connection = xxxxTestEngine.getDataSource().getConnection();
            this.preparedStatement = connection.prepareStatement(sqlHeader + sqlColumns + value + sqlBody);

            for (int i = 0; i < this.loopCount; i++) {
                // 公共信息
                int offset = i * this.configModel.getPerCustCardNums();

                // 防止溢出
                if ((this.startOffset + offset) == this.endOffset) {
                    offset = 0;
                }

                // 客户维度信息
                String custId = DataBuildUtil.generateCustId(custIdPrefix, this.startOffset, offset, custIdFillLength);
                String custName = this.faker.name().fullName();
                String[] cardNoArray = new String[this.configModel.getPerCustCardNums()];
                String[] accountArray = new String[this.configModel.getPerCustCardNums()];
                for (int k = 0; k < this.configModel.getPerCustCardNums(); k++) {
                    cardNoArray[k] = DataBuildUtil.generateCardNo(cardNumberPrefix, this.startOffset,
                            offset + k, cardNumberFillLength);
                    accountArray[k] = DataBuildUtil.generateAccount(accountNumberPrefix, this.startOffset,
                            offset + k, accountNumberFillLength);
                }

                for (int j = 0; j < this.configModel.getCustTransNumEveryday(); j++) {
                    // 交易明细维度信息
                    testTable1 = new TestTable1();
                    buildTestTable1(testTable1, custId, cardNoArray, accountArray, custName,
                            startLongTime, endLongTime);

                    testTable1s.add(testTable1);

                    // 递增总生成数量和待提交数量
                    this.processedNums.incrementAndGet();
                    this.committedRows++;

                    // 数据写入操作
                    writeData(testTable1s, false);

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
            }

            // 补提交剩余部分
            writeData(testTable1s, true);
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
                this.connection.close();
                this.preparedStatement.close();
            } catch (Exception e) {
                log.warn("Close connection failed!");
            }
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
                runtimeDatas.put(ConfigConstant.TASK_TOTAL_NUMS_KEY, this.loopCount);
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

//        try {
//            taskLogService.recordTaskLog();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 构建TestTable1对象
     *
     * @param testTable1
     * @param custId
     * @param cardNoArray
     * @param accountArray
     * @param custName
     * @param startLongTime
     * @param endLongTime
     */
    private void buildTestTable1(TestTable1 testTable1, String custId, String[] cardNoArray, String[] accountArray,
                                 String custName, long startLongTime, long endLongTime) {
        int num = DataBuildUtil.getIntegerRandom(this.configModel.getPerCustCardNums());

//        testTable1.setColumn1(new BigDecimal(IdUtil.getSnowflakeNextIdStr()));
//        testTable1.setColumn2("001");
//        testTable1.setColumn3("00221");
//        testTable1.setColumn4(accountArray[num]);
//        testTable1.setColumn5(new Timestamp(DateTimeUtil.randomLongTime(startLongTime, endLongTime)));
//        testTable1.setColumn6(String.valueOf(this.column6--));
//        testTable1.setColumn7(testTable1.getColumn6());
//        testTable1.setColumn8("00001");
//        testTable1.setColumn9(testTable1.getColumn4());
//        testTable1.setColumn10("0001");
//        testTable1.setColumn11(110);
//        testTable1.setColumn12(92);
//        testTable1.setColumn13(testTable1.getColumn6());
//        testTable1.setColumn14(UUID.randomUUID().toString().replaceAll("-", ""));
//        testTable1.setColumn15("0001");
//        testTable1.setColumn16("0001");
//        testTable1.setColumn17(DataBuildUtil.generateMsg("某", 12, "产品"));
//        testTable1.setColumn18(DataBuildUtil.generateMsg("某", 16, "文书合同"));
//        testTable1.setColumn19("1");
//        testTable1.setColumn20("01");
//        testTable1.setColumn21("001");
//        testTable1.setColumn22(cardNoArray[num]);
//        testTable1.setColumn23("01");
//        testTable1.setColumn24("1");
//        testTable1.setColumn25(DataBuildUtil.getTranType());
//        testTable1.setColumn26(DataBuildUtil.getTranTypeDesc(testTable1.getColumn25()));
//        testTable1.setColumn27(DateUtil.date(testTable1.getColumn5().getTime()).toTimeStr());
//        testTable1.setColumn28("0000001");
//        testTable1.setColumn29(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn30(DataModelConstant.DEFAULT_BRANCH_NAME);
//        testTable1.setColumn31(DateUtil.parse("2021-05-01", "yyyy-MM-dd"));
//        testTable1.setColumn32("0");
//        testTable1.setColumn33("112233");
//        testTable1.setColumn34("112233");
//        testTable1.setColumn35("01");
//        testTable1.setColumn36("提示码");
//        testTable1.setColumn37(DataBuildUtil.getChannel());
//        testTable1.setColumn38(DateUtil.date(testTable1.getColumn5().getTime()).toSqlDate());
//        testTable1.setColumn39(testTable1.getColumn13());
//        testTable1.setColumn40("156");
//        testTable1.setColumn41("CNY");
//        testTable1.setColumn42(NumberUtil.round("1.00", 2));
//        testTable1.setColumn43(DataBuildUtil.getDebitOrCreditFlag());
//        testTable1.setColumn44(DataBuildUtil.getChongzhengFlag(testTable1.getColumn43()));
//        testTable1.setColumn45(DataBuildUtil.getRandomBigDecimal(10000, 2));
//        testTable1.setColumn46(testTable1.getColumn45().add(new BigDecimal(100)));
//        testTable1.setColumn47(testTable1.getColumn46());
//        testTable1.setColumn48(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn49(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn50(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn51(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn52(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn53(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn54(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn55(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn56(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn57(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn58(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn59(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn60(DataBuildUtil.generateMsg("某", 30, "备注"));
//        testTable1.setColumn61(DataBuildUtil.generateMsg("某", 15, "用途"));
//        testTable1.setColumn62(DataBuildUtil.generateMsg("某", 30, "附言"));
//        testTable1.setColumn63(DataBuildUtil.generateMsg("某", 30, "摘要"));
//        testTable1.setColumn64("1");
//        testTable1.setColumn65("现钞");
//        testTable1.setColumn66(DataModelConstant.DEFAULT_PAYER_NAME);
//        testTable1.setColumn67("1234567890123456789");
//        testTable1.setColumn68("1234567890123456789");
//        testTable1.setColumn69("1111");
//        testTable1.setColumn70(221);
//        testTable1.setColumn71(55);
//        testTable1.setColumn72(DataModelConstant.DEFAULT_BRANCH_NAME);
//        testTable1.setColumn73(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn74(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
//        // 付款人姓名
//        if ("D".equalsIgnoreCase(testTable1.getColumn43())) {
//            testTable1.setColumn75(custName);
//        } else {
//            testTable1.setColumn75(DataModelConstant.DEFAULT_PAYER_NAME);
//        }
//
//        testTable1.setColumn76("1234567890123456789");
//        testTable1.setColumn77("12345678901234567890001");
//        testTable1.setColumn78("1111");
//        testTable1.setColumn79(632);
//        testTable1.setColumn80(20);
//        testTable1.setColumn81(DataModelConstant.DEFAULT_BRANCH_NAME);
//        testTable1.setColumn82(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn83(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
//        testTable1.setColumn84(DataModelConstant.DEFAULT_RECEIVER_NAME);
//        testTable1.setColumn85("9876543210987654321");
//        testTable1.setColumn86("12345678901234567890001");
//        testTable1.setColumn87("1111");
//        testTable1.setColumn88(551);
//        testTable1.setColumn89(10);
//        testTable1.setColumn90(DataModelConstant.DEFAULT_BRANCH_NAME);
//        testTable1.setColumn91(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn92(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
//        // 收款人姓名
//        if ("C".equalsIgnoreCase(testTable1.getColumn43())) {
//            testTable1.setColumn93(custName);
//        } else {
//            testTable1.setColumn93(DataModelConstant.DEFAULT_RECEIVER_NAME);
//        }
//
//        testTable1.setColumn94("9876543210987654321");
//        testTable1.setColumn95("98765432109876543210001");
//        testTable1.setColumn96("1111");
//        testTable1.setColumn97(810);
//        testTable1.setColumn98(5);
//        testTable1.setColumn99(DataModelConstant.DEFAULT_BRANCH_NAME);
//        testTable1.setColumn100(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn101(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
//        testTable1.setColumn102("2222");
//        testTable1.setColumn103(DataBuildUtil.generateMsg("某", 2, "凭证"));
//        testTable1.setColumn104("112233445566778899");
//        testTable1.setColumn105("2222");
//        testTable1.setColumn106(DataBuildUtil.generateMsg("某", 2, "产生凭证"));
//        testTable1.setColumn107("112233445566778899");
//        testTable1.setColumn108("998877665544332211");
//        testTable1.setColumn109("1111");
//        testTable1.setColumn110(Integer.valueOf(4));
//        testTable1.setColumn111(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn112(DataModelConstant.ZERO_AMOUNT);
//        testTable1.setColumn113(DataModelConstant.DEFAULT_ADDRESS_PHONE);
//        testTable1.setColumn114(DataModelConstant.DEFAULT_ADDRESS_PHONE);
//        testTable1.setColumn115("1122334455");
//        testTable1.setColumn116("1122334455");
//        testTable1.setColumn117("1122334455");
//        testTable1.setColumn118("1122334455");
//        testTable1.setColumn119("1122334455");
//        testTable1.setColumn120("1122334455");
//        testTable1.setColumn121(DataBuildUtil.generateMsg("某", 1, "系统"));
//        testTable1.setColumn122("11111");
//        testTable1.setColumn123(DateUtil.date(testTable1.getColumn5().getTime()).toSqlDate());
//        testTable1.setColumn124("1111");
//        testTable1.setColumn125("11111");
//        testTable1.setColumn126("1");
//        testTable1.setColumn127("1");
//        testTable1.setColumn128("11112222");
//        testTable1.setColumn129(DataModelConstant.DEFAULT_BRANCH_NO);
//        testTable1.setColumn130("1");
//        testTable1.setColumn131("1");
//        testTable1.setColumn132("1");
//        testTable1.setColumn133("1");
//        testTable1.setColumn134(DateUtil.date(testTable1.getColumn5().getTime()).toSqlDate());
//        testTable1.setColumn135("1");
//        testTable1.setColumn136("1");
//        testTable1.setColumn137("1");
//        testTable1.setColumn138("1");
//        testTable1.setColumn139("1");
//        testTable1.setColumn140("1");
//        testTable1.setColumn141("1");
//        testTable1.setColumn142("1");
//        testTable1.setColumn143("1");
//        testTable1.setColumn144(custId);
//        testTable1.setColumn145("1");
//        testTable1.setColumn146("1");
//
//        // 生成json字符串
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
                this.simpleTestTable1Service.batchInsert(testTable1s, this.connection, this.preparedStatement,
                        this.configModel);
                testTable1s.clear();
                this.committedRows = 0;
            } else if (flag) {
                this.simpleTestTable1Service.batchInsert(testTable1s, this.connection, this.preparedStatement,
                        this.configModel);
                testTable1s.clear();
                this.committedRows = 0;
            }

        }
    }
}
