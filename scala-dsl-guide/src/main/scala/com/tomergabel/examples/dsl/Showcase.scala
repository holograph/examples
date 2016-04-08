package com.tomergabel.examples.dsl

object Showcase extends App {

  import com.tomergabel.examples.dsl.DSL._

  Nil shouldBe empty
  val ref: String = null
  ref shouldBe null
  (3*4 > 10) shouldBe true
  3*4 shouldBe equalTo(12)

  List(1, 2, 3) shouldBe not(empty)
  
  "ScalaUA" should startWith("Scala")
  "ScalaUA" should endWith("IO")
  List(1, 2, 3) should contain(2)
  "ScalaUA" should matchRegex("Sc.*")
  "RubyConf" should not(startWith("Scala"))
}
