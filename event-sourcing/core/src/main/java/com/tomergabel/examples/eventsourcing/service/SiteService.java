package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public interface SiteService {
    Optional<SiteSnapshot> getSnapshot(UUID siteId, Long atVersion) throws IOException;

    boolean create(UUID siteId, UUID ownerId) throws IOException;
    OptionalLong update(UUID siteId, UUID userId, long baseVersion, JsonNode delta) throws IOException;
    OptionalLong delete(UUID siteId, UUID userId) throws IOException;
    OptionalLong restore(UUID siteId, UUID userId, long toVersion) throws IOException;
}
