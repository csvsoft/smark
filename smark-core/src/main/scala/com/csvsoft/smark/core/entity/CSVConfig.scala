package com.csvsoft.smark.core.entity

import com.csvsoft.smark.CSVReadMode

case class CSVField(name:String, dataType:String, nullable:Boolean = true, format:String)
case class CSVConfig(charset:String="UTF-8", inferSchema:Boolean  = false, header:String = "false", parserLib:String = "commons", comment:String = "#", delimiter:Char = ',', quoteChar:Char = '"', escapeChar:Char = '\\', csvReadMode: CSVReadMode = CSVReadMode.PERMISSIVE, dateFormat:Option[String] = None ) {

  def toMap():Map[String,String]={
    Map[String,String]("charset" ->charset
      ,"comment" -> comment
      ,"delimiter" -> String.valueOf(delimiter)
      ,"header" -> header
      ,"quote" ->String.valueOf(quoteChar)
      ,"parserLib" -> parserLib
      ,"escape" -> String.valueOf(escapeChar)
      ,"inferSchema" -> String.valueOf(inferSchema)
      ,"mode" -> csvReadMode.getValue

      ,"dateFormat" -> dateFormat.getOrElse("")
      )
  }
}
