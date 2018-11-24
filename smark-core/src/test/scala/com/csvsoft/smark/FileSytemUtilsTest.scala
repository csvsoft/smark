package com.csvsoft.smark

import com.csvsoft.smark.core.util.FileSystemUtils

import org.scalatest.{FlatSpec, Matchers}

class FileSytemUtilsTest extends FlatSpec with Matchers {
  "Get file from classpath" should "work " in {
    val carPath= FileSystemUtils.getFilePathFromClassPath("cars.csv")
    carPath shouldNot be (None)
  }
}
