package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.SampleSite;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SiteServiceSpec {

    private SiteService service;
    private ObjectMapper mapper = new ObjectMapper();

    protected abstract SiteService instantiateService();

    @BeforeAll
    void setupService() {
        service = instantiateService();
    }

    @Test
    void getSnapshotReturnsEmptyForNonexistentSite() throws IOException {
        Optional<SiteSnapshot> result = service.getSnapshot(UUID.randomUUID(), null);
        assertFalse(result.isPresent());
    }

    @Test
    void getSnapshotReturnsLatestSnapshotForExistingSiteByDefault() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        service.update(siteId, owner, 0, SampleSite.delta1);
        service.update(siteId, owner, 1, SampleSite.delta2);

        SiteSnapshot expected = new SiteSnapshot(siteId, 2, owner, SampleSite.blob2, false);

        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void getSnapshotReturnsAppropriateSnapshotIfVersionSpecified() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        service.update(siteId, owner, 0, SampleSite.delta1);
        service.update(siteId, owner, 1, SampleSite.delta2);

        SiteSnapshot expected = new SiteSnapshot(siteId, 1, owner, SampleSite.blob1, false);

        Optional<SiteSnapshot> result = service.getSnapshot(siteId, 1L);
        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
    }

    @Test
    void createReturnsTrueIfSiteCreatedSuccessfully() throws IOException {
        boolean result = service.create(UUID.randomUUID(), UUID.randomUUID());
        assertTrue(result);
    }
    
    @Test
    void createReturnsFalseIfSiteAlreadyExists() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);

        boolean result = service.create(siteId, owner);
        assertFalse(result);
    }

    @Test
    void updateReturnsEmptyForNonexistentSite() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();

        OptionalLong result = service.update(siteId, owner, 0, SampleSite.delta1);
        assertFalse(result.isPresent());
    }
    
    @Test
    void updateReturnsNewVersionIfSuccessful() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);

        OptionalLong result = service.update(siteId, owner, 0, SampleSite.delta1);
        assertTrue(result.isPresent());
        assertEquals(1L, result.getAsLong());
    }

    @Test
    void deleteReturnsEmptyForNonexistentSite() throws IOException {
        OptionalLong result = service.delete(UUID.randomUUID(), UUID.randomUUID());
        assertFalse(result.isPresent());
    }

    @Test
    void deleteReturnsNewVersionIfSuccessful() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);

        OptionalLong result = service.delete(siteId, owner);
        assertTrue(result.isPresent());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void deleteReturnsLatestVersionIfAlreadyDeleted() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        long version = service.delete(siteId, owner).getAsLong();

        OptionalLong result = service.delete(siteId, owner);
        assertTrue(result.isPresent());
        assertEquals(version, result.getAsLong());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void deleteReflectsInLatestSnapshot() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        long version = service.delete(siteId, owner).getAsLong();

        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);
        assertTrue(result.isPresent());
        assertTrue(result.get().getDeleted());
    }

    @Test
    void restoreReturnsEmptyForNonexistentSite() throws IOException {
        OptionalLong result = service.restore(UUID.randomUUID(), UUID.randomUUID(), 0);
        assertFalse(result.isPresent());
    }
    @Test
    void restoreReturnsEmptyForNonexistentVersionNumber() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);

        OptionalLong result = service.restore(siteId, owner, 1000);
        assertFalse(result.isPresent());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void restoreReturnsNewVersionIfSuccessful() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        long version = service.update(siteId, owner, 0, SampleSite.delta1).getAsLong();

        OptionalLong result = service.restore(siteId, owner, 0);
        assertTrue(result.isPresent());
        assertEquals(version + 1, result.getAsLong());
    }

    @Test
    void restoreReflectsInLatestSnapshot() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        service.create(siteId, owner);
        service.update(siteId, owner, 0, SampleSite.delta1).getAsLong();
        service.restore(siteId, owner, 0);

        Optional<SiteSnapshot> result = service.getSnapshot(siteId, null);
        assertTrue(result.isPresent());
        assertEquals(mapper.createObjectNode(), result.get().getBlob());
    }
}
