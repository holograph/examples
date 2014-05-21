package com.tomergabel.examples.bullet3

/**
 * Created by tomer on 5/20/14.
 */
object OptionsShowcase {

  case class PartiallyKnownPerson( name:    Option[ String ] = None,
                                   surname: Option[ String ] = None,
                                   age:     Option[ Int ]    = None ) {

    def isAdult = age.map( _ > 18 )
  }

  val person = PartiallyKnownPerson( name = Some( "Brandt" ) )
  
  val explicitAccess = person.age.get   // <-- NoSuchElementException thrown here
  
  val withDefault = person.name.getOrElse( "Anonymous" )
  println( "Hello " + withDefault + "!" )

  // Options are also zero- or single-element collections!
  def greet( whom: Iterable[ String ] ) =
    whom.foreach { name => println( "hello" + name ) }
  greet( person.name )
}
