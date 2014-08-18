package com.tomergabel.examples.json

import AST._

object SampleData {
  case class Person( name: String, surname: String, age: Int )

  case object Person {
    val instance = Person( "Max", "Hammer", 40 )

    val json = JsonObject(
      JsonField( "name", JsonString( "Max" ) ),
      JsonField( "surname", JsonString( "Hammer" ) ),
      JsonField( "age", JsonInt( 40 ) )
    )
    
    val compact = """{"name":"Max","surname":"Hammer","age":40}"""
    
    val pretty =
      """
        |{
        |  "name": "Max",
        |  "surname": "Hammer",
        |  "age": 40
        |}
      """.stripMargin.trim

    implicit object PersonSerializer extends Serialization.JsonSerializer[ Person ] {
      def serialize( value: Person ) = JsonObject(
        JsonField( "name",    Serialization.serialize( value.name    ) ),
        JsonField( "surname", Serialization.serialize( value.surname ) ),
        JsonField( "age",     Serialization.serialize( value.age     ) )
      )
      def deserialize( value: JsonValue ) = value match {
        case obj: JsonObject =>
          Person(
            name    = Serialization.deserialize[ String ]( obj \ "name"    ),
            surname = Serialization.deserialize[ String ]( obj \ "surname" ),
            age     = Serialization.deserialize[ Int    ]( obj \ "age"     )
          )

        case _ => error( value )
      }
    }
  }

  case class Address( street1: String, street2: Option[ String ], city: String,
                      state: Option[ String ], zip: Option[ String ], country: String )

  case object Address {
    val instance = Address(
      street1 = "200 Larkin Street",
      street2 = None,
      city = "San Francisco",
      state = Some( "CA" ),
      zip = Some( "94102" ),
      country = "United States"
    )

    val json = JsonObject(
      JsonField( "street1", JsonString( "200 Larkin Street" ) ),
      JsonField( "street2", JsonNull ),
      JsonField( "city", JsonString( "San Francisco" ) ),
      JsonField( "state", JsonString( "CA" ) ),
      JsonField( "zip", JsonString( "94102" ) ),
      JsonField( "country", JsonString( "United States" ) )
    )

    val compact =
      """{"street1":"200 Larkin Street","street2":null,"city":"San Francisco","state":"CA","zip":"94102","country":"United States"}"""
    
    val pretty =
      """
        |{
        |  "street1": "200 Larkin Street",
        |  "street2": null,
        |  "city": "San Francisco",
        |  "state": "CA",
        |  "zip": "94102",
        |  "country": "United States"
        |}
      """.stripMargin.trim

    implicit object AddressSerializer extends Serialization.JsonSerializer[ Address ] {
      def serialize( value: Address ) = JsonObject(
        JsonField( "street1", Serialization.serialize( value.street1 ) ),
        JsonField( "street2", Serialization.serialize( value.street2 ) ),
        JsonField( "city",    Serialization.serialize( value.city    ) ),
        JsonField( "state",   Serialization.serialize( value.state   ) ),
        JsonField( "zip",     Serialization.serialize( value.zip     ) ),
        JsonField( "country", Serialization.serialize( value.country ) )
      )

      def deserialize( value: JsonValue ) = value match {
        case obj: JsonObject =>
          Address(
            street1 = Serialization.deserialize[         String   ]( obj \ "street1" ),
            street2 = Serialization.deserialize[ Option[ String ] ]( obj \ "street2" ),
            city    = Serialization.deserialize[         String   ]( obj \ "city"    ),
            state   = Serialization.deserialize[ Option[ String ] ]( obj \ "state"   ),
            zip     = Serialization.deserialize[ Option[ String ] ]( obj \ "zip"     ),
            country = Serialization.deserialize[         String   ]( obj \ "country" )
          )

        case _ => error( value )
      }
    }
  }

  case class Order( from: Person, to: Address )

  case object Order {
    val instance = Order( from = Person.instance, to = Address.instance )

    val json = JsonObject(
      JsonField( "from", Person.json ),
      JsonField( "to", Address.json )
    )
    
    val compact =
      """{"from":{"name":"Max","surname":"Hammer","age":40},"to":{"street1":"200 Larkin Street","street2":null,"city":"San Francisco","state":"CA","zip":"94102","country":"United States"}}"""
    
    val pretty =
      """
        |{
        |  "from": {
        |    "name": "Max",
        |    "surname": "Hammer",
        |    "age": 40
        |  },
        |  "to": {
        |    "street1": "200 Larkin Street",
        |    "street2": null,
        |    "city": "San Francisco",
        |    "state": "CA",
        |    "zip": "94102",
        |    "country": "United States"
        |  }
        |}
      """.stripMargin.trim

    implicit object OrderSerializer extends Serialization.JsonSerializer[ Order ] {
      def serialize( value: Order ) = JsonObject(
        JsonField( "from", Serialization.serialize( value.from ) ),
        JsonField( "to",   Serialization.serialize( value.to   ) )
      )

      def deserialize( value: JsonValue ) = value match {
        case obj: JsonObject =>
          val from = Serialization.deserialize[ Person  ]( obj \ "from" )
          val to   = Serialization.deserialize[ Address ]( obj \ "to"   )
          Order( from, to )

        case _ => error( value )
      }
    }
  }
}

object ASTTests extends App {
  import SampleData._

  val f1 = JsonObject( JsonField( "name", JsonString( "Max" ) ) )
  val f2 = JsonObject( JsonField( "surname", JsonString( "Hammer" ) ) )
  val f3 = JsonObject( JsonField( "age", JsonInt( 40 ) ) )
  assert( f1 + f2 + f3 == Person.json )
}

object PrinterTests extends App {
  import SampleData._

  assert( Printer.printJson( JsonArray() ) == "[]" )
  assert( Printer.printJson( JsonArray(), pretty = true ) == "[]" )
  assert( Printer.printJson( JsonObject(), pretty = true ) == "{}" )
  assert( Printer.printJson( JsonObject(), pretty = true ) == "{}" )

  assert( Printer.printJson( Person.json ) == Person.compact )
  assert( Printer.printJson( Person.json, pretty = true ) == Person.pretty )

  assert( Printer.printJson( Address.json ) == Address.compact )
  assert( Printer.printJson( Address.json, pretty = true ) == Address.pretty )

  assert( Printer.printJson( Order.json ) == Order.compact )
  assert( Printer.printJson( Order.json, pretty = true ) == Order.pretty )
}

object ParserTests extends App {
  import SampleData._

  assert( Parser.parseJson( "[]" ) == JsonArray() )
  assert( Parser.parseJson( "{}" ) == JsonObject() )
  assert( Parser.parseJson( "3.1415926539" ) == JsonDouble( 3.1415926539 ) )
  assert( Parser.parseJson( "42" ) == JsonInt( 42 ) )

  assert( Parser.parseJson( Person.compact ) == Person.json )
  assert( Parser.parseJson( Person.pretty ) == Person.json )

  assert( Parser.parseJson( Address.compact ) == Address.json )
  assert( Parser.parseJson( Address.pretty ) == Address.json )

  assert( Parser.parseJson( Order.compact ) == Order.json )
  assert( Parser.parseJson( Order.pretty ) == Order.json )
}

object SerializationTests extends App {
  import SampleData._

  assert( Serialization.serialize( Person.instance ) == Person.json )
  assert( Serialization.deserialize[ Person ]( Person.json ) == Person.instance )

  assert( Serialization.serialize( Address.instance ) == Address.json )
  assert( Serialization.deserialize[ Address ]( Address.json ) == Address.instance )

  assert( Serialization.serialize( Order.instance ) == Order.json )
  assert( Serialization.deserialize[ Order ]( Order.json ) == Order.instance )
}