package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class CreateSiteRequest {
    private UUID owner;

    @JsonCreator
    public CreateSiteRequest(@JsonProperty(value = "owner", required = true) UUID owner) {
        this.owner = owner;
    }

    @JsonProperty
    public UUID getOwner() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateSiteRequest that = (CreateSiteRequest) o;
        return Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner);
    }
}
