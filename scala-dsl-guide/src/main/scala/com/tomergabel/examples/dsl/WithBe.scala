package com.tomergabel.examples.dsl

/**
 * Created by tomer on 10/14/14.
 */
object WithBe extends App {

  trait Predicate[ T ] {
    def test( value: T ): Boolean
  }

  case object be {
    def apply( b: Boolean ) = new Predicate[ Boolean ] {
      def test( v: Boolean ) = v == b
      override def toString() = b.toString
    }
  }
  class Between[ T ]( v: T, l: T )( implicit num: Numeric[ T ] ) {
    def and( r: T ) =
      require( num.gteq( v, l ) && num.lt( v, r ), s"Value $v isn't between $l and $r" )
  }
  class BeExt[ T ]( v: T ) {
    def equalTo( rhs: T ) = require( v == rhs, s"Value $v isn't equal to $rhs" )
    def between( l: T )( implicit num: Numeric[ T ] ) = new Between( v, l )
  }
  implicit class EntryPoint[ T <: Any ]( val v: T ) extends AnyVal {
    private def apply( pred: Predicate[ T ] ) = require( pred test v, s"Value $v isn't $pred" )
    def should( pred: Predicate[ T ] ) = apply( pred )
    def should( beWord: be.type ) = new BeExt( v )
  }

  15 should be equalTo 15
  5 should be between 10 and 20
  true should be( true )
  true should be( false )
  println( "good" )
}
