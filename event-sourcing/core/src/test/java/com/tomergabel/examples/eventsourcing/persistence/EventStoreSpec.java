package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SampleSite;
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

import static org.junit.jupiter.api.Assertions.*;

public abstract class EventStoreSpec {

    protected abstract EventStore getStore();

    private EventStore store;

    @BeforeEach
    void setupStore() {
        store = getStore();
    }

    @Test
    void getEventsReturnsEmptySequenceForNonexistentSite() throws IOException {
        List<SiteEvent> events = store.getEvents(UUID.randomUUID());
        assertTrue(events.isEmpty());
    }

    @Test
    void getEventsReturnsFullEventStreamByDefault() throws IOException {
        SampleSite site = new SampleSite();
        store.addEvents(site.id, site.allEvents);

        List<SiteEvent> storedEvents = store.getEvents(site.id);

        assertIterableEquals(site.allEvents, storedEvents);
    }

    @Test
    void getEventsRespectsFromVersion() throws IOException {
        SampleSite site = new SampleSite();
        store.addEvents(site.id, site.allEvents);

        List<SiteEvent> eventTail = store.getEvents(site.id, site.updated3.getVersion(), null);

        assertIterableEquals(Arrays.asList(site.updated3, site.restored4, site.archived5), eventTail);
    }

    @Test
    void getEventsRespectsToVersion() throws IOException {
        SampleSite site = new SampleSite();
        store.addEvents(site.id, site.allEvents);

        List<SiteEvent> eventSlice = store.getEvents(site.id, null, site.updated3.getVersion());

        assertIterableEquals(Arrays.asList(site.created0, site.updated1, site.updated2, site.updated3), eventSlice);
    }

    @Test
    void addEventReturnsFalseIfVersionAlreadyExists() throws IOException {
        SampleSite site = new SampleSite();
        store.addEvents(site.id, Arrays.asList(site.created0, site.updated1));

        SiteEvent conflicting = new SiteDeleted(site.updated1.getVersion(), site.user, Instant.now());

        boolean result = store.addEvents(site.id, Collections.singletonList(conflicting));
        assertFalse(result);
    }
}
