package com.csvsoft.smark.core.util

import java.io.{File, InputStreamReader, OutputStream}
import java.nio.charset.StandardCharsets
import javax.xml.bind.{JAXBContext, JAXBException, Marshaller}

import scala.tools.nsc.interpreter.InputStream


object XmlUtils {

  def toObject[T](xmlFileName: String, clazz: Class[T]): T = {
    toObject(new File(xmlFileName), clazz)
  }

  def toObject[T](file: File, clazz: Class[T]): T = {
    val jaxbContext = JAXBContext.newInstance(clazz)
    val jaxbUnmarshaller = jaxbContext.createUnmarshaller
    val obj = jaxbUnmarshaller.unmarshal(file).asInstanceOf[T]
    obj
  }

  def toObject[T](in: InputStream, clazz: Class[T]): T = {
    val jaxbContext = JAXBContext.newInstance(clazz)
    val jaxbUnmarshaller = jaxbContext.createUnmarshaller
    val obj = jaxbUnmarshaller.unmarshal(new InputStreamReader(in, StandardCharsets.UTF_8)).asInstanceOf[T]
    obj
  }


  def objectToXml[T](obj: T, clazz: Class[T], out: OutputStream) = {
    val context = JAXBContext.newInstance(clazz)
    val marshaller = context.createMarshaller

    /** output the XML in pretty format */
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

    /** display the output in the console */
    marshaller.marshal(obj, out)

  }
}

