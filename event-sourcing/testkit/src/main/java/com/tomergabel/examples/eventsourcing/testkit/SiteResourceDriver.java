package com.tomergabel.examples.eventsourcing.testkit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class SiteResourceDriver {
    public static final String JSON_PATCH_MIME_TYPE = "application/json-patch+json";

    static private class CreateSiteRequest {
        private UUID owner;

        public CreateSiteRequest(UUID owner) {
            this.owner = owner;
        }

        public UUID getOwner() {
            return owner;
        }
    }
    static private class DeleteSiteRequest {
        private UUID user;

        public DeleteSiteRequest(UUID user) {
            this.user = user;
        }

        public UUID getUser() {
            return user;
        }
    }
    static private class RestoreSiteRequest {
        private UUID user;
        private long targetVersion;

        public RestoreSiteRequest(UUID user, long targetVersion) {
            this.user = user;
            this.targetVersion = targetVersion;
        }

        public UUID getUser() {
            return user;
        }

        public long getTargetVersion() {
            return targetVersion;
        }
    }
    static private class UpdateSiteRequest {
        private UUID user;
        private JsonNode delta;

        public UpdateSiteRequest(UUID user, JsonNode delta) {
            this.user = user;
            this.delta = delta;
        }

        public UUID getUser() {
            return user;
        }

        public JsonNode getDelta() {
            return delta;
        }
    }
    static private class VersionResponse {
        long version;

        @JsonCreator public VersionResponse(@JsonProperty("version") long version) {
            this.version = version;
        }

        public long getVersion() {
            return version;
        }
    }

    private final WebTarget baseUrl;

    public SiteResourceDriver(WebTarget baseUrl) {
        // Note: if you use Jersey, you should set the SUPPRESS_HTTP_COMPLIANCE_VALIDATION property to true
        this.baseUrl = baseUrl;
    }

    private Invocation.Builder resource(UUID siteId, long atVersion) {
        return baseUrl.path("/" + siteId + "/versions/" + atVersion).request();
    }
    private Invocation.Builder resource(UUID siteId) {
        return baseUrl.path("/" + siteId).request();
    }

    public SiteSnapshot getSite(UUID siteId, long atVersion) {
        return resource(siteId, atVersion).get(SiteSnapshot.class);
    }
    public SiteSnapshot getSite(UUID siteId) {
        return resource(siteId).get(SiteSnapshot.class);
    }
    public Response getSiteRaw(UUID siteId) {
        return resource(siteId).get();
    }
    public long createSite(UUID siteId, UUID owner) {
        return resource(siteId)
                .post(Entity.json(new CreateSiteRequest(owner)), VersionResponse.class)
                .getVersion();
    }
    public Response createSiteRaw(UUID siteId, UUID owner) {
        return resource(siteId)
                .post(Entity.json(new CreateSiteRequest(owner)));
    }
    public long deleteSite(UUID siteId, UUID user) {
        return resource(siteId)
                .method("DELETE", Entity.json(new DeleteSiteRequest(user)), VersionResponse.class)
                .getVersion();
    }
    public Response deleteSiteRaw(UUID siteId, UUID user) {
        return resource(siteId)
//                .property(SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                .method("DELETE", Entity.json(new DeleteSiteRequest(user)));
    }
    public long restoreSite(UUID siteId, UUID user, long targetVersion) {
        return resource(siteId)
                .put(Entity.json(new RestoreSiteRequest(user, targetVersion)), VersionResponse.class)
                .getVersion();
    }
    public Response restoreSiteRaw(UUID siteId, UUID user, long targetVersion) {
        return resource(siteId)
                .put(Entity.json(new RestoreSiteRequest(user, targetVersion)));
    }
    public long updateSite(UUID siteId, long atVersion, UUID user, JsonNode delta) {
        return resource(siteId, atVersion)
                .method("PATCH", Entity.entity(new UpdateSiteRequest(user, delta), JSON_PATCH_MIME_TYPE), VersionResponse.class)
                .getVersion();
    }
    public Response updateSiteRaw(UUID siteId, long atVersion, UUID user, JsonNode delta) {
        return resource(siteId, atVersion)
                .method("PATCH", Entity.entity(new UpdateSiteRequest(user, delta), JSON_PATCH_MIME_TYPE));
    }
}
