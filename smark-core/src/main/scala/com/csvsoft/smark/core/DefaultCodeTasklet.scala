package com.csvsoft.smark.core

import com.csvsoft.smark.TaskletType
import com.csvsoft.smark.config.SmarkTaskCodeSpec

class DefaultCodeTasklet(name: String, order: Int, code: String) extends BaseTasklet(name, order) {

  def this( smarkCodeSpec:SmarkTaskCodeSpec){
  this(smarkCodeSpec.getName,smarkCodeSpec.getOrder,smarkCodeSpec.getCode)
  }

  override def getTaskletType(): TaskletType = TaskletType.CODE

  override def executeTask(upTo:Int = -1): Option[Set[String]] = {
   None
  }


}
