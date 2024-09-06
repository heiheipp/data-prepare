package com.heiheipp.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className DataHolderContext
 * @desc TODO
 * @date 2022/3/5 22:49
 */
public class RuntimeContext {

    /**
     * 线程缓存数据
     */
    private static final ThreadLocal<Map<String, Object>> runtimeDatas = new ThreadLocal<>();

    /**
     * 设置运行时上下文单个值
     *
     * @param key
     *            运行时参数名
     * @param value
     *            运行时参数值
     */
    public static void setRuntimeData(String key, Object value)
    {
        if (runtimeDatas.get() == null) {
            runtimeDatas.set(new HashMap<String, Object>());
        }
        runtimeDatas.get().put(key, value);
    }

    /**
     * 设置运行时上下文
     *
     * @param map
     *            运行时参数集合
     */
    public static void setRuntimeDatas(Map<String, Object> map)
    {
        if (map == null || map.size() < 1 || map == runtimeDatas.get()) {
            return;
        }
        if (runtimeDatas.get() == null) {
            runtimeDatas.set(new HashMap<String, Object>());
        }
        runtimeDatas.get().putAll(map);
    }

    /**
     * 清除当前线程数据
     */
    public static void clearRuntimeData()
    {
        runtimeDatas.remove();
    }

    /**
     * 获取运行时参数
     *
     * @param key
     *            运行时参数名
     * @return 运行时参数值
     */
    public static Object getRuntimeData(String key)
    {
        if (key == null || runtimeDatas.get() == null) {
            return null;
        }
        return runtimeDatas.get().get(key);
    }

    /**
     * 获取运行时参数
     *
     * @return 运行时参数集合
     */
    public static Map<String, Object> getRuntimeDatas()
    {
        if (runtimeDatas.get() == null) {
            return null;
        }
        return runtimeDatas.get();
    }
}
