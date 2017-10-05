package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
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
}
