package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxBatchSceneConfigModel
 * @desc TODO
 * @date 2022/3/16 19:26
 */
@Data
@Component("xxxxBatchSceneConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.batchmodel")
public class XxxxBatchSceneConfigModel extends AbstractConfigModel {

    /**
     * 客户数量
     */
    private int custNum;

    /**
     * 每个客户的卡号数量
     */
    private int perCustCardNums;

    /**
     * 每个卡号对应的账户数
     */
    private int perCardAccountNums;

    /**
     * begin offset
     */
    private int beginOffset;

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
     * 账号前缀
     */
    private String accountPrefix;

    /**
     * 账号长度
     */
    private int accountLength;

    /**
     * 对私：对公
     */
    private int[] privateCorporateRatio = new int[] { 5, 1 };

    /**
     * sex
     */
    private int[] sexRatio = new int[] { 1, 1 };

    /**
     * maritial status
     */
    private int[] maritialStatusRatio = new int[] { 1, 2 };

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "真实场景批量操作相关表";
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
     * 获取表头文件名数组
     * @return
     */
    @Override
    public String[] getFileHeaderNames() {
        return new String[] {
                "test_table_3_header.csv", "test_table_4_header.csv", "test_table_5_header.csv",
                "test_table_6_header.csv", "test_table_7_header.csv", "test_table_8_header.csv",
        };
    }

    /**
     * 获取表文件名数组
     * @return
     */
    @Override
    public String[] getTargetFileNames() {
        return new String[] {
                "test_table_3.csv", "test_table_4.csv", "test_table_5.csv",
                "test_table_6.csv", "test_table_7.csv", "test_table_8.csv",
        };
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable3() {
        return "test_table_3";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable4() {
        return "test_table_4";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable5() {
        return "test_table_5";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable6() {
        return "test_table_6";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable7() {
        return "test_table_7";
    }

    /**
     * file suffix
     */
    public String getFileSuffixWithTestTable8() {
        return "test_table_8";
    }
}
