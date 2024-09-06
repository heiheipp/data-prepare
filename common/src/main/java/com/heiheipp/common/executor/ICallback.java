package com.heiheipp.common.executor;

/**
 * @description 回调函数接口类。
 *              回调函数接口类。
 * @version 1.0.0
 * @errorcode
 *            错误码: 错误描述 <br>
 */
public interface ICallback<T>
{
    
    /**
     * 成功调用方法
     *
     * @param result
     *            返回结果
     */
    public void onSucess(T result);
    
    /**
     * 失败调用方法
     *
     * @param exception
     *            异常信息
     */
    public void onFail(Throwable exception);
    
}
