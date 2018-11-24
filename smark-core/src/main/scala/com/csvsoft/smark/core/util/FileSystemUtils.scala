package com.csvsoft.smark.core.util

import java.io._
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.hadoop.fs.{FileSystem, Path}

object FileSystemUtils {

  def createDirSafe(fs: FileSystem, dir: String): Unit = {
    val path = new Path(dir)
    if (!fs.exists(path)) {
      fs.mkdirs(path)
    }
  }

  def getFilePathFromClassPath(path: String): Option[String] = {
    val resource = FileSystemUtils.getClass.getClassLoader.getResource(path);
    Option(resource) match {
      case Some(r) => Some(r.getPath)
      case _ => None
    }
  }

  def mergeFile(targetFile: File, sourceFile: File): Unit = {
    val inputStream = new FileInputStream(sourceFile)
    val outStream = new FileOutputStream(targetFile, true)
    IOUtils.copy(inputStream, outStream)
    IOUtils.closeQuietly(inputStream)
    IOUtils.closeQuietly(outStream)
  }

  def createTempFile(fileContent: String): File = {
    import java.io.File
    val propFile = File.createTempFile(s"test", ".tmp")
    val outWriter = new OutputStreamWriter(new FileOutputStream(propFile), StandardCharsets.UTF_8)
    outWriter.write(fileContent)
    IOUtils.closeQuietly(outWriter)
    propFile
  }

  def mergeFile(targetFile: File, inputStream: InputStream): Unit = {

    val outStream = new FileOutputStream(targetFile, true)
    IOUtils.copy(inputStream, outStream)
    IOUtils.closeQuietly(inputStream)
    IOUtils.closeQuietly(outStream)
  }
}
