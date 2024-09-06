package com.heiheipp.dataprepare.executor;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.javafaker.Faker;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.executor.AbstractFutureTask;
import com.heiheipp.common.mbg.model.*;
import com.heiheipp.common.service.MergeModelService;
import com.heiheipp.common.service.TaskLogService;
import com.heiheipp.common.service.TestTable1Service;
import com.heiheipp.common.util.DataBuildUtil;
import com.heiheipp.common.util.DateTimeUtil;
import com.heiheipp.dataprepare.model.MergeModelConfigModel;
import com.heiheipp.dataprepare.model.XxxxTransMainTableConfigModel;
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

import static java.util.Calendar.LONG;

/**
 * @author zhangxi
 * @version 1.0
 * @className MergeModelDataFutureTask
 * @desc TODO
 * @date 2022/3/16 16:58
 */
@Slf4j
public class MergeModelDataFutureTask extends AbstractFutureTask<String> {

    private int targetType;

    /**
     * 文件位置
     */
    private String baseFileLocation;

    /**
     * target file location
     */
    private String[] targetFileLocations = new String[9];

    /**
     * 首行标志
     */
    private boolean isFirstLine;

    private int threadCustNums;

    private long threadTotalNums;

    private int batchNums;

    private TaskLogService taskLogService;

    private MergeModelService mergeModelService;

    private Map<String, Object> runtimeDatas = new ConcurrentHashMap<>();

    private String subThreadId;

    private AtomicLong processedNums = new AtomicLong(0L);

    private int committedRows = 0;

    private int startOffset, startOffset1;

    private MergeModelConfigModel configModel;

    private Faker faker = new Faker(Locale.CHINA);

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Date start;

    private Date end;

    private long dayBetween = 1L;

    private boolean isRegisterDBLog;

    /**
     * adsTxnTifBas50c
     */
    private AdsTxnTifBas50c adsTxnTifBas50c;

    /**
     * adsTxnTifBas100c
     */
    private AdsTxnTifBas100c adsTxnTifBas100c;

    /**
     * adsTxnTifBas200c
     */
    private AdsTxnTifBas200c adsTxnTifBas200c;

    /**
     * adsTxnTifBas300c
     */
    private AdsTxnTifBas300c adsTxnTifBas300c;

    /**
     * adsTxnTifBas400c
     */
    private AdsTxnTifBas400c adsTxnTifBas400c;

    /**
     * adsTxnTifBasFollow
     */
    private AdsTxnTifBasFollow adsTxnTifBasFollow;

    /**
     * adsTxnTifBas
     */
    private AdsTxnTifBas adsTxnTifBas;

    /**
     * adsTxnTifBasDetail
     */
    private AdsTxnTifBasDetail adsTxnTifBasDetail;

    /**
     * adsTxnTifDetail
     */
    private AdsTxnTifDetail adsTxnTifDetail;

    /**
     * 构造函数
     *
     * @param parentThreadId
     * @param threadCustNums
     * @param threadOrder
     * @param configModel
     */
    public MergeModelDataFutureTask(long parentThreadId, int threadCustNums, int threadOrder,
                                    MergeModelConfigModel configModel) {
        this.parentThreadId = String.valueOf(parentThreadId);
        this.threadCustNums = threadCustNums;
        this.taskLogService = SpringContextUtil.getBean(TaskLogService.class);
        this.mergeModelService = SpringContextUtil.getBean(MergeModelService.class);
        this.configModel = configModel;
        this.batchNums = this.configModel.getBatchNum();

        // 参数计算
        this.startOffset = this.threadCustNums * configModel.getPerCustCardNums() * threadOrder + 1;
        this.startOffset1 = this.threadCustNums * configModel.getCustTransNumEveryday() * threadOrder + 1;
        this.targetType = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getTargetType();
        this.isRegisterDBLog = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).isRegisterDBLog();

        // 文件信息
        this.baseFileLocation = SpringContextUtil.getBean(XxxxDataPrepareServiceImpl.class).getFileLocation();
        this.isFirstLine = false;

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
        this.threadTotalNums = (threadCustNums * configModel.getCustTransNumEveryday()) * this.dayBetween;
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
        String[] fileName = new String[9];
        if (this.targetType == 2) {
            fileName[0] = "ads_txn_tif_bas_50c" + ".0" + this.subThreadId + ".csv";
            fileName[1] = "ads_txn_tif_bas_100c" + ".0" + this.subThreadId + ".csv";
            fileName[2] = "ads_txn_tif_bas_200c" + ".0" + this.subThreadId + ".csv";
            fileName[3] = "ads_txn_tif_bas_300c" + ".0" + this.subThreadId + ".csv";
            fileName[4] = "ads_txn_tif_bas_400c" + ".0" + this.subThreadId + ".csv";
            fileName[5] = "ads_txn_tif_bas_follow" + ".0" + this.subThreadId + ".csv";
            fileName[6] = "ads_txn_tif_bas" + ".0" + this.subThreadId + ".csv";
            fileName[7] = "ads_txn_tif_bas_detail" + ".0" + this.subThreadId + ".csv";
            fileName[8] = "ads_txn_tif_detail" + ".0" + this.subThreadId + ".csv";

            for (int i = 0; i < this.targetFileLocations.length; i++) {
                this.targetFileLocations[i] = this.baseFileLocation.equalsIgnoreCase("default") ?
                        new File(".").getAbsolutePath() : this.baseFileLocation;

                if (fileName[i].contains("ads_txn_tif_bas_50c")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas50c() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_100c")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas100c() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_200c")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas200c() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_300c")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas300c() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_400c")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas400c() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_follow")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBasFollow() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas.0")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBas() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_bas_detail")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifBasDetail() +
                            File.separator + fileName[i];
                } else if (fileName[i].contains("ads_txn_tif_detail")) {
                    this.targetFileLocations[i] += File.separator + configModel.getFileSuffixWithAdsTxnTifDetail() +
                            File.separator + fileName[i];
                }
            }
            log.info("Sub thread[{}] write data to file[{}] / [{}] / [{}] / [{}] / [{}] / [{}] / [{}] / [{}] / [{}].",
                    this.subThreadId,
                    this.targetFileLocations[0], this.targetFileLocations[1], this.targetFileLocations[2],
                    this.targetFileLocations[3], this.targetFileLocations[4], this.targetFileLocations[5],
                    this.targetFileLocations[6], this.targetFileLocations[7], this.targetFileLocations[8]);
        }

        // 处理业务公共信息
        int custIdFillLength;
        String custIdPrefix = null;
        switch (this.configModel.getCustType()) {
            case "Personal":
                custIdFillLength = this.configModel.getCustIdLength() -
                        this.configModel.getPersonalCustIdPrefix().length() -
                        (this.configModel.isCustWithProvinceFlag() ? DataModelConstant.PROVINCE_LENGTH : 0);
                custIdPrefix = this.configModel.getPersonalCustIdPrefix();
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
        List<BaseModel> adsTxnTifBas50c = new ArrayList<>();
        List<BaseModel> adsTxnTifBas100c = new ArrayList<>();
        List<BaseModel> adsTxnTifBas200c = new ArrayList<>();
        List<BaseModel> adsTxnTifBas300c = new ArrayList<>();
        List<BaseModel> adsTxnTifBas400c = new ArrayList<>();
        List<BaseModel> adsTxnTifBasFollow = new ArrayList<>();
        List<BaseModel> adsTxnTifBas = new ArrayList<>();
        List<BaseModel> adsTxnTifBasDetail = new ArrayList<>();
        List<BaseModel> adsTxnTifDetail = new ArrayList<>();
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY, adsTxnTifBas50c);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY, adsTxnTifBas100c);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY, adsTxnTifBas200c);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY, adsTxnTifBas300c);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY, adsTxnTifBas400c);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY, adsTxnTifBasFollow);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_KEY, adsTxnTifBas);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY, adsTxnTifBasDetail);
        batchListDatas.put(DataModelConstant.ADS_TXN_TIF_DETAIL_KEY, adsTxnTifDetail);

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
     * 构建多表对象
     *
     * @param batchDatas
     * @param custId
     * @param cardNoArray
     * @param accountArray
     * @param custName
     * @param startLongTime
     * @param endLongTime
     * @param dateStr
     * @param tranSeqStr
     */
    private void buildMergeModel(Map<String, BaseModel> batchDatas, String custId,
                                 String[] cardNoArray, String[] accountArray, String custName,
                                 long startLongTime, long endLongTime, String dateStr, String tranSeqStr) {
        // 初始化对象
        adsTxnTifBas50c = new AdsTxnTifBas50c();
        adsTxnTifBas100c = new AdsTxnTifBas100c();
        adsTxnTifBas200c = new AdsTxnTifBas200c();
        adsTxnTifBas300c = new AdsTxnTifBas300c();
        adsTxnTifBas400c = new AdsTxnTifBas400c();
        adsTxnTifBasFollow = new AdsTxnTifBasFollow();
        adsTxnTifBas = new AdsTxnTifBas();
        adsTxnTifBasDetail = new AdsTxnTifBasDetail();
        adsTxnTifDetail = new AdsTxnTifDetail();

        // 公共信息
        int num = DataBuildUtil.getIntegerRandom(this.configModel.getPerCustCardNums());
        long timeLong = new Timestamp(
                DateTimeUtil.randomLongTime(startLongTime, endLongTime)).getTime();
        String unnTrnoStr = IdUtil.getSnowflakeNextIdStr();
        BigDecimal unnTrno = new BigDecimal(unnTrnoStr);
        long unnTrnoLong = Long.valueOf(unnTrnoStr);
        String dateStr1 = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6);
        Date txnDt = DateUtil.parse(dateStr1, "yyyy-MM-dd");
        String dbtcrIdr = DataBuildUtil.getDebitOrCreditFlag();
        String rvrsIdr = DataBuildUtil.getChongzhengFlag(dbtcrIdr);
        String txnChnl = DataBuildUtil.getChannel();
        String ctrywClrgOrgCode = DataBuildUtil.getRandomWithLength(5);
        String txnAccnoVlmno = DataBuildUtil.getRandomWithLength(3);
        String txnAccnoSn = DataBuildUtil.getRandomWithLength(4);
        String txnSn = DataBuildUtil.getRandomWithLength(12);
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String pdLgcls = DataBuildUtil.getRandomWithLength(4);
        String pdAs = DataBuildUtil.getRandomWithLength(4);
        String pdDes = DataBuildUtil.generateMsg("某", 12, "产品");
        String docCtrNo = DataBuildUtil.generateMsg("某", 16, "文书合同");
        String accAtr = "1";
        String cusTp = "01";
        String accTp = "001";
        String trnsc = "01";
        String txnAtr = "1";
        String ctiqTrty = DataBuildUtil.getTranType();
        String trtyDes = DataBuildUtil.getTranTypeDesc(ctiqTrty);
        Time txnTime = new Time(0);
        txnTime.setTime(timeLong);
        String txnTlrRefno = DataBuildUtil.getRandomWithLength(7);
        Date valueDt = DateUtil.parse("2021-05-01", "yyyy-MM-dd");
        String bchpoSpclIdr = "0";
        String atomtCd = "112233";
        String promp = "01";
        String prompDes = "提示码";
        BigDecimal txnExrt = NumberUtil.round("1.00", 2);
        BigDecimal txnAmt = DataBuildUtil.getRandomBigDecimal(10000, 2);
        BigDecimal txnAfBal = txnAmt.add(new BigDecimal(100));
        String nomSn = DataBuildUtil.getRandomWithLength(2);

        // 根据掩码处理不同的表
        // ADS_TXN_TIF_BAS_50c
        if (configModel.getTableMask().substring(0, 1).equals("1")) {
            adsTxnTifBas50c.setUnnTrno(unnTrno);
            //adsTxnTifBas50c.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas50c.setTxnDt(txnDt);
            adsTxnTifBas50c.setBocgpInstNo("003");
            adsTxnTifBas50c.setTxnAccno(accountArray[num]);
            adsTxnTifBas50c.setRcrdNo(tranSeqStr);
            adsTxnTifBas50c.setMsacAccno(accountArray[num]);
            adsTxnTifBas50c.setCdno(cardNoArray[num]);
            adsTxnTifBas50c.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas50c.setTxnCurrEngCd("CNY");
            adsTxnTifBas50c.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas50c.setRvrsIdr(rvrsIdr);
            adsTxnTifBas50c.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas50c.setTxnChnl(txnChnl);
            adsTxnTifBas50c.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas50c.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas50c.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas50c.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas50c.setTxnSn(txnSn);
            adsTxnTifBas50c.setUuid(uuid);
            adsTxnTifBas50c.setPdLgcls(pdLgcls);
            adsTxnTifBas50c.setPdAs(pdAs);
            adsTxnTifBas50c.setPdDes(pdDes);
            adsTxnTifBas50c.setDocCtrNo(docCtrNo);
            adsTxnTifBas50c.setAccAtr(accAtr);
            adsTxnTifBas50c.setCusTp(cusTp);
            adsTxnTifBas50c.setAccTp(accTp);
            adsTxnTifBas50c.setTrnsc(trnsc);
            adsTxnTifBas50c.setTxnAtr(txnAtr);
            adsTxnTifBas50c.setCtiqTrty(ctiqTrty);
            adsTxnTifBas50c.setTrtyDes(trtyDes);
            adsTxnTifBas50c.setTxnTime(txnTime);
            adsTxnTifBas50c.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas50c.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas50c.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas50c.setValueDt(valueDt);
            adsTxnTifBas50c.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas50c.setAtomtCd(atomtCd);
            adsTxnTifBas50c.setOrgntCd(atomtCd);
            adsTxnTifBas50c.setPromp(promp);
            adsTxnTifBas50c.setPrompDes(prompDes);
            adsTxnTifBas50c.setOriTxnDt(txnDt);
            adsTxnTifBas50c.setOrgntSn(txnSn);
            adsTxnTifBas50c.setTxnCurrTp("156");
            adsTxnTifBas50c.setTxnExrt(txnExrt);
            adsTxnTifBas50c.setTxnAmt(txnAmt);
            adsTxnTifBas50c.setTxnAfBal(txnAfBal);
            adsTxnTifBas50c.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas50c.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas50c.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas50c.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
        }

        // ADS_TXN_TIF_BAS_100c
        if (configModel.getTableMask().substring(1, 2).equals("1")) {
            adsTxnTifBas100c.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas100c.setTxnDt(txnDt);
            adsTxnTifBas100c.setBocgpInstNo("003");
            adsTxnTifBas100c.setTxnAccno(accountArray[num]);
            adsTxnTifBas100c.setRcrdNo(tranSeqStr);
            adsTxnTifBas100c.setMsacAccno(accountArray[num]);
            adsTxnTifBas100c.setCdno(cardNoArray[num]);
            adsTxnTifBas100c.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas100c.setTxnCurrEngCd("CNY");
            adsTxnTifBas100c.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas100c.setRvrsIdr(rvrsIdr);
            adsTxnTifBas100c.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas100c.setTxnChnl(txnChnl);
            adsTxnTifBas100c.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas100c.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas100c.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas100c.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas100c.setTxnSn(txnSn);
            adsTxnTifBas100c.setUuid(uuid);
            adsTxnTifBas100c.setPdLgcls(pdLgcls);
            adsTxnTifBas100c.setPdAs(pdAs);
            adsTxnTifBas100c.setPdDes(pdDes);
            adsTxnTifBas100c.setDocCtrNo(docCtrNo);
            adsTxnTifBas100c.setAccAtr(accAtr);
            adsTxnTifBas100c.setCusTp(cusTp);
            adsTxnTifBas100c.setAccTp(accTp);
            adsTxnTifBas100c.setTrnsc(trnsc);
            adsTxnTifBas100c.setTxnAtr(txnAtr);
            adsTxnTifBas100c.setCtiqTrty(ctiqTrty);
            adsTxnTifBas100c.setTrtyDes(trtyDes);
            adsTxnTifBas100c.setTxnTime(txnTime);
            adsTxnTifBas100c.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas100c.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas100c.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas100c.setValueDt(valueDt);
            adsTxnTifBas100c.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas100c.setAtomtCd(atomtCd);
            adsTxnTifBas100c.setOrgntCd(atomtCd);
            adsTxnTifBas100c.setPromp(promp);
            adsTxnTifBas100c.setPrompDes(prompDes);
            adsTxnTifBas100c.setOriTxnDt(txnDt);
            adsTxnTifBas100c.setOrgntSn(txnSn);
            adsTxnTifBas100c.setTxnCurrTp("156");
            adsTxnTifBas100c.setTxnExrt(txnExrt);
            adsTxnTifBas100c.setTxnAmt(txnAmt);
            adsTxnTifBas100c.setTxnAfBal(txnAfBal);
            adsTxnTifBas100c.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas100c.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas100c.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBas100c.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBas100c.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBas100c.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBas100c.setCshexChar("1");
            adsTxnTifBas100c.setCshexCharDes("现钞");
            adsTxnTifBas100c.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBas100c.setNomprCrdno("1234567890123456789");
            adsTxnTifBas100c.setNomprAccno("1234567890123456789");
            adsTxnTifBas100c.setNomprSbaccTypeNo("1111");
            adsTxnTifBas100c.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBas100c.setNomprSn(nomSn);
            adsTxnTifBas100c.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas100c.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas100c.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBas100c.getDbtcrIdr())) {
                adsTxnTifBas100c.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBas100c.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBas100c.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBas100c.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBas100c.setAcprSbaccTypeNo("1111");
            adsTxnTifBas100c.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas100c.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas100c.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas100c.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas100c.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBas100c.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBas100c.setNompeCrdno("9876543210987654321");
            adsTxnTifBas100c.setNompeAccno("12345678901234567890001");
            adsTxnTifBas100c.setNompeSbaccTypeNo("1111");
            adsTxnTifBas100c.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas100c.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas100c.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas100c.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas100c.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBas100c.getDbtcrIdr())) {
                adsTxnTifBas100c.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBas100c.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBas100c.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBas100c.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBas100c.setAcpeSbaccTypeNo("1111");
            adsTxnTifBas100c.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas100c.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas100c.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas100c.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
        }

        // ADS_TXN_TIF_BAS_200c
        if (configModel.getTableMask().substring(2, 3).equals("1")) {
            adsTxnTifBas200c.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas200c.setTxnDt(txnDt);
            adsTxnTifBas200c.setBocgpInstNo("003");
            adsTxnTifBas200c.setTxnAccno(accountArray[num]);
            adsTxnTifBas200c.setRcrdNo(tranSeqStr);
            adsTxnTifBas200c.setMsacAccno(accountArray[num]);
            adsTxnTifBas200c.setCdno(cardNoArray[num]);
            adsTxnTifBas200c.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas200c.setTxnCurrEngCd("CNY");
            adsTxnTifBas200c.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas200c.setRvrsIdr(rvrsIdr);
            adsTxnTifBas200c.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas200c.setTxnChnl(txnChnl);
            adsTxnTifBas200c.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas200c.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas200c.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas200c.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas200c.setTxnSn(txnSn);
            adsTxnTifBas200c.setUuid(uuid);
            adsTxnTifBas200c.setPdLgcls(pdLgcls);
            adsTxnTifBas200c.setPdAs(pdAs);
            adsTxnTifBas200c.setPdDes(pdDes);
            adsTxnTifBas200c.setDocCtrNo(docCtrNo);
            adsTxnTifBas200c.setAccAtr(accAtr);
            adsTxnTifBas200c.setCusTp(cusTp);
            adsTxnTifBas200c.setAccTp(accTp);
            adsTxnTifBas200c.setTrnsc(trnsc);
            adsTxnTifBas200c.setTxnAtr(txnAtr);
            adsTxnTifBas200c.setCtiqTrty(ctiqTrty);
            adsTxnTifBas200c.setTrtyDes(trtyDes);
            adsTxnTifBas200c.setTxnTime(txnTime);
            adsTxnTifBas200c.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas200c.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas200c.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas200c.setValueDt(valueDt);
            adsTxnTifBas200c.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas200c.setAtomtCd(atomtCd);
            adsTxnTifBas200c.setOrgntCd(atomtCd);
            adsTxnTifBas200c.setPromp(promp);
            adsTxnTifBas200c.setPrompDes(prompDes);
            adsTxnTifBas200c.setOriTxnDt(txnDt);
            adsTxnTifBas200c.setOrgntSn(txnSn);
            adsTxnTifBas200c.setTxnCurrTp("156");
            adsTxnTifBas200c.setTxnExrt(txnExrt);
            adsTxnTifBas200c.setTxnAmt(txnAmt);
            adsTxnTifBas200c.setTxnAfBal(txnAfBal);
            adsTxnTifBas200c.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas200c.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas200c.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBas200c.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBas200c.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBas200c.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBas200c.setCshexChar("1");
            adsTxnTifBas200c.setCshexCharDes("现钞");
            adsTxnTifBas200c.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBas200c.setNomprCrdno("1234567890123456789");
            adsTxnTifBas200c.setNomprAccno("1234567890123456789");
            adsTxnTifBas200c.setNomprSbaccTypeNo("1111");
            adsTxnTifBas200c.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBas200c.setNomprSn(nomSn);
            adsTxnTifBas200c.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas200c.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas200c.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBas200c.getDbtcrIdr())) {
                adsTxnTifBas200c.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBas200c.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBas200c.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBas200c.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBas200c.setAcprSbaccTypeNo("1111");
            adsTxnTifBas200c.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas200c.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas200c.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas200c.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas200c.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBas200c.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBas200c.setNompeCrdno("9876543210987654321");
            adsTxnTifBas200c.setNompeAccno("12345678901234567890001");
            adsTxnTifBas200c.setNompeSbaccTypeNo("1111");
            adsTxnTifBas200c.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas200c.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas200c.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas200c.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas200c.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBas200c.getDbtcrIdr())) {
                adsTxnTifBas200c.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBas200c.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBas200c.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBas200c.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBas200c.setAcpeSbaccTypeNo("1111");
            adsTxnTifBas200c.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas200c.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas200c.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas200c.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了101-129、131-147、149-156、159-200列
            adsTxnTifBas200c.setTxnSeqNo(tranSeqStr);
            adsTxnTifBas200c.setCusno(custId);
            adsTxnTifBas200c.setCorpPrvtIdr("P");
            adsTxnTifBas200c.setTrankey(Long.valueOf(tranSeqStr));
        }

        // ADS_TXN_TIF_BAS_300c
        if (configModel.getTableMask().substring(3, 4).equals("1")) {
            adsTxnTifBas300c.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas300c.setTxnDt(txnDt);
            adsTxnTifBas300c.setBocgpInstNo("003");
            adsTxnTifBas300c.setTxnAccno(accountArray[num]);
            adsTxnTifBas300c.setRcrdNo(tranSeqStr);
            adsTxnTifBas300c.setMsacAccno(accountArray[num]);
            adsTxnTifBas300c.setCdno(cardNoArray[num]);
            adsTxnTifBas300c.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas300c.setTxnCurrEngCd("CNY");
            adsTxnTifBas300c.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas300c.setRvrsIdr(rvrsIdr);
            adsTxnTifBas300c.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas300c.setTxnChnl(txnChnl);
            adsTxnTifBas300c.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas300c.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas300c.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas300c.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas300c.setTxnSn(txnSn);
            adsTxnTifBas300c.setUuid(uuid);
            adsTxnTifBas300c.setPdLgcls(pdLgcls);
            adsTxnTifBas300c.setPdAs(pdAs);
            adsTxnTifBas300c.setPdDes(pdDes);
            adsTxnTifBas300c.setDocCtrNo(docCtrNo);
            adsTxnTifBas300c.setAccAtr(accAtr);
            adsTxnTifBas300c.setCusTp(cusTp);
            adsTxnTifBas300c.setAccTp(accTp);
            adsTxnTifBas300c.setTrnsc(trnsc);
            adsTxnTifBas300c.setTxnAtr(txnAtr);
            adsTxnTifBas300c.setCtiqTrty(ctiqTrty);
            adsTxnTifBas300c.setTrtyDes(trtyDes);
            adsTxnTifBas300c.setTxnTime(txnTime);
            adsTxnTifBas300c.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas300c.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas300c.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas300c.setValueDt(valueDt);
            adsTxnTifBas300c.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas300c.setAtomtCd(atomtCd);
            adsTxnTifBas300c.setOrgntCd(atomtCd);
            adsTxnTifBas300c.setPromp(promp);
            adsTxnTifBas300c.setPrompDes(prompDes);
            adsTxnTifBas300c.setOriTxnDt(txnDt);
            adsTxnTifBas300c.setOrgntSn(txnSn);
            adsTxnTifBas300c.setTxnCurrTp("156");
            adsTxnTifBas300c.setTxnExrt(txnExrt);
            adsTxnTifBas300c.setTxnAmt(txnAmt);
            adsTxnTifBas300c.setTxnAfBal(txnAfBal);
            adsTxnTifBas300c.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas300c.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas300c.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBas300c.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBas300c.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBas300c.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBas300c.setCshexChar("1");
            adsTxnTifBas300c.setCshexCharDes("现钞");
            adsTxnTifBas300c.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBas300c.setNomprCrdno("1234567890123456789");
            adsTxnTifBas300c.setNomprAccno("1234567890123456789");
            adsTxnTifBas300c.setNomprSbaccTypeNo("1111");
            adsTxnTifBas300c.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBas300c.setNomprSn(nomSn);
            adsTxnTifBas300c.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas300c.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas300c.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBas300c.getDbtcrIdr())) {
                adsTxnTifBas300c.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBas300c.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBas300c.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBas300c.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBas300c.setAcprSbaccTypeNo("1111");
            adsTxnTifBas300c.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas300c.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas300c.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas300c.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas300c.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBas300c.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBas300c.setNompeCrdno("9876543210987654321");
            adsTxnTifBas300c.setNompeAccno("12345678901234567890001");
            adsTxnTifBas300c.setNompeSbaccTypeNo("1111");
            adsTxnTifBas300c.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas300c.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas300c.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas300c.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas300c.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBas300c.getDbtcrIdr())) {
                adsTxnTifBas300c.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBas300c.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBas300c.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBas300c.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBas300c.setAcpeSbaccTypeNo("1111");
            adsTxnTifBas300c.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas300c.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas300c.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas300c.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了101-129、131-147、149-156、159-300列
            adsTxnTifBas300c.setTxnSeqNo(tranSeqStr);
            adsTxnTifBas300c.setCusno(custId);
            adsTxnTifBas300c.setCorpPrvtIdr("P");
            adsTxnTifBas300c.setTrankey(Long.valueOf(tranSeqStr));
        }

        // ADS_TXN_TIF_BAS_400c
        if (configModel.getTableMask().substring(4, 5).equals("1")) {
            adsTxnTifBas400c.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas400c.setTxnDt(txnDt);
            adsTxnTifBas400c.setBocgpInstNo("003");
            adsTxnTifBas400c.setTxnAccno(accountArray[num]);
            adsTxnTifBas400c.setRcrdNo(tranSeqStr);
            adsTxnTifBas400c.setMsacAccno(accountArray[num]);
            adsTxnTifBas400c.setCdno(cardNoArray[num]);
            adsTxnTifBas400c.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas400c.setTxnCurrEngCd("CNY");
            adsTxnTifBas400c.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas400c.setRvrsIdr(rvrsIdr);
            adsTxnTifBas400c.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas400c.setTxnChnl(txnChnl);
            adsTxnTifBas400c.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas400c.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas400c.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas400c.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas400c.setTxnSn(txnSn);
            adsTxnTifBas400c.setUuid(uuid);
            adsTxnTifBas400c.setPdLgcls(pdLgcls);
            adsTxnTifBas400c.setPdAs(pdAs);
            adsTxnTifBas400c.setPdDes(pdDes);
            adsTxnTifBas400c.setDocCtrNo(docCtrNo);
            adsTxnTifBas400c.setAccAtr(accAtr);
            adsTxnTifBas400c.setCusTp(cusTp);
            adsTxnTifBas400c.setAccTp(accTp);
            adsTxnTifBas400c.setTrnsc(trnsc);
            adsTxnTifBas400c.setTxnAtr(txnAtr);
            adsTxnTifBas400c.setCtiqTrty(ctiqTrty);
            adsTxnTifBas400c.setTrtyDes(trtyDes);
            adsTxnTifBas400c.setTxnTime(txnTime);
            adsTxnTifBas400c.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas400c.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas400c.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas400c.setValueDt(valueDt);
            adsTxnTifBas400c.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas400c.setAtomtCd(atomtCd);
            adsTxnTifBas400c.setOrgntCd(atomtCd);
            adsTxnTifBas400c.setPromp(promp);
            adsTxnTifBas400c.setPrompDes(prompDes);
            adsTxnTifBas400c.setOriTxnDt(txnDt);
            adsTxnTifBas400c.setOrgntSn(txnSn);
            adsTxnTifBas400c.setTxnCurrTp("156");
            adsTxnTifBas400c.setTxnExrt(txnExrt);
            adsTxnTifBas400c.setTxnAmt(txnAmt);
            adsTxnTifBas400c.setTxnAfBal(txnAfBal);
            adsTxnTifBas400c.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas400c.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas400c.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBas400c.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBas400c.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBas400c.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBas400c.setCshexChar("1");
            adsTxnTifBas400c.setCshexCharDes("现钞");
            adsTxnTifBas400c.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBas400c.setNomprCrdno("1234567890123456789");
            adsTxnTifBas400c.setNomprAccno("1234567890123456789");
            adsTxnTifBas400c.setNomprSbaccTypeNo("1111");
            adsTxnTifBas400c.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBas400c.setNomprSn(nomSn);
            adsTxnTifBas400c.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas400c.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas400c.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBas400c.getDbtcrIdr())) {
                adsTxnTifBas400c.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBas400c.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBas400c.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBas400c.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBas400c.setAcprSbaccTypeNo("1111");
            adsTxnTifBas400c.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas400c.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas400c.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas400c.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas400c.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBas400c.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBas400c.setNompeCrdno("9876543210987654321");
            adsTxnTifBas400c.setNompeAccno("12345678901234567890001");
            adsTxnTifBas400c.setNompeSbaccTypeNo("1111");
            adsTxnTifBas400c.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas400c.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas400c.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas400c.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas400c.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBas400c.getDbtcrIdr())) {
                adsTxnTifBas400c.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBas400c.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBas400c.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBas400c.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBas400c.setAcpeSbaccTypeNo("1111");
            adsTxnTifBas400c.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas400c.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas400c.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas400c.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了101-129、131-147、149-156、159-400列
            adsTxnTifBas400c.setTxnSeqNo(tranSeqStr);
            adsTxnTifBas400c.setCusno(custId);
            adsTxnTifBas400c.setCorpPrvtIdr("P");
            adsTxnTifBas400c.setTrankey(Long.valueOf(tranSeqStr));
        }

        // ADS_TXN_TIF_BAS_FOLLOW
        if (configModel.getTableMask().substring(5, 6).equals("1")) {
            adsTxnTifBasFollow.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBasFollow.setTxnDt(txnDt);
            adsTxnTifBasFollow.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasFollow.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBasFollow.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBasFollow.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBasFollow.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBasFollow.setCshexChar("1");
            adsTxnTifBasFollow.setCshexCharDes("现钞");
            adsTxnTifBasFollow.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBasFollow.setNomprCrdno("1234567890123456789");
            adsTxnTifBasFollow.setNomprAccno("1234567890123456789");
            adsTxnTifBasFollow.setNomprSbaccTypeNo("1111");
            adsTxnTifBasFollow.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBasFollow.setNomprSn(nomSn);
            adsTxnTifBasFollow.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasFollow.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasFollow.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBasFollow.setAcpayrAcnm("");
            adsTxnTifBasFollow.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBasFollow.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBasFollow.setAcprSbaccTypeNo("1111");
            adsTxnTifBasFollow.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasFollow.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasFollow.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasFollow.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasFollow.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBasFollow.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBasFollow.setNompeCrdno("9876543210987654321");
            adsTxnTifBasFollow.setNompeAccno("12345678901234567890001");
            adsTxnTifBasFollow.setNompeSbaccTypeNo("1111");
            adsTxnTifBasFollow.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasFollow.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasFollow.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasFollow.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasFollow.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBasFollow.setAcpayeAcnm("");
            adsTxnTifBasFollow.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBasFollow.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBasFollow.setAcpeSbaccTypeNo("1111");
            adsTxnTifBasFollow.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasFollow.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasFollow.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasFollow.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了52-80、82-98、100-107、109-150列
            adsTxnTifBasFollow.setTxnSeqNo(tranSeqStr);
            adsTxnTifBasFollow.setCusno(custId);
            adsTxnTifBasFollow.setCorpPrvtIdr("P");
        }

        // ADS_TXN_TIF_BAS
        if (configModel.getTableMask().substring(6, 7).equals("1")) {
            adsTxnTifBas.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBas.setTxnDt(txnDt);
            adsTxnTifBas.setBocgpInstNo("003");
            adsTxnTifBas.setTxnAccno(accountArray[num]);
            adsTxnTifBas.setRcrdNo(tranSeqStr);
            adsTxnTifBas.setMsacAccno(accountArray[num]);
            adsTxnTifBas.setCdno(cardNoArray[num]);
            adsTxnTifBas.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBas.setTxnCurrEngCd("CNY");
            adsTxnTifBas.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBas.setRvrsIdr(rvrsIdr);
            adsTxnTifBas.setOriRcrdNo(tranSeqStr);
            adsTxnTifBas.setTxnChnl(txnChnl);
            adsTxnTifBas.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBas.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBas.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBas.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBas.setTxnSn(txnSn);
            adsTxnTifBas.setUuid(uuid);
            adsTxnTifBas.setPdLgcls(pdLgcls);
            adsTxnTifBas.setPdAs(pdAs);
            adsTxnTifBas.setPdDes(pdDes);
            adsTxnTifBas.setDocCtrNo(docCtrNo);
            adsTxnTifBas.setAccAtr(accAtr);
            adsTxnTifBas.setCusTp(cusTp);
            adsTxnTifBas.setAccTp(accTp);
            adsTxnTifBas.setTrnsc(trnsc);
            adsTxnTifBas.setTxnAtr(txnAtr);
            adsTxnTifBas.setCtiqTrty(ctiqTrty);
            adsTxnTifBas.setTrtyDes(trtyDes);
            adsTxnTifBas.setTxnTime(txnTime);
            adsTxnTifBas.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBas.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas.setValueDt(valueDt);
            adsTxnTifBas.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBas.setAtomtCd(atomtCd);
            adsTxnTifBas.setOrgntCd(atomtCd);
            adsTxnTifBas.setPromp(promp);
            adsTxnTifBas.setPrompDes(prompDes);
            adsTxnTifBas.setOriTxnDt(txnDt);
            adsTxnTifBas.setOrgntSn(txnSn);
            adsTxnTifBas.setTxnCurrTp("156");
            adsTxnTifBas.setTxnExrt(txnExrt);
            adsTxnTifBas.setTxnAmt(txnAmt);
            adsTxnTifBas.setTxnAfBal(txnAfBal);
            adsTxnTifBas.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBas.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBas.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBas.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBas.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBas.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBas.setCshexChar("1");
            adsTxnTifBas.setCshexCharDes("现钞");
            adsTxnTifBas.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBas.setNomprCrdno("1234567890123456789");
            adsTxnTifBas.setNomprAccno("1234567890123456789");
            adsTxnTifBas.setNomprSbaccTypeNo("1111");
            adsTxnTifBas.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBas.setNomprSn(nomSn);
            adsTxnTifBas.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBas.getDbtcrIdr())) {
                adsTxnTifBas.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBas.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBas.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBas.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBas.setAcprSbaccTypeNo("1111");
            adsTxnTifBas.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBas.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBas.setNompeCrdno("9876543210987654321");
            adsTxnTifBas.setNompeAccno("12345678901234567890001");
            adsTxnTifBas.setNompeSbaccTypeNo("1111");
            adsTxnTifBas.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBas.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBas.getDbtcrIdr())) {
                adsTxnTifBas.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBas.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBas.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBas.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBas.setAcpeSbaccTypeNo("1111");
            adsTxnTifBas.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBas.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBas.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBas.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了101-129、131-147、149-156列
            adsTxnTifBas.setTxnSeqNo(tranSeqStr);
            adsTxnTifBas.setCusno(custId);
            adsTxnTifBas.setCorpPrvtIdr("P");
        }

        // ADS_TXN_TIF_BAS_DETAIL
        if (configModel.getTableMask().substring(7, 8).equals("1")) {
            adsTxnTifBasDetail.setUnnTrno(new BigDecimal(tranSeqStr));
            adsTxnTifBasDetail.setTxnDt(txnDt);
            adsTxnTifBasDetail.setBocgpInstNo("003");
            adsTxnTifBasDetail.setTxnAccno(accountArray[num]);
            adsTxnTifBasDetail.setRcrdNo(tranSeqStr);
            adsTxnTifBasDetail.setMsacAccno(accountArray[num]);
            adsTxnTifBasDetail.setCdno(cardNoArray[num]);
            adsTxnTifBasDetail.setTxnAccnoSbaccTypeNo("0001");
            adsTxnTifBasDetail.setTxnCurrEngCd("CNY");
            adsTxnTifBasDetail.setDbtcrIdr(dbtcrIdr);
            adsTxnTifBasDetail.setRvrsIdr(rvrsIdr);
            adsTxnTifBasDetail.setOriRcrdNo(tranSeqStr);
            adsTxnTifBasDetail.setTxnChnl(txnChnl);
            adsTxnTifBasDetail.setCtrywClrgOrgCode(ctrywClrgOrgCode);
            adsTxnTifBasDetail.setAccProno(ctrywClrgOrgCode);
            adsTxnTifBasDetail.setTxnAccnoVlmno(txnAccnoVlmno);
            adsTxnTifBasDetail.setTxnAccnoSn(txnAccnoSn);
            adsTxnTifBasDetail.setTxnSn(txnSn);
            adsTxnTifBasDetail.setUuid(uuid);
            adsTxnTifBasDetail.setPdLgcls(pdLgcls);
            adsTxnTifBasDetail.setPdAs(pdAs);
            adsTxnTifBasDetail.setPdDes(pdDes);
            adsTxnTifBasDetail.setDocCtrNo(docCtrNo);
            adsTxnTifBasDetail.setAccAtr(accAtr);
            adsTxnTifBasDetail.setCusTp(cusTp);
            adsTxnTifBasDetail.setAccTp(accTp);
            adsTxnTifBasDetail.setTrnsc(trnsc);
            adsTxnTifBasDetail.setTxnAtr(txnAtr);
            adsTxnTifBasDetail.setCtiqTrty(ctiqTrty);
            adsTxnTifBasDetail.setTrtyDes(trtyDes);
            adsTxnTifBasDetail.setTxnTime(txnTime);
            adsTxnTifBasDetail.setTxnTlrRefno(txnTlrRefno);
            adsTxnTifBasDetail.setTxnOrgRefno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasDetail.setTxnOrgName(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasDetail.setValueDt(valueDt);
            adsTxnTifBasDetail.setBchpoSpclIdr(bchpoSpclIdr);
            adsTxnTifBasDetail.setAtomtCd(atomtCd);
            adsTxnTifBasDetail.setOrgntCd(atomtCd);
            adsTxnTifBasDetail.setPromp(promp);
            adsTxnTifBasDetail.setPrompDes(prompDes);
            adsTxnTifBasDetail.setOriTxnDt(txnDt);
            adsTxnTifBasDetail.setOrgntSn(txnSn);
            adsTxnTifBasDetail.setTxnCurrTp("156");
            adsTxnTifBasDetail.setTxnExrt(txnExrt);
            adsTxnTifBasDetail.setTxnAmt(txnAmt);
            adsTxnTifBasDetail.setTxnAfBal(txnAfBal);
            adsTxnTifBasDetail.setTxnAfAvlBal(txnAfBal);
            adsTxnTifBasDetail.setFrzAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setAvlOdQot(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setDsctSsoniAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setRglrPnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setOduePnpBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setDrintBal(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setOduePnpAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setOdueIntAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setPnpPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setIntPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setPnintOfPnintAmt(DataModelConstant.ZERO_AMOUNT);
            adsTxnTifBasDetail.setRmrk(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifBasDetail.setPurp(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifBasDetail.setMemo(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifBasDetail.setSumm(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifBasDetail.setCshexChar("1");
            adsTxnTifBasDetail.setCshexCharDes("现钞");
            adsTxnTifBasDetail.setNomprAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            adsTxnTifBasDetail.setNomprCrdno("1234567890123456789");
            adsTxnTifBasDetail.setNomprAccno("1234567890123456789");
            adsTxnTifBasDetail.setNomprSbaccTypeNo("1111");
            adsTxnTifBasDetail.setNomprVlmno(txnAccnoVlmno);
            adsTxnTifBasDetail.setNomprSn(nomSn);
            adsTxnTifBasDetail.setNomprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasDetail.setNomprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasDetail.setNomprAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 付款人姓名
            if ("D".equalsIgnoreCase(adsTxnTifBasDetail.getDbtcrIdr())) {
                adsTxnTifBasDetail.setAcpayrAcnm(custName);
            } else {
                adsTxnTifBasDetail.setAcpayrAcnm(DataModelConstant.DEFAULT_PAYER_NAME);
            }
            adsTxnTifBasDetail.setAcpayrCrdno("1234567890123456789");
            adsTxnTifBasDetail.setAcpayrAccno("12345678901234567890001");
            adsTxnTifBasDetail.setAcprSbaccTypeNo("1111");
            adsTxnTifBasDetail.setAcprVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasDetail.setAcprSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasDetail.setAcprAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasDetail.setAcprAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasDetail.setAcpayrAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            adsTxnTifBasDetail.setNompeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            adsTxnTifBasDetail.setNompeCrdno("9876543210987654321");
            adsTxnTifBasDetail.setNompeAccno("12345678901234567890001");
            adsTxnTifBasDetail.setNompeSbaccTypeNo("1111");
            adsTxnTifBasDetail.setNompeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasDetail.setNompeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasDetail.setNompeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasDetail.setNompeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);
            adsTxnTifBasDetail.setNompeAccCtrywClrgOrgCode(DataModelConstant.DEFAULT_PROVINCE_LH_NO);
            // 收款人姓名
            if ("C".equalsIgnoreCase(adsTxnTifBasDetail.getDbtcrIdr())) {
                adsTxnTifBasDetail.setAcpayeAcnm(custName);
            } else {
                adsTxnTifBasDetail.setAcpayeAcnm(DataModelConstant.DEFAULT_RECEIVER_NAME);
            }
            adsTxnTifBasDetail.setAcpayeCrdno("9876543210987654321");
            adsTxnTifBasDetail.setAcpayeAccno("98765432109876543210001");
            adsTxnTifBasDetail.setAcpeSbaccTypeNo("1111");
            adsTxnTifBasDetail.setAcpeVlmno(DataBuildUtil.getRandomWithLength(3));
            adsTxnTifBasDetail.setAcpeSn(DataBuildUtil.getRandomWithLength(2));
            adsTxnTifBasDetail.setAcpeAccBlngOrgnm(DataModelConstant.DEFAULT_BRANCH_NAME);
            adsTxnTifBasDetail.setAcpeAccBlngOrgno(DataModelConstant.DEFAULT_BRANCH_NO);

            // 省略了101-129、131-147、149-156列
            adsTxnTifBasDetail.setTxnSeqNo(tranSeqStr);
            adsTxnTifBasDetail.setCusno(custId);
            adsTxnTifBasDetail.setCorpPrvtIdr("P");

            adsTxnTifBasDetail.setTranKey(tranSeqStr);
            adsTxnTifBasDetail.setDay(Long.valueOf(dateStr));
        }

        // ADS_TXN_TIF_DETAIL
        if (configModel.getTableMask().substring(8).equals("1")) {
            adsTxnTifDetail.setTranKey(tranSeqStr);
            adsTxnTifDetail.setSrcSys("CTIQ");
            adsTxnTifDetail.setCusNo(custId);
            adsTxnTifDetail.setTranCusmNo(accountArray[num]);
            adsTxnTifDetail.setActNo(accountArray[num]);
            adsTxnTifDetail.setTranDate(dateStr);
            adsTxnTifDetail.setTranTime(txnTime);
            adsTxnTifDetail.setPostDate(dateStr);
            adsTxnTifDetail.setClassifyId(ctiqTrty);
            adsTxnTifDetail.setTranCat(dbtcrIdr);
            adsTxnTifDetail.setTranType(ctiqTrty);
            adsTxnTifDetail.setTranMsg(trtyDes);
            adsTxnTifDetail.setCrDrInd(dbtcrIdr);
            adsTxnTifDetail.setReversalCode(rvrsIdr);
            adsTxnTifDetail.setUuid(uuid);
            adsTxnTifDetail.setCardNo(cardNoArray[num]);
            adsTxnTifDetail.setTranAmount(txnAmt);
            adsTxnTifDetail.setTranCureEn("CNY");
            adsTxnTifDetail.setActAvailBalance(txnAfBal);
            adsTxnTifDetail.setRemitType("1");
            adsTxnTifDetail.setRemitTypeName("现钞");
            adsTxnTifDetail.setRembak(DataBuildUtil.generateMsg("某", 30, "备注"));
            adsTxnTifDetail.setUsageName(DataBuildUtil.generateMsg("某", 15, "用途"));
            adsTxnTifDetail.setTranPs(DataBuildUtil.generateMsg("某", 30, "附言"));
            adsTxnTifDetail.setSummary(DataBuildUtil.generateMsg("某", 30, "摘要"));
            adsTxnTifDetail.setTranJrnlNo(tranSeqStr);
            adsTxnTifDetail.setStatus("1");
            adsTxnTifDetail.setSumFlag("1");
            adsTxnTifDetail.setDay(Long.valueOf(dateStr));
        }

        // 组装返回值
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY, adsTxnTifBas50c);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY, adsTxnTifBas100c);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY, adsTxnTifBas200c);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY, adsTxnTifBas300c);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY, adsTxnTifBas400c);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY, adsTxnTifBasFollow);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_KEY, adsTxnTifBas);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY, adsTxnTifBasDetail);
        batchDatas.put(DataModelConstant.ADS_TXN_TIF_DETAIL_KEY, adsTxnTifDetail);
    }

    /**
     * 构建多表数据
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
                                      String custIdPrefix, String cardNoPrefix, String accountPrefix, int currentOffset,
                                      int custIdFillLength, int cardNoFillLength, int accountFillLength) {
        // 公共信息
        int offset = currentOffset * this.configModel.getPerCustCardNums();
        int offset1 = currentOffset * this.configModel.getCustTransNumEveryday();

        // 客户id
        String custId = DataBuildUtil.generateCustId(custIdPrefix +
                        (configModel.isCustWithProvinceFlag() ?
                                DataBuildUtil.provinceCodeArray[DataBuildUtil.getIntegerRandom(DataBuildUtil.provinceCodeArray.length)] : ""),
                this.startOffset, offset, custIdFillLength);
        String custName = this.faker.name().fullName();

        // 账号/卡号
        String[] cardNoArray = new String[this.configModel.getPerCustCardNums()];
        String[] accountArray = new String[this.configModel.getPerCustCardNums()];
        for (int k = 0; k < this.configModel.getPerCustCardNums(); k++) {
            cardNoArray[k] = DataBuildUtil.generateCardNo(cardNoPrefix, this.startOffset,
                    offset + k, cardNoFillLength);
            accountArray[k] = DataBuildUtil.generateAccount(accountPrefix, this.startOffset,
                    offset + k, accountFillLength);
        }

        // 外层按照日期循环
        for (int i = 0; i < (new Long(this.dayBetween)).intValue(); i++) {
            long startLongTime = DateUtil.offsetDay(this.start, i).getTime();
            long endLongTime = DateUtil.offsetDay(this.start, i + 1).getTime() - 1L;
            String dateStr = DateUtil.offsetDay(this.start, i).toDateStr().replaceAll("-", "");

            // 内层按照每日交易数循环
            for (int j = 0; j < this.configModel.getCustTransNumEveryday(); j++) {
                // 交易流水号
                String tranSeqStr = DataBuildUtil.generateSeqStr("88", this.startOffset1,
                        offset1 + j, this.configModel.getTranSeqLength() - 2);

                buildMergeModel(batchDatas, custId, cardNoArray, accountArray, custName, startLongTime, endLongTime,
                        dateStr, tranSeqStr);

                // 填充结果
                for (Map.Entry<String, BaseModel> entry : batchDatas.entrySet()) {
                    switch (entry.getKey()) {
                        case DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY:
                            if (((AdsTxnTifBas50c) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY:
                            if (((AdsTxnTifBas100c) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY:
                            if (((AdsTxnTifBas200c) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY:
                            if (((AdsTxnTifBas300c) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY:
                            if (((AdsTxnTifBas400c) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY:
                            if (((AdsTxnTifBasFollow) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_KEY:
                            if (((AdsTxnTifBas) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY:
                            if (((AdsTxnTifBasDetail) entry.getValue()).getUnnTrno() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY).add(entry.getValue());
                            }
                            break;
                        case DataModelConstant.ADS_TXN_TIF_DETAIL_KEY:
                            if (((AdsTxnTifDetail) entry.getValue()).getTranKey() != null) {
                                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_DETAIL_KEY).add(entry.getValue());
                            }
                            break;
                        default:
                            break;
                    }
                }

                // 递增总生成数量和待提交数量
                this.processedNums.incrementAndGet();
                this.committedRows++;
            }
        }
    }

    /**
     * 写数据操作
     *
     * @param batchListDatas
     * @param flag
     */
    private void writeData(Map<String, List<BaseModel>> batchListDatas, boolean flag) {
        if (batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY).size() > 0 ||
                batchListDatas.get(DataModelConstant.ADS_TXN_TIF_BAS_KEY).size() > 0) {
            if (this.targetType == 1) {
                //写入数据库
                if (this.committedRows >= this.batchNums) {
                    this.mergeModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                } else if (flag) {
                    this.mergeModelService.batchInsertMultiTables(batchListDatas);
                    clearBatchMap(batchListDatas);
                    this.committedRows = 0;
                }
            } else if (this.targetType == 2) {
                //写入csv文件
                this.mergeModelService.writeCsv(this.targetFileLocations, batchListDatas, this.isFirstLine);
                clearBatchMap(batchListDatas);
                this.isFirstLine = false;

                //末尾追加空行
                if (flag) {
                    this.mergeModelService.writeCsv();
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
