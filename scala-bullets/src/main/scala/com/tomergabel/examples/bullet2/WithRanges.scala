package com.tomergabel.examples.bullet2

import scala.util.Random
import scala.collection.mutable


/**
 * Created by tomer on 5/20/14.
 *
 * Second bullet: Collection framework, first step
 */
object WithRanges {

  private val names = List( "Jeffrey", "Walter", "Donald" )
  private val surnames = List( "Lebowsky", "Sobchak", "Kerabatsos" )
  private val random = new Random
  val humanAgeRange = 0 to 125
  val toddlerAgeRange = 1 to 3

  def generatePerson( ageRange: Range = humanAgeRange ): Person =
    Person(
      name = names( random.nextInt( names.size ) ),
      surname = surnames( random.nextInt( surnames.size ) ),
      age = ageRange( random.nextInt( ageRange.length ) )
    )

  def generatePeople( count: Int, ageRange: Range = humanAgeRange ): List[ Person ] = {
    val people = mutable.ListBuffer.empty[ Person ]
    for ( i <- 0 until count ) {
      people.append( generatePerson( ageRange ) )
    }
    people.result()
  }

  def generateToddler: Person = generatePerson( toddlerAgeRange )

  def generateToddlers( count: Int ): List[ Person ] = generatePeople( count, toddlerAgeRange )

  def main( args: Array[ String ] ) {
    println( "Five people:" )
    for ( person <- generatePeople( 5 ) )
      println( person.toString )
    println( "Two toddlers:" )
    for ( toddler <- generateToddlers( 2 ) )
      println( toddler.toString )
  }
}