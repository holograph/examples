package com.tomergabel.examples.eventsourcing.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SiteServiceConfiguration extends Configuration {
    @Valid
    @NotNull
    private DataSourceFactory factory;

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        this.factory = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return factory;
    }
}
