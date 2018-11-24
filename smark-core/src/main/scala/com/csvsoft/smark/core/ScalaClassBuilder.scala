package com.csvsoft.smark.core

import java.io.{ByteArrayOutputStream, File, FileOutputStream}
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap}

import org.apache.commons.io.IOUtils

import scala.collection.{concurrent, mutable}
import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter
import scala.util.{Failure, Success, Try}
import collection.JavaConverters._

class ScalaClassBuilder {
  val classMap :concurrent.Map[String,Class[_]] = new ConcurrentHashMap[String,Class[_]] ().asScala
  def generateClass(className:String, workDir:String, outDir:String,code:String):Try[Class[_]] = {

    val key = s"$className/$code"

    if (classMap.contains(key)){
      return Success(classMap.get(key).get)
    }

    import scala.tools.nsc.{Global}
    val settings = new Settings
    settings.usejavacp.value = true
    settings.deprecation.value = true
    settings.outdir.value = outDir


    val reporter = new ConsoleReporter(settings)
    val global = new Global(settings, reporter)

    val lastDot = className.lastIndexOf(".")
    val pureClassName = if (lastDot > 0)  className.substring(lastDot+1) else className
    val generatedFile = s"$workDir/${pureClassName}.scala"

    IOUtils.write(code,new FileOutputStream(generatedFile))
    val run = (new global.Run)
    run.compile(List(generatedFile))
    if(reporter.hasErrors){
      println("Errors found:")
      val byteout = new ByteArrayOutputStream()
      Console.withOut(byteout) {
        reporter.printSummary
      }
      return Failure(new RuntimeException(s"Failed to compile class:$className"+ new String(byteout.toByteArray)))
    }else{
      println("Compiled")
    }

    val classLoader = new java.net.URLClassLoader(
      Array(new File(settings.outdir.value ).toURI.toURL),  // Using current directory.
      this.getClass.getClassLoader)

    val clazz = classLoader.loadClass(className) // load class
    classMap += key -> clazz
    Success(clazz)
  }

}
