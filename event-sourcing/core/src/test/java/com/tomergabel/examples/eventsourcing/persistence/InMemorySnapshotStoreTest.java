package com.tomergabel.examples.eventsourcing.persistence;

class InMemorySnapshotStoreTest extends SnapshotStoreSpec {
    @Override
    protected SnapshotStore getStore() {
        return new InMemorySnapshotStore();
    }
}
