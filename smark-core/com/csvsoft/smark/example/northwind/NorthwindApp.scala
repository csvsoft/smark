package com.csvsoft.smark.example.northwind

import com.csvsoft.smark.core.SmarkAppRunner
import org.apache.spark.sql.SparkSession

object  NorthwindApp {

   def main(args:Array[String]):Unit ={
      val specStream:InputStream = getClass.getResourceAsStream(s"/${appSpec.getName}.xml")
      val appSpec = XmlUtils.toObject(specStream,classOf[SmarkAppSpec])

      val appPropStream:InputStream = getClass.getResourceAsStream(s"/${appSpec.getName}.properties")

      import java.io.File
      val propFile = File.createTempFile(s"/${appSpec.getName}.properties", ".tmp")
      val inReader = new InputStreamReader(appPropStream,StandardCharsets.UTF_8)
      val outWriter = new OutputStreamWriter(new FileOutputStream(propFile),StandardCharsets.UTF_8)
      IOUtils.copy(inReader,outWriter)
      IOUtils.closeQuietly(inReader)
      IOUtils.closeQuietly(outWriter)

      val sparkSession = SparkSession.builder().appName(s"${appSpec.getName}").master("local").getOrCreate()
      SmarkAppRunner.run(sparkSession,100L,-1,appSpec,propFile.getAbsolutePath)

    }
  }


}
