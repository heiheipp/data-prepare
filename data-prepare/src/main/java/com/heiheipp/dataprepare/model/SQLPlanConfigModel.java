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
@Component("sqlPlanConfigModel")
public class SQLPlanConfigModel extends AbstractConfigModel {

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "执行计划跳变";
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
