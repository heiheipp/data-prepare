package com.heiheipp.ctiqbusidemo;

import com.heiheipp.ctiqbusiapi.entity.TestTable1;
import com.heiheipp.ctiqbusiapi.mapper.TestTable1Mapper;
import com.heiheipp.ctiqbusicommon.constant.BusinessConstant;
import com.heiheipp.ctiqbusicommon.context.DynamicDataSourceContextHolder;
import com.heiheipp.ctiqbusidemo.context.MapperContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestTable1ControllerTest {

    @Autowired
    TestTable1Mapper testTable1Mapper;

    @Test
    public void selectAllTest() {
        //DynamicDataSourceContextHolder.setContextKey(BusinessConstant.DB2_DATASOURCE_KEY);
        ReflectionTestUtils.invokeMethod(DynamicDataSourceContextHolder.class, "setContextKey", BusinessConstant.DB1_DATASOURCE_KEY);
        List<TestTable1> resultDataDefault = ReflectionTestUtils.invokeMethod(testTable1Mapper, "selectAll", null);
        log.info("result is [{}]", resultDataDefault.toString());
    }

    @Test
    public void selectByIdTest() {
        TestTable1 testTable1 = new TestTable1();
        testTable1.setId(1);

        MapperContext context = new MapperContext(testTable1Mapper, "selectById", testTable1);
        Object resule = context.executeSQL();
        log.info("result is [{}]", resule.toString());

        log.info("切换数据源");
        DynamicDataSourceContextHolder.setContextKey(BusinessConstant.DB2_DATASOURCE_KEY);
        testTable1.setId(1008);
        context = new MapperContext(testTable1Mapper, "selectById", testTable1);
        resule = context.executeSQL();
        log.info("result is [{}]", resule.toString());

        DynamicDataSourceContextHolder.removeContextKey();
    }
}
