package com.tomergabel.examples

import com.twitter.finagle.http.Status
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTestMixin
import org.scalatest.{Matchers, WordSpec}

class EchoServerSpec extends WordSpec with Matchers with FeatureTestMixin {

  val server = new EmbeddedHttpServer(new EchoServer)

  "Echo server" should {

    "respond with the default greeting" in {
      server.httpGet("/?name=dude", andExpect = Status.Ok, withBody = "Yo, dude!")
    }

    "support greetings" in {
      server.httpGet("/?name=dude&greeting=Hey", andExpect = Status.Ok, withBody = "Hey, dude!")
    }
  }
}
