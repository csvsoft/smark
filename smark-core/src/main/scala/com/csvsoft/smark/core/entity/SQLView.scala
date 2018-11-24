package com.csvsoft.smark.core.entity
 class SQL2target(order:Int)
case class SQLVar(order:Int,sql:String,varName:String,dataType:String) extends SQL2target(order)
case class SQLView(order:Int,sql:String,viewName:String,persistMode:String) extends SQL2target(order)

