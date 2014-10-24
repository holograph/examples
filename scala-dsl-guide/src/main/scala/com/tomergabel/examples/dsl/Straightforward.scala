package com.tomergabel.examples.dsl

/**
 * Created by tomer on 10/22/14.
 */
object Straightforward extends App {

  trait Predicate[ T ] {
    type Self <: Predicate[ T ]

    def test: T => Boolean
    def failure: String
    def negate(): Self
  }
  case class RawPredicate[ T ]( test: T => Boolean, failure: String, negative: String ) extends Predicate[ T ] {
    type Self = RawPredicate[ T ]
    def negate() = RawPredicate( test andThen { !_ }, negative, failure )
  }
  case class BePredicate[ T ]( test: T => Boolean, failure: String, negative: String ) extends Predicate[ T ] {
    type Self = BePredicate[ T ]
    def negate() = BePredicate( test andThen { !_ }, negative, failure )
    def toRaw = RawPredicate( test, failure, negative )
  }

  implicit class EntryPoint[ T ]( val value: T ) extends AnyVal {
    private def apply( pred: Predicate[ T ] ) =
      if ( !( pred test value ) ) println( s"Failure: Value $value ${pred.failure}" )
//      require( pred test value, s"Value $value ${pred.failure}" )

    def should( pred: RawPredicate[ T ] ) = apply( pred )
    def shouldBe( pred: BePredicate[ T ] ) = apply( pred )
  }

  def not( pred: Predicate[_] ) = pred.negate()
  object not {
    def be[ T ]( pred: BePredicate[ T ] ): RawPredicate[ T ] = pred.negate().toRaw
  }

  def equal[ T ]( rhs: T ) =
    RawPredicate[ T ]( _ == rhs, s"is not equal to $rhs", s"is equal to $rhs" )
  def startWith( rhs: String ) =
    RawPredicate[ String ]( _ startsWith rhs, s"""does not start with "$rhs"""", s"""starts with "$rhs""" )
  def empty[ T <: AnyRef { def isEmpty: Boolean } ] =
    BePredicate[ T ]( _.isEmpty, "is not empty", "is empty" )

  15 should equal( 15 )
  List( 1, 2, 3 ) shouldBe empty
  List( 1, 2, 3 ) should( not be empty )
  "asdf" should startWith( "as" )
  15 should equal( 12 )
}
