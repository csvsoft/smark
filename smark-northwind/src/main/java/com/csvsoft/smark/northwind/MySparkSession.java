package com.csvsoft.smark.northwind;

import org.apache.spark.sql.SparkSession;

public class MySparkSession implements  com.csvsoft.smark.spark.IMySparkSession {
    @Override
    public SparkSession getSparkSession() {
        System.setProperty("hadoop.home.dir", "/tmp");
        SparkSession sparkSession = SparkSession.builder().config ("spark.driver.host", "localhost")
                .config("spark.driver.userClassPathFirst","true")
                .appName("test").master("local").getOrCreate();
        return sparkSession;
    }
}
