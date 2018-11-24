package com.csvsoft.smark.core

import java.io._
import java.nio.charset.StandardCharsets

import com.csvsoft.smark.config._
import com.csvsoft.smark.core.builder.{SmarkAppMavenInvoker, TaskletBuilder}
import com.csvsoft.smark.core.util.{SmarkAppSpecSerializer, TaskletContextSerializer, XmlUtils}
import com.csvsoft.smark.util.TemplateUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory

import collection.JavaConverters._
import scala.tools.nsc.interpreter.InputStream
import scala.util.{Failure, Success, Try}

object SmarkAppRunner {
  val logger = LoggerFactory.getLogger("SmarkAppRunner")
  def getStreamFromClassPath(classPath: String): InputStream = {
    val specStream: InputStream = getClass.getResourceAsStream(classPath)
    if (Option(specStream).isEmpty) {
      throw new RuntimeException(s"Resource not found in the class path:$classPath")
    }
    specStream
  }

  def getAppSpecFromClassPath(classPath: String): SmarkAppSpec = {
    val specStream: InputStream = getStreamFromClassPath(classPath)
    val appSpec = XmlUtils.toObject(specStream, classOf[SmarkAppSpec])
    appSpec
  }


  def run(sparkSession: SparkSession, runId: Long, runTo: Int, runToSubTask :Int,specXml: String, appPropFile: String): Unit = {
    val appSpec = XmlUtils.toObject(specXml, classOf[SmarkAppSpec])
    run(sparkSession, runId, runTo,runToSubTask, appSpec, appPropFile)
  }

  def run(sparkSession: SparkSession, runId: Long, runTo: Int,runToSubTask :Int, specBytes: Array[Byte], appPropFile: String): Unit = {
    val appSpec = SmarkAppSpecSerializer.deserializeSmarkAppSpcec(specBytes)
    run(sparkSession, runId, runTo,runToSubTask, appSpec, appPropFile)

  }

    def refreshSession(sparkSession: SparkSession, runId: Long, taskUntil: SmarkTaskSpec, sqlPairOpt: Option[BaseSQLPair], appSpec: SmarkAppSpec): Unit = {
    // Drop all views
    sparkSession.catalog.listTables().collect().map(t => t.name).foreach(tableName => {
      try {
        logger.info(s"Dropping view:$tableName")
        sparkSession.catalog.dropTempView(tableName)
      } catch {
        case e: Exception => logger.warn(s"Error when dropping view:$tableName")
      }

    })
    //Reload views for tasks before the taskUntil
    val taskList = appSpec.getSmarkTasks.asScala.filter(t => t.isInstanceOf[HaveViews] && t.getOrder <= taskUntil.getOrder)
      .foreach(taskSpec => {
        if (!taskSpec.equals(taskUntil)) {
          taskSpec.asInstanceOf[HaveViews].getViewNames.asScala.foreach(view => reloadView(sparkSession, runId, view, appSpec))
        } else if (taskSpec.isInstanceOf[SmarkTaskSQLSpec] && sqlPairOpt.isDefined) {
          val sqlPair: BaseSQLPair = sqlPairOpt.get
          val haveViewList = taskSpec.asInstanceOf[SmarkTaskSQLSpec].getSqlviewPairs.asScala.filter(sp => sp.getOrder < sqlPair.getOrder && sp.isInstanceOf[HaveViews])
            .map(_.asInstanceOf[HaveViews]).toList
          logger.info(s"haveViewList size:${haveViewList.size}")

          reloadViews(sparkSession, runId, appSpec, haveViewList)
        }
      })
  }

  def compileApp(appSpec: SmarkAppSpec): Try[String] = {
    val goal = s"compile"
    SmarkAppMavenInvoker.invokeAppGoal(appSpec, goal);
  }

  def runSmarkWithMaven(appSpec: SmarkAppSpec): Try[String] = {
    val goal = s"-Dtest=${appSpec.getPackageName}.app.${appSpec.getClassName}Test test"
    SmarkAppMavenInvoker.invokeAppGoal(appSpec, goal);
  }


  def getTaskletContext(appSpec: SmarkAppSpec, taskSpec: SmarkTaskSpec): Option[TaskletContext] = {
    val taskletCtxOpt =TaskletContextSerializer.deserialize(SmarkAppConfig(appSpec), taskSpec.getName, taskSpec.getOrder.toString)
    val taskSpecs = appSpec.getSmarkTasks
    if(Option(taskSpecs).isEmpty){
      return None
    }
    var i= taskSpecs.size()-1
    var prevTaskletCtx:Option[TaskletContext] = None
    var prevTaskspec : SmarkTaskSpec= null
    while(i>=0 && prevTaskletCtx.isEmpty){
      prevTaskspec = taskSpecs.get(i)
      prevTaskletCtx = TaskletContextSerializer.deserialize(SmarkAppConfig(appSpec), prevTaskspec.getName, prevTaskspec.getOrder.toString)
      i = i -1
    }
    prevTaskletCtx


  }

  def getTaskletContext(appSpec: SmarkAppSpec, taskSpec: SmarkTaskSQLSpec, sqlViewPair: SQLViewPair): Option[TaskletContext] = {
    val taskletCtxOpt = TaskletContextSerializer.deserialize(SmarkAppConfig(appSpec), sqlViewPair.getView, s"${taskSpec.getOrder}_${sqlViewPair.getOrder}")
    taskletCtxOpt match {
      case Some(taskletContext) => taskletCtxOpt
      case _ => getPrevTaskletContext(appSpec, taskSpec, sqlViewPair)
    }

  }

  def getTaskletContext(appSpec: SmarkAppSpec, taskSpec: SmarkTaskSQLSpec, sqlVarPair: SQLVarPair): Option[TaskletContext] = {
    val taskletCtxOpt = TaskletContextSerializer.deserialize(SmarkAppConfig(appSpec), sqlVarPair.getVarialeName, s"${taskSpec.getOrder}_${sqlVarPair.getOrder}")
    taskletCtxOpt match {
      case Some(taskletContext) => taskletCtxOpt
      case _ => getPrevTaskletContext(appSpec, taskSpec, sqlVarPair)
    }
  }

  def getPrevTaskletContext(appSpec: SmarkAppSpec, taskSpec: SmarkTaskSQLSpec, sqlVarPair: BaseSQLPair): Option[TaskletContext] = {
    val sqlPairs = taskSpec.getSqlviewPairs
    if (Option(sqlPairs).isDefined) {
      val lastSQLPair = sqlPairs.get(sqlPairs.size() - 1)
      val lastViewName = lastSQLPair match {
        case sqlviewPair: SQLViewPair => sqlviewPair.getView
        case sqlVarPair: SQLVarPair => sqlVarPair.getVarialeName
        case _ => throw new RuntimeException("Unsupported sqlpair:" + lastSQLPair.getClass.getName)
      }
      TaskletContextSerializer.deserialize(SmarkAppConfig(appSpec), lastViewName, s"${taskSpec.getOrder}_${lastSQLPair.getOrder}")
    } else {
      getTaskletContext(appSpec, taskSpec)
    }
  }


  private def reloadViews(sparkSession: SparkSession, runId: Long, appSpec: SmarkAppSpec, viewList: List[HaveViews]): Unit = {
    viewList.foreach(haveView => {
      haveView.getViewNames.asScala.foreach(view => reloadView(sparkSession, runId, view, appSpec))
    })
  }

  def reloadView(sparkSession: SparkSession, runId: Long, viewName: String, appSpec: SmarkAppSpec): Unit = {
    val dirWork = appSpec.getConfigProps.getProperty("app.dir.work")
    val parquetFile = s"$dirWork/_$runId/${viewName}.parquet"
    val file = new File(parquetFile)
    if (file.exists()) {
      logger.info(s"refresh view--Loading view:${viewName}")
      val df = sparkSession.read.parquet(parquetFile)
      df.createOrReplaceTempView(viewName)
      df.cache()
    } else {
      logger.warn(s"refresh view--View not found:${viewName}")
    }
  }

  def run(sparkSession: SparkSession, runId: Long, runTo: Int, runToSubTask:Int,appSpec: SmarkAppSpec, appPropFile: String,classLoaderOpt:Option[ClassLoader]=None): Unit = {
    val smarkAppConfig = SmarkAppConfig(appSpec, appPropFile)
    // evaluate the fileName in readCSVTasks
    val configMap = PropertiesBuilder.prop2Map(smarkAppConfig.configProps)
    appSpec.getSmarkTasks.asScala.foreach(taskSpec => {
      taskSpec match {
        case readCSV: SmarkTaskReadCSVSpec if (readCSV.getFileName.contains("$")) => readCSV.setFileName(TemplateUtils.merge(readCSV.getFileName, configMap))
        case _ =>
      }
    })
    // val sparkSession = SparkSessionFactory
    val taskletList = appSpec.getSmarkTasks.asScala.map(taskSpec => {
      TaskletBuilder.buildFromSpec(taskSpec, appSpec,classLoaderOpt)
    }).toList

    val ctx = new TaskletContext(smarkAppConfig)

    val auditLogger = getAuditLogger(smarkAppConfig)

    val taskExecutor = new DefaultTaskletExecutor(sparkSession, ctx, runId, auditLogger)
    val smarkAppExecutor = getSmarkAppExecutor(smarkAppConfig)
    smarkAppExecutor.execute(taskExecutor, smarkAppConfig, taskletList, runTo,runToSubTask)
  }

  private def getAuditLogger(smarkAppConfig: SmarkAppConfig): TaskletAuditLogger = {
    val auditLoggerClass = smarkAppConfig.getAuditLoggerClass()
    val clazz = this.getClass.getClassLoader.loadClass(auditLoggerClass)
    clazz.newInstance().asInstanceOf[TaskletAuditLogger]
  }

  private def getSmarkAppExecutor(smarkAppConfig: SmarkAppConfig): SmarkAppExecutor = {
    val smarkAppExecutorClass = smarkAppConfig.getAppExecutorClass()
    val clazz = this.getClass.getClassLoader.loadClass(smarkAppExecutorClass)
    clazz.newInstance().asInstanceOf[SmarkAppExecutor]
  }
}
