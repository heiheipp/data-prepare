package com.heiheipp.common.config;

/**
 * @author zhangxi
 * @version 1.0
 * @className BaseConfigModel
 * @desc TODO
 * @date 2022/3/1 16:00
 */
public interface ConfigModel {

    int getConcurrency();

    int getQueueSize();

    int getKeepAliveTime();

    int getBatchNum();

    String getConfigModelDesc();

    int getTotalNums();

    String getTargetFileName();

    String[] getTargetFileNames();

    String getFileHeaderName();

    String[] getFileHeaderNames();
}
