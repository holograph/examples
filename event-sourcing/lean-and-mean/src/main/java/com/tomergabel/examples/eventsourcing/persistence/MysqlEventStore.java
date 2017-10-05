package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;

import java.util.List;
import java.util.UUID;

public class MysqlEventStore implements EventStore {



    @Override
    public List<SiteEvent> getEvents(UUID siteId, Long from, Long to) {
        return null;
    }

    @Override
    public boolean addEvents(UUID siteId, List<SiteEvent> events) {
        return false;
    }

    @Override
    public List<SiteEvent> getEvents(UUID siteId) {
        return null;
    }
}
