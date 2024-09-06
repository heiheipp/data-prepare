package com.heiheipp.xxxxtest.engine.impl;

import com.heiheipp.xxxxtest.engine.IEngine;

/**
 * @author zhangxi
 * @version 1.0
 * @className AbstractEngine
 * @desc TODO
 * @date 2022/3/22 23:43
 */
public abstract class AbstractEngine<T> implements IEngine<T> {

    /**
     * 流程引擎
     */
    public void doProcess() {
        beforeProcess();

        process();

        afterProcess();
    }
}
