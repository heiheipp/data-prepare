package com.heiheipp.dataprepare;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.jupiter.api.Test;

public class TestSpark {

    @Test
    public void testSparkRead() {
        // 创建一个SparkSession
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL MySQL Example")
                .master("local[*]") // 使用本地模式，或者设置为你的集群管理器URL
                .getOrCreate();

        // MySQL JDBC连接参数
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "123456";
        String table = "t1";
        String driver = "com.mysql.cj.jdbc.Driver";
        //String query = "select * from " + table;

        // 读取MySQL表中的数据
        Dataset<Row> df = spark.read()
                .format("jdbc")
                .option("url", jdbcUrl)
                .option("dbtable", table) // 你要读取的表名
                .option("user", username)
                .option("password", password)
                .option("driver", driver)
                .load();

        // 显示前几行数据
        df.show();

        // 停止SparkSession
        spark.stop();
    }
}
