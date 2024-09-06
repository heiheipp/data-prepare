package com.heiheipp.common.executor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @project rsp-service
 * @description 类中文名
 * @version 1.0.0
 * @author
 */
@Slf4j
public class MyRejectedExecutionHandler implements RejectedExecutionHandler
{
    
    /**
     * @param r
     *            线程
     * @param executor
     *            线程池
     * @see RejectedExecutionHandler#rejectedExecution(Runnable,
     *      ThreadPoolExecutor)
     */
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
    {
        try {
            executor.getQueue().put(r);
        }
        catch (InterruptedException e) {
            log.warn("中断异常!", e);
        }
    }
}
