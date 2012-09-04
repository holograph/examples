package com.tomergabel.examples.stackexchange;

import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by tomer on 9/4/12.
 */
public interface Post {
    long getId();
    PostType getPostType();
    Long getAcceptedAnswerId();         // Optional
    Long getParentId();                 // Optional
    DateTime getCreationDate();         // Required
    Integer getScore();
    Integer getViewCount();
    String getBody();
    Long getOwnerUserId();              // Optional, -1 for tag entries
    String getOwnerDisplayName();       // Optional
    Long getLastEditorUserId();
    String getLastEditorDisplayName();
    DateTime getLastEditDate();         // Required
    DateTime getLastActivityDate();     // Required
    String getTitle();
    Set<String> getTags();
    Integer getAnswerCount();           // Optional
    Integer getCommentCount();          // Optional
    Integer getFavoriteCount();         // Optional
    DateTime getClosedDate();           // Optional
    DateTime getCommunityOwnedDate();   // Optional
}
