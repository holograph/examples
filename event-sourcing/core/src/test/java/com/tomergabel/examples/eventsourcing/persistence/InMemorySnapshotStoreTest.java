package com.tomergabel.examples.eventsourcing.persistence;

public class InMemorySnapshotStoreTest extends SnapshotStoreSpec {
    @Override
    protected SnapshotStore instantiateStore() {
        return new InMemorySnapshotStore();
    }
}
