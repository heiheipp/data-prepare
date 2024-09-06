package com.heiheipp.dataprepare;

import com.heiheipp.common.executor.ExecutorServiceFactory;
import com.heiheipp.dataprepare.service.impl.XxxxDataPrepareServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangxi
 * @version 1.0
 * @className XxxxDataPrepareServiceTest
 * @desc TODO
 * @date 2022/3/5 0:40
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class XxxxDataPrepareServiceTest {

    @Autowired
    private XxxxDataPrepareServiceImpl xxxxDataPrepareService;

    @Test
    public void testInitialData() throws Exception {
        xxxxDataPrepareService.initialData();

        //ExecutorServiceFactory.getInstance().getExecutor().awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }
}
