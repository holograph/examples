package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SampleSite;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySnapshotStoreTest extends SnapshotStoreSpec {
    @Override
    protected InMemorySnapshotStore getStore() {
        return new InMemorySnapshotStore();
    }

    private InMemorySnapshotStore store;

    @BeforeEach
    void setupStore() {
        store = getStore();
    }

    @Test
    public void getPersistedSnapshotReturnsEmptyOnMissingSite() {
        Optional<SiteSnapshot> result = store.getPersistedSnapshot(UUID.randomUUID(), 0);
        assertFalse(result.isPresent());
    }

    @Test
    public void getPersistedSnapshotReturnsEmptyOnMissingVersion() {
        SampleSite site = new SampleSite();

        store.persistSnapshot(site.finalState);

        Optional<SiteSnapshot> result = store.getPersistedSnapshot(site.id, 0);
        assertFalse(result.isPresent());
    }

    @Test
    public void getPersistedSnapshotReturnsCorrectSnapshot() {
        SampleSite site = new SampleSite();

        store.persistSnapshot(site.finalState);

        Optional<SiteSnapshot> result = store.getPersistedSnapshot(site.id, site.finalState.getVersion());
        assertTrue(result.isPresent());
        assertEquals(site.finalState, result.get());
    }
}
