package com.tomergabel.examples.eventsourcing.persistence;

class InMemoryEventStoreTest extends EventStoreSpec {

    private EventStore store = new InMemoryEventStore();

    @Override
    EventStore getStore() {
        return store;
    }
}
