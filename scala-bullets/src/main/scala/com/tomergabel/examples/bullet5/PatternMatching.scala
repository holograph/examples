package com.tomergabel.examples.bullet5


/**
 * Created by tomer on 5/20/14.
 */

sealed trait MaritalStatus
case object Single extends MaritalStatus
case object Married extends MaritalStatus
case object Divorced extends MaritalStatus
case object Widowed extends MaritalStatus

sealed trait Gender
case object Male extends Gender
case object Female extends Gender

case class Person( name: String, surname: String, age: Int,
                   maritalStatus: Option[ MaritalStatus ] = None,
                   gender: Option[ Gender ] = None ) {

  def isAdult = age >= 18

  // Based on rules from http://www.formsofaddress.info/Social_M_W.html
  def salutation: Option[ String ] = ( gender, maritalStatus ) match {
    case ( Some( Male   ), _              ) => Some( "Mr." )
    case ( Some( Female ), Some( Single ) ) => Some( "Miss" )
    case ( Some( Female ), None           ) => Some( "Ms." )
    case ( Some( Female ), _              ) => Some( "Mrs." )
    case ( None          , _              ) => None
  }
}

object PatternMatching extends App {
  val fred    = Person( "Fred",    "Flintstone", 25, gender = Some( Male   ), maritalStatus = Some( Married ) )
  val wilma   = Person( "Wilma",   "Flintstone", 24, gender = Some( Female ), maritalStatus = Some( Married ) )
  val pebbles = Person( "Pebbles", "Flintstone", 1,  gender = Some( Female ), maritalStatus = Some( Single  ) )

  assert( fred.salutation == Some( "Mr." ) )
  assert( wilma.salutation == Some( "Mrs." ) )
  assert( pebbles.salutation == Some( "Miss" ) )

  val johnDoe = Person( "John", "Doe", 40, gender = Some( Male   ) )
  val janeDoe = Person( "Jane", "Doe", 29, gender = Some( Female ) )

  assert( johnDoe.salutation == Some( "Mr." ) )
  assert( janeDoe.salutation == Some( "Ms." ) )

  val introvert = Person( "Blaise", "Zabini", 17 )

  assert( introvert.salutation == None )
}
