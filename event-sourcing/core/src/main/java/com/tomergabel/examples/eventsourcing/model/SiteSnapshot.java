package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.UUID;

public class SiteSnapshot {
    private long version;
    private UUID owner;
    private JsonNode blob;
    private boolean deleted;

    public SiteSnapshot(long version, UUID owner, JsonNode blob, boolean deleted) {
        this.version = version;
        this.owner = owner;
        this.blob = blob;
        this.deleted = deleted;
    }

    public long getVersion() {
        return version;
    }

    public UUID getOwner() {
        return owner;
    }

    public JsonNode getBlob() {
        return blob;
    }

    public boolean getDeleted() {
        return deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SiteSnapshot that = (SiteSnapshot) o;
        return version == that.version &&
                deleted == that.deleted &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(blob, that.blob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, owner, blob, deleted);
    }
}
