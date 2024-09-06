package com.heiheipp.ctiqbusidemo.controller;

import com.heiheipp.ctiqbusiapi.entity.TestTable1;
import com.heiheipp.ctiqbusiapi.mapper.TestTable1Mapper;
import com.heiheipp.ctiqbusicommon.constant.BusinessConstant;
import com.heiheipp.ctiqbusicommon.context.DynamicDataSourceContextHolder;
import com.heiheipp.ctiqbusidemo.vo.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test1")
public class TestTable1Controller {

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

    @Autowired
    @Qualifier(BusinessConstant.DB1_DATASOURCE_KEY)
    private DataSource dataSource1;

    @Autowired
    @Qualifier(BusinessConstant.DB2_DATASOURCE_KEY)
    private DataSource dataSource2;

    @Autowired
    TestTable1Mapper testTable1Mapper;

    /**
     * 查询全部
     */
    @GetMapping("/listall")
    public Object listAll() {
        Map<String, Object> result = new HashMap<>();

        //默认查询db1
        List<TestTable1> resultDataDefault = testTable1Mapper.selectAll();
        result.put(BusinessConstant.DB1_DATASOURCE_KEY, resultDataDefault);

        //切换数据源，在db2查询
        DynamicDataSourceContextHolder.setContextKey(BusinessConstant.DB2_DATASOURCE_KEY);
        List<TestTable1> resultDataDB2 = testTable1Mapper.selectAll();
        result.put(BusinessConstant.DB2_DATASOURCE_KEY, resultDataDB2);
        //恢复数据源
        DynamicDataSourceContextHolder.removeContextKey();
        //返回数据
        return ResponseResult.success(result);
    }

    /**
     * 插入记录
     */
    @PostMapping("/add")
    public Object add(TestTable1 testTable1) {
        Map<String, Object> result = new HashMap<>();

        // 数据库操作
        TransactionStatus transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            dataSourceTransactionManager.setDataSource(dataSource1);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            //默认db1
            int resultDataDefault = testTable1Mapper.insert(testTable1);
            result.put(BusinessConstant.DB1_DATASOURCE_KEY, resultDataDefault);

            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        transactionStatus = null;
        try {
            // 开启新事务
            ((DefaultTransactionDefinition) transactionDefinition).setPropagationBehavior(
                    TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            dataSourceTransactionManager.setDataSource(dataSource2);
            transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            //切换数据源，在db2
            DynamicDataSourceContextHolder.setContextKey(BusinessConstant.DB2_DATASOURCE_KEY);
            testTable1.setId(testTable1.getId() + 1000);
            int resultDataDB2 = testTable1Mapper.insert(testTable1);
            result.put(BusinessConstant.DB2_DATASOURCE_KEY, resultDataDB2);

            dataSourceTransactionManager.commit(transactionStatus);
        } catch (Exception e) {
            e.printStackTrace();
            dataSourceTransactionManager.rollback(transactionStatus);
        }

        //恢复数据源
        DynamicDataSourceContextHolder.removeContextKey();
        //返回数据
        return ResponseResult.success(result);
    }
}
