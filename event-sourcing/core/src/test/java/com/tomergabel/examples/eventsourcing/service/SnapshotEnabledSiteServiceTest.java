package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemoryEventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemorySnapshotStore;
import com.tomergabel.examples.eventsourcing.persistence.SnapshotStore;

import java.time.Clock;

public class SnapshotEnabledSiteServiceTest extends SiteServiceSpec {
    private EventStore eventStore = new InMemoryEventStore();
    private SnapshotStore snapshotStore = new InMemorySnapshotStore();
    private SnapshotStrategy snapshotStrategy = (base, tail) -> true;

    @Override
    protected SiteService instantiateService() {
        return new SnapshotEnabledSiteService(eventStore, snapshotStore, snapshotStrategy, Clock.systemUTC());
    }
}
