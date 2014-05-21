package com.tomergabel.examples.bullet4

/**
 * Created by tomer on 5/20/14.
 */
class ClassWithLogs extends Logging {

  def getNormalizedName( person: Person ) = {
    logInfo( "getNormalizedName called" )
    logDebug( "Normalizing " + person.toString )
    val normalizedName: String = person.name.toUpperCase.trim
    logDebug( "Normalized name is: " + normalizedName )
  }
}
