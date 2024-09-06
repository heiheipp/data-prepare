package com.heiheipp.dataprepare.model;

import com.heiheipp.common.config.ConfigModel;
import com.heiheipp.common.context.SpringContextUtil;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataPrepareControllModel
 * @desc TODO
 * @date 2022/3/1 14:40
 */
public class ConfigModelFactory {

    public ConfigModel getConfigModel(String configType) {
        if (configType.equalsIgnoreCase("XXXX")) {
            return SpringContextUtil.getBean(XxxxTranDetailConfigModel.class);
        }

        return null;
    }
}
