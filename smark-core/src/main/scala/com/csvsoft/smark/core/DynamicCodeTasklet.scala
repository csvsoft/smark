package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType

class DynamicCodeTasklet(name: String, order: Int, code: String) extends BaseTasklet(name, order) {
  override def getTaskletType(): TaskletType = TaskletType.CODE
  override def executeTask(upTo:Int = -1): Option[Set[String]] = {
   None
  }
}
