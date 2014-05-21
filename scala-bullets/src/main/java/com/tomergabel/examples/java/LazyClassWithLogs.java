package com.tomergabel.examples.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tomer on 5/20/14.
 */
public class LazyClassWithLogs {
    private static Logger log = LoggerFactory.getLogger( LazyClassWithLogs.class );

    public String getNormalizedName( Person person ) {
        log.info( "getNormalizedName called" );
        if ( log.isDebugEnabled() )
            log.debug( "Normalizing " + person.toString() );
        String normalizedName = person.getName().toUpperCase().trim();
        if ( log.isDebugEnabled() )
            log.debug( "Normalized name is: " + normalizedName );
        return normalizedName;
    }
}
