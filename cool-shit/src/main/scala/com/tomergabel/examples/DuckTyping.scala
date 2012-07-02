package com.tomergabel.examples

import java.io.{PrintStream, File}

/**
 * Created by tomer on 7/2/12.
 */

object DuckTyping {

    class UnmanagedResource {
        println( "created" )
        def doStuff() { println( "stuffGetsDone" ) }
        def close() { println( "closed" ) }
    }

    def using[ T <: { def close() }, R ]( generator: => T )( processor: T => R ): R = {
        val resource = generator
        try processor( resource )
        finally { resource.close() }
    }

    def main( args: Array[ String ] ) {
        using( new PrintStream( File.createTempFile( "test", "txt" ) ) ) { ps =>
            ps.println( "hurrah!" )
        }

        using( new UnmanagedResource ) { r =>
            r.doStuff()
        }
    }
}
