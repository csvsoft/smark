package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType

class UDFCodeTasklet (name: String, order: Int, code: String) extends BaseTasklet(name, order) {
  override def getTaskletType(): TaskletType = TaskletType.USER_DEFINE_FUNCTION
  override def executeTask(upTo:Int = -1): Option[Set[String]] = {
    None
  }
}
