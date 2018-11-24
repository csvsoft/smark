package com.csvsoft.smark

import org.apache.spark.sql.SparkSession

object SparkSessionFactory {
  def getSparkSession(appName:String = "Spark simple example"):SparkSession ={
    SparkSession.builder().appName(appName).master("local").getOrCreate()

  }

  def getTestSparkSession(appName:String = "Spark simple example"):SparkSession = {
    val sparkSession = SparkSession.builder().appName(appName).master("local").getOrCreate()

    val ctx = sparkSession.sqlContext

    val createTable = "CREATE  TABLE boxes (name STRING,width INT, length INT, height INT) USING PARQUET  OPTIONS ('compression'='snappy') "
    ctx.sql(createTable)
    ctx.sql("truncate table boxes")
    for (i <- 1 to 10){
      val insertSQL = s"INSERT INTO boxes values('boxname$i',$i,2,3)"
      ctx.sql(insertSQL)
    }
    sparkSession
  }


}
