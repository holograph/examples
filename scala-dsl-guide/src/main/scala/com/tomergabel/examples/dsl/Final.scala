package com.tomergabel.examples.dsl

import scala.collection.GenTraversableOnce

/**
 * Created by tomer on 10/24/14.
 */
object Final extends App {

  trait Predicate[-T] {
    def test: T => Boolean
    def failure: String
  }


  object NullPredicate extends Predicate[AnyRef] {
    def test: AnyRef => Boolean = _ == null
    def failure = s"is not null"
  }

//  class BooleanPredicate(bool: Boolean)
//    extends Predicate[Boolean] {
//
//    def test: Boolean => Boolean = _ == bool
//    def failure = s"is not null"
//  }

//  implicit class EntryPoint[T](data: T) {
//    private def test[R](predicate: Predicate[R])(implicit ev: T <:< R): Unit =
//      require(predicate.test(data), s"Value $data ${predicate.failure}")
//
//    def shouldBe(n: Null)(implicit ev: T <:< AnyRef): Unit =
//      require(data == null, s"Value $data is not null")
////      test(NullPredicate)
//
//    def shouldBe(predicate: Predicate[T]): Unit =
//      test(predicate)
//
//    def should(predicate: Predicate[T]): Unit =
//      test(predicate)
//    def shouldBe(bool: Boolean)(implicit ev: T <:< Boolean) =
//      test(new BooleanPredicate(bool))
//  }

  def empty[T <: GenTraversableOnce[_]] = new Predicate[T] {
    def test: T => Boolean = _.isEmpty
    def failure = "is not empty"
  }

implicit class EntryPoint[T](data: T) {
  private def test(predicate: Predicate[T]): Unit =
    require(predicate.test(data), s"Value $data ${predicate.failure}")

  def shouldBe(n: Null)(implicit ev: T <:< AnyRef): Unit =
    require(data == null, s"Value $data is not null")

  def shouldBe(predicate: Predicate[T]): Unit =
    test(predicate)

  def should(predicate: Predicate[T]): Unit =
    test(predicate)
}

  implicit class BooleanPredicate(b: Boolean) extends Predicate[Boolean] {
    def test: Boolean => Boolean = _ == b
    def failure = s"is not $b"
  }

  def equalTo[T](rhs: T) = new Predicate[T] {
    def test: T => Boolean = _ == rhs
    def failure = "is not empty"
  }

  Nil shouldBe empty
  val ref: String = null
  ref shouldBe null
  (3*4>10) shouldBe true
  3*4 shouldBe equalTo(12)
  
  
  def startWith(prefix: String) = new Predicate[String] {
    def test: String => Boolean = _ startsWith prefix
    def failure = s"does not start with $prefix"
  }
  def endWith(suffix: String) = new Predicate[String] {
    def test: String => Boolean = _ endsWith suffix
    def failure = s"does not end with $suffix"
  }
  def contain[T <: GenTraversableOnce[E], E](elem: E) =
    new Predicate[T] {
      def test: T => Boolean = _.exists(_ == elem)
      def failure = s"does not contain element $elem"
    }
  def matchRegex(pattern:String) = new Predicate[String] {
    def test: String => Boolean = _ matches pattern
    def failure = s"does not match pattern $pattern"
  }

  "Scala.IO" should startWith("Scala")
  "Scala.IO" should endWith("IO")
  List(1, 2, 3) should contain(2)
  List(1, 2, 3) shouldBe contain(2)
  "Scala.IO" should matchRegex("Sc.*")



  /*
empty
null
true
equalTo
startWith
endWith
contain
matchRegex
not
and
or
   */
}
