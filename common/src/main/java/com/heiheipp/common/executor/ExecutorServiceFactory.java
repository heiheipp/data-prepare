package com.heiheipp.common.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @description 异步执行服务工厂类。 异步执行服务工厂类。
 * @version 1.0.0
 * @author
 */
@Slf4j
public class ExecutorServiceFactory
{

    private static volatile ExecutorServiceFactory instance = null;

    /**
     * 核心线程数
     */
    @Getter
    private int poolSize;

    /**
     * 等待释放时长
     */
    private final int keepAliveTime = 5;

    /**
     * 默认的队列数
     */
    private final int queueSize = 200;
    
    /**
     * 线程执行服务
     */
    private ExecutorService executorService;
    
    /**
     * 私有构造方法
     */
    private ExecutorServiceFactory()
    {
        // 线程池大小
        this.poolSize = 2 * Runtime.getRuntime().availableProcessors() + 1;

        ThreadPoolExecutor e = new ThreadPoolExecutor(this.poolSize, this.poolSize, this.keepAliveTime, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingDeque<Runnable>(queueSize), new MyRejectedExecutionHandler()) {

        };
        e.allowCoreThreadTimeOut(true);
        executorService = e;
    }

    /**
     * 双重校验,确保单例
     * 
     * @return 返回值
     */
    public static ExecutorServiceFactory getInstance()
    {
        if (instance == null) {
            synchronized (ExecutorServiceFactory.class) {
                if (instance == null) {
                    instance = new ExecutorServiceFactory();
                }
            }
        }
        return instance;
    }
    
    /**
     * 线程池获取方法
     * @return 返回值
     */
    public ExecutorService getExecutor()
    {
        return executorService;
    }

    /**
     * 更新线程池信息
     * @param poolSize
     */
    public void setExecutorService(int poolSize) {
        this.poolSize = poolSize;
        ((ThreadPoolExecutor) this.executorService).setCorePoolSize(poolSize);
        ((ThreadPoolExecutor) this.executorService).setMaximumPoolSize(poolSize);
    }
}
