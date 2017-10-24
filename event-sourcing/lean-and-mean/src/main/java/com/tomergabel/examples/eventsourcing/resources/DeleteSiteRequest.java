package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class DeleteSiteRequest {
    private UUID user;

    @JsonCreator
    public DeleteSiteRequest(@JsonProperty("user") UUID user) {
        this.user = user;
    }

    @JsonProperty
    public UUID getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteSiteRequest that = (DeleteSiteRequest) o;
        return Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
