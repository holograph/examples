package com.tomergabel.examples

import java.util.UUID

import com.twitter.finagle.http.{Request, Status}
import com.twitter.finatra.http.Controller
import com.twitter.finatra.request.RouteParam

case class ExistingItem(title: String, completed: Boolean, url: String, order: Option[Int])
case class NewItemRequest(title: String, order: Option[Int])
case class ExistingItemRequest(@RouteParam id: UUID, title: Option[String], completed: Option[Boolean], order: Option[Int])

class TodoMvcBackendController(baseUrl: String, store: ItemStore) extends Controller {

  private def buildUrl(id: UUID): String = baseUrl + "/" + id.toString

  private def externalizeItem(item: PersistedItem): ExistingItem =
    ExistingItem(item.title, item.completed, buildUrl(item.id), item.order)

  options("/:*") { _: Request =>
    // Endpoint for CORS support
    response.ok
  }

  get("/") { _: Request =>
    store.allItems() map externalizeItem
  }

  post("/") { request: NewItemRequest =>
    val item = PersistedItem(UUID.randomUUID(), request.title, completed = false, request.order)
    if (!store.add(item))
      throw new IllegalMonitorStateException("Safety net; should never happen (IDs are always generated)")
    response.created.json(externalizeItem(item))
  }

  delete("/") { request: Request =>
    store.reset()
    response.ok
  }

  get("/:id") { request: ExistingItemRequest =>
    store
      .lookup(request.id)
      .map(externalizeItem)
      .map(response.ok.json(_))
      .getOrElse(response.notFound)
  }

  patch("/:id") { request: ExistingItemRequest =>
    store
      .lookup(request.id)
      .map { oldItem =>
        val newItem = PersistedItem(
          oldItem.id,
          request.title getOrElse oldItem.title,
          request.completed getOrElse oldItem.completed,
          request.order orElse oldItem.order
        )
        if (!store.update(newItem))
          throw new IllegalMonitorStateException("Safety net; should never happen (lookup was successful)")
        response.ok.json(externalizeItem(newItem))
      }
     .getOrElse(response.notFound)
  }

  delete("/:id") { request: ExistingItemRequest =>
    if (store.delete(request.id))
      response.ok
    else
      response.notFound
  }
}
