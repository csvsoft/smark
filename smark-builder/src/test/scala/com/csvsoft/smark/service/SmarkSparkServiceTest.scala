package com.csvsoft.smark.service

import java.io.{ByteArrayInputStream, File, ObjectInputStream}
import java.net.URL

import com.csvsoft.smark.config.SmarkAppSpec
import com.csvsoft.smark.core.util.{SmarkAppSpecSerializer, XmlUtils}
import com.csvsoft.smark.entity.SparkQueryResult
import com.csvsoft.smark.sevice.{ISmarkSparkService, SparkCatalogProvider}
import com.csvsoft.smark.util.SerializationUtils
import org.scalatest.{FlatSpec, Matchers}
import org.xeustechnologies.jcl.{JarClassLoader, JclObjectFactory}
import org.xeustechnologies.jcl.JclUtils


class SmarkSparkServiceTest extends FlatSpec with Matchers {

  "ClassLoader " should "work " in {
   // test();
  }

  def test(): Unit = {
    val baseDir = "/Users/zrq/workspace/vaddin/smark-northwind"
    val specXML = s"$baseDir/src/main/resources/NorthWind.xml"
    val appProp = s"$baseDir/src/main/resources/NorthWind.properties"
    val targetLib = s"$baseDir/target/lib"
    val targetClasses = s"$baseDir/target/classes"
    val appSpec = XmlUtils.toObject(specXML, classOf[SmarkAppSpec])

    val libFile = new File(targetLib)
    val classPath = new File(targetClasses) :: libFile.listFiles().toList
    val classPathArray = classPath.map(_.toURI.toURL).toArray
    val classLoader = getNetClassLoader(classPathArray)

    //This is neceesary due to the hadoop library class loading
    Thread.currentThread().setContextClassLoader(classLoader)
    val clazz = classLoader.loadClass("com.csvsoft.smark.sevice.SmarkSparkService")
    val serviceObj = clazz.newInstance()


    val smarkSparkService = JclUtils.cast(serviceObj, classOf[ISmarkSparkService])

    try {
      val specBytes = SmarkAppSpecSerializer.serializeSmarkAppSpec(appSpec)
      val sparkCatalogProviderBytes = smarkSparkService executeSpecRemote(-1L, "5", specBytes, appProp)
      val sparkCatalogProvider = SerializationUtils.deserialize(sparkCatalogProviderBytes,classOf[SparkCatalogProvider])

      val sqlResult = smarkSparkService.executeSQLRemote("select * from category", 10)
      val sparkQueryResult = SerializationUtils.deserialize(sqlResult,classOf[SparkQueryResult])
      //print("x")
    }catch{
      case e:Exception=>e.printStackTrace()
    }

  }

  def getJarClassLoader(classPathArray: Array[URL]): ClassLoader = {
    val classLoader = new JarClassLoader(ClassLoader.getSystemClassLoader.getParent)
    classPathArray.foreach(classLoader.add(_))
    classLoader
  }

  def getNetClassLoader(classPathArray: Array[URL]): ClassLoader = {
    val classLoader = new java.net.URLClassLoader(
      classPathArray, // Using current directory.
      ClassLoader.getSystemClassLoader.getParent) {
      override def findClass(name: String): Class[_] = {
        if (name == "org.apache.hadoop.security.JniBasedUnixGroupsMappingWithFallback") {
          println(s"Loading class:$name")
        }

        super.findClass(name)

      }



    }
    classLoader
  }

  def addNewClassPath(classPath: String): Unit = {
    import java.lang.reflect.Method
    val cl = ClassLoader.getSystemClassLoader
    val clazz = cl.getClass

    // Get the protected addURL method from the parent URLClassLoader class
    val method: Method = clazz.getSuperclass.getDeclaredMethod("addURL", classOf[URL])


    // Run projected addURL method to add JAR to classpath
    method.setAccessible(true)
    method.invoke(cl, new File(classPath).toURI.toURL)
  }

}
