package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SitePublished extends SiteEvent {
    public SitePublished(long version, UUID userId, Instant timestamp) {
        super(version, userId, timestamp);
    }
}
