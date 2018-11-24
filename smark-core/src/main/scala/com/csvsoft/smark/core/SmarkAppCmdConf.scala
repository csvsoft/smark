package com.csvsoft.smark.core

import org.rogach.scallop.ScallopConf
import org.rogach.scallop._
class SmarkAppCmdConf(arguments: Seq[String],appName:String, appDesc:String) extends ScallopConf(arguments) {
  version(s"$appName")
  banner(s"""Usage: $appName [OPTION]
           |$appDesc
           |Options:
           |""".stripMargin)
  //footer("\nFor all other tricks, consult the documentation!")
  val runId = opt[Long]("runId", default = Some(-1L), descr = "An integer represents the run, default to -1", short = 'r')
  val configProp = opt[String]("configProp",short = 'c', descr = "An optional Java properties file provides additional configuration for the application")
  verify()

  override def verify(): Unit = {
    super.verify()
    if (configProp.supplied) {
      val confFile = configProp.apply()
      val file = new java.io.File(confFile)
      if (!file.exists()) {
        throw new RuntimeException(s"File not found:$confFile")
      }
    }
  }
}
