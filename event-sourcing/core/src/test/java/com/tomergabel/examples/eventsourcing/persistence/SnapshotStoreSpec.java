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
    
    protected abstract SnapshotStore getStore();
    
    private SnapshotStore store;
    
    @BeforeEach
    void setupStore() {
        store = getStore();
    }
    
    @Test
    void findLatestSnapshotEmptyOnNonexistentSite() throws IOException {
        Optional<SiteSnapshot> snapshot = store.findLatestSnapshot(UUID.randomUUID(), null);
        assertFalse(snapshot.isPresent());
    }

    @Test
    void findLatestSnapshotReturnsLatestAvailableSnapshotIfNoVersionSpecified() throws IOException {
        SampleSite site = new SampleSite();
        store.persistSnapshot(site.intermediateState);
        store.persistSnapshot(site.finalState);

        Optional<SiteSnapshot> snapshot = store.findLatestSnapshot(site.id, null);
        assertTrue(snapshot.isPresent());
        assertEquals(site.finalState, snapshot.get());
    }
    
    @Test
    void findLatestSnapshotEmptyOnExistingSiteWithNoSuitableSnapshot() throws IOException {
        SampleSite site = new SampleSite();
        store.persistSnapshot(site.finalState);

        Optional<SiteSnapshot> snapshot =
                store.findLatestSnapshot(site.id, site.finalState.getVersion() - 1);
        assertFalse(snapshot.isPresent());
    }
    
    @Test
    void findLatestSnapshotReturnsSuitableSnapshotIfVersionSpecified() throws IOException {
        SampleSite site = new SampleSite();
        store.persistSnapshot(site.intermediateState);
        store.persistSnapshot(site.finalState);

        Optional<SiteSnapshot> snapshot =
                store.findLatestSnapshot(site.id, site.finalState.getVersion() - 1);

        assertTrue(snapshot.isPresent());
        assertEquals(site.intermediateState, snapshot.get());
    }
    
    @Test
    void persistSnapshotReturnsFalseIfSnapshotAlreadyAvailableForVersion() throws IOException {
        SampleSite site = new SampleSite();
        store.persistSnapshot(site.finalState);

        boolean result = store.persistSnapshot(site.finalState);
        assertFalse(result);
    }
}
