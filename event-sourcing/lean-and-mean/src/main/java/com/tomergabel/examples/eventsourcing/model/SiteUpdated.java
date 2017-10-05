package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public class SiteUpdated extends SiteEvent {
    private JsonNode delta;

    public SiteUpdated(long version, UUID userId, Instant timestamp, JsonNode delta) {
        super(version, userId, timestamp);
        this.delta = delta;
    }

    public JsonNode getDelta() {
        return delta;
    }
}
