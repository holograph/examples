package com.tomergabel.examples.eventsourcing.testkit;

import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import com.tomergabel.examples.eventsourcing.persistence.InMemoryEventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemorySnapshotStore;
import com.tomergabel.examples.eventsourcing.service.MockSnapshotStrategy;
import com.tomergabel.examples.eventsourcing.service.SnapshotEnabledSiteService;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

public class TestableSiteService extends SnapshotEnabledSiteService {

    public TestableSiteService() {
        super(new InMemoryEventStore(),
              new InMemorySnapshotStore(),
              new MockSnapshotStrategy(),
              Clock.fixed(Instant.now(), ZoneId.systemDefault())
        );
    }

    public InMemoryEventStore eventStore() {
        return (InMemoryEventStore) this.eventStore;
    }

    public InMemorySnapshotStore snapshotStore() {
        return (InMemorySnapshotStore) this.snapshotStore;
    }

    public MockSnapshotStrategy snapshotStrategy() {
        return (MockSnapshotStrategy) this.snapshotStrategy;
    }

    public Clock clock() {
        return this.clock;
    }

    public Optional<SiteSnapshot> forceSnapshot(UUID siteId, long atVersion) throws IOException {
        Optional<SiteSnapshot> existing = snapshotStore().getPersistedSnapshot(siteId, atVersion);
        if (existing.isPresent())
            return existing;

        Optional<SiteSnapshot> snapshot = getSnapshot(siteId, atVersion);
        if (!snapshot.isPresent() || snapshot.get().getVersion() != atVersion)
            return Optional.empty();
        if (!snapshotStore().persistSnapshot(snapshot.get()))
            throw new IllegalStateException("Failed to persist snapshot");
        return snapshot;
    }
}