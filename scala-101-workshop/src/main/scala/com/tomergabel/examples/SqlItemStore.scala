package com.tomergabel.examples

import java.io.DataInputStream
import java.util.UUID

import scalikejdbc._


/**
  * Created by tomerga on 09/05/2017.
  */
class SqlItemStore extends ItemStore {

  def initializeSchema(): Unit =
    DB autoCommit { implicit session =>
      sql"""
            create table items (
              id binary(16) not null primary key,
              title varchar(100) not null,
              completed boolean not null,
              `order` int null
            )
         """.execute().apply()
    }

  override def lookup(id: UUID): Option[PersistedItem] =
    DB readOnly { implicit session =>
      sql"select title, completed, `order` from items where id=$id"
        .map(rs => PersistedItem(id, rs.string("title"), rs.boolean("completed"), rs.intOpt("order")))
        .headOption
        .apply()
    }

  override def add(item: PersistedItem): Boolean =
    try {
      DB autoCommit { implicit session =>
        sql"""
              insert into items (id, title, completed, `order`)
              values (${item.id}, ${item.title}, ${item.completed}, ${item.order})
           """
          .update()
          .apply()
        true
      }
    } catch {
      case _: java.sql.SQLIntegrityConstraintViolationException => false
    }

  override def delete(id: UUID): Boolean =
    DB autoCommit { implicit session =>
      val deleted = sql"delete from items where id=$id".update().apply()
      deleted > 0
    }

  override def update(item: PersistedItem): Boolean =
    DB autoCommit { implicit session =>
      val updated =
        sql"""
              update items
              set title=${item.title}, completed=${item.completed}, `order`=${item.order}
              where id=${item.id}
           """
          .update()
          .apply()
      updated > 0
    }

  implicit class ExtendsResultSetWithUUID(rs: WrappedResultSet) {
    def uuid(field: String): UUID = {
      val r = new DataInputStream(rs.binaryStream(field))
      new UUID(r.readLong(), r.readLong())
    }
  }

  override def allItems(): Seq[PersistedItem] =
    DB readOnly { implicit session =>
      sql"select id, title, completed, `order` from items"
        .map(rs =>
          PersistedItem(
            rs.uuid("id"),
            rs.string("title"),
            rs.boolean("completed"),
            rs.intOpt("order")
          ))
        .list()
        .apply()
    }

  override def reset(): Unit = DB autoCommit { implicit session => sql"truncate items".execute().apply() }
}
