package com.csvsoft.smark.core

import java.util.Properties

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.{PropertiesBuilder, SmarkTaskReadCSVSpec, SmarkTaskReadJDBCSpec}
import scala.collection.JavaConverters._

case class JDBCPartitioningInfo(column: String,
                                 lowerBound: Long,
                                 upperBound: Long,
                                 numPartitions: Int)
class ReadJDBCTasklet( name: String, order: Int, sql: String, viewName: String, desc: String, jdbcURL:String,jdbcOpitons: Properties,jdbcPartition:Option[JDBCPartitioningInfo]) extends BaseTasklet( name, order, desc) {
  override def getTaskletType(): TaskletType = TaskletType.CODE
  override def executeTask(upTo:Int = -1): Option[Set[String]] = {
 val dbTable = s"($sql) sparkJDBCAlias"
 val jdbcDataFrame = jdbcPartition match{
   case Some(partition) => sparkSession.read
     .jdbc(jdbcURL,dbTable,columnName = partition.column ,lowerBound = partition.lowerBound,upperBound = partition.upperBound,numPartitions = partition.numPartitions,jdbcOpitons)
   case None =>   sparkSession.read.jdbc(jdbcURL,dbTable,jdbcOpitons)
 }
    jdbcDataFrame.createOrReplaceTempView(viewName)
    Some(Set(viewName))
  }



}
