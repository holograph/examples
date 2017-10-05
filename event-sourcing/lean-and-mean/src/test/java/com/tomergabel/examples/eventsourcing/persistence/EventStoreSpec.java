package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteDeleted;
import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.tomergabel.examples.eventsourcing.model.SampleSite.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class EventStoreSpec {

    protected EventStore store;

    @Test
    void getEventsReturnsEmptySequenceForNonexistentSite() {
        List<SiteEvent> events = store.getEvents(UUID.randomUUID());
        assertTrue(events.isEmpty());
    }

    @Test
    void getEventsReturnsFullEventStreamByDefault() {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> storedEvents = store.getEvents(siteId);

        assertIterableEquals(allEvents, storedEvents);
    }

    @Test
    void getEventsRespectsFromVersion() {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> eventTail = store.getEvents(siteId, updated3.getVersion(), null);

        assertIterableEquals(eventTail, Arrays.asList(updated3, restored4, archived5));
    }

    @Test
    void getEventsRespectsToVersion() {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> eventSlice = store.getEvents(siteId, null, updated3.getVersion());

        assertIterableEquals(eventSlice, Arrays.asList(created0, updated1, updated2, updated3));
    }

    @Test
    void addEventReturnsFalseIfVersionAlreadyExists() {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, Arrays.asList(created0, updated1));

        SiteEvent conflicting = new SiteDeleted(updated1.getVersion(), user, Instant.now());

        boolean result = store.addEvents(siteId, Collections.singletonList(conflicting));
        assertFalse(result);
    }
}
