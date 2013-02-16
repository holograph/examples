package com.tomergabel.examples;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by tomer on 2/4/13.
 */
public class Scenario2 {

    static int ITEM_COUNT = 10000000;
    static int ITEM_SIZE = 50;
    static int SLEEP_INTERVAL = 100;
    static int SLEEP_LENGTH = 5;
    static Random r = new Random();

    public static void main( String[] args ) throws IOException, InterruptedException {
        if ( args.length > 0 ) SLEEP_LENGTH = Integer.parseInt( args[ 0 ] );

        System.out.print( "Press Enter to begin: " );
        System.in.read();

        Set<String> testSet = new HashSet<String>();
        byte[] buf = new byte[ ITEM_SIZE ];
        for ( int i = 0; i < ITEM_COUNT; i++ ) {
            r.nextBytes( buf );
            testSet.add( new String( buf, Charset.defaultCharset() ) );
            if ( SLEEP_LENGTH > 0 && i % SLEEP_INTERVAL == 0 )
                Thread.sleep( SLEEP_LENGTH );
        }

        System.out.println( "Done" );
    }
}
