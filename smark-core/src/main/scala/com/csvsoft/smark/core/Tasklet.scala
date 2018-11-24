
package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import org.apache.spark.sql.SparkSession

abstract class  Tasklet{

   protected var ctx:TaskletContext = _
   protected var sparkSession:SparkSession = _

  def setTaskContext(taskletContext:TaskletContext):Unit = this.ctx =taskletContext
  def setSparkSession(sparkSession:SparkSession):Unit = this.sparkSession = sparkSession

  def getTaskletContext():TaskletContext = this.ctx
  def getName():String
  def getDesc():String
  def getOrder():Int
  def getTaskletType():TaskletType
  def preTask():Boolean
  def executeTask(upTo:Int = -1):Option[Set[String]]
  def postTask():Boolean
  def isSavePoint():Boolean = false
}