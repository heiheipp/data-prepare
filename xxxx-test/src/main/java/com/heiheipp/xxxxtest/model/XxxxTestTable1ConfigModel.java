package com.heiheipp.xxxxtest.model;

import cn.hutool.setting.Setting;
import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxTestTable1ConfigModel
 * @desc TODO
 * @date 2022/3/16 17:05
 */
@Data
public class XxxxTestTable1ConfigModel extends AbstractConfigModel {

    /**
     * operate type
     */
    private String operateType;

    /**
     * 步长
     */
    private int stepSize;

    /**
     * 每个客户的卡号数量
     */
    private int perCustCardNums;

    /**
     * 客户每日交易笔数
     */
    private int custTransNumEveryday;

    /**
     * 交易起始日期
     */
    private String currentDate;

    /**
     * 客户类型
     */
    private String custType;

    /**
     * 客户号前缀
     */
    private String custIdPrefix;

    /**
     * 客户号长度
     */
    private int custIdLength;

    /**
     * 卡号前缀
     */
    private String cardNumberPrefix;

    /**
     * 卡号长度
     */
    private int cardNumberLength;

    /**
     * 账号前缀
     */
    private String accountNumberPrefix;

    /**
     * 账号长度
     */
    private int accountNumberLength;

    /**
     * cds config model
     */
    private XdsDemoConfigModel xdsDemoConfigModel;

    /**
     * isolation
     */
    private String isolation;

    /**
     * 构造函数
     * @param setting
     */
    public XxxxTestTable1ConfigModel(Setting setting) {
        this.operateType = setting.getByGroup("operateType", "test_table_1");
        this.stepSize = setting.getInt("stepSize", "test_table_1");
        this.perCustCardNums = setting.getInt("perCustCardNums", "test_table_1");
        this.custTransNumEveryday = setting.getInt("custTransNumEveryday", "test_table_1");
        this.currentDate = setting.getByGroup("currentDate", "test_table_1");
        this.custType = setting.getByGroup("custType", "test_table_1");

        if (this.custType.equalsIgnoreCase("Personal")) {
            this.custIdPrefix = setting.getByGroup("personalCustIdPrefix", "test_table_1");
        }

        this.custIdLength = setting.getInt("custIdLength", "test_table_1");
        this.cardNumberPrefix = setting.getByGroup("cardNumberPrefix", "test_table_1");
        this.cardNumberLength = setting.getInt("cardNumberLength", "test_table_1");
        this.accountNumberPrefix = setting.getByGroup("accountNumberPrefix", "test_table_1");
        this.accountNumberLength = setting.getInt("accountNumberLength", "test_table_1");
        this.isolation = setting.getByGroup("isolation", "test_table_1");

        this.xdsDemoConfigModel = new XdsDemoConfigModel(setting);
    }

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "真实场景交易基础信息主表";
    }

    /**
     * 获取总处理数量
     * @return
     */
    @Override
    public int getTotalNums() {
        return 0;
    }

    /**
     * 获取表头文件名
     * @return
     */
    @Override
    public String getFileHeaderName() {
        return "test_table_1_header.csv";
    }

    /**
     * 获取表文件名
     * @return
     */
    @Override
    public String getTargetFileName() {
        return "test_table_1.csv";
    }
}
