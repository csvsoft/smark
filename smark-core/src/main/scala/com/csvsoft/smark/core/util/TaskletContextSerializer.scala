package com.csvsoft.smark.core.util

import java.io._

import com.csvsoft.smark.config.SmarkTaskSpec
import com.csvsoft.smark.core.{SmarkAppConfig, TaskletContext}
import org.apache.logging.log4j.scala.Logging

object TaskletContextSerializer extends Logging {

  def serialize(taskletContext: TaskletContext, name: String, order: String) = {
    logger.info(s"Serializing task spec/sql pair context:$order")
    val outFileName = getSerizlizedFileName(taskletContext.appConfig, name, order)

    if (outFileName == None) {
      throw new RuntimeException("taskcontext can not be serialized to an blank file name")
    }
    val fileOutStream = new FileOutputStream(outFileName.get)
    val objOutStream = new ObjectOutputStream(fileOutStream)
    objOutStream.writeObject(taskletContext)
    objOutStream.close();
  }

  def getSerizlizedFileName(appConfig: SmarkAppConfig, name: String, order: String): Option[String] = {
    if (Option(name) == None) {
      return None
    }
    val normalizedName = name.replace(" ", "_");
    val normalizedOrder = order.replace(" ", "_");
    val runId = appConfig.debugRunId;
    val dirName = s"${appConfig.getDirWork()}/_${runId}/taskContext"
    val dir = new File(dirName)
    if (!dir.exists()) {
      dir.mkdirs()
    }
    val outFileName = s"${dirName}/${normalizedName}_${normalizedOrder}_ctx.ser"
    Some(outFileName)
  }

  def deserialize(appConfig: SmarkAppConfig, name: String, order: String): Option[TaskletContext] = {
    logger.info(s"Serializing task spec/sql pair context:$order")
    val outFileName = getSerizlizedFileName(appConfig, name, order)
    if (outFileName == None) {
      return None
    }
    val file = new File(outFileName.get)
    if (!file.exists()) {
      return None
    }
    val fileInputStream = new FileInputStream(outFileName.get)
    val objecInStream = new ObjectInputStream(fileInputStream)
    val taskletContext = objecInStream.readObject().asInstanceOf[TaskletContext]
    objecInStream.close()
    Some(taskletContext)

  }
}
