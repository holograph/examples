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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
public class SnapshotEnabledSiteServiceTest extends SiteServiceSpec {

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
        service.create(siteId, owner).getAsLong();

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
        service.create(siteId, owner).getAsLong();

        snapshotStrategy.response = true;
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);

        assertTrue(result.isPresent());
        assertEquals(result, snapshotStore.findLatestSnapshot(siteId, null));
    }

    @Test
    void getSnapshotUtilizesLatestSnapshotIfAvailable() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long atCreation = service.create(siteId, owner).getAsLong();
        SiteSnapshot expected = new SiteSnapshot(siteId, atCreation, owner, mapper.createObjectNode(), false);
        snapshotStore.persistSnapshot(expected);

        // Snapshots are persisted on reads; no call to getSnapshot => only the snapshot explicitly persisted
        // in the test is available
        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        assertEquals(siteId, snapshotStore.getLastQueriedSite());
    }

    private static class MockSnapshotStrategy implements SnapshotStrategy {
        SiteSnapshot lastQueriedSnapshot = null;
        List<SiteEvent> lastQueriedTail = null;
        boolean response = false;

        @Override
        public boolean shouldTakeSnapshot(SiteSnapshot baseSnapshot, List<SiteEvent> tail) {
            lastQueriedSnapshot = baseSnapshot;
            lastQueriedTail = tail;
            return response;
        }

        boolean queried() {
            return lastQueriedSnapshot != null || lastQueriedTail != null;
        }

        void reset() {
            lastQueriedTail = null;
            lastQueriedSnapshot = null;
            response = false;
        }
    }

}
