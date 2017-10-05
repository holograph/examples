package com.tomergabel.examples.eventsourcing.service;

import java.util.UUID;

public class IllegalEventStreamException extends RuntimeException {
    private UUID siteId;
    private long version;

    public IllegalEventStreamException(String message, Throwable cause, UUID siteId, long atVersion) {
        super("Illegal event stream for site " + siteId + " at version " + atVersion + ": " + message, cause);
        this.siteId = siteId;
        this.version = version;
    }

    public IllegalEventStreamException(String message, UUID siteId, long atVersion) {
        this(message, null, siteId, atVersion);
    }

    public UUID getSiteId() {
        return siteId;
    }

    public long getVersion() {
        return version;
    }
}
