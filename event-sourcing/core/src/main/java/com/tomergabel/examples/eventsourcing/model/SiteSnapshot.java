package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.UUID;

public class SiteSnapshot {
    private UUID siteId;
    private long version;
    private UUID owner;
    private JsonNode blob;
    private boolean deleted;

    @JsonCreator
    public SiteSnapshot(@JsonProperty("siteId") UUID siteId,
                        @JsonProperty("version") long version,
                        @JsonProperty("owner") UUID owner,
                        @JsonProperty("blob") JsonNode blob,
                        @JsonProperty("deleted") boolean deleted) {
        this.siteId = siteId;
        this.version = version;
        this.owner = owner;
        this.blob = blob;
        this.deleted = deleted;
    }

    @JsonProperty public UUID getSiteId() {
        return siteId;
    }

    @JsonProperty public long getVersion() {
        return version;
    }

    @JsonProperty public UUID getOwner() {
        return owner;
    }

    @JsonProperty public JsonNode getBlob() {
        return blob;
    }

    @JsonProperty public boolean getDeleted() {
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
