package com.tomergabel.examples

import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.request.QueryParam

case class EchoRequest(@QueryParam name: String, @QueryParam greeting: String = "Yo")

class EchoController extends Controller {
  get("/") { req: EchoRequest =>
    s"${req.greeting}, ${req.name}!"
  }
}

class EchoServer extends HttpServer {
  override protected def configureHttp(router: HttpRouter) =
    router.add(new EchoController)
}
