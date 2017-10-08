package com.tomergabel.examples.eventsourcing.persistence;

class InMemoryEventStoreTest extends EventStoreSpec {

    @Override
    protected EventStore getStore() {
        return new InMemoryEventStore();
    }
}
