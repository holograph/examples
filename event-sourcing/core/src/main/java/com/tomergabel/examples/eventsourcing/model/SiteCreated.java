package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteCreated extends SiteEvent {
    public SiteCreated(UUID userId, Instant timestamp) {
        super(0, userId, timestamp);
    }
}
