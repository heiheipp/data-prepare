package com.heiheipp.ctiqbusidemo.context;

import cn.hutool.core.util.ReflectUtil;
import com.google.common.base.Preconditions;

import java.lang.reflect.Method;

public class MapperContext {

    private final Object mapper;

    private final String methodName;

    private final Object parameter;

    private final Class resultClass;

    public MapperContext(Object mapper, String methodName, Object parameter) {
        Preconditions.checkArgument(mapper != null, "mapper cannot be null.");
        this.mapper = mapper;
        this.methodName = methodName;
        this.parameter = parameter;

        Method method = ReflectUtil.getMethodOfObj(mapper, methodName, parameter);
        this.resultClass = method.getReturnType();
    }

    public <T> T executeSQL() {
        T result = ReflectUtil.invoke(this.mapper, this.methodName, this.parameter);
        return result;
    }
}
