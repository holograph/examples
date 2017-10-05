package com.tomergabel.examples.eventsourcing.model;

import java.time.Instant;
import java.util.UUID;

public class SiteArchived extends SiteEvent {
    public SiteArchived(long version, UUID userId, Instant timestamp) {
        super(version, userId, timestamp);
    }
}
