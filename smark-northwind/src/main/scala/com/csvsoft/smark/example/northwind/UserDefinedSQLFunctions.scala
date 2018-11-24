
package com.csvsoft.smark.example.northwind
import com.csvsoft.smark.config.SmarkTaskCodeSpec
import com.csvsoft.smark.example.northwind.generated.BaseUserDefinedSQLFunctions
import org.apache.logging.log4j.scala.Logging
 /**
 * A class that defines functions so that SQL can reference it.
 */
class UserDefinedSQLFunctions(taskSpec:SmarkTaskCodeSpec) extends BaseUserDefinedSQLFunctions(taskSpec) {
  def myUpper(input:String):String ={
    input.toUpperCase()
  }
   override def executeTask(upTo:Int): Option[Set[String]] = {
    sparkSession.udf.register("myUpper", (input: String) => input.toUpperCase)
     sparkSession.udf.register("myLower", (input: String) => input.toLowerCase)
    None
  }
 }
      