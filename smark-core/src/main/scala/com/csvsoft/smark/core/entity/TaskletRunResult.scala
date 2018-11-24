package com.csvsoft.smark.core.entity

case class TaskletRunResult(val runInfo:RunInfo, newViews:Option[Set[String]] = None)