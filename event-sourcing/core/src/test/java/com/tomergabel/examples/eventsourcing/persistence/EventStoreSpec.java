package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteDeleted;
import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    protected abstract EventStore instantiateStore();

    private EventStore store;

    @BeforeEach
    void setupStore() {
        store = instantiateStore();
    }

    @Test
    void getEventsReturnsEmptySequenceForNonexistentSite() throws IOException {
        List<SiteEvent> events = store.getEvents(UUID.randomUUID());
        assertTrue(events.isEmpty());
    }

    @Test
    void getEventsReturnsFullEventStreamByDefault() throws IOException {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> storedEvents = store.getEvents(siteId);

        assertIterableEquals(allEvents, storedEvents);
    }

    @Test
    void getEventsRespectsFromVersion() throws IOException {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> eventTail = store.getEvents(siteId, updated3.getVersion(), null);

        assertIterableEquals(Arrays.asList(updated3, restored4, archived5), eventTail);
    }

    @Test
    void getEventsRespectsToVersion() throws IOException {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, allEvents);

        List<SiteEvent> eventSlice = store.getEvents(siteId, null, updated3.getVersion());

        assertIterableEquals(Arrays.asList(created0, updated1, updated2, updated3), eventSlice);
    }

    @Test
    void addEventReturnsFalseIfVersionAlreadyExists() throws IOException {
        UUID siteId = UUID.randomUUID();
        store.addEvents(siteId, Arrays.asList(created0, updated1));

        SiteEvent conflicting = new SiteDeleted(updated1.getVersion(), user, Instant.now());

        boolean result = store.addEvents(siteId, Collections.singletonList(conflicting));
        assertFalse(result);
    }
}
