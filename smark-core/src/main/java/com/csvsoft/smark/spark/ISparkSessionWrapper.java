package com.csvsoft.smark.spark;

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

public interface ISparkSessionWrapper {

    public SparkSession getSparkSession();
    static SparkSession.Builder builder() {
        return SparkSession.builder();
    }

    static void clearActiveSession() {
        SparkSession.clearActiveSession();
    }

    static void setActiveSession(SparkSession session) {
        SparkSession.setActiveSession(session);
    }

    static void setDefaultSession(SparkSession session) {
        SparkSession.setDefaultSession(session);
    }

    Dataset<Row> baseRelationToDataFrame(BaseRelation baseRelation);

    void close();

    RuntimeConfig conf();

    Dataset<Row> createDataFrame(JavaRDD<?> rdd, Class<?> beanClass);

    Dataset<Row> reateDataFrame(JavaRDD<Row> rowRDD, StructType schema);

    Dataset<Row> createDataFrame(java.util.List<?> data, Class<?> beanClass);

    Dataset<Row> createDataFrame(java.util.List<Row> rows, StructType schema);

    Dataset<Row> createDataFrame(RDD<?> rdd, Class<?> beanClass);

    Dataset<Row> createDataFrame(RDD<Row> rowRDD, StructType schema);

    Dataset<Row> emptyDataFrame();

    ExecutionListenerManager listenerManager();

    SparkSession newSession();

    DataFrameReader read();

    DataStreamReader readStream();

    org.apache.spark.sql.internal.SessionState sessionState();

    org.apache.spark.sql.internal.SharedState sharedState();

    SparkContext sparkContext();

    Dataset<Row> sql(String sqlText);

    SQLContext sqlContext();

    void stop();

    StreamingQueryManager streams();

    Dataset<Row> table(String tableName);

    <T> T time(scala.Function0<T> f);

    String version();

    Catalog catalog();

    UDFRegistration udf();
}