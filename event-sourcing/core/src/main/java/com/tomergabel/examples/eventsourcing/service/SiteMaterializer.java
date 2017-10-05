package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonPatch;
import com.tomergabel.examples.eventsourcing.model.*;

import java.util.UUID;

public class SiteMaterializer {
    private UUID siteId;

    public SiteMaterializer(UUID siteId) {
        this.siteId = siteId;
    }

    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode blob = mapper.createObjectNode();
    private UUID owner = null;
    private long version = Long.MIN_VALUE;
    private boolean deleted = false;

    SiteSnapshot materialize() throws IllegalEventStreamException {
        if (version == Long.MIN_VALUE)
            throw new IllegalEventStreamException("Empty event stream", siteId, 0);

        return new SiteSnapshot(version, owner, blob, deleted);
    }

    SiteMaterializer append(SiteEvent event) throws IllegalEventStreamException {
        if (version == Long.MIN_VALUE) {
            if (!(event instanceof SiteCreated))
                throw new IllegalEventStreamException("First event must be a creation event", siteId, 0);
            version = event.getVersion();
            owner = event.getUserId();
            return this;
        }

        if (event.getVersion() != version + 1)
            throw new IllegalEventStreamException(
                    "Unexpected version " + event.getVersion() + " specified",
                    siteId,
                    version + 1);

        if (event instanceof SiteCreated)
            throw new IllegalEventStreamException("Unexpected creation event specified", siteId, event.getVersion());
        else if (event instanceof SiteUpdated) {
            if (deleted)
                throw new IllegalEventStreamException("Site already deleted", siteId, event.getVersion());
            blob = JsonPatch.apply(((SiteUpdated) event).getDelta(), blob);
        } else if (event instanceof SiteRestored) {
            blob = JsonPatch.apply(((SiteRestored) event).getDelta(), blob);
        } else if (event instanceof SiteDeleted) {
            deleted = true;
        }

        version = event.getVersion();
        return this;
    }
}
