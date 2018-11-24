package com.csvsoft.smark.core.builder

import java.io.File

import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter
import scala.tools.nsc.{Global}

class SmarkClassCompiler {

  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true
  val reporter = new ConsoleReporter(settings)
  val global = new Global(settings, reporter)
  val run = (new global.Run)


  def compileClass(classNames:List[String],outputPath:String):Unit ={

    settings.outdir.value = outputPath
    run.compile(classNames)  // invoke compiler. it creates Test.class.

   val classLoader = new java.net.URLClassLoader(
     Array(new File(settings.outdir.value ).toURI.toURL),  // Using current directory.
     this.getClass.getClassLoader)

   val clazz = classLoader.loadClass("com.csvsoft.test.Hello") // load class
 }
}
