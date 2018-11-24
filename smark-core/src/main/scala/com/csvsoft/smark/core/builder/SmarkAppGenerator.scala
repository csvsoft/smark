package com.csvsoft.smark.core.builder

import java.io.File
import javax.xml.bind.JAXBContext

import com.csvsoft.smark.config.SmarkAppSpec

object SmarkAppGenerator {

  def buildSmarkAppSpec(specFileName: String): SmarkAppSpec = {
    val file = new File(specFileName)
    val jaxbContext = JAXBContext.newInstance(classOf[SmarkAppSpec])
    val jaxbUnmarshaller = jaxbContext.createUnmarshaller
    val smarkAppSpec = jaxbUnmarshaller.unmarshal(file).asInstanceOf[SmarkAppSpec]
    smarkAppSpec
  }

  def genSmarkAppCode(spmarkAppSpec:SmarkAppSpec):Unit ={

  }
}
