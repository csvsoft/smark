package com.csvsoft.smark

import java.util.Properties

import com.csvsoft.smark.core.{SmarkAppConfig, TaskletContext}
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag

class TaskletContextTest extends FlatSpec with Matchers{

  "Add var " should "work " in {

    val prop = new Properties()
    prop.setProperty(SmarkAppConfig.DEBUG_MODE,"true")
    prop.setProperty(SmarkAppConfig.DIR_WORK,"/tmp/readcsv")
    val smarkAppConfig = new SmarkAppConfig(prop)
    val ctx = new TaskletContext(smarkAppConfig)

    ctx.addTaskVar("testString","stringValue")
    val varReturned = ctx.getTaskVar("testString",classOf[String])
    varReturned shouldBe Some("stringValue")
  }

}
