package com.csvsoft.smark

import com.csvsoft.smark.config.SmarkAppSpec
import com.csvsoft.smark.core.builder.{SmarkAppBuilder}
import com.csvsoft.smark.core.util.XmlUtils
import org.scalatest.{FlatSpec, Matchers}

class SmarkAppBuilderTest extends FlatSpec with Matchers {

  "toolbox evaluation " should "work " in {
    val northWind = "/tmp/smark_app_repo/testUser1/NorthWind.xml"
    val appSpec = XmlUtils.toObject(northWind, classOf[SmarkAppSpec])
    SmarkAppBuilder.generateApp(appSpec)

  }

}
