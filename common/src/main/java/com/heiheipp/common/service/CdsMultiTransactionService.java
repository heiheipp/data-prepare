//package com.heiheipp.common.service;
//
//import cn.hutool.core.lang.UUID;
//import com.heiheipp.common.mbg.mapper.CdsTable1Mapper;
//import com.heiheipp.common.mbg.mapper.CdsTable2Mapper;
//import com.heiheipp.common.mbg.mapper.CdsTable3Mapper;
//import com.heiheipp.common.mbg.mapper.CdsTranLogMapper;
//import com.heiheipp.common.mbg.model.CdsTable1;
//import com.heiheipp.common.mbg.model.CdsTable3;
//import com.heiheipp.common.mbg.model.CdsTranLog;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.TransactionStatus;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.support.TransactionCallback;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import javax.transaction.xa.XAException;
//
///**
// * @author zhangxi
// * @version 1.0
// * @className CdsMultiTransactionService
// * @desc TODO
// * @date 2022/2/25 20:45
// */
//@Service
//@Slf4j
//public class CdsMultiTransactionService {
//
//    @Autowired
//    private TransactionTemplate transactionTemplate;
//
//    @Autowired
//    private DataSourceTransactionManager dataSourceTransactionManager;
//
//    @Autowired
//    private CdsTable1Mapper cdsTable1Mapper;
//
//    @Autowired
//    private CdsTable2Mapper cdsTable2Mapper;
//
//    @Autowired
//    private CdsTable3Mapper cdsTable3Mapper;
//
//    @Autowired
//    private CdsTranLogMapper cdsTranLogMapper;
//
//    @Transactional
//    public void multiTransaction() {
//        log.info("外部事务开始");
//        String uuid = UUID.fastUUID().toString().replace("-", "");
//
//        CdsTable1 cdsTable1 = new CdsTable1();
//        cdsTable1.setId(uuid);
//        cdsTable1.setField1("111");
//
//        cdsTable1Mapper.insert(cdsTable1);
//
//        // 内部事务
//        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
//        transactionTemplate.execute(new TransactionCallback() {
//            @Override
//            public Object doInTransaction(TransactionStatus paramTransactionStatus) {
//                log.info("内层事务开始");
//                CdsTranLog cdsTranLog = new CdsTranLog();
//                cdsTranLog.setId(uuid);
//                cdsTranLog.setField1("222");
//
//                try {
//                    cdsTranLogMapper.insert(cdsTranLog);
//                } catch (Exception e) {
//                    log.error("内层事务error");
//                }
//
//                return true;
//            }
//        });
//
//        CdsTable3 cdsTable3 = new CdsTable3();
//        cdsTable3.setId(uuid);
//        cdsTable3.setField1("333444");
//
//        cdsTable3Mapper.insert(cdsTable3);
//
//        throw new RuntimeException("外层事务error");
//    }
//}
