package com.csvsoft.smark.core

trait SmarkAppExecutor {
  def execute(taskletExecutor: TaskletExecutor,appConfig:SmarkAppConfig,taskletList:List[Tasklet],runTo:Int = -1,runToSubTask:Int = -1):Unit

  def executeUDFs(taskletExecutor: TaskletExecutor,appConfig:SmarkAppConfig,taskletList:List[Tasklet],runTo:Int = -1,runToSubTask:Int = -1):Unit

}
