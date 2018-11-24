package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.SmarkTaskSaveCSVSpec
import com.csvsoft.smark.util.SaveModeUtils
import org.apache.spark.sql.SaveMode

import scala.collection.JavaConverters._

class DefaultSaveCSVTasklet(name: String, order: Int, fileName: String, sql: String, desc: String = "",viewName:String, csvOpitons: Map[String, String],saveMode:String) extends BaseTasklet(name, order, desc) {

  def this(smarkTaskSaveCSVSpec: SmarkTaskSaveCSVSpec){
    this(smarkTaskSaveCSVSpec.getName,smarkTaskSaveCSVSpec.getOrder,smarkTaskSaveCSVSpec.getFileName,smarkTaskSaveCSVSpec.getSql,smarkTaskSaveCSVSpec.getDesc,smarkTaskSaveCSVSpec.getViewName,smarkTaskSaveCSVSpec.getCsvConf.asScala.toMap,smarkTaskSaveCSVSpec.getSaveMode)
  }

  override def getTaskletType(): TaskletType = TaskletType.CODE

  override def executeTask(upTo:Int = -1): Option[Set[String]] = {

    val dfToSave= sparkSession.sql(sql)
    dfToSave.write
        .mode(SaveModeUtils.getSaveMode(saveMode))
      .options(csvOpitons).csv(fileName);
    None
  }




}
