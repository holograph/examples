package com.tomergabel.examples.elasticsearch;

/**
 * Created by tomer on 9/4/12.
 */

import com.tomergabel.examples.stackexchange.Post;
import com.tomergabel.examples.stackexchange.PostEmitter;
import com.tomergabel.examples.stackexchange.PostType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.*;

/**
 * Created by tomer on 9/3/12.
 */
public class ESIndexer {
    private static final String base = "http://localhost:9200/";
    private static final String index = "serverfault/";
    private static final HttpClient client = new DefaultHttpClient();

    private static void createIndex() throws IOException {
        // Create index and set up mapping
        HttpDelete delete = new HttpDelete( base + index );
        try {
            client.execute( delete );
        } finally {
            delete.releaseConnection();
        }

        HttpPut req = new HttpPut( base + index );
        try {
            HttpEntity body = new StringEntity(
                    "{\"mappings\":{" +
                        "\"post\":{" +
                           "\"_id\":{\"index\":\"analyzed\",\"store\":\"yes\"}," +
                           "\"body\":{\"index\":\"analyzed\",\"store\":\"no\"}," +
                           "\"title\":{\"index\":\"analyzed\",\"store\":\"yes\",\"boost\":5.0}" +
                        "}" +
                    "}}" );
            req.setEntity( body );
            HttpResponse response = client.execute( req );
            assert response.getStatusLine().getStatusCode() == 200;
            EntityUtils.consumeQuietly( response.getEntity() );
        } finally {
            req.releaseConnection();
        }
    }

    @SuppressWarnings( "unchecked" )
    private static void sendDocument( final Post post ) throws IOException {
        JSONObject json = new JSONObject();
        json.put( "body", post.getBody() );
        json.put( "title", post.getTitle() );

        HttpPut req = new HttpPut( base + index + "/post/" + post.getId() );
        try {
            HttpEntity body = new StringEntity( json.toJSONString() );
            req.setEntity( body );
            HttpResponse response = client.execute( req );
            assert response.getStatusLine().getStatusCode() == 200;
            EntityUtils.consumeQuietly( response.getEntity() );
        } finally {
            req.releaseConnection();
        }
    }

    public static void main( String[] args ) throws Exception {

        // Iterate posts
        final XMLInputFactory factory = XMLInputFactory.newFactory();
        final File postFile = new File( args.length > 0 ? args[ 0 ] : "/Users/tomer/demo/serverfault/posts.xml" );
        final InputStream is = new BufferedInputStream( new FileInputStream( postFile ) );
        try {
            final XMLStreamReader reader = factory.createXMLStreamReader( is );
            final PostEmitter emitter = new PostEmitter( reader, PostType.QUESTION );

            createIndex();

            long start = System.currentTimeMillis();
            int count = 0;
            while ( emitter.hasNext() ) {
                final Post post = emitter.next();
                sendDocument( post );

                if ( ++count % 1000 == 0 )
                    System.err.print( String.format(
                            "\rProcessed %d documents (avg %f documents/second)",
                            count,
                            count * 1000.0f / ( System.currentTimeMillis() - start )
                    ) );
            }
        } finally {
            is.close();
        }

        System.err.println( "\nDone" );
    }
}
