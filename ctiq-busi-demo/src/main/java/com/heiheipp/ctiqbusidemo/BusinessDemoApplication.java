package com.heiheipp.ctiqbusidemo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.heiheipp"}, exclude = DataSourceAutoConfiguration.class)
@EnableTransactionManagement
@Slf4j
public class BusinessDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessDemoApplication.class, args);
    }
}
