package com.csvsoft.smark

import java.io.File
import java.net.URL

import com.csvsoft.smark.config.{SmarkAppSpec, SmarkTaskSQLSpec}
import com.csvsoft.smark.core.SmarkAppRunner
import com.csvsoft.smark.core.util.XmlUtils
import com.csvsoft.smark.spark.{IMySparkSession, ISparkSessionWrapper, SparkSessionWrapper}
import org.apache.spark.sql.SparkSession
import org.scalatest.{FlatSpec, Matchers}

class TestSmarkAppStandaloneClassloader extends FlatSpec with Matchers {

  "ClassLoader " should "work " in {
    test();
  }

  def test(): Unit = {
    val baseDir = "/Users/zrq/workspace/vaddin/smark-northwind"
    val specXML = s"$baseDir/src/main/resources/NorthWind.xml"
    val appProp = s"$baseDir/src/main/resources/NorthWind.properties"
    val targetLib = s"$baseDir/target/lib"
    val targetClasses = s"$baseDir/target/classes"

    val libFile = new File(targetLib)
    val classPath = new File(targetClasses) :: libFile.listFiles().toList
    val classPathArray =classPath.map(_.toURI.toURL).toArray

    import org.xeustechnologies.jcl.JarClassLoader
    import org.xeustechnologies.jcl.JclObjectFactory


    val classLoader = new JarClassLoader(ClassLoader.getSystemClassLoader.getParent)
    classPathArray.foreach(classLoader.add(_))
    val factory = JclObjectFactory.getInstance

    val appSpec = XmlUtils.toObject(specXML, classOf[SmarkAppSpec])
    import scala.collection.JavaConverters._
    appSpec.getSmarkTasks.asScala.map(taskSpec => {
      classLoader.loadClass(s"${appSpec.getPackageName}.${taskSpec.getClassName}")
      taskSpec
    })

    Thread.currentThread().setContextClassLoader(classLoader)
    val sparkSessionObj = factory.create(classLoader, "com.csvsoft.smark.northwind.MySparkSession")


    import org.xeustechnologies.jcl.JclUtils
    val sparkSessionWrapper = JclUtils.cast(sparkSessionObj, classOf[IMySparkSession])
    val sparkSession = sparkSessionWrapper.getSparkSession;

     SmarkAppRunner.run(sparkSessionWrapper.getSparkSession,-1L,-1,-1,appSpec,appProp,Some(classLoader))

  }

  def addNewClassPath(classPath:String):Unit ={
    import java.lang.reflect.Method
    val cl = ClassLoader.getSystemClassLoader
    val clazz = cl.getClass

    // Get the protected addURL method from the parent URLClassLoader class
    val method:Method = clazz.getSuperclass.getDeclaredMethod("addURL", classOf[URL])


    // Run projected addURL method to add JAR to classpath
    method.setAccessible(true)
    method.invoke(cl, new File(classPath).toURI.toURL)
  }

}
