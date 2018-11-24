package com.csvsoft.smark.core


import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.{PropertiesBuilder, SmarkTaskReadCSVSpec, SmarkTaskReadJDBCSpec, SmarkTaskSaveJDBCSpec}
import org.apache.spark.sql.SaveMode

import scala.collection.JavaConverters._


class SaveJDBCTasklet(name:String,order:Int,desc:String,smarkTaskSaveJDBCSpec: SmarkTaskSaveJDBCSpec) extends BaseTasklet( name, order, desc) {
 def this(smarkTaskSaveJDBCSpec:SmarkTaskSaveJDBCSpec){
   this(smarkTaskSaveJDBCSpec.getName,smarkTaskSaveJDBCSpec.getOrder,smarkTaskSaveJDBCSpec.getDesc,smarkTaskSaveJDBCSpec)
 }
  override def getTaskletType(): TaskletType = TaskletType.CODE
  override def executeTask(upTo:Int = -1): Option[Set[String]] = {

    val df = sparkSession.sql(s"select * from ${smarkTaskSaveJDBCSpec.getFromView}")

    df.write
        .mode(SaveMode.valueOf(smarkTaskSaveJDBCSpec.getSaveMode))
      .options(smarkTaskSaveJDBCSpec.getJdbcOptionProps.asScala)
      .jdbc(smarkTaskSaveJDBCSpec.getJdbcurl,smarkTaskSaveJDBCSpec.getTargetTable,smarkTaskSaveJDBCSpec.getJdbcOptionProps)
     None
  }



}
