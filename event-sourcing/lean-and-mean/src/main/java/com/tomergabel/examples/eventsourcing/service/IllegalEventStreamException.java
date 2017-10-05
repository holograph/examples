package com.tomergabel.examples.eventsourcing.service;

import java.util.UUID;

public class IllegalEventStreamException extends RuntimeException {
    private UUID siteId;
    private long version;

    public IllegalEventStreamException(String message, Throwable cause, UUID siteId, long version) {
        super("Illegal event stream for site " + siteId + " at version " + version + ": " + message, cause);
        this.siteId = siteId;
        this.version = version;
    }

    public IllegalEventStreamException(String message, UUID siteId, long version) {
        this(message, null, siteId, version);
    }

}
