package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxi
 * @version 1.0
 * @className MergeModelConfigModel
 * @desc TODO
 * @date 2022/9/11 17:05
 */
@Data
@Component("mergeModelConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.mergemodel")
public class MergeModelConfigModel extends AbstractConfigModel {

    /**
     * 待处理表掩码
     */
    private String tableMask;

    /**
     * 客户数量
     */
    private int custNum;

    /**
     * 每个客户的卡号数量
     */
    private int perCustCardNums;

    /**
     * 客户每日交易笔数
     */
    private int custTransNumEveryday;

    /**
     * 交易日期跨度
     */
    private int days;

    /**
     * 交易起始日期
     */
    private String startDay;

    /**
     * 交易终止日期
     */
    private String endDay;

    /**
     * 数据库单批次提交数量
     */
    private int batchNum;

    /**
     * 客户类型
     */
    private String custType;

    /**
     * 个人客户号前缀
     */
    private String personalCustIdPrefix;

    /**
     * 客户号附带2位省市代码标志
     */
    private boolean custWithProvinceFlag;

    /**
     * 客户号长度
     */
    private int custIdLength;

    /**
     * 卡号前缀
     */
    private String cardBin;

    /**
     * 卡号长度
     */
    private int cardLength;

    /**
     * 账号前缀
     */
    private String accountPrefix;

    /**
     * 账号长度
     */
    private int accountLength;

    /**
     * 主交易流水长度
     */
    private int tranSeqLength;

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "模型整合";
    }

    /**
     * 获取总处理数量
     * @return
     */
    @Override
    public int getTotalNums() {
        return getCustNum();
    }

    /**
     * 获取表头文件名
     * @return
     */
    @Override
    public String getFileHeaderName() {
        return "";
    }

    /**
     * 获取表文件名
     * @return
     */
    @Override
    public String getTargetFileName() {
        return "";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas50c() {
        return "ads_txn_tif_bas_50c";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas100c() {
        return "ads_txn_tif_bas_100c";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas200c() {
        return "ads_txn_tif_bas_200c";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas300c() {
        return "ads_txn_tif_bas_300c";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas400c() {
        return "ads_txn_tif_bas_400c";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBasFollow() {
        return "ads_txn_tif_bas_follow";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBas() {
        return "ads_txn_tif_bas";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifBasDetail() {
        return "ads_txn_tif_bas_detail";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithAdsTxnTifDetail() {
        return "ads_txn_tif_detail";
    }
}
