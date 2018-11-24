package com.csvsoft.smark.core.util

import java.io._

import com.csvsoft.smark.config.SmarkAppSpec

object SmarkAppSpecSerializer {
  def serializeSmarkAppSpec(smarkAppSpec:SmarkAppSpec):Array[Byte]={
    val fileOutStream = new ByteArrayOutputStream()
    val objOutStream = new ObjectOutputStream(fileOutStream)
    objOutStream.writeObject(smarkAppSpec)
    val bytes = fileOutStream.toByteArray
    fileOutStream.close()
    bytes
  }
  def deserializeSmarkAppSpcec(bytes:Array[Byte]):SmarkAppSpec ={
    val bin = new ByteArrayInputStream(bytes);
    val objIn = new ObjectInputStream(bin);
    val smarkAppSpec= objIn.readObject().asInstanceOf[SmarkAppSpec];
    bin.close();
    smarkAppSpec

  }
}
