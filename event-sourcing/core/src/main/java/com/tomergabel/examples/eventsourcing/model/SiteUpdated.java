package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SiteUpdated that = (SiteUpdated) o;
        return Objects.equals(delta, that.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), delta);
    }

    @Override
    public String toString() {
        return "SiteUpdated{" +
                "delta=" + delta +
                ", version=" + version +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
