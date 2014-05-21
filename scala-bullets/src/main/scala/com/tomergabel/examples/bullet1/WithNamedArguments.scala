package com.tomergabel.examples.bullet1

import scala.util.Random
import scala.collection.mutable


/**
 * Created by tomer on 5/20/14.
 *
 * First bullet: Pervasive type inference, extra example on using named arguments to simplify code. Shows one
 * example where it makes sense to drop the formal result type declaration on a public API.
 */
object WithNamedArguments {

  private val names = List( "Jeffrey", "Walter", "Donald" )
  private val surnames = List( "Lebowsky", "Sobchak", "Kerabatsos" )
  private val random = new Random
  val MINIMUM_HUMAN_AGE = 0
  val MAXIMUM_HUMAN_AGE = 125
  val MINIMUM_TODDLER_AGE = 1
  val MAXIMUM_TODDLER_AGE = 3

  def generatePerson( minAge: Int = MINIMUM_HUMAN_AGE,
                      maxAge: Int = MAXIMUM_HUMAN_AGE ) =
    Person(
      name    = names( random.nextInt( names.size ) ),
      surname = surnames( random.nextInt( surnames.size ) ),
      age     = random.nextInt( maxAge - minAge + 1 ) + minAge
    )

  def generatePeople( count: Int,
                      minAge: Int = MINIMUM_HUMAN_AGE,
                      maxAge: Int = MAXIMUM_HUMAN_AGE ): List[ Person ] = {

    val people = mutable.ListBuffer.empty[ Person ]
    for ( i <- 0 until count ) {
      people.append( generatePerson( minAge, maxAge ) )
    }
    people.result()
  }

  def generateToddler: Person =
    generatePerson( MINIMUM_TODDLER_AGE, MAXIMUM_TODDLER_AGE )

  def generateToddlers( count: Int ): List[ Person ] =
    generatePeople( count, MINIMUM_TODDLER_AGE, MAXIMUM_TODDLER_AGE )

  def main( args: Array[ String ] ) {
    println( "Five people:" )
    for ( person <- generatePeople( 5 ) )
      println( person.toString )
    println( "Two toddlers:" )
    for ( toddler <- generateToddlers( 2 ) )
      println( toddler.toString )
  }
}