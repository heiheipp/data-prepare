package com.heiheipp.common.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.mbg.mapper.*;
import com.heiheipp.common.mbg.model.*;
import com.heiheipp.common.util.CsvWriterNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className ZdjModelService
 * @desc TODO
 * @date 2022/3/16 23:20
 */
@Service
@Scope("prototype")
@Slf4j
public class ZdjModelService {

    /**
     * 事务管理器
     */
    @Autowired
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * 事务定义
     */
    @Autowired
    private TransactionDefinition transactionDefinition;

    /**
     * Mapper
     */
    @Autowired
    private ZdjTable1Mapper zdjTable1Mapper;

    /**
     * Mapper
     */
    @Autowired
    private ZdjTable2Mapper zdjTable2Mapper;

    /**
     * Mapper
     */
    @Autowired
    private ZdjTable3Mapper zdjTable3Mapper;

    /**
     * 多表批量插入
     *
     * @param dataMap
     * @return
     */
    public boolean batchInsertMultiTables(Map<String, List<BaseModel>> dataMap) {
        boolean result = false;
        Map<String, Object> runtimeDatas = RuntimeContext.getRuntimeDatas();

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            for (Map.Entry<String, List<BaseModel>> entry : dataMap.entrySet()) {
                switch (entry.getKey()) {
                    case DataModelConstant.ZDJ_TABLE1_KEY:
                        zdjTable1Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new ZdjTable1[0])));
                        break;
                    case DataModelConstant.ZDJ_TABLE2_KEY:
                        if (entry.getValue().size() > 0) {
                            zdjTable2Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new ZdjTable2[0])));
                        }
                        break;
                    case DataModelConstant.ZDJ_TABLE3_KEY:
                        if (entry.getValue().size() > 0) {
                            zdjTable3Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new ZdjTable3[0])));
                        }
                        break;
                    default:
                        break;
                }
            }

            dataSourceTransactionManager.commit(transactionStatus);
            result = true;
        } catch (Exception e) {
            log.error("子线程[{}]批量插入batch相关表失败!!!");
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        return result;
    }
}
