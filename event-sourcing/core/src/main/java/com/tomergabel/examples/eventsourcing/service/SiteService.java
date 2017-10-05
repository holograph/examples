package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.io.IOException;
import java.util.UUID;

public interface SiteService {
    SiteSnapshot getSnapshot(UUID siteId, Long atVersion) throws IOException;

    boolean createSite(UUID siteId, UUID ownerId) throws IOException;
    Long update(UUID siteId, UUID userId, long baseVersion, JsonNode delta) throws IOException;
    long delete(UUID siteId, UUID userId) throws IOException;
    long restore(UUID siteId, UUID userId, long toVersion) throws IOException;
}
