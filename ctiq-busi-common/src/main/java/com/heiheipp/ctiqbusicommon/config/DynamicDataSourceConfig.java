package com.heiheipp.ctiqbusicommon.config;

import com.heiheipp.ctiqbusicommon.constant.BusinessConstant;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@MapperScan(basePackages = "com.heiheipp.ctiqbusiapi.mapper")
@Slf4j
public class DynamicDataSourceConfig {

    @Bean(name = BusinessConstant.DB1_DATASOURCE_KEY)
    @ConfigurationProperties(prefix = "spring.db1")
    public DataSource db1DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = BusinessConstant.DB2_DATASOURCE_KEY)
    @ConfigurationProperties(prefix = "spring.db2")
    public DataSource db2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(BusinessConstant.DB1_DATASOURCE_KEY, db1DataSource());
        targetDataSources.put(BusinessConstant.DB2_DATASOURCE_KEY, db2DataSource());

        //设置动态数据源
        DynamicDatasource dynamicDataSource = new DynamicDatasource();
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(db1DataSource());
        return dynamicDataSource;
    }
}
