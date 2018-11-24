package com.csvsoft.smark.spark;


import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.sources.BaseRelation;
import org.apache.spark.sql.streaming.DataStreamReader;
import org.apache.spark.sql.streaming.StreamingQueryManager;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.util.ExecutionListenerManager;

import java.util.Properties;

public class SparkSessionWrapper implements ISparkSessionWrapper {

    private SparkSession spark;

    public SparkSessionWrapper(String appName,String master,Properties prop) {

        SparkConf conf = new SparkConf();
        prop.stringPropertyNames().stream().forEach(key->conf.set(key,prop.getProperty(key)));
        spark = SparkSession.builder().config(conf).appName(appName).master(master).getOrCreate();

    }
    public SparkSessionWrapper() {


        spark = SparkSession.builder().appName("Test").master("local").getOrCreate();
    }

    public SparkSession getSparkSession(){
        return spark;
    }

    @Override
    public Dataset<Row> baseRelationToDataFrame(BaseRelation baseRelation) {
        return spark.baseRelationToDataFrame(baseRelation);
    }

    @Override
    public void close() {
        spark.close();

    }

    @Override
    public RuntimeConfig conf() {
        return spark.conf();
    }

    @Override
    public Dataset<Row> createDataFrame(JavaRDD<?> rdd, Class<?> beanClass) {
        return spark.createDataFrame(rdd, beanClass);
    }

    @Override
    public Dataset<Row> reateDataFrame(JavaRDD<Row> rowRDD, StructType schema) {
        return spark.createDataFrame(rowRDD, schema);

    }

    @Override
    public Dataset<Row> createDataFrame(java.util.List<?> data, Class<?> beanClass) {
        return spark.createDataFrame(data, beanClass);
    }

    @Override
    public Dataset<Row> createDataFrame(java.util.List<Row> rows, StructType schema) {
        return spark.createDataFrame(rows, schema);
    }

    @Override
    public Dataset<Row> createDataFrame(RDD<?> rdd, Class<?> beanClass) {
        return spark.createDataFrame(rdd, beanClass);
    }

    @Override
    public Dataset<Row> createDataFrame(RDD<Row> rowRDD, StructType schema) {
        return spark.createDataFrame(rowRDD, schema);

    }

    @Override
    public Dataset<Row> emptyDataFrame() {
        return spark.emptyDataFrame();
    }

    @Override
    public ExecutionListenerManager listenerManager() {
        return spark.listenerManager();
    }

    @Override
    public SparkSession newSession() {
        return spark.newSession();
    }

    @Override
    public DataFrameReader read() {
        return spark.read();
    }

    @Override
    public DataStreamReader readStream() {
        return spark.readStream();
    }

    @Override
    public org.apache.spark.sql.internal.SessionState sessionState() {
        return spark.sessionState();
    }

    @Override
    public org.apache.spark.sql.internal.SharedState sharedState() {
        return spark.sharedState();
    }

    @Override
    public SparkContext sparkContext() {
        return spark.sparkContext();
    }

    @Override
    public Dataset<Row> sql(String sqlText) {
        return spark.sql(sqlText);
    }

    @Override
    public SQLContext sqlContext() {
        return spark.sqlContext();
    }

    @Override
    public void stop() {
        spark.stop();
    }

    @Override
    public StreamingQueryManager streams() {
        return spark.streams();
    }

    @Override
    public Dataset<Row> table(String tableName) {
        return spark.table(tableName);
    }

    @Override
    public <T> T time(scala.Function0<T> f) {
        return spark.time(f);
    }


    @Override
    public String version() {
        return spark.version();
    }

    @Override
    public Catalog catalog() {
        return spark.catalog();
    }

    @Override
    public UDFRegistration udf() {
        return spark.udf();
    }


}