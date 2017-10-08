package com.tomergabel.examples.eventsourcing.server;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class SiteServiceApplication extends Application<SiteServiceConfiguration> {

    @Override
    public String getName() {
        return "Site Service";
    }

    @Override
    public void run(SiteServiceConfiguration siteServiceConfiguration, Environment environment) throws Exception {
        DBI jdbi = new DBIFactory().build(environment, siteServiceConfiguration.getDataSourceFactory(), "event store");

//        SiteService siteService = new SnapshotEnabledSiteService();

//        environment.jersey().register(new SiteResource(siteService));
    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
