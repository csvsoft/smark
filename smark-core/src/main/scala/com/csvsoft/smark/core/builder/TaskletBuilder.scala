package com.csvsoft.smark.core.builder

import java.util.Properties

import com.csvsoft.smark.config._
import com.csvsoft.smark.core._
import com.csvsoft.smark.core.util.{FileSystemUtils, XmlUtils}
import org.apache.logging.log4j.scala.Logging

import scala.collection.JavaConverters._

object TaskletBuilder extends Logging {

  protected def getTaskSpecFromClassPath[T](smarkTaskSpec:Class[T]):T ={
    val specFileName = smarkTaskSpec.getName.replace('.','/')+".xml"
    val absolutePath = FileSystemUtils.getFilePathFromClassPath(specFileName)
    absolutePath match {
      case Some(p) => XmlUtils.toObject(p,smarkTaskSpec)
      case _ => throw new RuntimeException(s"No tasklet spec file found in the classpath:${specFileName}")
    }
  }


  def buildFromSpec(smarkTaskSpec:SmarkTaskSpec,smarkAppSpec:SmarkAppSpec,classLoaderOpt:Option[ClassLoader]=None): Tasklet = {
    val classLoader = if(classLoaderOpt.isDefined) classLoaderOpt.get else TaskletBuilder.getClass.getClassLoader
    //logger.info("myclassLoader:"+classLoader.toString)

    val taskletClaxxName = s"${smarkAppSpec.getPackageName}.${smarkTaskSpec.getClassName()}"
    logger.info(s"Instantiating class:$taskletClaxxName")
    val taskletClass = classLoader.loadClass(taskletClaxxName)

     val taskletInstance = smarkTaskSpec match {
      case smarkTaskReadCSV:SmarkTaskReadCSVSpec => {
      val constructor = taskletClass.getConstructor(classOf[SmarkTaskReadCSVSpec])
        constructor.newInstance(smarkTaskReadCSV)
      }
      case smarkTaskSaveCSV:SmarkTaskSaveCSVSpec => {
        val constructor = taskletClass.getConstructor(classOf[SmarkTaskSaveCSVSpec])
        constructor.newInstance(smarkTaskSaveCSV)
      }
      case smarkTaskSQL:SmarkTaskSQLSpec => {
        val constructor = taskletClass.getConstructor(classOf[SmarkTaskSQLSpec])
        constructor.newInstance(smarkTaskSQL)
      }
      case smarkTaskReadJDBC:SmarkTaskReadJDBCSpec =>{
        val csvOptionsMap = PropertiesBuilder.getMap(smarkTaskReadJDBC.getJdbcOpitons)
        new ReadJDBCTasklet(smarkTaskReadJDBC.getName,smarkTaskReadJDBC.getOrder,smarkTaskReadJDBC.getSql,smarkTaskReadJDBC.getViewName,smarkTaskReadJDBC.getDesc,smarkTaskReadJDBC.getJdbcurl,smarkTaskReadJDBC.getJdbcOptionProps,None)
      }
      case smarkTaskSaveJDBC:SmarkTaskSaveJDBCSpec =>{
        val constructor = taskletClass.getConstructor(classOf[SmarkTaskSQLSpec])
        constructor.newInstance(smarkTaskSaveJDBC)
        }
      case smarkCodeSpec:SmarkTaskCodeSpec =>{
        val constructor = taskletClass.getConstructor(classOf[SmarkTaskCodeSpec])
        constructor.newInstance(smarkCodeSpec)
      }
      case _=> throw new IllegalArgumentException("Unsupported smarktask spec:"+ smarkTaskSpec.getClassName)
    }
    taskletInstance.asInstanceOf[Tasklet]
  }

}
