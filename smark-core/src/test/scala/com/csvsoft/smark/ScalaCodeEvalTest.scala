package com.csvsoft.smark

import java.io.{File, FileOutputStream}

import com.csvsoft.smark.core.BaseTasklet
import org.apache.commons.io.IOUtils
import org.scalatest.{FlatSpec, Matchers}

import scala.tools.nsc.Settings
import scala.tools.nsc.reporters.ConsoleReporter

class ScalaCodeEvalTest extends FlatSpec with Matchers {

  "toolbox evaluation " should "work " in {

    import scala.tools.reflect.ToolBox
    import scala.reflect.runtime.{universe => ru}
    import ru._

    //  Scala compiler tool box
    val tb = ru.runtimeMirror(
      this.getClass.getClassLoader).mkToolBox()

    val exp = """"2".toInt + 4"""
    val astTree = tb.parse(exp)
    println(ru.showRaw(astTree))
    println(tb.eval(astTree))


    val code = """val name = ctx.getString("test")"""

    val testCode =
      s"""
    |{  import  com.csvsoft.smark.TaskletType
    |
    |   new com.csvsoft.smark.core.BaseTasklet(name="test1",order =1) {
         |
    |      override def executeTask(): Option[Set[String]] = {
    |      $code
         |        None
         |      }
         |
 |      override def getTaskletType(): TaskletType = TaskletType.CODE
         |    }
         }
       """.stripMargin

    val taskletTree = tb.parse(testCode);

    val aTasklet = tb.eval(taskletTree).asInstanceOf[BaseTasklet]
    aTasklet.executeTask()
    println(aTasklet.getClass.getName)

  }

  "scala compiler" should "work" in {
    import scala.tools.nsc.{Global}
    val settings = new Settings
    settings.usejavacp.value = true
    settings.deprecation.value = true
    settings.outdir.value ="/tmp/scalacompiler"


    val reporter = new ConsoleReporter(settings)
    val global = new Global(settings, reporter)

    //val res = global.newUnitParser("class C(val name:String)  {}").parse()
    //println(res)

    val code = """val x= ctx.getString("test1")"""
    val testCode =
      s"""
         | package com.csvsoft.adhoc
         |  import  com.csvsoft.smark.TaskletType
         |  import com.csvsoft.smark.core.BaseTasklet
         |
    |      class MyTasklet(name:String,order:Int) extends BaseTasklet(name,order) {
         |
    |      override def executeTask(): Option[Set[String]] = {
    |           println("Executing my task:")
         |      $code
         |        None
         |      }
         |
         |      override def getTaskletType(): TaskletType = TaskletType.CODE
         |    }

       """.stripMargin

    val generatedFile = "/tmp/TaskTasklet.scala";

    IOUtils.write(testCode,new FileOutputStream(generatedFile))
    val run = (new global.Run)
    run.compile(List(generatedFile))
    if(reporter.hasErrors){
      println("Errors found:")
      reporter.printSummary
    }else{
      println("Compiled")
    }

    val classLoader = new java.net.URLClassLoader(
      Array(new File(settings.outdir.value ).toURI.toURL),  // Using current directory.
      this.getClass.getClassLoader)

    val clazz = classLoader.loadClass("com.csvsoft.adhoc.MyTasklet") // load class
    val newInstance = clazz.getDeclaredConstructor(classOf[String],classOf[Int]).newInstance("name",Integer.valueOf(1))
    val tasklet = newInstance.asInstanceOf[BaseTasklet]
    tasklet.executeTask()
  }

}
