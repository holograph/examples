package com.tomergabel.examples;

import com.tomergabel.examples.stackexchange.Post;
import com.tomergabel.examples.stackexchange.PostEmitter;
import com.tomergabel.examples.stackexchange.PostType;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by tomer on 9/3/12.
 */
public class LuceneIndexer {

    public static void main( String[] args ) throws Exception {
//        final Directory indexDirectory = new SimpleFSDirectory( new File( "/tmp/index" ) );
//        final IndexWriterConfig conf = new IndexWriterConfig(
//                Version.LUCENE_36,
//                new StandardAnalyzer( Version.LUCENE_36 )
//        );

        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final InputStream is = new BufferedInputStream( new FileInputStream(
                new File( "/Users/tomer/demo/serverfault/posts.xml" ) ) );
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader( is );
            final PostEmitter emitter = new PostEmitter( reader, PostType.QUESTION );

            while ( emitter.hasNext() ) {
                final Post post = emitter.next();
                System.out.println( String.format( "Got post %d with title=%s", post.getId(), post.getTitle() ) );
            }

        } finally {
            is.close();
        }

//        final IndexWriter writer = new IndexWriter( indexDirectory, conf );
//        try {
//        // Iterate and add
//
//        } finally {
//            writer.close( true );
//        }


    }
}
