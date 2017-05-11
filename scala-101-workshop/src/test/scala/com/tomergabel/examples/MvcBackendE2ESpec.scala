package com.tomergabel.examples

import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTestMixin
import org.scalatest.{Matchers, WordSpec}

case class Item(url: String, title: String, order: Option[Int], completed: Boolean)

class MvcBackendE2ESpec
  extends WordSpec
  with Matchers
  with FeatureTestMixin
  with TodoMvcApiDriver
{
  val server = new EmbeddedHttpServer(new TodoMvcServer with InMemoryStore)

  "TodoMVC server" when {

    "starting up" should {

      "succeed" in {
        server.assertHealthy()
      }

      "report no items on GET from root" in {
        listAllItems() shouldBe empty
      }

      "support CORS preflight request from todobackend.com" in {
        val corsRequestHeaders = Map(
          "Access-Control-Request-Method" -> "GET",
          "Origin" -> "http://www.todobackend.com"
        )

        val response = server.httpOptions("/", headers = corsRequestHeaders)

        response.headerMap should contain allOf(
          "Access-Control-Allow-Methods" -> "GET",
          "Access-Control-Allow-Origin" -> "http://www.todobackend.com"
        )
      }
    }

    "adding an item through POST to root" should {

      "succeed with HTTP status 201 (Created)" in {
        addItem("test")
      }
    }

    "getting an item through GET from its URL" should {

      "succeed" in {
        getItem(addItem("test").url)
      }

      "include a \"url\" field in the result body matching the item URL" in {
        val item = addItem("test")
        val loaded = getItem(item.url)
        loaded.url shouldEqual item.url
      }

      "include a \"title\" field in the result body matching the item's title" in {
        val item = addItem("test")
        val loaded = getItem(item.url)
        loaded.title shouldEqual "test"
      }

      "include an \"order\" field in the result body, if specified" in {
        val item = addItem("ordered", order = Some(5))
        val field = getItem(item.url)
        field.order shouldEqual Some(5)
      }
    }

    "updating an existing item through PATCH to its URL" should {

      "include the resulting item in the response body" in {
        val item = addItem("test")
        val modified = patchItem(item.url, newTitle = Some("modified"))
        modified.title shouldEqual "modified"
      }

      "include the updated information on GET from the item URL" in {
        val item = addItem("test")
        patchItem(item.url, newTitle = Some("modified"))
        getItem(item.url).title shouldEqual "modified"
      }

      "include the updated information on GET from root" in {
        val item = addItem("test")
        patchItem(item.url, newTitle = Some("modified"))

        val found = listAllItems().find(_.url == item.url)
        found should not be empty
        found.get.title shouldEqual "modified"
      }
    }

    "deleting an existing item through DELETE to its URL" should {

      "succeed" in {
        val item = addItem("test")
        deleteItem(item.url)
      }

      "make the item inaccessible (HTTP 404) on subsequent GETs" in {
        val item = addItem("test")
        deleteItem(item.url)
        assertItemDoesNotExist(item.url)
      }

      "elide the item from subsequent listing" in {
        val item = addItem("test")
        deleteItem(item.url)
        val listing = listAllItems()
        listing.find(_.url == item.url) shouldBe empty
      }
    }

    "DELETE to root" should {

      "make existing items inaccessible" in {
        val item = addItem("test")
        deleteAllItems()
        assertItemDoesNotExist(item.url)
      }

      "report no items on GET from root" in {
        val item = addItem("test")
        deleteAllItems()
        listAllItems() shouldBe empty
      }
    }
  }
}

trait TodoMvcApiDriver {
  import com.twitter.finagle.http.Status._

  protected def server: EmbeddedHttpServer

  protected def listAllItems(): Seq[Item] =
    server.httpGetJson[Seq[Item]]("/", andExpect = Ok)

  protected def addItem(title: String, order: Option[Int] = None): Item = {
    val json = server.mapper.objectMapper.createObjectNode()
    json.put("title", title)
    order.foreach(json.put("order", _))
    server.httpPostJson[Item]("/", postBody = json.toString, andExpect = Created)
  }

  def getItem(url: String): Item =
    server.httpGetJson[Item](url, andExpect = Ok)

  def assertItemDoesNotExist(url: String): Unit =
    server.httpGet(url, andExpect = NotFound)

  def patchItem(url: String,
                newTitle: Option[String] = None,
                newCompleted: Option[Boolean] = None,
                newOrder: Option[Int] = None): Item =
  {
    val json = server.mapper.objectMapper.createObjectNode()
    newTitle.foreach(json.put("title", _))
    newCompleted.foreach(json.put("completed", _))
    newOrder.foreach(json.put("order", _))
    server.httpPatchJson[Item](url, patchBody = json.toString, andExpect = Ok)
  }

  def deleteItem(url: String): Unit =
    server.httpDelete(url, andExpect = Ok)

  def deleteAllItems(): Unit =
    server.httpDelete("/", andExpect = Ok)
}