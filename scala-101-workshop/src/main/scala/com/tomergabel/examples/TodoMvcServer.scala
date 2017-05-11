package com.tomergabel.examples

import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import scalikejdbc.ConnectionPool

abstract class TodoMvcServer extends HttpServer {
  def itemStore: ItemStore

  lazy val externalUri: String =
    "http://localhost" + httpExternalPort.map(":" + _).getOrElse(defaultFinatraHttpPort)

  override protected def configureHttp(router: HttpRouter) = {
    val backendController = new TodoMvcBackendController(externalUri, itemStore)
    val corsFilter = new Cors.HttpFilter(Cors.UnsafePermissivePolicy)

    router
      .filter(corsFilter)
      .add(backendController)
  }
}

trait InMemoryStore { self: TodoMvcServer =>
  val itemStore = new InMemoryItemStore
}

trait H2Store { self: TodoMvcServer =>
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton("jdbc:h2:mem:hello", "user", "pass")
  val itemStore = new SqlItemStore
  itemStore.initializeSchema()
}

object TodoMvcServerMain extends TodoMvcServer with H2Store
