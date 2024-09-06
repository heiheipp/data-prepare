package com.heiheipp.common;

import cn.hutool.core.util.ReUtil;
import org.junit.Test;

public class RegexpTest {

    @Test
    public void testRegexp() {
        String replaceAll = ReUtil.replaceAll("AA()!112@", "([^ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789():'',+-./?])", " ");
        System.out.println("result=" + replaceAll);
    }
}
