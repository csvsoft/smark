package com.csvsoft.smark.core.builder

import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXBContext

import com.csvsoft.smark.config._
import com.csvsoft.smark.core.util.XmlUtils
import com.csvsoft.smark.core._
import com.csvsoft.smark.util.TemplateUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.scala.Logging

import scala.collection.JavaConversions._


object SmarkAppBuilder extends Logging {

  val specTaskLetMapping: Map[Class[_], Class[_]] = Map(
    classOf[SmarkTaskReadCSVSpec] -> classOf[DefaultReadCSVTasklet]
    , classOf[SmarkTaskSaveCSVSpec] -> classOf[DefaultSaveCSVTasklet]

    , classOf[SmarkTaskReadJDBCSpec] -> classOf[ReadJDBCTasklet]
    , classOf[SmarkTaskSaveJDBCSpec] -> classOf[SaveJDBCTasklet]
    , classOf[SmarkTaskSQLSpec] -> classOf[DefaultSQLTasklet]
    , classOf[SmarkTaskCodeSpec] -> classOf[DefaultCodeTasklet]


  )


  def generateApp(specFileName: String): Unit = {
    val smarkAppSpec = XmlUtils.toObject(specFileName, classOf[SmarkAppSpec])
    //   val contextMap = Map("appSpec" -> smarkAppSpec)
    //smarkAppSpec.setCodeOutRootDir("/Users/zrq/workspace/vaddin/smark-example")
    // TemplateUtils.genFile("tasklet-templates/smarkapp.vm", "/tmp/northwind/test.scala", contextMap)
    generateApp(smarkAppSpec)

  }

  def generateApp(appSpec: SmarkAppSpec): Unit = {
    val outDir = appSpec.getCodeOutRootDir;
    val packageDir = appSpec.getPackageName.replace('.', '/')
    val scalaOutDir = s"$outDir/src/main/scala/$packageDir"
    val scalaOutGeneratedDir = s"$scalaOutDir/generated"

    // dirs
    mkDirs(scalaOutGeneratedDir)
    mkDirs(s"$outDir/src/main/java")
    mkDirs(s"$outDir/src/main/resources")
    mkDirs(s"$outDir/src/test/java")
    mkDirs(s"$outDir/src/test/scala")
    mkDirs(s"$outDir/src/test/resources")

    makePOM(appSpec)
    makeLog4j(appSpec)

    // AppSpec xml
    val specXmlStream = new FileOutputStream(s"$outDir/src/main/resources/${appSpec.getName}.xml")
    XmlUtils.objectToXml(appSpec, classOf[SmarkAppSpec], specXmlStream)
    val appPropStream = new FileOutputStream(s"$outDir/src/main/resources/${appSpec.getName}.properties")
    writeToFile("", s"$outDir/src/main/resources/${appSpec.getName}.properties", false)

    //TestApplication class
    val contextMap = Map("appSpec" -> appSpec)
    TemplateUtils.genFile("smark_app.vm", s"$scalaOutDir/app/Test${appSpec.getClassName}.scala", contextMap)

    // Application test class
    TemplateUtils.genFile("smark_app_test.vm", s"$outDir/src/test/java/${packageDir}/app/${appSpec.getClassName}Test.java", contextMap)

    //Application class
    TemplateUtils.genFile("smark_app_final.vm", s"$scalaOutDir/app/${appSpec.getClassName}.scala", contextMap, false)

    //TaskLet class
    import scala.collection.JavaConverters._
    if (Option(appSpec.getSmarkTasks).isDefined) {
      appSpec.getSmarkTasks.asScala.map(taskSpec => {
        val (baseCode, taskCode) = getTaskletCode(taskSpec, appSpec)
        writeToFile(baseCode, s"$scalaOutGeneratedDir/Base${taskSpec.getClassName}.scala")
        writeToFile(taskCode, s"$scalaOutDir/${taskSpec.getClassName}.scala", overWrite = false)
      })
    }
  }

  private def makePOM(appSpec: SmarkAppSpec): Unit = {
    val outDir = appSpec.getCodeOutRootDir
    val outPomFile = s"$outDir/pom.xml"
    //if (!new File(outPomFile).exists()) {
    val contextMap = Map("appSpec" -> appSpec)
    TemplateUtils.genFile("pom.xml", s"$outDir/pom.xml", contextMap)
    //}
  }

  private def makeLog4j(appSpec: SmarkAppSpec): Unit = {
    val outDir = appSpec.getCodeOutRootDir
    val outLog4j2XMLFile = s"$outDir/log4j2.xml"
    val outLog4jPropertiesFile = s"$outDir/log4j.properties"
    //if (!new File(outPomFile).exists()) {
    val contextMap = Map("appSpec" -> appSpec)
    TemplateUtils.genFile("log4j2_template.xml", outLog4j2XMLFile, contextMap,false)
    TemplateUtils.genFile("log4j.properties", outLog4jPropertiesFile, contextMap,false)
    //}
  }
  private def mkDirs(dirName: String): Unit = {
    val dir = new File(dirName)
    if (!dir.exists()) {
      dir.mkdirs()
    }
  }

  private def writeToFile(str: String, fileName: String, overWrite: Boolean = true): Unit = {
    val file = new File(fileName)
    if (file.exists() && !overWrite) {
      return
    }
    val baseFileOut = new FileOutputStream(fileName)
    IOUtils.write(str, baseFileOut, StandardCharsets.UTF_8)
    IOUtils.closeQuietly(baseFileOut)
  }

  private def getTaskletCode(taskSpec: SmarkTaskSpec, appSpec: SmarkAppSpec): (String, String) = {

    val specClassName = taskSpec.getClass.getName
    val specClassSimpleName = taskSpec.getClass.getSimpleName
    val taskletClassOpt = specTaskLetMapping.get(taskSpec.getClass)
    require(taskletClassOpt.isDefined, s"No tasklet is defined for task spec:$specClassName")
    val taskletClassName = taskletClassOpt.get.getName
    val taskletClassSimpleName = taskletClassOpt.get.getSimpleName
    logger.info(s"Generating class:${appSpec.getPackageName}.${taskSpec.getClassName}")
    val templateBase =
      s"""
         |package ${appSpec.getPackageName}.generated
         |import $specClassName
         |import $taskletClassName
         | /**
         | * Warining: Generated code, manual modifications will be overridden upon.
         | */
         |class Base${taskSpec.getClassName}(taskSpec:$specClassSimpleName) extends $taskletClassSimpleName(taskSpec){
         |}
      """.stripMargin

    val desc = Option(taskSpec.getDesc).getOrElse("")
    val templateTasklet =
      s"""
         |package ${appSpec.getPackageName}
         |import $specClassName
         |import ${appSpec.getPackageName}.generated.Base${taskSpec.getClassName}
         | /**
         | * ${desc}
         | */
         |class ${taskSpec.getClassName}(taskSpec:$specClassSimpleName) extends Base${taskSpec.getClassName}(taskSpec){
         |}
      """.stripMargin
    return (templateBase, templateTasklet)
  }

  def buildSmarkAppSpec(specFileName: String): SmarkAppSpec = {

    val file = new File(specFileName)
    val jaxbContext = JAXBContext.newInstance(classOf[SmarkAppSpec])

    val jaxbUnmarshaller = jaxbContext.createUnmarshaller
    val smarkApp = jaxbUnmarshaller.unmarshal(file).asInstanceOf[SmarkAppSpec]
    smarkApp
  }

  def buildSmarkApp(smarkAppSpec: SmarkAppSpec): Unit = {
    //val smarkApp = new SmarkApp();

    val smarkAppConfig = SmarkAppConfig(smarkAppSpec.getConfigProps)

    val taskletList = smarkAppSpec.getSmarkTasks.toList.map({ (sm: SmarkTaskSpec) =>
      sm match {
        case c: SmarkTaskCodeSpec =>
      }

    })

    // val smarkApp = SmarkApp(smarkAppConfig,)

  }
}
