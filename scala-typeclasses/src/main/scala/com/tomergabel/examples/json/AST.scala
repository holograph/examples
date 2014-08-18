package com.tomergabel.examples.json

/**
 * Created by tomer on 7/14/14.
 */
object AST {
  sealed trait JsonValue
  case class JsonField( name: String, value: JsonValue )
  case class JsonObject( fields: JsonField* ) extends JsonValue {
    def +( other: JsonObject ) = JsonObject( ( fields ++ other.fields ):_* )
    def get( field: String ): Option[ JsonValue ] = fields.find( _.name == field ).map( _.value )
    def \( field: String ) = this.get( field ).get
  }

  case class JsonBoolean( value: Boolean ) extends JsonValue
  case class JsonString( value: String ) extends JsonValue {
    require( !value.exists( _.isControl ), "JSON strings may not contain control characters" )
    def quoted = value.replace( "\\", "\\\\" ).replace( "\"", "\\\"" )
  }
  case class JsonArray( values: JsonValue* ) extends JsonValue

  sealed trait JsonPrimitive extends JsonValue
  case object JsonNull extends JsonPrimitive
  sealed trait JsonNumber extends JsonPrimitive
  case class JsonInt( value: Int ) extends JsonNumber
  case class JsonDouble( value: Double ) extends JsonNumber
}
