
package com.csvsoft.smark.example.northwind
import com.csvsoft.smark.config.SmarkTaskReadCSVSpec
import com.csvsoft.smark.example.northwind.generated.BaseLoadCategory
 /**
 * 
 */
class LoadCategory(taskSpec:SmarkTaskReadCSVSpec) extends BaseLoadCategory(taskSpec){
  override def executeTask(upTo: Int): Option[Set[String]] = {
   logger.info("Starting my loading of category...")
   super.executeTask(upTo)

  }
 }
      