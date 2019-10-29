package com.tomergabel.examples.eventsourcing.siterestorer;

import com.tomergabel.examples.eventsourcing.model.SampleSite;
import com.tomergabel.examples.eventsourcing.testkit.TestableSiteService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SiteRestorerTest {

    private TestableSiteService service = new TestableSiteService();
    private SiteRestorer restorer = new SiteRestorer(service.eventStore(), service.snapshotStore(), service);

    @Test
    void findVersionsReturnsEmptyForExistingSiteWithNoSnapshots() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        assertTrue(service.create(siteId, ownerId).isPresent());

        List<Long> result = restorer.findVersions(siteId);
        assertTrue(result.isEmpty());
    }

    @Test
    void findVersionsReturnsAvailableSnapshotsForExistingSite() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        service.create(siteId, ownerId);
        service.forceSnapshot(siteId, 0);
        service.update(siteId, ownerId, 0, SampleSite.delta1);
        service.forceSnapshot(siteId, 1);
        service.update(siteId, ownerId, 1, SampleSite.delta2);

        List<Long> result = restorer.findVersions(siteId);
        assertTrue(result.contains(0L));
        assertTrue(result.contains(1L));
    }

    @Test
    void findVersionsIgnoresLatestVersionOfSite() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        service.create(siteId, ownerId);
        service.update(siteId, ownerId, 0, SampleSite.delta1);
        service.update(siteId, ownerId, 1, SampleSite.delta2);
        service.forceSnapshot(siteId, 2L);

        List<Long> result = restorer.findVersions(siteId);
        assertTrue(result.isEmpty());
    }

    @Test
    void restoreSiteCorrectlyRollsBackToSpecifiedVersion() throws IOException {
        UUID siteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        service.create(siteId, ownerId);
        service.update(siteId, ownerId, 0, SampleSite.delta1);
        service.update(siteId, ownerId, 1, SampleSite.delta2);

        OptionalLong version = restorer.restoreSite(siteId, ownerId, 1);
        assertTrue(version.isPresent());
        assertEquals(3L, version.getAsLong());

    }
}