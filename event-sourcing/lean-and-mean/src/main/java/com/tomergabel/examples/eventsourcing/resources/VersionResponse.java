package com.tomergabel.examples.eventsourcing.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class VersionResponse {
    private long version;

    @JsonCreator public VersionResponse(@JsonProperty("version") long version) {
        this.version = version;
    }

    @JsonProperty
    public long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersionResponse that = (VersionResponse) o;
        return version == that.version;
    }

    @Override
    public int hashCode() {

        return Objects.hash(version);
    }
}
