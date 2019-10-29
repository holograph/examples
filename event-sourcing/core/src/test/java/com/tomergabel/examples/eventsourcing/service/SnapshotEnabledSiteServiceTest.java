package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.model.SiteCreated;
import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import com.tomergabel.examples.eventsourcing.persistence.InMemoryEventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemorySnapshotStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotEnabledSiteServiceTest extends SiteServiceSpec {

    private InMemoryEventStore eventStore = new InMemoryEventStore();
    private InMemorySnapshotStore snapshotStore = new InMemorySnapshotStore();
    private MockSnapshotStrategy snapshotStrategy = new MockSnapshotStrategy();
    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());

    @Override
    protected SiteService instantiateService() {
        return new SnapshotEnabledSiteService(eventStore, snapshotStore, snapshotStrategy, clock);
    }

    @BeforeEach
    void setupTest() {
        snapshotStrategy.reset();
    }

    @Test
    void getSnapshotQueriesStrategyIfEventTailExists() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        assertTrue(service.create(siteId, owner).isPresent());

        service.getSnapshot(siteId, null);

        List<SiteEvent> expected = Collections.singletonList(new SiteCreated(owner, clock.instant()));
        assertTrue(snapshotStrategy.queried());
        assertNull(snapshotStrategy.lastQueriedSnapshot);
        assertIterableEquals(expected, snapshotStrategy.lastQueriedTail);
    }

    @Test
    void getSnapshotPersistsNewSnapshotIfStrategyApproves() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        assertTrue(service.create(siteId, owner).isPresent());

        snapshotStrategy.response = true;
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);

        assertTrue(result.isPresent());
        assertEquals(result, snapshotStore.findLatestSnapshot(siteId, null));
    }

    @Test
    void getSnapshotUtilizesLatestSnapshotIfAvailable() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        OptionalLong atCreation = service.create(siteId, owner);
        assertTrue(atCreation.isPresent());
        SiteSnapshot expected =
                new SiteSnapshot(siteId, atCreation.getAsLong(), owner, mapper.createObjectNode(), false);
        snapshotStore.persistSnapshot(expected);

        // Snapshots are persisted on reads; no call to getSnapshot => only the snapshot explicitly persisted
        // in the test is available
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        assertEquals(siteId, snapshotStore.getLastQueriedSite());
    }
}
