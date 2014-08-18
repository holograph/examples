package com.tomergabel.examples.json

object Serialization {
  import AST._

  trait JsonSerializer[ T ] {
    def serialize( value: T ): JsonValue
    def deserialize( value: JsonValue ): T

    protected def error( value: JsonValue ) = throw new IllegalArgumentException( s"Unexpected JSON node $value" )
  }

  implicit def optionSerializer[ T ]( implicit serializer: JsonSerializer[ T ] ) =
    new JsonSerializer[ Option[ T ] ] {
      def serialize( value: Option[ T ] ) = value map serializer.serialize getOrElse JsonNull
      def deserialize( value: JsonValue ) = value match {
        case JsonNull => None
        case other => Some( serializer.deserialize( other ) )
      }
    }

  implicit object IntSerializer extends JsonSerializer[ Int ] {
    def serialize( value: Int ) = JsonInt( value )
    def deserialize( value: JsonValue ) = value match {
      case JsonInt( int ) => int
      case other => error( other )
    }
  }

  implicit object BooleanSerializer extends JsonSerializer[ Boolean ] {
    def serialize( value: Boolean ) = JsonBoolean( value )
    def deserialize( value: JsonValue ) = value match {
      case JsonBoolean( bool ) => bool
      case other => error( other )
    }
  }

  implicit object StringSerializer extends JsonSerializer[ String ] {
    def serialize( value: String ) = JsonString( value )
    def deserialize( value: JsonValue ) = value match {
      case JsonString( string ) => string
      case other => error( other )
    }
  }

  implicit def iterableSerializer[ T ]( implicit serializer: JsonSerializer[ T ] ) =
    new JsonSerializer[ Iterable[ T ] ] {
      def serialize( value: Iterable[ T ] ) = JsonArray( ( value map serializer.serialize ).toSeq :_* )
      def deserialize( value: JsonValue ) = value match {
        case JsonArray( values @ _* ) => values map serializer.deserialize
        case other => error( other )
      }
    }

  def serialize[ T ]( instance: T )( implicit serializer: JsonSerializer[ T ] ) =
    serializer.serialize( instance )
  def deserialize[ T ]( json: JsonValue )( implicit serializer: JsonSerializer[ T ] ) =
    serializer.deserialize( json )
}
