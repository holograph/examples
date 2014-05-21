package com.tomergabel.examples.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tomer on 5/20/14.
 */
public class ClassWithLogs {
    private static Logger log = LoggerFactory.getLogger( ClassWithLogs.class );

    public String getNormalizedName( Person person ) {
        log.info( "getNormalizedName called" );
        log.debug( "Normalizing " + person.toString() );
        String normalizedName = person.getName().toUpperCase().trim();
        log.debug( "Normalized name is: " + normalizedName );
        return normalizedName;
    }
}
