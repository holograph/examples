package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteDeleted extends SiteEvent {
    public SiteDeleted(long version, UUID userId, Instant timestamp) {
        super(version, userId, timestamp);
    }

    @Override
    public String toString() {
        return "SiteDeleted{" +
                "version=" + version +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
