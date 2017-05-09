package com.tomergabel.examples

import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter

object TodoMvcServer extends HttpServer {

  override protected def configureHttp(router: HttpRouter) = {
    val externalUri = "http://localhost" + httpExternalPort.map(":" + _).getOrElse(defaultFinatraHttpPort)
    val itemStore = new InMemoryItemStore
    val backendController = new TodoMvcBackendController(externalUri, itemStore)
    val corsFilter = new Cors.HttpFilter(Cors.UnsafePermissivePolicy)

    router
      .filter(corsFilter)
      .add(backendController)
  }
}
