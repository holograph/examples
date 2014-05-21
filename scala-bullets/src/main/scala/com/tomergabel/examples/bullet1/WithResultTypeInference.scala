package com.tomergabel.examples.bullet1

import scala.util.Random
import scala.collection.mutable


/**
 * Created by tomer on 5/20/14.
 *
 * First bullet: Pervasive type inference (with result types, which is not necessarily a good idea for public APIs)
 */
object WithResultTypeInference {

  private val names = List( "Jeffrey", "Walter", "Donald" )
  private val surnames = List( "Lebowsky", "Sobchak", "Kerabatsos" )
  private val random = new Random
  val MINIMUM_HUMAN_AGE = 0
  val MAXIMUM_HUMAN_AGE = 125
  val MINIMUM_TODDLER_AGE = 1
  val MAXIMUM_TODDLER_AGE = 3

  def generatePerson( minAge: Int = MINIMUM_HUMAN_AGE,
                      maxAge: Int = MAXIMUM_HUMAN_AGE ) = {

    val name = names( random.nextInt( names.size ) )
    val surname = surnames( random.nextInt( surnames.size ) )
    val age = random.nextInt( maxAge - minAge + 1 ) + minAge
    new Person( name, surname, age )
  }

  def generatePeople( count: Int,
                      minAge: Int = MINIMUM_HUMAN_AGE,
                      maxAge: Int = MAXIMUM_HUMAN_AGE ) = {

    val people = mutable.ListBuffer.empty[ Person ]
    for ( i <- 0 until count ) {
      people.append( generatePerson( minAge, maxAge ) )
    }
    people.result()
  }

  def generateToddler =
    generatePerson( MINIMUM_TODDLER_AGE, MAXIMUM_TODDLER_AGE )

  def generateToddlers( count: Int ) =
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