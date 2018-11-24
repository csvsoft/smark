package com.csvsoft.smark.core

import java.nio.charset.StandardCharsets

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.{SQLVarPair, SQLViewPair, SmarkTaskSQLSpec}
import com.csvsoft.smark.core.entity.{SQL2target, SQLVar, SQLView}
import com.csvsoft.smark.core.util.TaskletContextSerializer
import com.csvsoft.smark.util.TemplateUtils
import org.apache.hadoop.fs.Path
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.Row

import collection.JavaConversions._
import scala.util.Try

class DefaultSQLTasklet(name: String, order: Int, desc: String, val sqlViews: List[SQL2target]) extends BaseTasklet(name, order, desc) with Logging {

  def getSQL2TargetList(smarkTaskSQL: SmarkTaskSQLSpec): List[SQL2target] = {
    val sQL2targetList: List[SQL2target] = smarkTaskSQL.getSqlviewPairs().toList.map(sv => {
      sv match {
        case sqlviewPair: SQLViewPair => new SQLView(sqlviewPair.getOrder, sqlviewPair.getSql, sqlviewPair.getView, sqlviewPair.getPersistMode)
        case sqlVarPair: SQLVarPair => new SQLVar(sqlVarPair.getOrder, sqlVarPair.getSql, sqlVarPair.getVarialeName, sqlVarPair.getDataType)
        case _ => throw new RuntimeException("Not supported sql pairs")
      }
    })
    sQL2targetList
  }

  def this(smarkTaskSQL: SmarkTaskSQLSpec) {
    this(smarkTaskSQL.getName, smarkTaskSQL.getOrder, smarkTaskSQL.getDesc, smarkTaskSQL.getSqlviewPairs().toList.map(sv => {
      sv match {
        case sqlviewPair: SQLViewPair => new SQLView(sqlviewPair.getOrder, sqlviewPair.getSql, sqlviewPair.getView, sqlviewPair.getPersistMode)
        case sqlVarPair: SQLVarPair => new SQLVar(sqlVarPair.getOrder, sqlVarPair.getSql, sqlVarPair.getVarialeName, sqlVarPair.getDataType)
        case _ => throw new RuntimeException("Not supported sql pairs")
      }
    }))
  }


  def this(name: String, order: Int, desc: String = "", sql: String, view: String) {
    this(name, order, desc, List(SQLView(order, sql, view, "DISK_ONLY")))
  }


  override def executeTask(upTo:Int = -1): Option[Set[String]] = {

    val viewNames = sqlViews.map(sv => {
     // logger.info(s"sv classLoader:${sv.getClass.getClassLoader}")

      sv match {
        case sview: SQLView if(upTo == -1 || sview.order <= upTo) => processSQLView(sview)
        case svar: SQLVar if(upTo == -1 || svar.order <= upTo)  => processSQLVar(svar)
        case _ =>""
      }


    })
    Some(viewNames.filter(_ != "").toSet)
  }

  private def processSQLVar(sv: SQLVar): String = {
    logger.info(s"Executing SQLVar:${sv.varName}...")
    if (ctx.appConfig.isDebugMode()) {
      TaskletContextSerializer.serialize(ctx, sv.varName, s"${order}_${sv.order}")
    }
    logger.info(s"Task:$name, raw sql:${sv.sql}")
    val runSQL = getRunnableSQL(sv.sql)
    logger.info(s"Task:$name, running sql:$runSQL")
    val df = sparkSession.sql(runSQL)
    val schema = df.schema
    if (schema.size != 1) {
      throw new RuntimeException("Multiple column returned, expected one column to populate a variable:" + schema.toList.map(_.name).mkString(","))
    }
    val count = df.count()
    if (count > 1) {
      throw new RuntimeException(s"$count rows returned, expected one row to populate the vairable:${sv.varName}")
    }
    val row: Row = df.collect()(0)
    val value = Try(sv.dataType match {
      case TaskVar.DATA_TYPE_INTEGER => this.ctx.addTaskVar(sv.varName, row.getInt(0))
      case TaskVar.DATA_TYPE_LONG => this.ctx.addTaskVar(sv.varName, row.getLong(0))
      case TaskVar.DATA_TYPE_DOUBLE => this.ctx.addTaskVar(sv.varName, row.getDouble(0))
      case TaskVar.DATA_TYPE_STRING => this.ctx.addTaskVar(sv.varName, row.getString(0))
      case TaskVar.DATA_TYPE_BIG_DECIMAL => this.ctx.addTaskVar(sv.varName, row.getDecimal(0))
      case TaskVar.DATA_TYPE_TIMESTAMP => this.ctx.addTaskVar(sv.varName, row.getTimestamp(0))
      case TaskVar.DATA_TYPE_DATE => this.ctx.addTaskVar(sv.varName, row.getDate(0))
      case TaskVar.DATA_TYPE_FLOAT => this.ctx.addTaskVar(sv.varName, row.getFloat(0))
      case TaskVar.DATA_TYPE_BOOLEAN => this.ctx.addTaskVar(sv.varName, row.getBoolean(0))
      case _ => throw new RuntimeException(s"Unsupported data type:${sv.dataType} ")
    })
    if (value.isFailure) {
      throw new RuntimeException(s"Unable to populate the variable:${sv.varName} as type:${sv.dataType} with sql:${sv.sql}", value.failed.get)
    }
    ""
  }

  private def processSQLView(sv: SQLView): String = {
    logger.info(s"Executing SQLView:${sv.viewName} ...")
    if (ctx.appConfig.isDebugMode()) {
      TaskletContextSerializer.serialize(ctx, sv.viewName, s"${order}_${sv.order}")
    }
    logger.info(s"Task:$name, raw sql:${sv.sql}")
    val runSQL = getRunnableSQL(sv.sql)
    logger.info(s"Task:$name, running sql:$runSQL")

    // log sql
    if (ctx.appConfig.isDebugMode()) {
      val sqlPath = new Path(s"${ctx.appConfig.getDirWork()}/_${ctx.appConfig.getRunId()}/${sv.viewName}.sql")
      val outStream =FileSystemFactory.buildFileSystem(ctx.appConfig).create(sqlPath)
      outStream.write(runSQL.getBytes(StandardCharsets.UTF_8))
      outStream.close()
    }

    val df = sparkSession.sql(runSQL)
    this.registerTempView(df, sv.viewName, ctx.appConfig.getRunId())

    sv.viewName
  }

  override def getTaskletType(): TaskletType = TaskletType.SQL


}


