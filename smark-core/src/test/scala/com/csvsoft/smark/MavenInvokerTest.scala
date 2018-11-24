package com.csvsoft.smark

import com.csvsoft.smark.core.builder.SmarkAppMavenInvoker
import com.csvsoft.smark.core.util.FileSystemUtils
import org.scalatest.{FlatSpec, Matchers}

class MavenInvokerTest extends FlatSpec with Matchers {
  "Get maven home" should "work " in {

    val mavenHome = SmarkAppMavenInvoker.getMavenHome()
    mavenHome should not be("")
  }
}
