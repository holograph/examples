package com.tomergabel.examples

import java.io.{PrintStream, FileOutputStream, File}

/**
 * Created by tomer on 7/2/12.
 */

object DuckTyping {

    class UnmanagedResource {
        println( "created" )
        def doStuff() { println( "stuffGetsDone" ) }
        def close() { println( "closed" ) }
    }

    def withResource[ T <: { def close() }, R ]( generator: => T )( processor: T => R ): R = {
        val resource = generator
        try processor( resource )
        finally { resource.close() }
    }

    def main( args: Array[ String ] ) {
        withResource( new PrintStream( File.createTempFile( "test", "txt" ) ) ) { ps =>
            ps.println( "hurrah!" )
        }

        withResource( new UnmanagedResource ) { r =>
            r.doStuff()
        }
    }
}
