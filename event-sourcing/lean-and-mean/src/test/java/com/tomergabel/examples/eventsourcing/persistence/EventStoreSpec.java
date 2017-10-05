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

    abstract EventStore getStore();
    
    @Test
    void getEventsReturnsEmptySequenceForNonexistentSite() {
        List<SiteEvent> events = getStore().getEvents(UUID.randomUUID());
        assertTrue(events.isEmpty());
    }

    @Test
    void getEventsReturnsFullEventStreamByDefault() {
        UUID siteId = UUID.randomUUID();
        getStore().addEvents(siteId, allEvents);

        List<SiteEvent> storedEvents = getStore().getEvents(siteId);

        assertIterableEquals(allEvents, storedEvents);
    }

    @Test
    void getEventsRespectsFromVersion() {
        UUID siteId = UUID.randomUUID();
        getStore().addEvents(siteId, allEvents);

        List<SiteEvent> eventTail = getStore().getEvents(siteId, updated3.getVersion(), null);

        assertIterableEquals(Arrays.asList(updated3, restored4, archived5), eventTail);
    }

    @Test
    void getEventsRespectsToVersion() {
        UUID siteId = UUID.randomUUID();
        getStore().addEvents(siteId, allEvents);

        List<SiteEvent> eventSlice = getStore().getEvents(siteId, null, updated3.getVersion());

        assertIterableEquals(Arrays.asList(created0, updated1, updated2, updated3), eventSlice);
    }

    @Test
    void addEventReturnsFalseIfVersionAlreadyExists() {
        UUID siteId = UUID.randomUUID();
        getStore().addEvents(siteId, Arrays.asList(created0, updated1));

        SiteEvent conflicting = new SiteDeleted(updated1.getVersion(), user, Instant.now());

        boolean result = getStore().addEvents(siteId, Collections.singletonList(conflicting));
        assertFalse(result);
    }
}
