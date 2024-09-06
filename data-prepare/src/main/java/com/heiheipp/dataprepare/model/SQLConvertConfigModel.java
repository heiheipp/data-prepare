package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangxi
 * @version 1.0
 * @className SQLConvertConfigModel
 * @desc TODO
 * @date 2024/3/7
 */
@Data
@Component("sqlConvertConfigModel")
@ConfigurationProperties(prefix = "xxxx.config.sqlconvert")
public class SQLConvertConfigModel extends AbstractConfigModel {

    /**
     * 源端orm类型，目前仅支持mybatis xml mapper形式
     */
    private String sourceType;

    /**
     * 源端文件路径
     */
    private String sourceFileLocation;

    /**
     * 目标端文件路径
     */
    private String targetFileLocation;

    /**
     * 目标文件切割方式
     */
    private String targetFileSplitType = "mapper";

    /**
     * 结果分隔符
     */
    private final String delimiter = "|@@|";

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "SQL转换模型";
    }

    /**
     * 获取总处理数量
     * @return
     */
    @Override
    public int getTotalNums() {
        return 1;
    }
}
