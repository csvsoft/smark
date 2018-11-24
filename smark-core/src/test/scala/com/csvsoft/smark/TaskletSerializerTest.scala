package com.csvsoft.smark

import java.sql.Date
import java.util.Properties

import com.csvsoft.smark.core.{SmarkAppConfig, TaskletContext}
import com.csvsoft.smark.core.util.{FileSystemUtils, TaskletContextSerializer}
import org.scalatest.{FlatSpec, Matchers}

class TaskletSerializerTest extends FlatSpec with Matchers {

  "Serialization" should "work " in {
    val prop = new Properties()
    prop.setProperty("app.dir.work","/tmp")
    val smarkAppConfig = new SmarkAppConfig(prop);
    val taskletContext  = new TaskletContext(smarkAppConfig)
    taskletContext.addTaskVar("firstName","My FirstName")
    val today = new Date(new java.util.Date().getTime)

    taskletContext.addTaskVar("today",new Date(new java.util.Date().getTime))
    TaskletContextSerializer.serialize(taskletContext,"testUUId","testName")
    val deserialized = TaskletContextSerializer.deserialize(smarkAppConfig,"testUUId","testName")
    deserialized.get.getTaskVar("firstName",classOf[String]) shouldBe (Some("My FirstName"))
    deserialized.get.getTaskVar("today",classOf[Date]) shouldBe (Some(today))
  }



}
