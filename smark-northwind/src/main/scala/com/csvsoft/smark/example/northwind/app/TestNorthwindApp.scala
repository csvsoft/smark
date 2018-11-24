package com.csvsoft.smark.example.northwind.app

import com.csvsoft.smark.core.SmarkAppRunner
import org.apache.spark.sql.SparkSession
import java.io.{InputStream,FileOutputStream, InputStreamReader, OutputStreamWriter}
import java.nio.charset.StandardCharsets

import com.csvsoft.smark.config.SmarkAppSpec
import com.csvsoft.smark.core.SmarkAppRunner
import com.csvsoft.smark.core.util.XmlUtils
import org.apache.commons.io.IOUtils

object  TestNorthwindApp {

   def main(args:Array[String]):Unit ={
      val specStream:InputStream = getClass.getResourceAsStream(s"/NorthWind.xml")
      val appSpec = XmlUtils.toObject(specStream,classOf[SmarkAppSpec])

      val appPropStream:InputStream = getClass.getResourceAsStream(s"/NorthWind.properties")

      import java.io.File
      val propFile = File.createTempFile(s"NorthWind.properties", ".tmp")
      val inReader = new InputStreamReader(appPropStream,StandardCharsets.UTF_8)
      val outWriter = new OutputStreamWriter(new FileOutputStream(propFile),StandardCharsets.UTF_8)
      // force to debug mode
      outWriter.write("debugMode=true")
      IOUtils.copy(inReader,outWriter)
      IOUtils.closeQuietly(inReader)
      IOUtils.closeQuietly(outWriter)

      val sparkSession = SparkSession.builder().config("spark.driver.host","localhost").appName(s"NorthWind").master("local").getOrCreate()
      SmarkAppRunner.run(sparkSession,-1L,-1,-1,appSpec,propFile.getAbsolutePath)

    }



}
