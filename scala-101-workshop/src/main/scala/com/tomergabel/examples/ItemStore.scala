package com.tomergabel.examples

import java.util.UUID


case class PersistedItem(id: UUID, title: String, completed: Boolean, order: Option[Int])

trait ItemStore {
  def lookup(id: UUID): Option[PersistedItem]
  def add(item: PersistedItem): Boolean
  def delete(id: UUID): Boolean
  def update(item: PersistedItem): Boolean
  def allItems(): Seq[PersistedItem]
  def reset(): Unit
}

class InMemoryItemStore extends ItemStore {
  private val items = scala.collection.concurrent.TrieMap.empty[UUID, PersistedItem]

  override def lookup(id: UUID): Option[PersistedItem] = items.get(id)

  override def add(item: PersistedItem): Boolean = items.putIfAbsent(item.id, item).isEmpty

  override def delete(id: UUID): Boolean = items.remove(id).nonEmpty

  override def update(item: PersistedItem): Boolean = { items += item.id -> item; true }

  override def allItems(): Seq[PersistedItem] = items.values.toSeq

  override def reset(): Unit = items.clear()
}
