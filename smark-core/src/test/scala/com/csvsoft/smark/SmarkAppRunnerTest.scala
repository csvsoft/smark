package com.csvsoft.smark

import com.csvsoft.smark.config.{SmarkAppSpec, SmarkTaskSQLSpec}
import com.csvsoft.smark.core.SmarkAppRunner
import com.csvsoft.smark.core.util.XmlUtils
import org.scalatest.{FlatSpec, Matchers}

class SmarkAppRunnerTest extends FlatSpec with Matchers {

  "Refresh session" should "work " in {
    val northWind = "/tmp/smark_app_repo/testUser1/NorthWind.xml"
    val appSpec = XmlUtils.toObject(northWind, classOf[SmarkAppSpec])
    //SmarkAppBuilder.generateApp(appSpec)
    val sparkSession = SparkSessionFactory.getSparkSession("test session refresh")

    val taskSpec = appSpec.getTaskSpecByName("LoadOrders");
    SmarkAppRunner.refreshSession(sparkSession,appSpec.getDebugRunId,taskSpec,None,appSpec)
    sparkSession.catalog.listTables().collect().size shouldBe(3)

    val joinProductCategory = appSpec.getTaskSpecByName("JoinProductCategory");
    val productCategorySQLView =joinProductCategory.asInstanceOf[SmarkTaskSQLSpec].getSQLViewPairByName("product_category")
    SmarkAppRunner.refreshSession(sparkSession,appSpec.getDebugRunId,joinProductCategory,Option(productCategorySQLView),appSpec)
    sparkSession.catalog.listTables().collect().size shouldBe(5)

    //totalOrders
    val totalOrders =joinProductCategory.asInstanceOf[SmarkTaskSQLSpec].getSQLVarByName("totalOrders")
    SmarkAppRunner.refreshSession(sparkSession,appSpec.getDebugRunId,joinProductCategory,Option(totalOrders),appSpec)
    sparkSession.catalog.listTables().collect().map(println(_))

  }

}
