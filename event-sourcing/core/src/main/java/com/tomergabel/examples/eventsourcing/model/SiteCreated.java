package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteCreated extends SiteEvent {
    public SiteCreated(UUID userId, Instant timestamp) {
        super(SiteEvent.INITIAL_VERSION, userId, timestamp);
    }

    @Override
    public String toString() {
        return "SiteCreated{" +
                "version=" + version +
                ", userId=" + userId +
                ", timestamp=" + timestamp +
                '}';
    }
}
