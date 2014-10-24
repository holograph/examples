package com.tomergabel.examples.dsl

/**
 * Created by tomer on 10/24/14.
 */
sealed trait Predicate[-T] {
  def test: T => Boolean
  def failure: String
}

trait ModalPredicate[-T] extends Predicate[T]
trait CompoundPredicate[-T] extends Predicate[T]
