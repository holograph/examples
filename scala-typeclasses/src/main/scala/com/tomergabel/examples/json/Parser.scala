package com.tomergabel.examples.json

import scala.annotation.tailrec

object Parser {
  import AST._
  import scala.collection.mutable

  sealed trait Token
  case object StartObject extends Token
  case object EndObject extends Token
  case object StartArray extends Token
  case object EndArray extends Token
  case class StringToken( unquoted: String ) extends Token
  sealed trait NumberToken extends Token
  case class IntToken( value: Int ) extends NumberToken
  case class DoubleToken( value: Double ) extends NumberToken
  case object FieldSeparator extends Token
  case object InstanceSeparator extends Token
  sealed trait Literal extends Token
  case object NullLiteral extends Literal
  case object FalseLiteral extends Literal
  case object TrueLiteral extends Literal


  class Tokenizer( _input: Iterator[ Char ] ) extends Iterator[ Token ] {
    private val input = new Iterator[ Char ] {
      private def readAhead() = if ( _input.hasNext ) Some( _input.next() ) else None

      private var buffer = readAhead()
      def peek = buffer
      def hasNext = buffer.isDefined
      def next() = { val temp = buffer; buffer = readAhead(); temp.get }
    }

    private def parseQuotedString(): StringToken = {
      val buffer = new StringBuilder
      var done = false
      while ( !done ) {
        var c = input.next()
        if ( c == '\\' ) buffer += input.next()  // Handle escapes
        else if ( c == '\"' ) done = true
        else buffer += c
      }
      StringToken( buffer.toString() )
    }

    private def parseNumber( startChar: Char ): NumberToken = {
      val buffer = new mutable.StringBuilder().append( startChar )
      var isReal = false
      var isExponent = false
      var done = false

      while ( !done )
        input.peek match {
          case Some( c ) if c.isDigit =>
            buffer += input.next()

          case Some( '.' ) if !isReal =>
            isReal = true
            buffer += input.next()

          case Some( 'e' ) if !isExponent =>
            isReal = true
            isExponent = true
            buffer += input.next()

          case _ =>
            done = true
        }

      if ( isReal )
        DoubleToken( buffer.toString().toDouble )
      else
        IntToken( buffer.toString().toInt )
    }

    private val literalMap = Map(
      "null"  -> NullLiteral,
      "false" -> FalseLiteral,
      "true"  -> TrueLiteral )

    @tailrec private def parseLiteral( acc: String ): Literal = {
      val target =
        literalMap.keys.find( _.startsWith( acc ) ).getOrElse( throw new IllegalArgumentException( "Unknown literal" ) )
      if ( target == acc )
        literalMap( target )
      else
        parseLiteral( acc + input.next() )
    }

    @tailrec private def nextToken: Option[ Token ] =
      if ( input.hasNext )
        input.next() match {
          case c if c.isWhitespace => nextToken
          case c if c.isLetter => Some( parseLiteral( c.toString ) )
          case '{' => Some( StartObject )
          case '}' => Some( EndObject )
          case '[' => Some( StartArray )
          case ']' => Some( EndArray )
          case '\"' => Some( parseQuotedString() )
          case ':' => Some( FieldSeparator )
          case ',' => Some( InstanceSeparator )
          case c if c.isDigit || c == '-' || c == '+' => Some( parseNumber( c ) )
        }
      else
        None

    private var buffer: Option[ Token ] = nextToken
    def hasNext = buffer.isDefined
    def next() = { val temp = buffer; buffer = nextToken; temp.get }
    def peek = buffer
  }

  private def readObject( tokens: Tokenizer ): JsonObject = {
    val fields = new mutable.ArrayBuffer[ JsonField ]
    var readyForField = true
    var done = false

    while ( !done )
      tokens.next() match {
        case StringToken( name ) if readyForField =>
          readyForField = false
          require( tokens.next() == FieldSeparator )
          fields += JsonField( name, readValue( tokens ) )
        case InstanceSeparator if !readyForField =>
          readyForField = true
        case EndObject =>
          done = true
      }

    JsonObject( fields:_* )
  }

  private def readArray( tokens: Tokenizer ): JsonArray = {
    val instances = new mutable.ArrayBuffer[ JsonValue ]
    var readyForInstance = true
    var done = false

    while ( !done )
      tokens.peek match {
        case Some( EndArray ) =>
          done = true
        case Some( InstanceSeparator ) if !readyForInstance =>
          readyForInstance = true
        case Some(_) if readyForInstance =>
          instances += readValue( tokens )
      }

    JsonArray( instances:_* )
  }

  private def readValue( tokens: Tokenizer ): JsonValue =
    tokens.next() match {
      case StartObject => readObject( tokens  )
      case StartArray => readArray( tokens )
      case StringToken( s ) => JsonString( s )
      case IntToken( i ) => JsonInt( i )
      case DoubleToken( i ) => JsonDouble( i )
      case NullLiteral => JsonNull
      case FalseLiteral => JsonBoolean( value = false )
      case TrueLiteral => JsonBoolean( value = true )
    }

  def parseJson( input: String ): JsonValue =
    readValue( new Tokenizer( input.iterator ) )
}
