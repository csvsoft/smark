package com.csvsoft.smark.core
import com.csvsoft.smark.core.entity.SQLView
import org.apache.logging.log4j.scala.Logging
import org.apache.logging.log4j.Level

class DefaultAuditLogger extends TaskletAuditLogger with Logging{
  override def logStart(tasklet: Tasklet): Unit = {
    logger.info(s"task: ${tasklet.getName()} started.")
  }

  override def logBeforeContext(tasklet: Tasklet): Unit = {
    logger.info(s"task: ${tasklet.getName()}  log before context")
  }

  override def logAfterContext(tasklet: Tasklet): Unit = {
    logger.info(s"task: ${tasklet.getName()}  log after context")
  }

  override def logSQLCode(tasklet: Tasklet): Unit = {
    tasklet match {
      case sqlTasklet:DefaultSQLTasklet => {
        sqlTasklet.sqlViews.filter(_.isInstanceOf[SQLView]).map(_.asInstanceOf[SQLView])
          .foreach( (sv:SQLView) => logger.info(s"task: ${tasklet.getName()} view: ${sv.viewName} sql: ${sv.sql}."))
      }
      case _=>
    }

  }


  override def logEnd(tasklet: Tasklet): Unit ={
    logger.info(s"task: ${tasklet.getName()} completed.")
  }



}
