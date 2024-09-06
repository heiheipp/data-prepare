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
 * @className MergeModelService
 * @desc TODO
 * @date 2022/3/16 23:20
 */
@Service
@Scope("prototype")
@Slf4j
public class MergeModelService {

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
    private AdsTxnTifBas50cMapper adsTxnTifBas50cMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBas100cMapper adsTxnTifBas100cMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBas200cMapper adsTxnTifBas200cMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBas300cMapper adsTxnTifBas300cMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBas400cMapper adsTxnTifBas400cMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBasFollowMapper adsTxnTifBasFollowMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBasMapper adsTxnTifBasMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifBasDetailMapper adsTxnTifBasDetailMapper;

    /**
     * Mapper
     */
    @Autowired
    private AdsTxnTifDetailMapper adsTxnTifDetailMapper;

    /**
     * CsvWriter
     */
    private CsvWriterNew[] csvWriters = new CsvWriterNew[9];

    /**
     * init flag
     */
    private boolean init = false;

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
                    case DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBas50cMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas50c[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBas100cMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas100c[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBas200cMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas200c[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBas300cMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas300c[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBas400cMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas400c[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBasFollowMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBasFollow[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBasMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBas[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifBasDetailMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifBasDetail[0])));
                        }
                        break;
                    case DataModelConstant.ADS_TXN_TIF_DETAIL_KEY:
                        if (entry.getValue().size() > 0) {
                            adsTxnTifDetailMapper.batchInsert(Arrays.asList(entry.getValue().toArray(
                                    new AdsTxnTifDetail[0])));
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
                // 写文件体
                if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_50c")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_50C_KEY).
                            toArray(new AdsTxnTifBas50c[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_100c")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_100C_KEY).
                            toArray(new AdsTxnTifBas100c[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_200c")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_200C_KEY).
                            toArray(new AdsTxnTifBas200c[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_300c")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_300C_KEY).
                            toArray(new AdsTxnTifBas300c[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_400c")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_400C_KEY).
                            toArray(new AdsTxnTifBas400c[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_follow")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_FOLLOW_KEY).
                            toArray(new AdsTxnTifBasFollow[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas.")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_KEY).
                            toArray(new AdsTxnTifBas[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_bas_detail")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_BAS_DETAIL_KEY).
                            toArray(new AdsTxnTifBasDetail[0])));
                } else if (csvWriters[i].getFilePath().contains("ads_txn_tif_detail")) {
                    csvWriters[i].writeBeans(Arrays.asList(dataMap.get(DataModelConstant.ADS_TXN_TIF_DETAIL_KEY).
                            toArray(new AdsTxnTifDetail[0])));
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
