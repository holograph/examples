package com.tomergabel.examples.lucene;

import com.tomergabel.examples.stackexchange.Post;
import com.tomergabel.examples.stackexchange.PostEmitter;
import com.tomergabel.examples.stackexchange.PostType;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

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

    private static Document buildDocument( final Post post ) {
        final Document document = new Document();

        document.add( new Field( "id", Long.toString( post.getId() ), Field.Store.YES, Field.Index.ANALYZED ) );
        document.add( new Field( "body", post.getBody(), Field.Store.NO, Field.Index.ANALYZED ) );
        document.add( new Field( "title", post.getTitle(), Field.Store.YES, Field.Index.ANALYZED ) );

        document.getFieldable( "title" ).setBoost( 5.0f );      // Set a title boost

        return document;
    }

    public static void main( String[] args ) throws Exception {
        // Set up index
        final Directory indexDirectory = new SimpleFSDirectory(
                new File( System.getProperty( "java.io.tmpdir" ), "question-index" ) );
        final IndexWriterConfig conf = new IndexWriterConfig(
                Version.LUCENE_36,
                new StandardAnalyzer( Version.LUCENE_36 )
        );

        // Iterate posts
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final File postFile = new File( args.length > 0 ? args[ 0 ] : "/Users/tomer/demo/serverfault/posts.xml" );
        final InputStream is = new BufferedInputStream( new FileInputStream( postFile ) );
        try {
            final IndexWriter writer = new IndexWriter( indexDirectory, conf );

            try {
                final XMLStreamReader reader = factory.createXMLStreamReader( is );
                final PostEmitter emitter = new PostEmitter( reader, PostType.QUESTION );

                long start = System.currentTimeMillis();
                int count = 0;
                while ( emitter.hasNext() ) {
                    final Post post = emitter.next();
                    final Document document = buildDocument( post );
                    writer.addDocument( document );
                    if ( ++count % 1000 == 0 )
                        System.err.print( String.format(
                                "\rProcessed %d documents (avg %f documents/second)",
                                count,
                                count * 1000.0f / ( System.currentTimeMillis() - start )
                        ) );
                }

            } finally {
                writer.close();
            }
        } finally {
            is.close();
        }

        System.err.println( "\nDone" );
    }
}
