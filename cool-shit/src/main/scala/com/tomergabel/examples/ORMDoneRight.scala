package com.tomergabel.examples

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Session, SessionFactory, KeyedEntity, Schema}

/**
 * Created by tomer on 7/2/12.
 */

object MySchema extends Schema {
    class Book( val title: String, val authorId: Int, val price: Float,
                val genre: String ) extends KeyedEntity[ Int ] {
        val id = Int.MinValue
    }
    class Author( val name: String ) extends KeyedEntity[ Int ] {
        val id = Int.MinValue
    }

    val books = table[ Book ]( "BOOK" )
    val authors = table[ Author ]( "AUTHOR" )

    on( books )( book => declare( book.id is( primaryKey, autoIncremented ) ) )
    on( authors )( author => declare( author.id is( primaryKey, autoIncremented ) ) )
}

object ORMDoneRight {
    def main( args: Array[ String ] ) {
        // Setup Squeryl
        new org.h2.Driver
        SessionFactory.concreteFactory = Some( () => new Session(
            java.sql.DriverManager.getConnection( "jdbc:h2:mem:sample" ),
            new org.squeryl.adapters.H2Adapter
        ) )

        import MySchema._

        def bookTitlesByGenre( genre: String ) =
            from( books )( book => where( book.genre === genre ) select( book.title ) )

        def bookGenresByAuthor( authorName: String ) =
            join( books, authors )( ( book, author ) =>
                where( author.name === authorName )
                select( book.genre )
                on( book.authorId === author.id )
            ).distinct

        inTransaction {
            // Create schema and add some data
            MySchema.create
            val brust = authors.insert( new Author( "Steven Brust" ) )
            val heinlein = authors.insert( new Author( "Robert A. Heinlein" ) )
            books.insert( new Book( "The Moon Is a Harsh Mistress", heinlein.id, 10.87f, "Sci-Fi" ) )
            books.insert( new Book( "The Book of Jhereg", brust.id, 10.88f, "Fantasy" ) )

            // Run some tests
            val scifiBooks = bookTitlesByGenre( "Sci-Fi" ).toList
            assert( scifiBooks == List( "The Moon Is a Harsh Mistress" ) )
            val brustGenres = bookGenresByAuthor( "Steven Brust" ).toList
            assert( brustGenres == List( "Fantasy" ) )
        }
    }}
