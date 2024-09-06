package com.heiheipp.common.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @description 异步任务执行类。 异步任务执行类。
 * @version 1.0.0
 * @author
 */
@Slf4j
public abstract class AbstractFutureTask<T> implements ExecutorTask
{

    /**
     * 由于taskTimeout在使用过程中会赋值，所以TASK_TIME_OUT任务执行延迟时间常量
     */
    private static final long TASK_TIME_OUT = 30;
    
    /**
     * 回调集合
     */
    @Getter
    public List<ICallback<T>> callbacks = new ArrayList<ICallback<T>>();
    
    /**
     * 任务执行延迟时间
     */
    protected long taskTimeout;
    
    /**
     * 异步执行服务
     */
    protected ExecutorService executorService = ExecutorServiceFactory.getInstance().getExecutor();
    
    /**
     * 任务线程ID
     */
    protected long taskTreadId;
    
    /**
     * 父日志ID
     */
    protected String parentThreadId;
    
    /**
     * 添加回调
     *
     * @param callback
     *            回调函数接口
     * @return 返回值
     */
    public AbstractFutureTask<T> addCallback(ICallback<T> callback)
    {
        callbacks.add(callback);
        return this;
    }
    
    /**
     * 提交
     *
     * @return 返回值
     */
    protected abstract T submit(long threadId);
    
    /**
     * 开始任务
     *
     * @return 返回值
     */
    private T startTask()
    {
        taskTreadId = Thread.currentThread().getId();

        return this.submit(taskTreadId);
    }
    
    /**
     * 执行方法
     *
     * @return 返回值
     */
    @Override
    public String execute()
    {
        //任务执行延迟时间默认30分钟
        taskTimeout = TASK_TIME_OUT;
        try {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> this.startTask(), executorService);
            future.thenAccept(t -> {
                this.doFinish(t);
            }).exceptionally(e -> {
                this.doError(e);
                return null;
            });
            return "";
        }
        catch (RejectedExecutionException e) {
            log.error("线程队列已满，请稍后再试!", e);
            throw new RuntimeException("线程队列已满，请稍后再试!");
        }
    }
    
    /**
     * 完成回调函数
     *
     * @param t
     *            返回结果
     */
    protected void doFinish(T t)
    {
        this.shutdownNowThread(this.taskTreadId);
        for (ICallback<T> callback : callbacks) {
            try {
                callback.onSucess(t);
            }
            catch (Throwable exp) {
                log.error("回调函数错误", exp);
                continue;
            }
        }
    }
    
    /**
     * 错误回调函数
     *
     * @param e
     *            异常信息
     */
    protected void doError(Throwable e)
    {
        this.shutdownNowThread(this.taskTreadId);
        for (ICallback<T> callback : callbacks) {
            try {
                callback.onFail(e);;
            }
            catch (Throwable exp) {
                log.error("回调函数错误", exp);
                continue;
            }
        }
    }
    
    /**
     * 关闭当前线程
     *
     * @param threadId
     *            线程ID
     */
    private void shutdownNowThread(long threadId)
    {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        Thread[] threads = new Thread[group.activeCount()];
        group.enumerate(threads);
        for (Thread thread : threads) {
            if (thread != null && thread.getId() == threadId) {
                thread.interrupt();
                /* 在for循环中找到目标thread后break当前循环 */
                break;
            }
        }
    }
}
