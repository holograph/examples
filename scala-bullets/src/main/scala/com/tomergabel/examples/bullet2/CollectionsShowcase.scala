package com.tomergabel.examples.bullet2

import WithFill._
import scala.util.Random

/**
 * Created by tomer on 5/20/14.
 * 
 * Second bullet: Collection framework, recap
 */

object CollectionBasics extends App {
  val people = generatePeople( 5 )

  val names = people.map( _.name )

  val adults = people.filter( p => p.age >= 18 )

  val averageAge = people.map( p => p.age ).sum / people.size

  // Functions are 1st class citizens
  def isAdult( p: Person ) = p.age >= 18
  val adultsFunction = people.filter( isAdult )
  assert( adultsFunction == adults )
}

object Maps extends App {
  val people = generatePeople( 5 )

  val directory: Map[ Char, List[ Person ] ] =
    people.groupBy( p => p.surname.head.toUpper ).withDefaultValue( List.empty )

  val beginningWithK: List[ Person ] = directory( 'K' )

  val countByFirstLetter = directory.mapValues( list => list.size )
}

object Sets extends App {
  val people = generatePeople( 5 )

  val surnameSet = people.map( _.surname ).toSet

  val union = surnameSet.union( Set( "Lebowsky" ) )               // The dude abides

  val intersection = surnameSet.intersect( Set( "Kerobatsos" ) )  // Donny died :-(
}

object CoolStuff extends App {
  val people = generatePeople( 5 )

  val ( adults, minors ) = people.partition( p => p.age >= 18 )

  val randomPairs = Random.shuffle( people ).grouped( 2 )

  val youngest = people.minBy( p => p.age )

  val oldest = people.maxBy( p => p.age )

  val hasSeniorCitizens = people.exists( p => p.age >= 65 )
}

object AlternativeSyntax extends App {
  val people = generatePeople( 5 )

  people.foreach( person => println( person ) )

  // Alternative syntax:
  people.foreach { person => println( person ) }

  // You can use placeholders:
  people.foreach { println(_) }

  // Functions are 1st class citizens
  people.foreach( println )

  // Postfix syntax (use with caution):
  people foreach println
}

