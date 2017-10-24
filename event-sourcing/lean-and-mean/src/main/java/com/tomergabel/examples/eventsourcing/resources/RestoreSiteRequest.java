package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class RestoreSiteRequest {
    private UUID user;
    private long targetVersion;

    @JsonCreator
    public RestoreSiteRequest(@JsonProperty("user") UUID user,
                              @JsonProperty("version") long targetVersion) {
        this.user = user;
        this.targetVersion = targetVersion;
    }

    @JsonProperty
    public UUID getUser() {
        return user;
    }

    @JsonProperty
    public long getTargetVersion() {
        return targetVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoreSiteRequest that = (RestoreSiteRequest) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(targetVersion, that.targetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, targetVersion);
    }
}
