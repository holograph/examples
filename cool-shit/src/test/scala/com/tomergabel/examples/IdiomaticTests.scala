package com.tomergabel.examples

import util.Random
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FlatSpec

/**
 * Created by tomer on 7/2/12.
 */

class IdiomaticTests extends FlatSpec with ShouldMatchers {

    val classUnderTest = new {
        def returns5 = 4
        def throwsAnError() { throw new Exception() }
        def returnsRandomInRange( r: Range ) =
            Random.nextInt( ( r.end - r.start ) / r.step ) * r.step + r.start
    }

    "classUnderTest.returns5" should "return 5" in {
        classUnderTest.returns5 should equal( 5 )
    }

    "classUnderTest.throwsAnError" should "throw an exception" in {
        evaluating {
            classUnderTest.throwsAnError()
        } should produce[ Exception ]
    }

    "classUnderTest.returnsRandomInRange" should "return a value in simple range" in {
        val r = Range( 1, 5 )
        r should contain( classUnderTest.returnsRandomInRange( r ) )
    }

    it should "return a correct value in stepped range" in {
        val r = Range( 1, 10, 2 )
        r should contain( classUnderTest.returnsRandomInRange( r ) )
    }

    "This test" should "fail" in { fail( "Excellent, excellent!" ) }

    "This test" should "be ignored" ignore {}
}
