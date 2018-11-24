package com.csvsoft.smark.core.entity

import java.time.LocalDateTime

case class RunInfo(runId:Long,startTime:LocalDateTime,endTime:Option[LocalDateTime] = None)
