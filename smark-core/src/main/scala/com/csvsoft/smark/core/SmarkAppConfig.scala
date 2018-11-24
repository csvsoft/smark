package com.csvsoft.smark.core

import java.io.{File, InputStreamReader}
import java.util.Properties

import collection.JavaConverters._

import com.csvsoft.smark.config.SmarkAppSpec

case class SmarkAppConfig(configProps: Properties,debugRunId:Long = -1L) {
  def getConfig(name: String): String = configProps.getProperty(name)

  def getRunId(): Long = Option(getConfig("runId")) match {
    case Some(s) => s.toLong
    case _ => -1L
  }

  def isDebugMode(): Boolean = Option(getConfig("debugMode")) match {

    case Some(x) if "true".equalsIgnoreCase(x) || "Y".equalsIgnoreCase(x) || "Yes".equalsIgnoreCase(x) => true
    case _ => false
  }
  def getDirWork(): String = Option(getConfig("app.dir.work")).getOrElse("/tmp")
  def getAuditLoggerClass(): String = Option(getConfig("app.auditlogger.class")).getOrElse("com.csvsoft.smark.core.DefaultAuditLogger")
  def getTaskletExecutorClass(): String = Option(getConfig("app.taskletexecutor.class")).getOrElse("com.csvsoft.smark.core.DefaultTaskletExecutor")
  def getAppExecutorClass(): String = Option(getConfig("app.smarkappexecutor.class")).getOrElse("com.csvsoft.smark.core.DefaultSmarkAppExecutor")

}

object SmarkAppConfig {
  val DEBUG_MODE = "debugmode"
  val DIR_WORK = "app.dir.work"


  def apply(smarkAppSpec: SmarkAppSpec): SmarkAppConfig = {
    val specProp: Properties = smarkAppSpec.getConfigProps()
    SmarkAppConfig(specProp,smarkAppSpec.getDebugRunId)
  }

  def apply(smarkAppSpec: SmarkAppSpec, propFile: String): SmarkAppConfig = {
    val specProp: Properties = smarkAppSpec.getConfigProps()
    val custProp: Properties = loadProps(propFile)
    custProp.stringPropertyNames().asScala.foreach(propName => {
      specProp.setProperty(propName, custProp.getProperty(propName))
    })
    SmarkAppConfig(specProp)
  }

  def loadProps(propFile: String): Properties = {
    val file = new File(propFile);
    if (!file.exists || !file.canRead) {
      throw new IllegalArgumentException("Smark configuration file not exsits.")
    }
    import java.io.FileInputStream
    val configProps: Properties = new Properties()
    val input = new FileInputStream(file)
    configProps.load(new InputStreamReader(input, "UTF-8"))
    configProps
  }

  def apply(propFile: String): SmarkAppConfig = {
    val configProps: Properties = loadProps(propFile);
    new SmarkAppConfig(configProps)
  }
}
