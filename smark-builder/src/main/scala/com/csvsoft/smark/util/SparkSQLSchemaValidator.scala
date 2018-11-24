package com.csvsoft.smark.util

import java.text.SimpleDateFormat

import com.csvsoft.smark.entity.FieldDataType
import org.apache.commons.lang.StringUtils

case class LineWithLineNo(line: String, lineNum: Int)

object SparkSQLSchemaValidator {

  val regexCol = "^[a-zA-Z_][a-zA-Z0-9_]*$".r

  def validateSchema(schemaText: String): Option[String] = {
    val errorMsgs = schemaText.split("\n").zipWithIndex.map(sInt => LineWithLineNo(sInt._1, sInt._2))
      .filter(lineWithNo => StringUtils.isNotBlank(lineWithNo.line) && !lineWithNo.line.trim.startsWith("#"))
      .map(lineWithNo => validateLine(lineWithNo))
      .filter(s => s != "")
    return if (errorMsgs.length == 0) None else Some(errorMsgs(0))

  }

  private def validateLine(lineWitNo: LineWithLineNo): String = {
    val line = lineWitNo.line
    val lineNum = lineWitNo.lineNum
    val fields = line.split(",")
    val fieldsCount = fields.length
    if (fieldsCount < 3) {
      return s"Incomplete field definition at line:$lineNum, expected Field,Data Type, NULL/NOt NULL, Date format(Only applicable to Date/TimeStamp"
    }

    val colName = fields(0)
    val dataType = fields(1)
    val nullable = fields(2)

    if (StringUtils.isBlank(colName)) {
      return s"Field Name required at line:$lineNum"
    }
    if (StringUtils.isBlank(dataType)) {
      return s"Data Type required at line:$lineNum"
    }
    if (StringUtils.isBlank(nullable)) {
      return s"Nullability required at line:$lineNum"
    }
    if (!isColumnValid(colName.trim)) {
      return s"Field Name not valid at line:$lineNum"
    }
    if (!isValidDateType(dataType.trim)) {
      return s"Data type not valid at line:$lineNum"
    }
    if (!"NULL".equalsIgnoreCase(nullable.trim) && !"NOT NULL".equalsIgnoreCase(nullable.trim)) {
      return s"Nullability can only be NULL or NOT NULL at line:$lineNum"
    }

    if (dataType == FieldDataType.DATE.getValue || dataType == FieldDataType.TIMESTAMP.getValue) {
      if (fieldsCount < 4 || StringUtils.isBlank(fields(3))) {
        return s"Date Format is required if data type is $dataType at line:$lineNum"
      }
      val dateFormat = fields(3);
      if (!isDateFormatValid(dateFormat)) {
        return s"Not a valid Date Format, at line:$lineNum, please check:"
      }
    }
    ""


  }


  private def isDateFormatValid(dateFormat: String): Boolean = {
    try {
      val sdf = new SimpleDateFormat(dateFormat)
      true
    } catch {
      case e: IllegalArgumentException => false
    }
  }

  private def isColumnValid(colName: String): Boolean = {
    if (StringUtils.isBlank(colName)) {
      false
    } else {
      regexCol.findFirstMatchIn(colName).isDefined
    }
  }

  private def isValidDateType(dataType: String): Boolean = {
    if (StringUtils.isBlank(dataType)) {
      false
    } else {
      import scala.collection.JavaConverters._
      FieldDataType.BOOLEAN.getAllDataType.asScala.map(_.getValue).contains(dataType)
    }
  }


}
