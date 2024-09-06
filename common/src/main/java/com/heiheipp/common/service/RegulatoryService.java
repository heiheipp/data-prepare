package com.heiheipp.common.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.mbg.mapper.TestHtap2Insert001Mapper;
import com.heiheipp.common.mbg.mapper.TestHtap2Sordata001Mapper;
import com.heiheipp.common.mbg.mapper.TestHtap2Sordata002Mapper;
import com.heiheipp.common.mbg.mapper.TestTable1Mapper;
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

import java.io.InvalidClassException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className RegulatoryService
 * @desc TODO
 * @date 2022/3/15 19:24
 */
@Service
@Scope("prototype")
@Slf4j
public class RegulatoryService {

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
    private TestHtap2Insert001Mapper testHtap2Insert001Mapper;

    /**
     * mappre
     */
    @Autowired
    private TestHtap2Sordata001Mapper testHtap2Sordata001Mapper;

    /**
     * mappre
     */
    @Autowired
    private TestHtap2Sordata002Mapper testHtap2Sordata002Mapper;

    /**
     * CsvWriter
     */
    private CsvWriterNew csvWriter = null;

    /**
     * 批量插入
     *
     * @param testTables
     * @return
     */
    public boolean batchInsert(String type, List<BaseModel> testTables) {
        boolean result = false;

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            switch (type) {
                case "1":
                    testHtap2Insert001Mapper.batchInsert(Arrays.asList(testTables.toArray(new TestHtap2Insert001[0])));
                    break;
                case "2":
                    testHtap2Sordata001Mapper.batchInsert(Arrays.asList(testTables.toArray(new TestHtap2Sordata001[0])));
                    break;
                case "3":
                    testHtap2Sordata002Mapper.batchInsert(Arrays.asList(testTables.toArray(new TestHtap2Sordata002[0])));
                    break;
                default:
                    throw new InvalidClassException("对象类型错误！！！");
            }

            dataSourceTransactionManager.commit(transactionStatus);

            result = true;
        } catch (Exception e) {
            log.error("子线程批量插入TEST_TABLE_1表失败!!!");
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        return result;
    }

    /**
     * List方式写入csv文件
     * @param targetFilePath
     * @param testTables
     * @return
     */
    public boolean writeCsv(String targetFilePath, List<BaseModel> testTables, boolean isFirstLine) {
        // 初始化
        if (csvWriter == null) {
            // 如果文件已存在，则删除
            if (FileUtil.exist(targetFilePath)) {
                log.debug("文件[{}]已存在，进行删除", targetFilePath);
                FileUtil.del(targetFilePath);
            }

            csvWriter = new CsvWriterNew(targetFilePath, CharsetUtil.CHARSET_UTF_8);
        }

        // 执行文件写入
        try {
            if (isFirstLine) {
                csvWriter.writeHeaderLine(TestHtap2Insert001.generateFileHeader().split(","));
            }
            csvWriter.writeBeans(testTables);

            return true;
        } catch (Exception e) {
            log.error("子线程[{}]写入文件失败", RuntimeContext.getRuntimeDatas().get(ConfigConstant.SUB_THREAD_ID_KEY));
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 追加空行
     * @return
     */
    public void writeCsv() {
        csvWriter.writeLine();
        csvWriter.flush();
    }
}
