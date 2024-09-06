package com.heiheipp.dataprepare;

import com.heiheipp.dataprepare.service.DataPrepareService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.heiheipp"})
@EnableTransactionManagement
@Slf4j
public class DataPrepareApplication implements ApplicationRunner {

    @Autowired
    @Qualifier("xxxxDataPrepareServiceImpl")
    private DataPrepareService dataPrepareService;

    public static void main(String[] args) {
        SpringApplication.run(DataPrepareApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            dataPrepareService.initialData();
        } catch (Exception e) {
            log.error("主任务执行失败，异常为[{}]", e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
    }
}
