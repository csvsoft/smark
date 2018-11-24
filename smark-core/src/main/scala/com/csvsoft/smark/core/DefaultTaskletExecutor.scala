package com.csvsoft.smark.core

import org.apache.spark.sql.SparkSession

class DefaultTaskletExecutor(val sparkSession:SparkSession,val ctx:TaskletContext,val  runId:Long, val auditLogger:TaskletAuditLogger) extends TaskletExecutor {

  import org.apache.spark.sql.SparkSession
  //override  val auditLogger: TaskletAuditLogger = auditLogger
}
