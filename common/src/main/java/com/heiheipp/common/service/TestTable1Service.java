package com.heiheipp.common.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
import com.heiheipp.common.mbg.mapper.CdmTransDetailMapper;
import com.heiheipp.common.mbg.mapper.TestTable1Mapper;
import com.heiheipp.common.mbg.model.CdmTransDetail;
import com.heiheipp.common.mbg.model.TestTable1;
import com.heiheipp.common.util.CsvWriterNew;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author zhangxi
 * @version 1.0
 * @className TestTable1Service
 * @desc TODO
 * @date 2022/3/15 19:24
 */
@Service
@Scope("prototype")
@Slf4j
public class TestTable1Service {

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
    private TestTable1Mapper testTable1Mapper;

    /**
     * CsvWriter
     */
    private CsvWriterNew csvWriter = null;

    /**
     * init flag
     */
    private boolean init = false;

    /**
     * 批量插入
     * @param testTable1s
     * @return
     */
    public boolean batchInsert(List<TestTable1> testTable1s) {
        boolean result = false;
        Map<String, Object> runtimeDatas = RuntimeContext.getRuntimeDatas();

        // 初始化操作
//        if (!init) {
//            dataSourceTransactionManager = SpringContextUtil.getBean(DataSourceTransactionManager.class);
//            transactionDefinition = SpringContextUtil.getBean(DefaultTransactionDefinition.class);
//
//            init = true;
//        }

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            //testTable1Mapper.batchInsert(testTable1s);
            dataSourceTransactionManager.commit(transactionStatus);

            result = true;
        } catch (Exception e) {
            log.error("子线程[{}]批量插入TEST_TABLE_1表失败!!!", runtimeDatas.get(ConfigConstant.SUB_THREAD_ID_KEY));
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        return result;
    }

    /**
     * List方式写入csv文件
     * @param targetFilePath
     * @param testTable1s
     * @return
     */
    public boolean writeCsv(String targetFilePath, List<TestTable1> testTable1s, boolean isFirstLine) {
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
                //csvWriter.writeHeaderLine(TestTable1.generateFileHeader().split(","));
            }
            csvWriter.writeBeans(testTable1s);

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
