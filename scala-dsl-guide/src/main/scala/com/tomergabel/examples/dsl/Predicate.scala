package com.tomergabel.examples.dsl

/**
 * Created by tomer on 10/24/14.
 */
sealed trait Predicate[-T] {
  type Self[-T] <: Predicate[T]
  def test: T => Boolean
  def failure: String
  def failureNeg: String
  def negate: Self[T]
}

trait ModalPredicate[-T] extends Predicate[T] { self =>
  type Self[-T] = ModalPredicate[T]
  def negate = new ModalPredicate[T] {
    def test = self.test andThen { !_ }
    def failure = self.failureNeg
    def failureNeg = self.failure
  }
}
trait CompoundPredicate[-T] extends Predicate[T] { self =>
  type Self[-T] = CompoundPredicate[T]
  def negate = new CompoundPredicate[T] {
    def test = self.test andThen { !_ }
    def failure = self.failureNeg
    def failureNeg = self.failure
  }
}
