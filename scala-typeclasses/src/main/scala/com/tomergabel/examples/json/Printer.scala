package com.tomergabel.examples.json

import java.io.{StringWriter, Writer}

import AST._

/**
 * Created by tomer on 7/14/14.
 */
object Printer {

  private trait Printer {
    protected def out: Writer

    def printJson( value: JsonValue ): Unit =
      value match {
        case JsonObject( fields @ _* ) =>
          inObject {
            fields.zipWithIndex foreach { case ( field, idx ) =>
              if ( idx > 0 ) out.append( ',' )
              inField( field.name ) { printJson( field.value ) }
            }
          }

        case JsonArray( values @ _* ) =>
          inArray {
            values.zipWithIndex foreach { case ( arrayValue, idx ) =>
              if ( idx > 0 ) out.append( ',' )
              printJson( arrayValue )
            }
          }

        // Primitives
        case JsonNull => out.append( "null" )
        case JsonBoolean( literal ) => out.append( literal.toString )
        case JsonInt( literal ) => out.append( literal.toString )
        case JsonDouble( literal ) => out.append( literal.toString )
        case string: JsonString => out.append( '"' ).append( string.quoted ).append( '"' )
      }

    protected def inObject( thunk: => Unit ): Unit
    protected def inField( name: String )( thunk: => Unit ): Unit
    protected def inArray( thunk: => Unit ): Unit
  }

  private class CompactPrinter( protected val out: Writer ) extends Printer {
    protected def inObject( thunk: => Unit ): Unit = {
      out.append( '{' )
      thunk
      out.append( '}' )
    }
    protected def inArray( thunk: => Unit ): Unit = {
      out.append( '[' )
      thunk
      out.append( ']' )
    }
    protected def inField( name: String )( thunk: => Unit ): Unit = {
      out.append( '"' ).append( name ).append( "\":" )
      thunk
    }
  }

  val indentation = 2

  private class PrettyPrinter( writer: Writer ) extends Printer {
    private var nesting = 0

    protected val out = new Writer() {
      var hasWrites = false
      def flush() = writer.flush()
      def write( cbuf: Array[ Char ], off: Int, len: Int ) = {
        hasWrites = true
        writer.write( cbuf, off, len )
      }
      def close() = writer.close()

      def trackWrites( thunk: => Unit ): Boolean = {
        hasWrites = false
        thunk
        hasWrites
      }
    }

    private def whitespace = String.valueOf( Array.fill( nesting * indentation )( ' ' ) )
    private def nest( thunk: => Unit ) = {
      nesting = nesting + 1
      thunk
      nesting = nesting - 1
    }

    def inObject( thunk: => Unit ): Unit = {
      out.append( "{" )
      if ( out.trackWrites( nest { thunk } ) ) out.append( '\n' ).append( whitespace )
      out.append( "}" )
    }
    def inArray( thunk: => Unit ): Unit = {
      out.append( "[" )
      if ( out.trackWrites( nest { thunk } ) ) out.append( '\n' ).append( whitespace )
      out.append( "]" )
    }
    def inField( name: String )( thunk: => Unit ): Unit = {
      out.append( '\n' ).append( whitespace ).append( '"' ).append( name ).append( "\": " )
      thunk
    }
  }

  def printJson( value: JsonValue, pretty: Boolean = false ): String = {
    val sw = new StringWriter()
    val printer = if ( pretty ) new PrettyPrinter( sw ) else new CompactPrinter( sw )
    printer.printJson( value )
    sw.toString
  }
}
