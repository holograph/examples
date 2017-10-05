package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class SiteRestored extends SiteEvent {
    long restoredVersion;
    JsonNode delta;

    public SiteRestored(long version, UUID userId, Instant timestamp, long restoredVersion, JsonNode delta) {
        super(version, userId, timestamp);
        this.restoredVersion = restoredVersion;
        this.delta = delta;
    }

    public long getRestoredVersion() {
        return restoredVersion;
    }

    public JsonNode getDelta() {
        return delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SiteRestored that = (SiteRestored) o;
        return restoredVersion == that.restoredVersion &&
                Objects.equals(delta, that.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), restoredVersion, delta);
    }
}
