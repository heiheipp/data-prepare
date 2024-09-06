package com.heiheipp.xxxxtest.engine;

/**
 * @author zhangxi
 * @version 1.0
 * @className IEngine
 * @desc TODO
 * @date 2022/3/22 23:30
 */
public interface IEngine<T> {

    /**
     * 测试前操作
     */
    void beforeProcess();

    /**
     * 执行测试
     * @return T
     */
    T process();

    /**
     * 测试后操作
     */
    void afterProcess();
}
