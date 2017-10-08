package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;
import java.util.UUID;

public class UpdateSiteRequest {
    private UUID user;
    private JsonNode delta;

    @JsonCreator
    public UpdateSiteRequest(@JsonProperty("user") UUID user,
                             @JsonProperty("delta") JsonNode delta) {
        this.user = user;
        this.delta = delta;
    }

    @JsonProperty
    public UUID getUser() {
        return user;
    }

    @JsonProperty
    public JsonNode getDelta() {
        return delta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateSiteRequest that = (UpdateSiteRequest) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(delta, that.delta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, delta);
    }
}
