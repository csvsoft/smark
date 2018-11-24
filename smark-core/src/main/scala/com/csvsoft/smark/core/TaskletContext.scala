package com.csvsoft.smark.core

import org.apache.commons.lang.StringEscapeUtils

import scala.collection.JavaConverters._
import scala.collection.mutable

case class TaskVar[T](clazz: Class[T], value: T) extends Serializable {

}

object TaskVar {
  final val DATA_TYPE_FLOAT = "float"
  final val DATA_TYPE_BOOLEAN = "boolean"
  final val DATA_TYPE_INTEGER = "int"
  final val DATA_TYPE_LONG = "long"
  final val DATA_TYPE_STRING = "string"
  final val DATA_TYPE_DOUBLE = "double"
  final val DATA_TYPE_DATE = "date"
  final val DATA_TYPE_TIMESTAMP = "timestamp"
  final val DATA_TYPE_BIG_DECIMAL = "bigDecimal"
  final val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS"
  final val DATA_TYPES = Array(DATA_TYPE_STRING, DATA_TYPE_INTEGER, DATA_TYPE_LONG, DATA_TYPE_FLOAT, DATA_TYPE_DOUBLE, DATA_TYPE_BOOLEAN
    , DATA_TYPE_DATE, DATA_TYPE_TIMESTAMP)
}

class TaskletContext(val appConfig: SmarkAppConfig) extends Serializable {
  var taskVarMap: mutable.Map[String, TaskVar[_]] = new mutable.HashMap()
  //appConfig.configProps.
  addAppConfig()
  //def addTak

  private def addAppConfig(): Unit = {
    val propNames = appConfig.configProps.stringPropertyNames().iterator()
    while (propNames.hasNext) {
      val propName = propNames.next().replace(".", "_")
      this.addTaskVar(s"appConfig_$propName", appConfig.getConfig(propName))
    }

  }

  def addTaskVar[T](name: String, value: T): Unit = {
    if (Option(value).isEmpty)
      return
    taskVarMap += name -> TaskVar(value.getClass.asInstanceOf[Class[T]], value)
  }

  def getTaskVar[T](name: String, clazz: Class[T]): Option[T] = {
    val taskVar = taskVarMap.get(name)
    taskVar.map(tv => tv.value.asInstanceOf[T])
  }

  def getSQLString(name: String): String = {
    val optionStr = this.getTaskVar(name, classOf[String])
    optionStr match {
      case Some(x) => StringEscapeUtils.escapeSql(x)
      case _ => ""
    }
  }

  def getVarNames(): List[String] = {
    this.taskVarMap.keySet.toList
  }

  def getInt(name: String): Option[Int] = {
    this.getTaskVar(name, classOf[Int])
  }

  def getString(name: String): Option[String] = {
    this.getTaskVar(name, classOf[String])
  }

  def getVarMap():java.util.Map[String,Any]= {

    this.taskVarMap.map(keyValue => (keyValue._1, keyValue._2.value)).toMap
  }.asJava

}
