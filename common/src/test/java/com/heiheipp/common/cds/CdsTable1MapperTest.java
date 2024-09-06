//package com.heiheipp.common.cds;
//
//import com.heiheipp.common.mbg.mapper.CdsTable1Mapper;
//import com.heiheipp.common.mbg.model.CdsTable1;
//import com.heiheipp.common.service.CdsMultiTransactionService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
///**
// * @author zhangxi
// * @version 1.0
// * @className CdsTable1MapperTest
// * @desc TODO
// * @date 2022/2/25 19:37
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class CdsTable1MapperTest {
//
//    @Autowired
//    private CdsTable1Mapper cdsTable1Mapper;
//
//    @Autowired
//    private CdsMultiTransactionService cdsMultiTransactionService;
//
//    @Test
//    public void testInsert() throws Exception {
//        CdsTable1 cdsTable1 = new CdsTable1();
//        cdsTable1.setId("1");
//        cdsTable1.setField1("a");
//        cdsTable1Mapper.insert(cdsTable1);
//
//        Assert.assertEquals(1, cdsTable1Mapper.selectAll().size());
//    }
//
//    @Test
//    public void testMultiTransaction() {
//        cdsMultiTransactionService.multiTransaction();
//    }
//}
