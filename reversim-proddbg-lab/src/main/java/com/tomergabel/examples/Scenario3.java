package com.tomergabel.examples;

import com.tomergabel.examples.util.FileIterator;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tomer on 2/16/13.
 */
public class Scenario3 {
    private static final Pattern extraction = Pattern.compile( "<a.*?href=\"(.*?)\".*?>" );

    private static final FileFilter htmlFilter = new FileFilter() {
        public boolean accept( File pathname ) { return pathname.getName().endsWith( ".html" ); }
    };

    private static String slurp( File f ) throws IOException {
        FileInputStream fis = new FileInputStream( f );
        try {
            FileChannel channel = fis.getChannel();
            MappedByteBuffer buffer = channel.map( FileChannel.MapMode.READ_ONLY, 0, channel.size() );
            return Charset.forName( "UTF-8" ).decode( buffer ).toString();
        } finally {
            fis.close();
        }
    }

    public static void main( String[] args ) throws IOException {
        File root = new File( "./wikipedia-he-excerpt" );
        FileIterator fsIter = new FileIterator( root, htmlFilter );
        Set<String> urlSet = new HashSet<String>();

        while ( fsIter.hasNext() ) {
            File f = fsIter.next();
            System.out.println( "Processing " + f.getAbsolutePath() );
            String rawHtml = slurp( f );
            Matcher m = extraction.matcher( rawHtml );
            while ( m.find() )
                urlSet.add( m.group( 1 ) );
        }

        System.out.println( "Set size: " + urlSet.size() );
    }
}
