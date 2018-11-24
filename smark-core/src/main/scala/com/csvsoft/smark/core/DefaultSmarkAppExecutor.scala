package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.core.entity.TaskletRunResult

case class TaskletResultPair(tasklet:Tasklet,result:TaskletRunResult)
class  DefaultSmarkAppExecutor extends SmarkAppExecutor {
  def execute(taskletExecutor: TaskletExecutor,smarkApp:SmarkApp,runto:Int ):Unit ={
    execute(taskletExecutor,smarkApp.smarkAppConfig,smarkApp.taskletList,runto)
  }


  override  def execute(taskletExecutor: TaskletExecutor,appConfig:SmarkAppConfig,taskletList:List[Tasklet],runTo:Int = -1,runToSubTask:Int = -1):Unit ={

    val sortedList = getSortedTaskList(taskletList,runTo)
    val taskletRunResultList = sortedList.map((t: Tasklet) => TaskletResultPair(t,taskletExecutor.execute(t,runToSubTask)))
    // save points
    if(!appConfig.isDebugMode()){
      taskletRunResultList.foreach( (pair: TaskletResultPair) =>{
         if(pair.tasklet.isSavePoint()){

         }
      })
    }
  }

  override  def executeUDFs(taskletExecutor: TaskletExecutor,appConfig:SmarkAppConfig,taskletList:List[Tasklet],runTo:Int = -1,runToSubTask:Int = -1):Unit={
    val sortedList = getSortedTaskList(taskletList,runTo)
    sortedList
        .filter(t=>t.getTaskletType() == TaskletType.USER_DEFINE_FUNCTION)
        .map((t: Tasklet) => TaskletResultPair(t,taskletExecutor.execute(t,runToSubTask)))
  }

  private def getSortedTaskList(taskletList:List[Tasklet],runTo:Int):List[Tasklet]={
    def comp(t1:Tasklet,t2:Tasklet):Boolean =t1.getOrder() < t2.getOrder()
     runTo match{
    case x if x == -1 => taskletList
    case _ => taskletList.filter(t=>t.getOrder() <=runTo)
   }
  }


  def getViewsToSave(sortedTaskList:List[TaskletResultPair],savePointTasklet:Tasklet): Set[String] ={
    // Get all the views generated prior to this tasklet
    sortedTaskList.filter(t => t.tasklet.getOrder() <= savePointTasklet.getOrder()).map(p=> p.result.newViews)
      .foldLeft(scala.collection.mutable.Set[String]())((viewSet,viewsOption)=> viewSet ++= viewsOption.getOrElse(Set()))
        .toSet
  }

}
