package com.heiheipp.common.util;

import cn.hutool.core.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className XmlTest
 * @desc TODO
 * @date 2022/4/5 20:52
 */
@Slf4j
public class XmlTest {

    private Map<String, Object> datas = new HashMap<>();

    @Test
    public void testXml() throws Exception {
        String key;
        for (int i = 0; i < 100 * 2000; i++) {
            key = DataBuildUtil.getRandomWithLength((int)(1+Math.random()*(10-1+1)));
            datas.put("key" + (i + 1), key);
        }

        float a = ((float) datas.toString().getBytes("utf-8").length) / (float) (1024 * 1024);
        DecimalFormat df = new DecimalFormat("0.00");
        log.info("map length is [{}]bytes, [{}]m", datas.toString().getBytes("utf-8").length, df.format(a));

        float b = (float) XmlUtil.mapToXmlStr(datas).getBytes("utf-8").length / (float) (1024 * 1024);
        log.info("xml length is [{}]bytes, [{}]m", XmlUtil.mapToXmlStr(datas).getBytes("utf-8").length, df.format(b));
    }
}
