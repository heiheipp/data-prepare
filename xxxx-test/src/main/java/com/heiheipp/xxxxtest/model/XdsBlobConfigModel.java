package com.heiheipp.xxxxtest.model;

import cn.hutool.setting.Setting;
import com.heiheipp.common.config.AbstractConfigModel;
import lombok.Data;

import java.util.Arrays;

/**
 * @author zhangxi
 * @version 1.0
 * @className CdsBlobConfigModel
 * @desc TODO
 * @date 2022/4/2 11:52
 */
@Data
public class XdsBlobConfigModel extends AbstractConfigModel {

    /**
     * operate type
     */
    private String operateTypes;

    /**
     * operate type array
     */
    private String[] operateArray;

    /**
     * operate ratio
     */
    private String operateRatioStr;

    /**
     * operate ratio
     */
    private int[] operateRatio;

    /**
     * isolation
     */
    private String isolation;

    /**
     * step size
     */
    private int stepSize;

    /**
     * 构造函数
     * @param setting
     */
    public XdsBlobConfigModel(Setting setting) {
        this.operateTypes = setting.getByGroup("operateType", "blob_test");
        this.operateRatioStr = setting.getByGroup("operateRatio", "blob_test");
        this.isolation = setting.getByGroup("isolation", "blob_test");
        this.stepSize = setting.getInt("stepSize", "blob_test");

        // 计算衍生参数
        this.operateArray = this.operateTypes.split(":");
        this.operateRatio = Arrays.stream(this.operateRatioStr.split(":")).mapToInt(Integer::parseInt).toArray();
    }

    /**
     * 获取配置模型描述
     * @return
     */
    @Override
    public String getConfigModelDesc() {
        return "CDS Blob字段测试";
    }

    /**
     * 获取总处理数量
     * @return
     */
    @Override
    public int getTotalNums() {
        return 0;
    }
}
