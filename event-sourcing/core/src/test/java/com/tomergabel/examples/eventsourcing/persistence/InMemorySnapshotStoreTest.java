package com.tomergabel.examples.eventsourcing.persistence;

public class InMemorySnapshotStoreTest extends SnapshotStoreSpec {
    @Override
    protected SnapshotStore getStore() {
        return new InMemorySnapshotStore();
    }
}
