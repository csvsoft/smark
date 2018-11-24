package com.csvsoft.smark.core.entity

case class TaskletRunResult(runInfo:RunInfo, newViews:Option[Set[String]] = None)