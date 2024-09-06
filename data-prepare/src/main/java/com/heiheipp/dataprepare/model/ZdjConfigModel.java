package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxi
 * @version 1.0
 * @className ZdjTableConfigModel
 * @desc TODO
 * @date 2022/9/11 17:05
 */
@Data
@Component("zdjConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.zdj")
public class ZdjConfigModel extends AbstractConfigModel {

    /**
     * 客户数量
     */
    private int custNum;

    /**
     * 客户每日活跃账户数量
     */
    private int perCustWithCardNums;

    /**
     * 每个账户每日交易笔数
     */
    private int perCardWithTransNums;

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
     * 客户号长度
     */
    private int custIdLength;

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
        return "指导价模型";
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
}
