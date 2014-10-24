package com.tomergabel.examples.dsl

import scala.collection.GenTraversableOnce

/**
 * Created by tomer on 10/24/14.
 */
object Final extends App {

  def empty[T <: GenTraversableOnce[_]] = new CompoundPredicate[T] {
    def test: T => Boolean = _.isEmpty
    def failure = "is not empty"
    def failureNeg = "is empty"
  }

  implicit class EntryPoint[T](data: T) {
    private def test(predicate: Predicate[T]): Unit =
      require(predicate.test(data), s"Value $data ${predicate.failure}")

    def shouldBe(n: Null)(implicit ev: T <:< AnyRef): Unit =
      require(data == null, s"Value $data is not null")

    def shouldBe(predicate: CompoundPredicate[T]): Unit =
      test(predicate)

    def should(predicate: ModalPredicate[T]): Unit =
      test(predicate)
  }

  implicit class BooleanPredicate(b: Boolean) extends CompoundPredicate[Boolean] {
    def test: Boolean => Boolean = _ == b
    def failure = s"is not $b"
    def failureNeg = s"is $b"
  }

  def equalTo[T](rhs: T) = new CompoundPredicate[T] {
    def test: T => Boolean = _ == rhs
    def failure = s"is not equal to $rhs"
    def failureNeg = s"is equal to $rhs"
  }

  Nil shouldBe empty
  val ref: String = null
  ref shouldBe null
  (3*4>10) shouldBe true
  3*4 shouldBe equalTo(12)

  def not[T](predicate: Predicate[T]): predicate.Self[T] = predicate.negate
  List(1, 2, 3) shouldBe not(empty)
  
  def startWith(prefix: String) = new ModalPredicate[String] {
    def test: String => Boolean = _ startsWith prefix
    def failure = s"does not start with $prefix"
    def failureNeg = s"starts with $prefix"
  }
  def endWith(suffix: String) = new ModalPredicate[String] {
    def test: String => Boolean = _ endsWith suffix
    def failure = s"does not end with $suffix"
    def failureNeg = s"ends with $suffix"
  }
  def contain[T <: GenTraversableOnce[E], E](elem: E) =
    new ModalPredicate[T] {
      def test: T => Boolean = _.exists(_ == elem)
      def failure = s"does not contain element $elem"
      def failureNeg = s"contains element $elem"
    }
  def matchRegex(pattern:String) = new ModalPredicate[String] {
    def test: String => Boolean = _ matches pattern
    def failure = s"does not match pattern $pattern"
    def failureNeg = s"matches pattern $pattern"
  }

  "Scala.IO" should startWith("Scala")
  "Scala.IO" should endWith("IO")
  List(1, 2, 3) should contain(2)
  "Scala.IO" should matchRegex("Sc.*")
  "RubyConf" should not(startWith("Scala"))
}
