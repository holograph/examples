package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;

import java.util.List;
import java.util.UUID;

public interface EventStore {
    List<SiteEvent> getEvents(UUID siteId, Long from, Long to);
    boolean addEvents(UUID siteId, List<SiteEvent> event);

    default List<SiteEvent> getEvents(UUID siteId) {
        return getEvents(siteId, null, null);
    }
}
