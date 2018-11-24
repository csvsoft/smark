package com.csvsoft.smark.core

//import java.util

import com.csvsoft.smark.core.util.{FileSystemUtils, XmlUtils}
import com.csvsoft.smark.util.TemplateUtils
import org.apache.commons.lang.StringEscapeUtils
import org.apache.logging.log4j.scala.Logging
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.expressions.Attribute
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.storage.StorageLevel
import org.json4s.DefaultFormats

import scala.collection.JavaConversions.mapAsJavaMap

abstract class BaseTasklet(val name: String, val order: Int, val desc: String="") extends Tasklet with Logging {

  override def getName(): String = name

  override def getDesc(): String = desc

  override def getOrder(): Int = order

  override def preTask(): Boolean = true

  protected def getRunnableSQL(sql: String): String = {
    if (sql.contains("$")) {
      //val mergeMap = mapAsJavaMap(Map("ctx" -> ctx))
      val mergeMap = ctx.getVarMap()
      import scala.collection.JavaConversions._
      // Escape string literals.
      val escapedMap = new java.util.HashMap[String, Any]()
      mergeMap.foreach(kv => {
        val value = kv._2
        val escapedValue = if (Option(value).isDefined && value.isInstanceOf[String]) {
          StringEscapeUtils.escapeSql(value.asInstanceOf[String])
        } else {
          value
        }
        escapedMap.put(kv._1, escapedValue)
      })

      TemplateUtils.merge(sql, escapedMap);
    } else {
      sql
    }
  }
  protected def registerTempView(df: DataFrame, viewName: String, runId: Long = -1): Unit = {
    df.createOrReplaceTempView(viewName);

    // check duplicate columns
    val columnCountTupleList:List[(String,Int)]= df.columns
      .groupBy[String](col=>col)  // group by column names
      .map(keyArrayTuple=>(keyArrayTuple._1,keyArrayTuple._2.size)) // map to columnName, columnName count
      .toList
      .filter(colCountTuple => colCountTuple._2 > 1)  // get duplicate columns
    if(!columnCountTupleList.isEmpty){
      throw new RuntimeException(s"Duplicate columns found in view:$viewName, $columnCountTupleList")
    }

    if (ctx.appConfig.isDebugMode()) {
      // df.persist(StorageLevel.DISK_ONLY_2)
      val dirToSave = s"${ctx.appConfig.getDirWork()}/_${runId}"
      val fs = FileSystemFactory.buildFileSystem(ctx.appConfig)
      FileSystemUtils.createDirSafe(fs, dirToSave)

      var newDF = df
      if(columnCountTupleList.size != 0) {
        val dpColumnList = columnCountTupleList.map(colCountTuple=>colCountTuple._1)
        val noDupCols = df.columns.zipWithIndex.map(tp=> if(dpColumnList.contains(tp._1)) tp._1+ String.valueOf(tp._2) else tp._1)
        newDF = df.toDF(noDupCols:_*)
      }
      newDF.write.mode("overwrite").parquet(s"${dirToSave}/${viewName}.parquet")
      //newDF.write.mode("overwrite").csv(s"${dirToSave}/${viewName}.parquet")

    }
  }
  override def postTask(): Boolean = {
    true
  }
  def getSparkContext(): SparkContext = sparkSession.sparkContext
}

/*
  private def testDF(df:DataFrame, count:Int,spark:SparkSession):String ={
    import org.json4s.DefaultFormats
    import org.json4s.jackson.Serialization.write
    import org.apache.spark.SparkContext
    import org.apache.spark.sql.catalyst.expressions.Attribute
    import org.apache.spark.sql.{DataFrame, Row, SparkSession}
    implicit val formats = DefaultFormats
    case class SQLResult(header:Seq[String],data:Array[Seq[String]]);


    val df = spark.read
      .format("csv")
      .option("header", "true") //reading the headers
      .option("mode", "DROPMALFORMED")
      .load("/Users/zrq/workspace/vaddin/smark-core/src/test/resources/cars.csv")



    def mapRow(row:Row,attrSeq:Seq[Attribute]):Seq[String]={
      val strList:Seq[String] = attrSeq.map(attr=>{
         val colValue = Option(row.getAs[Any](attr.name));
         colValue match{
           case None => "null"
           case Some(x) => x.toString
         }
       })
      strList
    }
    def getSQLResult(sql:String):String= {
      try {
        val df = spark.sql(sql);
        df.createOrReplaceTempView("$viewName")
        val attrList = df.queryExecution.analyzed.output;
        val rowArray = df.take(count);
        val listOfList = rowArray.map(mapRow(_, attrList))
        val result = SQLResult(attrList.map(attr => attr.name), listOfList)
        write(result)
      } catch {
        case ex: Exception => throw new RuntimeException(s"Failed to execute SQL:${ex.getMessage},$sql")
      }

    val sqlResult= getSQLResult(sql);
   // println(s"<start>$sqlResult<end>")
      sqlResult
  }

*/
object BaseTasklet{


  protected def getTaskSpecFromClassPath[T](smarkTaskSpec:Class[T]):T ={
    val specFileName = smarkTaskSpec.getName.replace('.','/')+".xml"
    val absolutePath = FileSystemUtils.getFilePathFromClassPath(specFileName)
    absolutePath match {
      case Some(p) => XmlUtils.toObject(p,smarkTaskSpec)
      case _ => throw new RuntimeException(s"No tasklet spec file found in the classpath:${specFileName}")
    }
  }

}
