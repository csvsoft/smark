package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.{PropertiesBuilder, SmarkTaskReadCSVSpec, SmarkTaskSQLSpec}
import org.apache.spark.sql.SparkSession
import scala.collection.convert._
import collection.JavaConverters._
class DefaultReadCSVTasklet(name: String, order: Int, fileName: String, viewName: String, desc: String = "", csvOpitons: Map[String, String]) extends BaseTasklet(name, order, desc) {

  def this(smarkTaskReadCSVSpec: SmarkTaskReadCSVSpec){
    this(smarkTaskReadCSVSpec.getName,smarkTaskReadCSVSpec.getOrder,smarkTaskReadCSVSpec.getFileName,smarkTaskReadCSVSpec.getViewName,smarkTaskReadCSVSpec.getDesc,smarkTaskReadCSVSpec.getCsvConf.asScala.toMap)
  }

  override def getTaskletType(): TaskletType = TaskletType.CODE

  override def executeTask(upTo:Int = -1): Option[Set[String]] = {
    val csvDataFrame = sparkSession.read
      .format("com.databricks.spark.csv")
      .options(csvOpitons)
      .load(fileName)
    this.registerTempView(csvDataFrame, viewName,ctx.appConfig.getRunId())
    Some(Set(viewName))
  }
}
