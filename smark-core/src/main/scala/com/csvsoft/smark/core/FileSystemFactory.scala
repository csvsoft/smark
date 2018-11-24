package com.csvsoft.smark.core

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

class FileSystemFactory(smarkAppConfig: SmarkAppConfig) {

  def getFileSystem():FileSystem={
     val conf = new Configuration()
    FileSystem.get(conf)
  }
}
object FileSystemFactory{
  def buildFileSystem(smarkAppConfig: SmarkAppConfig):FileSystem={
    val conf = new Configuration()
    FileSystem.get(conf)
  }
}
