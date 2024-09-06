package com.heiheipp.common.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import com.heiheipp.common.Person;
import com.heiheipp.common.util.CsvWriterNew;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className CdmTransDetailServiceTest
 * @desc TODO
 * @date 2022/3/13 15:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CdmTransDetailServiceTest {

    @Autowired
    private CdmTransDetailService cdmTransDetailService;

    @Test
    public void testWriteCsv() {
        String path = "E:\\test.csv";
        List<Map> personList = new ArrayList<>();
        Person p1 = new Person();
        p1.setName("a");
        p1.setEmail("aaa");

        Person p2 = new Person();
        p2.setName("b");
        p2.setEmail("bbb");
        personList.add(BeanUtil.beanToMap(p1));
        personList.add(BeanUtil.beanToMap(p2));

        CsvWriterNew csvWriter = new CsvWriterNew(path, CharsetUtil.CHARSET_UTF_8);
        //csvWriter.writeBeans(personList);
    }
}
