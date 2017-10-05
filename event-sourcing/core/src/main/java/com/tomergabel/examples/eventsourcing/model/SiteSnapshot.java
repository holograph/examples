package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.UUID;

public class SiteSnapshot {
    private UUID siteId;
    private long version;
    private UUID owner;
    private JsonNode blob;
    private boolean deleted;

    public SiteSnapshot(UUID siteId, long version, UUID owner, JsonNode blob, boolean deleted) {
        this.siteId = siteId;
        this.version = version;
        this.owner = owner;
        this.blob = blob;
        this.deleted = deleted;
    }

    public UUID getSiteId() {
        return siteId;
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
                Objects.equals(siteId, that.siteId) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(blob, that.blob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId, version, owner, blob, deleted);
    }
}
