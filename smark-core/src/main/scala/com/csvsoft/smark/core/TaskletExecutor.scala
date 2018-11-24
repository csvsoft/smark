package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import java.time.LocalDateTime

import com.csvsoft.smark.core.entity.{RunInfo, SQLView, TaskletRunResult}
import com.csvsoft.smark.core.util.TaskletContextSerializer
import org.apache.spark.sql.SparkSession

trait TaskletExecutor {
  val sparkSession: SparkSession
  val ctx: TaskletContext
  val runId: Long
  val auditLogger: TaskletAuditLogger

  def execute(tasklet: Tasklet,upTo:Int = -1): TaskletRunResult = {
    val startTime = LocalDateTime.now
    auditLogger.logStart(tasklet)
    auditLogger.logBeforeContext(tasklet)
    tasklet.setSparkSession(sparkSession)
    tasklet.setTaskContext(ctx)
    tasklet.preTask()

    if(ctx.appConfig.isDebugMode()){
      TaskletContextSerializer.serialize(ctx,tasklet.getName(),tasklet.getOrder().toString)

    }

    if (tasklet.getTaskletType() == TaskletType.SQL) {
      auditLogger.logSQLCode(tasklet)
    }
    val newViews = tasklet.executeTask(upTo)
    tasklet.postTask()

    auditLogger.logAfterContext(tasklet)
    auditLogger.logEnd(tasklet)
    TaskletRunResult(RunInfo(runId, startTime, Some(LocalDateTime.now)), newViews)

  }

}
