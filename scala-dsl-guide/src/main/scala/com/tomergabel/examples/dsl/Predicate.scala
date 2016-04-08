package com.tomergabel.examples.dsl


sealed trait Predicate[-T] {
  type Self[-T] <: Predicate[T]
  def test(data: T): Boolean
  def failure: String
  def failureNeg: String
  def negate: Self[T]
}

trait ModalPredicate[-T] extends Predicate[T] { self =>
  type Self[-T] = ModalPredicate[T]
  def negate = new ModalPredicate[T] {
    def test(data: T) = !self.test(data)
    def failure = self.failureNeg
    def failureNeg = self.failure
  }
}
trait CompoundPredicate[-T] extends Predicate[T] { self =>
  type Self[-T] = CompoundPredicate[T]
  def negate = new CompoundPredicate[T] {
    def test(data: T) = !self.test(data)
    def failure = self.failureNeg
    def failureNeg = self.failure
  }
}
