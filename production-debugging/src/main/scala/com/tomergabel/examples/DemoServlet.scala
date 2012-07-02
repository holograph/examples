package com.tomergabel.examples

import org.scalatra.ScalatraServlet
import java.util.concurrent.atomic.AtomicBoolean
import util.Random

/**
 * Created by tomer on 7/3/12.
 */

class DemoServlet extends ScalatraServlet {

    implicit def elevateToRunnable( x: () => Unit ) = new Runnable() {
        def run() { x() }
    }

    get( "/deadlock" ) {
        val a = new AnyRef
        val b = new AnyRef

        val   primary = () => while( true ) { a.synchronized { b.synchronized { println( "a->b is wonderful!" ) } } }
        val secondary = () => while( true ) { b.synchronized { a.synchronized { println( "a->b is wonderful!" ) } } }

        new Thread(   primary, "deadlock-1" ).start()
        new Thread( secondary, "deadlock-2" ).start()

        "Done"
    }

    val waitLock = new AtomicBoolean( false )
    var rand: Int = 0

    get( "/busywait" ) {
        if ( waitLock.compareAndSet( false, true ) ) {
            while ( waitLock.get() ) { rand = rand + 1 }
        }
    }

    get( "/releasewait" ) {
        if ( waitLock.compareAndSet( true, false ) )
            "result: " + rand
        else
            "wait not set"
    }

    get( "/gcstorm" ) {
        var ht = Set.empty[ String ]
        ( 1 to 10000000 ) foreach { _ => ht += Random.nextString( 1000 ) }
    }
}
