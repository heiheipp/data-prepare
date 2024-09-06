package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.CdmTransDetail;
import com.heiheipp.common.service.CdmTransDetailService;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.dataprepare.model.XxxxTranDetailConfigModel;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxDataPrepareFutureTask
 * @desc Xxxx相关数据表的造数任务执行类
 * @date 2022/3/2 15:18
 */
@Slf4j
public class XxxxDataPrepareFutureTask extends AbstractFutureTask<String> {

    /**
     * 目标存储类型，1-数据库，2-CSV文件（文件名默认为表名.csv）
     */
    private int targetType;

    /**
     * 文件存放位置，default-执行目录
     */
    private String fileLocation;

    /**
     * 线程客户总数
     */
    private int threadCustNums;

    /**
     * 线程执行总数
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
     * CDM_TRANS_DETAIL表操作服务
     */
    private CdmTransDetailService cdmTransDetailService;

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
    private AtomicLong processedNums = new AtomicLong(0);

    /**
     * 已提交记录数
     */
    private int committedRows = 0;

    /**
     * 当前线程偏移量
     */
    private int threadOrder;

    /**
     * 发号器起始位移
     */
    private int startOffset;

    /**
     * 配置模型
     */
    private XxxxTranDetailConfigModel configModel;

    /**
     * faker
     */
    private Faker faker = new Faker(Locale.CHINA);

    /**
     * 是否需要表头
     */
    private boolean isFirstLine;

    /**
     * 日期格式
     */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 开始、终止日期
     */
    private Date start, end;

    /**
     * 日期时间差
     */
    private long dayBetween = 1;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public XxxxDataPrepareFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                     XxxxTranDetailConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.cdmTransDetailService = SpringContextUtil.getBean(CdmTransDetailService.class);
        this.threadOrder = threadOrder;
        this.configModel = configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 计算总数、起始位移
        this.threadTotalNums = threadCustNums * configModel.getCustTransNumEveryday() * configModel.getDays();
        this.startOffset = this.threadCustNums * configModel.getPerCustCardNums() * threadOrder + 1;

        // 目标数据公共信息
        this.targetType = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTargetType();
        this.fileLocation = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getFileLocation();

        this.isFirstLine = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isFileMerge() ?
                false : true;

        // 优先以startDay和endDay计算要循环的天数
        if (!StrUtil.isEmpty(configModel.getStartDay()) && !StrUtil.isEmpty(configModel.getEndDay())) {
            try {
                start = dateFormat.parse(configModel.getStartDay());
                end = dateFormat.parse(configModel.getEndDay());

                // 日期校验
                if (start.compareTo(end) > 0) {
                    log.error("起始日期不能大于终止日期");
                    throw new RuntimeException("起始日期不能大于终止日期");
                }

                // 计算时间差
                dayBetween = DateUtil.between(start, end, DateUnit.DAY) + 1;
            } catch (ParseException e) {
                log.error("子线程[{}]解析起始、终止时间异常", subThreadId);
                e.printStackTrace();
                throw new RuntimeException("解析起始、终止时间异常");
            }
        } else if (configModel.getDays() > 0) {
            dayBetween = configModel.getDays();
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

        log.info("Sub thread[{}] start, threadTotalNums is {}, custNums is {}, duration is {} days, batchNums is {}, startOffset is {}.",
                this.subThreadId, threadTotalNums, threadCustNums, dayBetween, batchNums, startOffset);

        // 计算公共变量
        String fileName = null;
        if (targetType == 2) {
            fileName = "cdm_trans_detail_" + this.subThreadId + ".csv";
            this.fileLocation = this.fileLocation.equalsIgnoreCase("default") ?
                    new File(".").getAbsolutePath() : this.fileLocation;
            this.fileLocation = this.fileLocation + File.separator + fileName;
            log.info("Sub thread[{}] write data to file[{}].", this.subThreadId, this.fileLocation);

            // 将文件名写入主线程
            SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTmpFileNameList().add(this.fileLocation);
        }

        int custIdFillLength;
        String custIdPrefix = null;
        switch (configModel.getCustType()) {
            case "Personal":
                custIdFillLength = configModel.getCustIdLength() - configModel.getPersonalCustIdPrefix().length();
                custIdPrefix = configModel.getPersonalCustIdPrefix();
                break;
            case "Company":
                custIdFillLength = configModel.getCustIdLength() - configModel.getCompanyCustIdPrefix().length();
                custIdPrefix = configModel.getCompanyCustIdPrefix();
                break;
            default:
                custIdFillLength = 0;
                custIdPrefix = "";
                break;
        }
        int cardNoFillLength = configModel.getCardLength() - configModel.getCardBin().length();
        String cardNoPrefix = configModel.getCardBin();

        // 登记任务开始日志
        recordTask(ConfigConstant.TaskStatusEnum.START);

        // 制造数据
        List<CdmTransDetail> cdmTransDetails = new ArrayList<>();
        CdmTransDetail cdmTransDetail = null;
        int currentPercentage = 0, prevPercentage = 0;
        try {
            for (int i = 0; i < this.threadCustNums; i++) {
                // 按单客户每次交易笔数制造数据
                buildMultiCdmTransDetail(cdmTransDetails, cdmTransDetail, custIdPrefix, cardNoPrefix, i,
                        custIdFillLength, cardNoFillLength);

                // 数据写入操作
                if (processedNums.get() < threadTotalNums) {
                    writeData(cdmTransDetails, false);
                } else {
                    writeData(cdmTransDetails, true);
                }

                // 登记任务处理中日志
                prevPercentage = currentPercentage;
                currentPercentage = DataBuildUtil.getPercentge(processedNums.get(), threadTotalNums);
                if (DataBuildUtil.getPercentageWtihTens(processedNums.get(), threadTotalNums) &&
                        (prevPercentage < currentPercentage)) {
                    recordTask(ConfigConstant.TaskStatusEnum.PROCESSING);
                    log.info("Sub thread {} has been processing {}%, processedNums is {}, totalNums is {}, execution time is {}.",
                            this.subThreadId, currentPercentage, processedNums.get(), threadTotalNums,
                            SystemClock.now() - startTime);
                }
            }

            // 补提交剩余部分
            writeData(cdmTransDetails, true);
        } catch (Exception e) {
            log.error("Sub thread {} error, and execution time is {}ms.",
                    this.subThreadId, System.currentTimeMillis() - startTime);
            e.printStackTrace();

            // 登记任务失败信息
            recordTask(ConfigConstant.TaskStatusEnum.ERROR);
        } finally {
            log.info("Sub thread {} finish, and execution time is {}ms.",
                    this.subThreadId, System.currentTimeMillis() - startTime);

            // 登记任务结束信息
            recordTask(ConfigConstant.TaskStatusEnum.SUCCESS);

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
        return "Xxxx项目数据准备";
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
     * 构建CdmTransDetail对象
     *
     * @param cdmTransDetail
     */
    private void buildCdmTransDetail(CdmTransDetail cdmTransDetail, String custId, String[] cardNoArray,
                                     String custName, long startLongTime, long endLongTime) {
        cdmTransDetail.setTranId(IdUtil.getSnowflakeNextIdStr());
        cdmTransDetail.setCustId(custId);
        cdmTransDetail.setCardNo(cardNoArray[DataBuildUtil.getIntegerRandom(configModel.getPerCustCardNums())]);

        // 生成按日期的随机时间
        cdmTransDetail.setDateTime(new Timestamp(DateTimeUtil.randomLongTime(startLongTime, endLongTime)));

        cdmTransDetail.setTranType(DataBuildUtil.getTranType());
        cdmTransDetail.setTranCat(DataBuildUtil.getTranCat(cdmTransDetail.getTranType()));
        cdmTransDetail.setTranMsg(DataBuildUtil.getTranMsg(cdmTransDetail.getTranType()));
        cdmTransDetail.setTranAmount(DataBuildUtil.getRandomBigDecimal(
                ConfigConstant.DEFAULT_ACCOUNT_MONEY, ConfigConstant.DEFAULT_ACCOUNT_MONEY_PRECISION));
        cdmTransDetail.setAcctAvaibala(cdmTransDetail.getTranAmount().add(new BigDecimal(100)));
        cdmTransDetail.setActName(custName);
        cdmTransDetail.setRemitType("1");
        cdmTransDetail.setOppActno("11223344556677889900");
        cdmTransDetail.setOppCardno("9988776655443322");
        cdmTransDetail.setOppActname("某某某某对手方");
        cdmTransDetail.setOppBrname("某某某某分行某某支行");
        cdmTransDetail.setOppBrno("11223344556677");
        cdmTransDetail.setTranAmountcny(DataBuildUtil.getRandomBigDecimal(
                ConfigConstant.DEFAULT_ACCOUNT_MONEY, ConfigConstant.DEFAULT_ACCOUNT_MONEY_PRECISION));
        cdmTransDetail.setMerNo("998877665544332");
        cdmTransDetail.setMerName("某某某某商户");
        cdmTransDetail.setRembak("某某某某备用信息");
        cdmTransDetail.setUsageName("某某交易用途");
        cdmTransDetail.setTranPs("某某某某交易备注");
        cdmTransDetail.setSummary("某某某某摘要");
        cdmTransDetail.setSumFlag("1");
        cdmTransDetail.setEditFlag("1");
        cdmTransDetail.setSysCode("01");
    }

    /**
     * 构建多交易记录模型
     * @param cdmTransDetails
     * @param cdmTransDetail
     * @param custIdPrefix
     * @param cardNoPrefix
     * @param currentOffset
     * @param custIdFillLength
     * @param cardNoFillLength
     */
    private void buildMultiCdmTransDetail(List<CdmTransDetail> cdmTransDetails, CdmTransDetail cdmTransDetail,
                                          String custIdPrefix, String cardNoPrefix,
                                          int currentOffset, int custIdFillLength, int cardNoFillLength) {
        // 公共信息
        int offset = currentOffset * configModel.getPerCustCardNums();
        String custId = DataBuildUtil.generateCustId(custIdPrefix, startOffset, offset, custIdFillLength);
        String custName = faker.name().fullName();
        String[] cardNoArray = new String[configModel.getPerCustCardNums()];
        for (int k = 0; k < configModel.getPerCustCardNums(); k++) {
            cardNoArray[k] = DataBuildUtil.generateCardNo(cardNoPrefix, startOffset, offset + k,
                    cardNoFillLength);
        }

        // 外层按照日期循环
        long startLongTime, endLongTime;

        for (int i = 0; i < new Long(dayBetween).intValue(); i++) {
            // 生成日期前缀
            startLongTime = DateUtil.offsetDay(start, i).getTime();
            endLongTime = DateUtil.offsetDay(start, i + 1).getTime() - 1;

            // 内层按照每日交易数循环
            for (int j = 0; j < configModel.getCustTransNumEveryday(); j++) {
                cdmTransDetail = new CdmTransDetail();
                buildCdmTransDetail(cdmTransDetail, custId, cardNoArray, custName, startLongTime, endLongTime);

                cdmTransDetails.add(cdmTransDetail);

                // 递增总生成数量和待提交数量
                processedNums.incrementAndGet();
                committedRows++;
            }
        }
    }

    /**
     * 写入数据到目标位置
     * @param cdmTransDetails
     * @param flag
     */
    private void writeData(List<CdmTransDetail> cdmTransDetails, boolean flag) {
        if (cdmTransDetails.size() > 0) {
            if (targetType == 1) {
                //写入数据库
                if (committedRows > batchNums) {
                    cdmTransDetailService.batchInsert(cdmTransDetails);
                    cdmTransDetails.clear();
                    committedRows = 0;
                } else if (flag) {
                    cdmTransDetailService.batchInsert(cdmTransDetails);
                    cdmTransDetails.clear();
                    committedRows = 0;
                }
            } else if (targetType == 2) {
                //写入csv文件
                cdmTransDetailService.writeCsv(this.fileLocation, cdmTransDetails, this.isFirstLine);
                cdmTransDetails.clear();
                this.isFirstLine = false;

                //末尾追加空行
                if (flag) {
                    cdmTransDetailService.writeCsv();
                }
            }
        }
    }
}
