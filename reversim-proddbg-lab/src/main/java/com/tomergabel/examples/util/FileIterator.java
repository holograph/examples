package com.tomergabel.examples.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
* Created by tomer on 2/18/13.
*/
@SuppressWarnings( "ConstantConditions" )
public class FileIterator implements Iterator<File> {
    private Stack<Iterator<File>> _fsStack = new Stack<Iterator<File>>();
    private FileFilter _filter;

    private Iterator<File> iteratorFor( File root ) {
        if ( !root.isDirectory() ) throw new IllegalArgumentException( "Root must be a directory." );
        return Arrays.asList( root.listFiles( _filter ) ).iterator();
    }

    public FileIterator( final File root, final FileFilter filter ) {
        // Accept directories + delegate to optional filter
        _filter = new FileFilter() {
            public boolean accept( File pathname ) {
                return pathname.isDirectory() || ( filter != null && filter.accept( pathname ) );
            }
        };

        _fsStack.push( iteratorFor( root ) );
    }
    public FileIterator( final File root ) { this( root, null ); }

    private Stack<File> _next = new Stack<File>();
    private boolean walk() {
        while ( !_fsStack.isEmpty() ) {
            final Iterator<File> current = _fsStack.peek();

            if ( current.hasNext() ) {
                final File f = current.next();
                if ( f.isFile() ) {
                    _next.push( f );
                    return true;
                } else if ( f.isDirectory() )
                    _fsStack.push( iteratorFor( f ) );
            } else
                _fsStack.pop();
        }
        return false;
    }

    public boolean hasNext() { return !_next.empty() || walk(); }

    public File next() {
        if ( !hasNext() ) throw new NoSuchElementException();
        return _next.pop();
    }

    public void remove() { throw new UnsupportedOperationException(); }
}
