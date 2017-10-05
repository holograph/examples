package com.tomergabel.examples.eventsourcing.persistence;

class InMemoryEventStoreTest extends EventStoreSpec {

    @Override
    protected EventStore instantiateStore() {
        return new InMemoryEventStore();
    }
}
