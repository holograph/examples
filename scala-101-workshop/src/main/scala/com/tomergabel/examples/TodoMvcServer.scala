package com.tomergabel.examples

import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import scalikejdbc.ConnectionPool

object TodoMvcServer extends HttpServer {

  override protected def configureHttp(router: HttpRouter) = {
    Class.forName("org.h2.Driver")
    ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")
    val itemStore = new SqlItemStore
    itemStore.initializeSchema()

    val externalUri = "http://localhost" + httpExternalPort.map(":" + _).getOrElse(defaultFinatraHttpPort)
    val backendController = new TodoMvcBackendController(externalUri, itemStore)
    val corsFilter = new Cors.HttpFilter(Cors.UnsafePermissivePolicy)

    router
      .filter(corsFilter)
      .add(backendController)
  }
}
