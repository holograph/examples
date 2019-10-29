package com.tomergabel.examples.eventsourcing.testkit;

import com.tomergabel.examples.eventsourcing.persistence.InMemoryEventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemorySnapshotStore;
import com.tomergabel.examples.eventsourcing.service.MockSnapshotStrategy;
import com.tomergabel.examples.eventsourcing.service.SnapshotEnabledSiteService;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

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
}