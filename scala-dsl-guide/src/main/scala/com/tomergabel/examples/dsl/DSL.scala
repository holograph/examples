package com.tomergabel.examples.dsl

import scala.collection.Iterable

object DSL {
  def empty[T <: Iterable[_]] = new CompoundPredicate[T] {
    def test(data: T): Boolean = data.isEmpty
    def failure = "is not empty"
    def failureNeg = "is empty"
  }

  implicit class ValidationContext[T](data: T) {
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
    def test(data: Boolean) = data == b
    def failure = s"is not $b"
    def failureNeg = s"is $b"
  }

  def startWith(prefix: String) = new ModalPredicate[String] {
    def test(data: String): Boolean = data startsWith prefix
    def failure = s"does not start with $prefix"
    def failureNeg = s"starts with $prefix"
  }
  def endWith(suffix: String) = new ModalPredicate[String] {
    def test(data: String): Boolean = data endsWith suffix
    def failure = s"does not end with $suffix"
    def failureNeg = s"ends with $suffix"
  }
  def contain[T <: Iterable[E], E](elem: E) =
    new ModalPredicate[T] {
      def test(data: T): Boolean = data.exists(_ == elem)
      def failure = s"does not contain element $elem"
      def failureNeg = s"contains element $elem"
    }
  def matchRegex(pattern:String) = new ModalPredicate[String] {
    def test(data: String): Boolean = data matches pattern
    def failure = s"does not match pattern $pattern"
    def failureNeg = s"matches pattern $pattern"
  }
  def equalTo[T](rhs: T) = new CompoundPredicate[T] {
    def test(data: T): Boolean = data == rhs
    def failure = s"is not equal to $rhs"
    def failureNeg = s"is equal to $rhs"
  }

  def not[T](predicate: Predicate[T]): predicate.Self[T] = predicate.negate

}
