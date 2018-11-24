package com.csvsoft.smark.core


trait TaskletAuditLogger {
   def logStart(tasklet:Tasklet):Unit
  def logBeforeContext(tasklet:Tasklet):Unit
  def logAfterContext(tasklet:Tasklet):Unit
  def logSQLCode(tasklet:Tasklet):Unit
  def logEnd(tasklet:Tasklet):Unit
}
