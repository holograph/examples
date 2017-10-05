package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SampleSite;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SnapshotStoreSpec {
    
    protected abstract SnapshotStore instantiateStore();
    
    private SnapshotStore store;
    
    @BeforeEach
    void setupStore() {
        store = instantiateStore();
    }
    
    @Test
    void findLatestSnapshotEmptyOnNonexistentSite() throws IOException {
        Optional<SiteSnapshot> snapshot = store.findLatestSnapshot(UUID.randomUUID(), null);
        assertFalse(snapshot.isPresent());
    }

    @Test
    void findLatestSnapshotReturnsLatestAvailableSnapshotIfNoVersionSpecified() throws IOException {
        store.persistSnapshot(SampleSite.intermediateState);
        store.persistSnapshot(SampleSite.finalState);

        Optional<SiteSnapshot> snapshot = store.findLatestSnapshot(SampleSite.siteId, null);
        assertTrue(snapshot.isPresent());
        assertEquals(SampleSite.finalState, snapshot.get());
    }
    
    @Test
    void findLatestSnapshotEmptyOnExistingSiteWithNoSuitableSnapshot() throws IOException {
        store.persistSnapshot(SampleSite.finalState);

        Optional<SiteSnapshot> snapshot =
                store.findLatestSnapshot(SampleSite.siteId, SampleSite.finalState.getVersion() - 1);
        assertFalse(snapshot.isPresent());
    }
    
    @Test
    void findLatestSnapshotReturnsSuitableSnapshotIfVersionSpecified() throws IOException {
        store.persistSnapshot(SampleSite.intermediateState);
        store.persistSnapshot(SampleSite.finalState);

        Optional<SiteSnapshot> snapshot =
                store.findLatestSnapshot(SampleSite.siteId, SampleSite.finalState.getVersion() - 1);

        assertTrue(snapshot.isPresent());
        assertEquals(SampleSite.intermediateState, snapshot.get());
    }
    
    @Test
    void persistSnapshotReturnsFalseIfSnapshotAlreadyAvailableForVersion() throws IOException {
        store.persistSnapshot(SampleSite.finalState);

        boolean result = store.persistSnapshot(SampleSite.finalState);
        assertFalse(result);
    }
}
