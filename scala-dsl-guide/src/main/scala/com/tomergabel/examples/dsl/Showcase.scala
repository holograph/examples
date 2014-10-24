package com.tomergabel.examples.dsl

/**
 * Created by tomer on 10/24/14.
 */
object Showcase extends App {

  import com.tomergabel.examples.dsl.DSL._

  Nil shouldBe empty
  val ref: String = null
  ref shouldBe null
  (3*4>10) shouldBe true
  3*4 shouldBe equalTo(12)

  List(1, 2, 3) shouldBe not(empty)
  
  "Scala.IO" should startWith("Scala")
  "Scala.IO" should endWith("IO")
  List(1, 2, 3) should contain(2)
  "Scala.IO" should matchRegex("Sc.*")
  "RubyConf" should not(startWith("Scala"))
}
