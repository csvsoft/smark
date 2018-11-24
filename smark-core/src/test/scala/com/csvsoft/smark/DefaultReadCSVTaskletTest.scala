package com.csvsoft.smark

import java.util.Properties

import com.csvsoft.smark.core._
import com.csvsoft.smark.core.entity.CSVConfig
import org.scalatest.{FlatSpec, Matchers}

class DefaultReadCSVTaskletTest extends FlatSpec with Matchers {

  def fixture =
    new {
      val sparkSession = SparkSessionFactory.getSparkSession("testcsv")
      val prop = new Properties()
      prop.setProperty(SmarkAppConfig.DEBUG_MODE,"true")
      prop.setProperty(SmarkAppConfig.DIR_WORK,"/tmp/readcsv")
      val smarkAppConfig = new SmarkAppConfig(prop)
      val ctx = new TaskletContext(smarkAppConfig)

      ctx.addTaskVar("yearmade","1972")
      val auditLogger = new DefaultAuditLogger()
      val runId = 100L
      val taskExecutor = new DefaultTaskletExecutor(sparkSession,ctx,runId,auditLogger)
      val smarkAppExecutor = new DefaultSmarkAppExecutor()
    }
  "A CSV tasklet" should "read csv as expected" in {
    val f= fixture
    val fileName = "src/test/resources/cars.csv"
    val csvConfig = new CSVConfig(header = "true")
    val csvTasklet = new DefaultReadCSVTasklet(name = "testReadCSV",order = 1,fileName= fileName, viewName = "cars", csvOpitons = csvConfig.toMap())

    val sqlYear1972 = """select * from cars where year = '${ctx.getSQLString("yearmade")}'"""
    val sqlTasklet = new DefaultSQLTasklet(name = "testSQL",order = 2,sql = sqlYear1972,view = "newCars")
    f.smarkAppExecutor.execute(f.taskExecutor,f.smarkAppConfig,List(csvTasklet,sqlTasklet))
    /*
    val views:Option[Set[String]] = csvTasklet.executeTask()
    val dfCars = f.sparkSession.sqlContext.sql("select * from cars")
    dfCars.printSchema()
    views shouldBe (Some(Set("cars")))
    */
  }
}
