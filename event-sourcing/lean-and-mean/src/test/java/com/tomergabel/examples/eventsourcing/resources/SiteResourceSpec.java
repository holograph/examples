package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static com.tomergabel.examples.eventsourcing.model.SampleSite.*;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class SiteResourceSpec {

    protected abstract WebTarget sites();

    // Driver --
    private Invocation.Builder resource(UUID siteId, Long atVersion) {
        return sites().path("/" + siteId + (atVersion != null ? ("/versions/" + atVersion) : "")).request();
    }
    private Invocation.Builder resource(UUID siteId) {
        return resource(siteId, null);
    }
    private SiteSnapshot getSite(UUID siteId, Long atVersion) {
        return resource(siteId, atVersion).get(SiteSnapshot.class);
    }
    private SiteSnapshot getSite(UUID siteId) {
        return getSite(siteId, null);
    }
    private Response getSiteRaw(UUID siteId) {
        return resource(siteId).get();
    }
    private long createSite(UUID siteId, UUID owner) {
        return resource(siteId).post(Entity.json(new CreateSiteRequest(owner)), VersionResponse.class).getVersion();
    }
    private Response createSiteRaw(UUID siteId, UUID owner) {
        return resource(siteId).post(Entity.json(new CreateSiteRequest(owner)));
    }
    private long updateSite(UUID siteId, long atVersion, UUID user, JsonNode delta) {
        return resource(siteId, atVersion)
                .method("PATCH", Entity.json(new UpdateSiteRequest(user, delta)), VersionResponse.class).getVersion();
    }
    private Response updateSiteRaw(UUID siteId, long atVersion, UUID user, JsonNode delta) {
        return resource(siteId, atVersion).method("PATCH", Entity.json(new UpdateSiteRequest(user, delta)));
    }

    @Test
    public void getSnapshotReturns404OnNonexistentSite() {
        Response response = getSiteRaw(UUID.randomUUID());

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void getSnapshotReturnsCorrectDataForExistingSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long version = createSite(siteId, owner);
        SiteSnapshot expected = new SiteSnapshot(siteId, version, owner, new ObjectMapper().createObjectNode(), false);

        SiteSnapshot result = getSite(siteId);

        assertEquals(expected, result);
    }

    @SuppressWarnings("unused")
    @Test
    public void getSnapshotAtVersionReturnsCorrectDataForExistingVersion() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = createSite(siteId, owner);
        long v1 = updateSite(siteId, v0, owner, delta1);
        long v2 = updateSite(siteId, v1, owner, delta2);
        SiteSnapshot expected = new SiteSnapshot(siteId, v1, owner, blob1, false);

        SiteSnapshot result = getSite(siteId, v1);

        assertEquals(expected, result);
    }

    @Test
    public void getSnapshotAtVersionReturnsLatestAvailableSnapshotOnNonexistentVersion() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = createSite(siteId, owner);
        long v1 = updateSite(siteId, v0, owner, delta1);
        long v2 = updateSite(siteId, v1, owner, delta2);
        SiteSnapshot expected = new SiteSnapshot(siteId, v2, owner, blob2, false);

        SiteSnapshot result = getSite(siteId, v2 + 5 /* Some arbitrary future version */);

        assertEquals(expected, result);
    }

    @Test
    public void updateSiteReturns409ForConflictingEvents() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        long v0 = createSite(siteId, owner);
        @SuppressWarnings("unused") long v1 = updateSite(siteId, v0, owner, delta1);

        Response response = updateSiteRaw(siteId, v0, owner, delta3);  // Note conflicting update with v1

        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
    }

    @Test
    public void createSiteReturns409ForExistingSite() {
        UUID siteId = UUID.randomUUID();
        UUID owner = UUID.randomUUID();
        createSite(siteId, owner);

        Response response = createSiteRaw(siteId, owner);              // Note conflicting creation

        assertEquals(CONFLICT.getStatusCode(), response.getStatus());
    }
}