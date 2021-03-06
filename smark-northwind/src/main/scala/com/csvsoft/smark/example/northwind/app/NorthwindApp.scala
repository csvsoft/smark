package com.csvsoft.smark.example.northwind.app

import java.io._
import java.nio.charset.StandardCharsets
import java.util.Properties

import com.csvsoft.smark.config.SmarkAppSpec
import com.csvsoft.smark.core.{SmarkAppCmdConf, SmarkAppRunner}
import com.csvsoft.smark.core.util.{FileSystemUtils, XmlUtils}
import org.apache.spark.sql.SparkSession


object NorthwindApp  {

  def main(args: Array[String]): Unit = {

    val specStream: InputStream = getClass.getResourceAsStream(s"/NorthWind.xml")
    val appSpec = XmlUtils.toObject(specStream, classOf[SmarkAppSpec])
    val conf = new SmarkAppCmdConf(args, appSpec.getName, appSpec.getDescription)

    //Merge spark configuration files
    val appPropStream: InputStream = getClass.getResourceAsStream(s"/NorthWind.properties")
    import java.io.File
    val propFile = File.createTempFile(s"NorthWind.properties", ".tmp")
    FileSystemUtils.mergeFile(propFile, appPropStream)

    if (conf.configProp.supplied) {
      FileSystemUtils.mergeFile(propFile, new File(conf.configProp.apply()))
    }

    // Get spark master setting
    val propInputReader = new InputStreamReader(new FileInputStream(propFile), StandardCharsets.UTF_8)
    val props = new Properties()
    props.load(propInputReader)
    val sparkMasterConf = props.getProperty("spark.master")
    val sparkMaster = if (Option(sparkMasterConf).isDefined) sparkMasterConf else "local"

    // runId
    val runId = conf.runId.apply()
    val sparkSession = SparkSession.builder().appName(s"NorthWind").master(sparkMaster).getOrCreate()
    SmarkAppRunner.run(sparkSession, runId, -1, -1,appSpec, propFile.getAbsolutePath)
  }


}
