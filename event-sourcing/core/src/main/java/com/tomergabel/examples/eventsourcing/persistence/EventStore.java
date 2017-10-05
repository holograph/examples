package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface EventStore {
    List<SiteEvent> getEvents(UUID siteId, Long from, Long to) throws IOException;
    boolean addEvents(UUID siteId, List<SiteEvent> events) throws IOException;

    default List<SiteEvent> getEvents(UUID siteId) throws IOException {
        return getEvents(siteId, null, null);
    }
}
