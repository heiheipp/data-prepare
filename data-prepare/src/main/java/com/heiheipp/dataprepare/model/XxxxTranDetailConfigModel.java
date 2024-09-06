package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxConfigModel
 * @desc TODO
 * @date 2022/3/1 15:45
 */
@Data
@Component("xxxxTranDetailConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.trandetail")
public class XxxxTranDetailConfigModel extends AbstractConfigModel {

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
     * 对公客户号前缀
     */
    private String companyCustIdPrefix;

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
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "模拟场景交易基础信息表";
    }

    /**
     * 总量为用户数
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
        return "cdm_trans_detail_header.csv";
    }

    /**
     * 获取表文件名
     * @return
     */
    @Override
    public String getTargetFileName() {
        return "cdm_trans_detail.csv";
    }
}
