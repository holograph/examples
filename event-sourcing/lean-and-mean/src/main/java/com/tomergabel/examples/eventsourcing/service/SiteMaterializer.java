package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

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

    SiteSnapshot materialize() throws IllegalEventStreamException {
        throw new RuntimeException("Not implemented yet");
    }

    SiteMaterializer append(SiteEvent event) throws IllegalEventStreamException {
        throw new RuntimeException("Not implemented yet");
    }
}
