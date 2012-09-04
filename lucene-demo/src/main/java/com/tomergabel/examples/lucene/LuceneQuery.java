package com.tomergabel.examples.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Created by tomer on 9/4/12.
 */
public class LuceneQuery {

    public static void report( final Query q, final TopDocs docs, final IndexReader r ) throws IOException {
        System.err.println( String.format( "Total %d results for query \"%s\"", docs.totalHits, q.toString() ) );
        for ( ScoreDoc result : docs.scoreDocs ) {
            final Document doc = r.document( result.doc );
            System.err.println( String.format( "\tid=%s, score=%f, title=\"%s\"",
                    doc.get( "id" ), result.score, doc.get( "title" ) ) );
        }
    }

    public static void main( String[] args ) throws IOException {
        final Directory indexDirectory = new SimpleFSDirectory(
                new File( System.getProperty( "java.io.tmpdir" ), "question-index" ) );
        final IndexReader reader = IndexReader.open( indexDirectory );
        try {
            final IndexSearcher searcher = new IndexSearcher( reader );

            // Test simple "key-value" term query
            Query q = new TermQuery( new Term( "id", "1" ) );
            TopDocs results = searcher.search( q, 10 );
            report( q, results, reader );

            // Test simple search term query
            q = new TermQuery( new Term( "body", "bash" ) );
            results = searcher.search( q, 10 );
            report( q, results, reader );

            // Test boolean query: multiple fields + boost
            BooleanQuery bq = new BooleanQuery();
            bq.add( new TermQuery( new Term( "body", "bash" ) ), BooleanClause.Occur.SHOULD );
            bq.add( new TermQuery( new Term( "title", "bash" ) ), BooleanClause.Occur.SHOULD );
            results = searcher.search( bq, 10 );
            report( bq, results, reader );

            // Test boolean query: exclusion
            bq = new BooleanQuery();
            bq.add( new TermQuery( new Term( "body", "bash" ) ), BooleanClause.Occur.MUST );
            bq.add( new TermQuery( new Term( "body", "bsd" ) ), BooleanClause.Occur.MUST_NOT );
            results = searcher.search( bq, 10 );
            report( bq, results, reader );

            // Test phrase query
            PhraseQuery pq = new PhraseQuery();
            pq.add( new Term( "title", "raid" ) );
            pq.add( new Term( "title", "controller" ) );
            pq.setSlop( 0 );
            results = searcher.search( pq, 10 );
            report( pq, results, reader );

        } finally {
            reader.close();
        }
    }
}
