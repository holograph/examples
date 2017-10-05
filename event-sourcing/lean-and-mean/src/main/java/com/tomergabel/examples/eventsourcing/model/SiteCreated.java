package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteCreated extends SiteEvent {
    public SiteCreated(long version, UUID userId, Instant timestamp) {
        super(version, userId, timestamp);
    }
}
