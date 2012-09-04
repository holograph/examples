package com.tomergabel.examples.stackexchange;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.xml.stream.XMLStreamConstants.*;

/**
 * Created by tomer on 9/4/12.
 */
public class PostEmitter implements Iterator<Post> {

    // Helper logic

    // Sample: "2009-04-30T06:49:01.807", similar to ISO but without the Z
    private static DateTimeFormatter localizedDateFormat = new DateTimeFormatterBuilder()
            .appendYear( 4, 4 )
            .appendLiteral( '-' ).appendMonthOfYear( 2 )
            .appendLiteral( '-' ).appendDayOfMonth( 2 )
            .appendLiteral( 'T' ).appendHourOfDay( 2 )
            .appendLiteral( ':' ).appendMinuteOfHour( 2 )
            .appendLiteral( ':' ).appendSecondOfMinute( 2 )
            .appendLiteral( '.' ).appendMillisOfSecond( 3 )
            .toFormatter();
    private static DateTime parseDate( final String s ) {
        return localizedDateFormat.parseLocalDateTime( s ).toDateTime( DateTimeZone.UTC );
    }

    private static Pattern tagMatcher = Pattern.compile( "<(.*)>" );
    private static Set<Integer> ignoredTags = new HashSet<Integer>( Arrays.asList( CHARACTERS ) );

    private enum State {
        START,
        PENDING,
        READING,
        DONE
    }

    private static class PostBuilder {
        public Long id;
        public PostType type;
        public Long acceptedAnswerId;
        public Long parentId;
        public DateTime creationDate;
        public Integer score;
        public Integer viewCount;
        public String body;
        public Long ownerUserId;
        public String ownerDisplayName;
        public Long lastEditorUserId;
        public String lastEditorDisplayName;
        public DateTime lastEditDate;
        public DateTime lastActivityDate;
        public String title;
        public Set<String> tags;
        public Integer answerCount;
        public Integer commentCount;
        public Integer favoriteCount;
        public DateTime closedDate;
        public DateTime communityOwnedDate;

        public Post build() throws IllegalStateException {
            if ( id == null ) throw new IllegalStateException( "Post ID not set" );
            if ( type == null ) throw new IllegalStateException( "Post type not set" );
            if ( creationDate == null ) throw new IllegalStateException( "Creation date not set" );
            if ( body == null ) throw new IllegalStateException( "Body not set" );

            return new Post() {
                @Override public long getId() { return id; }
                @Override public PostType getPostType() { return type; }
                @Override public Long getAcceptedAnswerId() { return acceptedAnswerId; }
                @Override public Long getParentId() { return parentId; }
                @Override public DateTime getCreationDate() { return creationDate; }
                @Override public Integer getScore() { return score; }
                @Override public Integer getViewCount() { return viewCount; }
                @Override public String getBody() { return body; }
                @Override public Long getOwnerUserId() { return ownerUserId; }
                @Override public String getOwnerDisplayName() { return ownerDisplayName; }
                @Override public Long getLastEditorUserId() { return lastEditorUserId; }
                @Override public String getLastEditorDisplayName() { return lastEditorDisplayName; }
                @Override public DateTime getLastEditDate() { return lastEditDate; }
                @Override public DateTime getLastActivityDate() { return lastActivityDate; }
                @Override public String getTitle() { return title; }
                @Override public Set<String> getTags() { return tags; }
                @Override public Integer getAnswerCount() { return answerCount; }
                @Override public Integer getCommentCount() { return commentCount; }
                @Override public Integer getFavoriteCount() { return favoriteCount; }
                @Override public DateTime getClosedDate() { return closedDate; }
                @Override public DateTime getCommunityOwnedDate() { return communityOwnedDate; }
            };
        }
    }

    private static Integer parseSafeInt( final String s ) {
        if ( s == null ) return null;
        if ( s.isEmpty() ) return null;
        return Integer.valueOf( s );
    }

    // Initialization
    private final XMLStreamReader _reader;
    private final Set<PostType> _typeFilter;

    public PostEmitter( XMLStreamReader reader, PostType... typeFilter ) {
        this._reader = reader;
        if ( typeFilter.length > 0 )
            this._typeFilter = EnumSet.copyOf( Arrays.asList( typeFilter ) );
        else
            this._typeFilter = EnumSet.allOf( PostType.class );
    }

    // State management
    private Post _next = null;
    private PostBuilder _builder = null;
    private State _state = State.START;

    boolean poll() throws XMLStreamException {
        while ( _reader.hasNext() ) {
            int tag = _reader.next();
            if ( ignoredTags.contains( tag ) )
                continue;

            switch ( _state ) {
                case START:
                    switch ( tag ) {
                        case START_DOCUMENT:
                            break;

                        case START_ELEMENT:
                            if ( "posts".equals( _reader.getName().getLocalPart() ) ) {
                                _state = State.PENDING;
                                break;
                            } else throw new XMLStreamException( "Unexpected element", _reader.getLocation() );

                        default:
                            throw new XMLStreamException( "Unexpected tag " + tag, _reader.getLocation() );
                    }
                    break;

                case PENDING:
                    switch ( tag ) {
                        case START_ELEMENT:
                            if ( "row".equals( _reader.getName().getLocalPart() ) ) {
                                _state = State.READING;
                                _builder = new PostBuilder();
                                for ( int i = 0; i < _reader.getAttributeCount(); ++i ) {
                                    final String attr = _reader.getAttributeLocalName( i );
                                    final String value = _reader.getAttributeValue( i );
                                    if ( "Id".equals( attr ) )
                                        _builder.id = Long.parseLong( value );
                                    else if ( "PostTypeId".equals( attr ) )
                                        _builder.type = PostType.fromId( Integer.parseInt( value ) );
                                    else if ( "AcceptedAnswerId".equals( attr ) )
                                        _builder.acceptedAnswerId = Long.parseLong( value );
                                    else if ( "ParentId".equals( attr ) )
                                        _builder.parentId = Long.parseLong( value );
                                    else if ( "CreationDate".equals( attr ) )
                                        _builder.creationDate = parseDate( value );
                                    else if ( "Score".equals( attr ) )
                                        _builder.score = Integer.valueOf( value );
                                    else if ( "ViewCount".equals( attr ) )
                                        _builder.viewCount = parseSafeInt( value );
                                    else if ( "Body".equals( attr ) )
                                        _builder.body = value;
                                    else if ( "OwnerUserId".equals( attr ) )
                                        _builder.ownerUserId = Long.parseLong( value );
                                    else if ( "OwnerDisplayName".equals( attr ) )
                                        _builder.ownerDisplayName = value;
                                    else if ( "LastEditorUserId".equals( attr ) )
                                        _builder.lastEditorUserId = Long.parseLong( value );
                                    else if ( "LastEditorDisplayName".equals( attr ) )
                                        _builder.lastEditorDisplayName = value;
                                    else if ( "LastEditDate".equals( attr ) )
                                        _builder.lastEditDate = parseDate( value );
                                    else if ( "LastActivityDate".equals( attr ) )
                                        _builder.lastActivityDate = parseDate( value );
                                    else if ( "Title".equals( attr ) )
                                        _builder.title = value;
                                    else if ( "Tags".equals( attr ) ) {
                                        final Set<String> tags = new HashSet<String>();
                                        final Matcher m = tagMatcher.matcher( value );
                                        while ( m.find() )
                                            tags.add( m.group( 1 ) );
                                        _builder.tags = Collections.unmodifiableSet( tags );
                                    } else if ( "AnswerCount".equals( attr ) )
                                        _builder.answerCount = Integer.parseInt( value );
                                    else if ( "CommentCount".equals( attr ) )
                                        _builder.commentCount = Integer.parseInt( value );
                                    else if ( "FavoriteCount".equals( attr ) )
                                        _builder.favoriteCount = Integer.parseInt( value );
                                    else if ( "ClosedDate".equals( attr ) )
                                        _builder.closedDate = parseDate( value );
                                    else if ( "CommunityOwnedDate".equals( attr ) )
                                        _builder.communityOwnedDate = parseDate( value );
                                }
                                break;
                            } else throw new XMLStreamException( "Unexpected element", _reader.getLocation() );

                        case END_ELEMENT:
                            if ( "posts".equals( _reader.getLocalName() ) ) {
                                _state = State.DONE;
                                return false;
                            } else throw new XMLStreamException( "Unexpected end of element", _reader.getLocation() );

                        default:
                            throw new XMLStreamException( "Unexpected tag " + tag, _reader.getLocation() );
                    }
                    break;

                case READING:
                    switch ( tag ) {
                        case END_ELEMENT:
                            if ( "row".equals( _reader.getName().getLocalPart() ) ) {
                                _state = State.PENDING;
                                if ( _builder.type != null && _typeFilter.contains( _builder.type ) ) {
                                    // All is well, emit
                                    _next = _builder.build();
                                    return true;
                                }
                                break;
                            } else throw new XMLStreamException( "Unexpected element", _reader.getLocation() );

                        default:
                            throw new XMLStreamException( "Unexpected tag " + tag, _reader.getLocation() );
                    }
                    break;

                case DONE:
                    switch ( tag ) {
                        case END_DOCUMENT:
                            return false;
                        default:
                            throw new XMLStreamException( "Unexpected tag " + tag, _reader.getLocation() );
                    }

                default:
                    throw new IllegalStateException( "Unexpected state " + _state );
            }
        }

        return false;
    }

    @Override
    public boolean hasNext() {
        try {
            return _next != null || poll();
        } catch( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Post next() {
        if ( !hasNext() ) throw new NoSuchElementException();   // Also polls
        final Post post = _next;
        _next = null;
        return post;
    }

    @Override
    public void remove() { throw new UnsupportedOperationException(); }
}
