package com.csvsoft.smark.util

import java.io.File
import java.nio.file.{FileSystem, Files, Paths}
import scala.collection.JavaConverters._;
import collection.mutable.{Map => MuteMap}

case class FileCheckSum(fileName: String, checkSum: String)

class FileWatcher {

  var fileMap: MuteMap[String, String] = MuteMap()

  def getCurrentCheckSums(dir: String): List[FileCheckSum] = {
    val path = Paths.get(dir);
    val fileList = FileUtils.getFile(new File(dir));

    val checkSumList = fileList.asScala.map(f => {
      val fileName = f.getName
      val checkSum = FileUtils.getFileChecksumSHA(f)
      FileCheckSum(fileName, checkSum)
    }).toList
    checkSumList
  }



}
