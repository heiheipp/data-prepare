package com.heiheipp.dataprepare.service;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataPrepareService
 * @desc 造数接口类
 * @date 2022/2/28 11:43
 */
public interface DataPrepareService<T> {

    /**
     * 初始化环境配置
     */
    void init();

    /**
     * 初始化数据
     * @return T
     */
    T initialData();
}
