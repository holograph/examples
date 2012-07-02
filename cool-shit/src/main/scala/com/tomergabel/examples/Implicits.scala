package com.tomergabel.examples

import java.io._

/**
 * Created by tomer on 7/2/12.
 */

object Implicits {

    implicit def extendString( s: String ) = new {
        def toFile = new File( s )
    }

    implicit def extendFile( f: File ) = new {
        def printTo( x: PrintStream => Unit ) {
            val w = new PrintStream( new BufferedOutputStream( new FileOutputStream( f ) ) )
            try { x( w ); w.close() } catch { case e => w.close(); f.delete(); throw e }
        }

        def slurp = {
            val r = new BufferedReader( new FileReader( f ) )
            try {
                Stream
                    .continually( Option( r.readLine() ) )
                    .takeWhile( _.isDefined )
                    .map( _.get )
                    .mkString( "\n" )
            } finally { r.close() }
        }
    }

    def main( args: Array[ String ] ) {
        val tempFile = "/tmp/test".toFile

        tempFile printTo { ps =>
            ps.println( "I do not like green eggs and ham" )
        }

        println( tempFile.slurp )

        tempFile.delete()
    }
}
