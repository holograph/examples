package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import com.tomergabel.examples.eventsourcing.testkit.SiteResourceDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static com.tomergabel.examples.eventsourcing.model.SampleSite.*;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class SiteResourceSpec {

    protected abstract WebTarget siteResource();
    private SiteResourceDriver driver;
    
    @BeforeEach
    public void setupDriver() {
        driver = new SiteResourceDriver(siteResource());
    }

    @Test
    public void getSnapshotReturns404OnNonexistentSite() {
        Response response = driver.getSiteRaw(UUID.randomUUID());

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getSnapshotReturnsCorrectDataForExistingSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long version = driver.createSite(siteId, owner);
        SiteSnapshot expected = new SiteSnapshot(siteId, version, owner, new ObjectMapper().createObjectNode(), false);

        SiteSnapshot result = driver.getSite(siteId);

        assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    @Test
    public void getSnapshotAtVersionReturnsCorrectDataForExistingVersion() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = driver.createSite(siteId, owner);
        long v1 = driver.updateSite(siteId, v0, owner, delta1);
        long v2 = driver.updateSite(siteId, v1, owner, delta2);
        SiteSnapshot expected = new SiteSnapshot(siteId, v1, owner, blob1, false);

        SiteSnapshot result = driver.getSite(siteId, v1);

        assertEquals(expected, result);
    }

    @Test
    public void getSnapshotAtVersionReturnsLatestAvailableSnapshotOnNonexistentVersion() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = driver.createSite(siteId, owner);
        long v1 = driver.updateSite(siteId, v0, owner, delta1);
        long v2 = driver.updateSite(siteId, v1, owner, delta2);
        SiteSnapshot expected = new SiteSnapshot(siteId, v2, owner, blob2, false);

        SiteSnapshot result = driver.getSite(siteId, v2 + 5 /* Some arbitrary future version */);

        assertEquals(expected, result);
    }

    @Test
    public void updateSiteReturns409ForConflictingEvents() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = driver.createSite(siteId, owner);
        @SuppressWarnings("unused") long v1 = driver.updateSite(siteId, v0, owner, delta1);

        Response response = driver.updateSiteRaw(siteId, v0, owner, delta3);  // Note conflicting update with v1

        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void createSiteReturns409ForExistingSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        driver.createSite(siteId, owner);

        Response response = driver.createSiteRaw(siteId, owner);              // Note conflicting creation

        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void deleteSiteReturns404ForNonexistentSite() {
        Response response = driver.deleteSiteRaw(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void restoreSiteReturns404ForNonexistentSite() {
        Response response = driver.restoreSiteRaw(UUID.randomUUID(), UUID.randomUUID(), 0L);

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void restoreSiteReturns400ForNonexistentTargetVersion() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        driver.createSite(siteId, owner);

        Response response = driver.restoreSiteRaw(siteId, owner, 14L);

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void restoreSiteReturnsNewVersionRepresentingCorrectState() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = driver.createSite(siteId, owner);
        long v1 = driver.updateSite(siteId, v0, owner, delta1);
        @SuppressWarnings("unused") long v2 = driver.updateSite(siteId, v1, owner, delta2);

        long restored = driver.restoreSite(siteId, owner, v1);
        SiteSnapshot result = driver.getSite(siteId);

        assertEquals(restored, result.getVersion());
        assertEquals(blob1, result.getBlob());
    }

    @Test
    public void getSnapshotReturns404OnDeletedSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        driver.createSite(siteId, owner);
        driver.deleteSite(siteId, owner);

        Response response = driver.getSiteRaw(siteId);

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getSnapshotAtVersionReflectsDeletionStatusOfSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        driver.createSite(siteId, owner);
        long deleted = driver.deleteSite(siteId, owner);

        SiteSnapshot response = driver.getSite(siteId, deleted);

        assertTrue(response.getDeleted());
    }

    @Test
    public void getSnapshotReturnsCorrectStateAfterRestoringDeletedSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = driver.createSite(siteId, owner);
        long v1 = driver.updateSite(siteId, v0, owner, delta1);
        @SuppressWarnings("unused") long v2 = driver.deleteSite(siteId, owner);

        long restored = driver.restoreSite(siteId, owner, v1);
        SiteSnapshot result = driver.getSite(siteId);

        assertEquals(restored, result.getVersion());
        assertEquals(blob1, result.getBlob());
    }
}