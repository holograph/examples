package com.tomergabel.examples.eventsourcing.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class SiteServiceConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory eventsDatabaseFactory;

    @Valid
    @NotNull
    private DataSourceFactory snapshotsDatabaseFactory;

    @JsonProperty("events")
    public void getEventsDatabaseFactory(DataSourceFactory factory) {
        this.eventsDatabaseFactory = factory;
    }

    @JsonProperty("events")
    public DataSourceFactory getEventsDatabaseFactory() {
        return eventsDatabaseFactory;
    }

    @JsonProperty("snapshots")
    public void setSnapshotsDatabaseFactory(DataSourceFactory factory) {
        this.snapshotsDatabaseFactory = factory;
    }

    @JsonProperty("snapshots")
    public DataSourceFactory getSnapshotsDatabaseFactory() {
        return snapshotsDatabaseFactory;
    }

    private int maxTailSize = 100;

    @JsonProperty("maxTailSize")
    public int getMaxTailSize() {
        return maxTailSize;
    }

    @JsonProperty("maxTailSize")
    public void setMaxTailSize(int maxTailSize) {
        this.maxTailSize = maxTailSize;
    }
}
