package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxi
 * @version 1.0
 * @className RegulatoryModelConfigModel
 * @desc TODO
 * @date 2022/9/11 17:05
 */
@Data
@Component("regulatoryModelConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.regulatory")
public class RegulatoryModelConfigModel extends AbstractConfigModel {

    /**
     * 待处理表掩码
     */
    private String tableType;

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
     * 省市代码
     */
    private String provinceCode;

    /**
     * 序列号起始偏移量
     */
    private int serialNoOffset;

    /**
     * 客户类型
     */
    private String custType;

    /**
     * 客户号前缀
     */
    private String custIdPrefix;

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
     * 网点编号长度
     */
    private int branchNoLength;

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "监管模型";
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
        switch (tableType) {
            case "1":
                return "TEST_HTAP2_INSERT_001_header.csv";
            case "2":
                return "TEST_HTAP2_SORDATA_001_header.csv";
            case "3":
                return "TEST_HTAP2_SORDATA_002_header.csv";
            default:
                return "";
        }
    }

    /**
     * 获取表文件名
     * @return
     */
    @Override
    public String getTargetFileName() {
        switch (tableType) {
            case "1":
                return "TEST_HTAP2_INSERT_001.csv";
            case "2":
                return "TEST_HTAP2_SORDATA_001.csv";
            case "3":
                return "TEST_HTAP2_SORDATA_002.csv";
            default:
                return "";
        }
    }
}
