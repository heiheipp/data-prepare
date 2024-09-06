package com.heiheipp.common.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.heiheipp.common.constant.ConfigConstant;
import com.heiheipp.common.constant.DataModelConstant;
import com.heiheipp.common.context.RuntimeContext;
import com.heiheipp.common.context.SpringContextUtil;
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
import java.util.*;

/**
 * @author zhangxi
 * @version 1.0
 * @className BatchModelService
 * @desc TODO
 * @date 2022/3/16 23:20
 */
@Service
@Scope("prototype")
@Slf4j
public class BatchModelService {

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
    private TestTable3Mapper testTable3Mapper;

    /**
     * Mapper
     */
    @Autowired
    private TestTable4Mapper testTable4Mapper;

    /**
     * Mapper
     */
    @Autowired
    private TestTable5Mapper testTable5Mapper;

    /**
     * Mapper
     */
    @Autowired
    private TestTable6Mapper testTable6Mapper;

    /**
     * Mapper
     */
    @Autowired
    private TestTable7Mapper testTable7Mapper;

    /**
     * Mapper
     */
    @Autowired
    private TestTable8Mapper testTable8Mapper;

    /**
     * CsvWriter
     */
    private CsvWriterNew[] csvWriters = new CsvWriterNew[6];

    /**
     * init flag
     */
    private boolean init = false;

    /**
     * db init flag
     */
    private boolean dbInit = false;

    /**
     * 多表批量插入
     *
     * @param dataMap
     * @return
     */
    public boolean batchInsertMultiTables(Map<String, List<BaseModel>> dataMap) {
        boolean result = false;
        Map<String, Object> runtimeDatas = RuntimeContext.getRuntimeDatas();

        // 初始化操作
//        if (!dbInit) {
//            dataSourceTransactionManager = SpringContextUtil.getBean(DataSourceTransactionManager.class);
//            transactionDefinition = SpringContextUtil.getBean(DefaultTransactionDefinition.class);
//
//            dbInit = true;
//        }

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            for (Map.Entry<String, List<BaseModel>> entry : dataMap.entrySet()) {
                switch (entry.getKey()) {
                    case DataModelConstant.TEST_TABLE_3_KEY:
                        testTable3Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable3[0])));
                        break;
                    case DataModelConstant.TEST_TABLE_4_KEY:
                        testTable4Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable4[0])));
                        break;
                    case DataModelConstant.TEST_TABLE_5_KEY:
                        testTable5Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable5[0])));
                        break;
                    case DataModelConstant.TEST_TABLE_6_KEY:
                        testTable6Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable6[0])));
                        break;
                    case DataModelConstant.TEST_TABLE_7_KEY:
                        testTable7Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable7[0])));
                        break;
                    case DataModelConstant.TEST_TABLE_8_KEY:
                        testTable8Mapper.batchInsert(Arrays.asList(entry.getValue().toArray(new TestTable8[0])));
                        break;
                    default:
                        break;
                }
            }

            dataSourceTransactionManager.commit(transactionStatus);
            result = true;
        } catch (Exception e) {
            log.error("子线程[{}]批量插入batch相关表失败!!!", runtimeDatas.get(ConfigConstant.SUB_THREAD_ID_KEY));
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        return result;
    }

    /**
     * 写入csv文件
     *
     * @param targetFilePaths
     * @param dataMap
     * @return
     */
    public boolean writeCsv(String[] targetFilePaths, Map<String, List<BaseModel>> dataMap, boolean isFirstLine) {
        // 初始化
        if (!init) {
            // 如果文件已存在，则删除
            try {
                for (int i = 0; i < targetFilePaths.length; i++) {
                    if (FileUtil.exist(targetFilePaths[i]) && FileUtil.isFile(targetFilePaths[i])) {
                        log.debug("文件[{}]已存在，进行删除", targetFilePaths[i]);
                        Files.delete(FileUtil.file(targetFilePaths[i]).toPath());
                    }

                    csvWriters[i] = new CsvWriterNew(targetFilePaths[i], CharsetUtil.CHARSET_UTF_8);
                }
            } catch (IOException e) {
                log.error("文件删除失败");
                e.printStackTrace();
            }

            init = true;
        }

        // 执行文件写入
        try {
            for (int i = 0; i < csvWriters.length; i++) {
                if (isFirstLine) {
                    if (csvWriters[i].getFilePath().contains("test_table_3")) {
                        csvWriters[i].writeHeaderLine(TestTable3.generateFileHeader().split(","));
                    } else if (csvWriters[i].getFilePath().contains("test_table_4")) {
                        csvWriters[i].writeHeaderLine(TestTable4.generateFileHeader().split(","));
                    } else if (csvWriters[i].getFilePath().contains("test_table_5")) {
                        csvWriters[i].writeHeaderLine(TestTable5.generateFileHeader().split(","));
                    } else if (csvWriters[i].getFilePath().contains("test_table_6")) {
                        csvWriters[i].writeHeaderLine(TestTable6.generateFileHeader().split(","));
                    } else if (csvWriters[i].getFilePath().contains("test_table_7")) {
                        csvWriters[i].writeHeaderLine(TestTable7.generateFileHeader().split(","));
                    } else if (csvWriters[i].getFilePath().contains("test_table_8")) {
                        csvWriters[i].writeHeaderLine(TestTable8.generateFileHeader().split(","));
                    }
                }

                // 写文件体
                if (csvWriters[i].getFilePath().contains("test_table_3")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_3_KEY).
                            toArray(new TestTable3[0])));
                } else if (csvWriters[i].getFilePath().contains("test_table_4")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_4_KEY).
                            toArray(new TestTable4[0])));
                } else if (csvWriters[i].getFilePath().contains("test_table_5")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_5_KEY).
                            toArray(new TestTable5[0])));
                } else if (csvWriters[i].getFilePath().contains("test_table_6")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_6_KEY).
                            toArray(new TestTable6[0])));
                } else if (csvWriters[i].getFilePath().contains("test_table_7")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_7_KEY).
                            toArray(new TestTable7[0])));
                } else if (csvWriters[i].getFilePath().contains("test_table_8")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.TEST_TABLE_8_KEY).
                            toArray(new TestTable8[0])));
                }
            }

            return true;
        } catch (Exception e) {
            log.error("子线程[{}]写入文件失败", RuntimeContext.getRuntimeDatas().get(ConfigConstant.SUB_THREAD_ID_KEY));
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 追加空行
     *
     * @return
     */
    public void writeCsv() {
        for (int i = 0; i < csvWriters.length; i++) {
            csvWriters[i].writeLine();
            csvWriters[i].flush();
        }
    }
}
