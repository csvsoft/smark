package com.csvsoft.smark.core.builder

import java.io.{File, OutputStreamWriter, StringWriter, Writer}

import com.csvsoft.smark.config.SmarkAppSpec
import org.apache.maven.shared.invoker._
import java.util.Collections

import org.apache.commons.lang.SystemUtils
import org.apache.logging.log4j.scala.Logging
import org.apache.maven.shared.invoker.InvocationOutputHandler

import scala.util.{Failure, Success, Try}


object SmarkAppMavenInvoker extends Logging {

  val invoker = new DefaultInvoker

  /*
  def getRepoLocalDir(smarkAppSpec: SmarkAppSpec):Unit ={
    val getLocalRepo = "help:evaluate -Dexpression=settings.localRepository"
    invokeApp(smarkAppSpec,getLocalRepo)
  }
  def getDependencyList(smarkAppSpec: SmarkAppSpec):Unit ={
     val dependencyList = "dependency:list"
    invokeApp(smarkAppSpec,dependencyList)
  }


*/


  def getMavenHome(): String = {
    import sys.process._

    val cmd = if(SystemUtils.IS_OS_WINDOWS) "mvn.cmd" else "mvn"

    try {
      val retCode: Int = s"$cmd -version" !
    }catch{
        case e:Exception =>throw new RuntimeException("Maven is not installed correctly, make sure maven is in the path.",e)
    }
    val result: String = s"$cmd -version" !!
    val homeLine = result.split("\n").filter(s => s.startsWith("Maven home"))
    homeLine(0).split(":")(1).trim

  }

  def invokeAppGoal(appSpec: SmarkAppSpec, goal: String): Try[String] = {
    logger.info(s"Invoking maven with goal:$goal")
    class MyStringWriter extends StringWriter {
      override def write(str: String): Unit = {
        super.write(str)
        logger.info(str)
      }
    }

    val regWriter = new MyStringWriter();
    val errWriter = new MyStringWriter();

    val success = SmarkAppMavenInvoker.invokeApp(appSpec, goal, regWriter, errWriter)
    logger.info(s"Invoking maven with goal:$goal completed.")
    val regMsg = regWriter.toString
    val errMsg = errWriter.toString
    if (success) Success(regMsg) else Failure(new RuntimeException(errMsg))

  }

  def invokeApp(smarkAppSpec: SmarkAppSpec, goal: String, normalOut: Writer, errorOut: Writer): Boolean = {

    val request = new DefaultInvocationRequest
    request.setPomFile(new File(s"${smarkAppSpec.getCodeOutRootDir}/pom.xml"))

    val outHandler = new InvocationOutputHandler() {
      override def consumeLine(line: String): Unit = {
        normalOut.write(line + "\n");
        //mavenOutput.append(line).append(System.lineSeparator)
      }
    }
    val errHandler = new InvocationOutputHandler() {
      override def consumeLine(line: String): Unit = {
        errorOut.write(line + "\n");
      }
    }

    request.setGoals(Collections.singletonList(goal))
    request.setOutputHandler(outHandler)
    request.setErrorHandler(errHandler)
    val mavenHome = getMavenHome()
    invoker.setMavenHome(new File(mavenHome))
    try {
      val mavenResult = invoker.execute(request)
      if (mavenResult.getExitCode != 0) {
        return false;
      }
      return true
    } catch {
      case e: MavenInvocationException =>
        throw e
    }
  }
}
