package com.tomergabel.examples.eventsourcing.testkit;

import com.tomergabel.examples.eventsourcing.model.SampleSite;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.service.SiteServiceSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TestableSiteServiceTest extends SiteServiceSpec {
    @Override
    protected SiteService instantiateService() {
        return new TestableSiteService();
    }

    TestableSiteService siteService;

    @BeforeEach
    public void initializeService() {
        siteService = new TestableSiteService();
    }

    @Test
    public void forceSnapshotReturnsEmptyOnMissingSite() throws IOException {
        Optional<SiteSnapshot> result = siteService.forceSnapshot(UUID.randomUUID(), 0L);
        assertFalse(result.isPresent());
    }

    @Test
    public void forceSnapshotReturnsEmptyOnMissingVersion() throws IOException {
        UUID siteId = UUID.randomUUID();
        OptionalLong version = siteService.create(siteId, UUID.randomUUID());
        assertTrue(version.isPresent());

        Optional<SiteSnapshot> result = siteService.forceSnapshot(siteId, 5L);
        assertFalse(result.isPresent());
    }

    @Test
    public void forceSnapshotReturnsPreexistingSnapshotIfAvailable() throws IOException {
        SampleSite site = new SampleSite();
        siteService.snapshotStore().persistSnapshot(site.finalState);

        Optional<SiteSnapshot> result = siteService.forceSnapshot(site.id, site.finalState.getVersion());
        assertTrue(result.isPresent());
        assertEquals(site.finalState, result.get());
    }

    @Test
    public void forceSnapshotReturnsNewlyPersistedSnapshot() throws IOException {
        SampleSite site = new SampleSite();
        siteService.create(site.id, site.owner);
        siteService.update(site.id, site.owner, 0L, SampleSite.delta1);
        siteService.update(site.id, site.owner, 1L, SampleSite.delta2);
        siteService.update(site.id, site.owner, 2L, SampleSite.delta3);
        siteService.update(site.id, site.owner, 3L, SampleSite.delta4);
        siteService.delete(site.id, site.owner);

        Optional<SiteSnapshot> result = siteService.forceSnapshot(site.id, site.finalState.getVersion());
        assertTrue(result.isPresent());
        assertEquals(site.finalState, result.get());
    }
}