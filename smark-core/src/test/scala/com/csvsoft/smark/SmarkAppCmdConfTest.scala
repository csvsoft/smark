package com.csvsoft.smark

import java.io.File

import com.csvsoft.smark.config.SmarkAppSpec
import com.csvsoft.smark.core.SmarkAppCmdConf
import com.csvsoft.smark.core.builder.SmarkAppBuilder
import com.csvsoft.smark.core.util.XmlUtils
import org.scalatest.{FlatSpec, Matchers}
import org.rogach.scallop._
class SmarkAppCmdConfTest extends FlatSpec with Matchers {

  "Arguments parsing" should "work " in {
    val confFileName = "/tmp/test.properties"
    val file = new File(confFileName)
    if(!file.exists()){
      file.createNewFile()
    }

    val appName = "MyTest App"
    val appDesc = "A test application that mimics description"
    val conf = new SmarkAppCmdConf(List("--runId" ,"10","--configProp",confFileName),appName,appDesc)
    conf.runId.toOption shouldBe(Some(10))
    conf.configProp.toOption shouldBe (Some(confFileName))

    val conf2 = new SmarkAppCmdConf(List("-r" ,"10","-c",confFileName),appName,appDesc)
    conf2.runId.toOption shouldBe(Some(10))
    conf2.configProp.toOption shouldBe (Some(confFileName))


    //default value
    val conf3 = new SmarkAppCmdConf(List("-c",confFileName),appName,appDesc)
    conf3.runId.toOption shouldBe(Some(-1))
    conf3.configProp.toOption shouldBe (Some(confFileName))
    println(conf3.printedName)

    val conf4 = new SmarkAppCmdConf(List("--help"),appName,appDesc)

  }

}
