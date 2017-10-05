package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class SiteEvent {
    protected long version;
    protected UUID userId;
    protected Instant timestamp;

    public SiteEvent(long version, UUID userId, Instant timestamp) {
        this.version = version;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public long getVersion() {
        return version;
    }

    public UUID getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteEvent siteEvent = (SiteEvent) o;
        return version == siteEvent.version &&
                Objects.equals(userId, siteEvent.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, userId);
    }
}
