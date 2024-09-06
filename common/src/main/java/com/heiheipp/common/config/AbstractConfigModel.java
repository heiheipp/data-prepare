package com.heiheipp.common.config;

import com.heiheipp.common.constant.ConfigConstant;

/**
 * @author zhangxi
 * @version 1.0
 * @className AbstractConfigModel
 * @desc TODO
 * @date 2022/3/1 18:03
 */
public abstract class AbstractConfigModel implements ConfigModel {

    @Override
    public int getConcurrency() {
        return ConfigConstant.DEFAULT_CONCURRENCY;
    }

    @Override
    public int getQueueSize() {
        return ConfigConstant.DEFAULT_QUEUE_SIZE;
    }

    @Override
    public int getKeepAliveTime() {
        return ConfigConstant.DEFAULT_KEEP_ALIVE_TIME;
    }

    @Override
    public String getConfigModelDesc() {
        return "";
    }

    @Override
    public String getTargetFileName() {
        return "";
    }

    @Override
    public String[] getTargetFileNames() {
        return new String[0];
    }

    @Override
    public String getFileHeaderName() { return ""; }

    @Override
    public String[] getFileHeaderNames() {
        return new String[0];
    }

    @Override
    public int getBatchNum() { return 1; }
}
