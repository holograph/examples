package com.tomergabel.examples.stackexchange;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomer on 9/4/12.
 */
public enum PostType {
    QUESTION( 1 ),
    ANSWER( 2 ),
    ORPHANED_TAG_WIKI( 3 ),
    TAG_WIKI_EXCERPT( 4 ),
    TAG_WIKI( 5 ),
    MODERATOR_NOMINATION( 6 ),
    WIKI_PLACEHOLDER( 7 ),
    PRIVILEGE_WIKI( 8 );

    public int id;
    private PostType( int id ) { this.id = id; }

    private static final Map<Integer, PostType> _map;
    static {
        Map<Integer, PostType> builder = new HashMap<Integer, PostType>( values().length );
        for ( PostType pt : values() )
            builder.put( pt.id, pt );
        _map = Collections.unmodifiableMap( builder );
    }
    public static PostType fromId( int id ) {
        return _map.get( id );
    }
}
