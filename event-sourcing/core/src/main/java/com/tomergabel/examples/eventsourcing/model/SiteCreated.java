package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteCreated extends SiteEvent {
    public SiteCreated(UUID userId, Instant timestamp) {
        super(SiteEvent.INITIAL_VERSION, userId, timestamp);
    }
}
