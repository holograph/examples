package com.tomergabel.examples.eventsourcing.server;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class SiteServiceApplication extends Application<SiteServiceConfiguration> {

    @Override
    public String getName() {
        return "Site Service";
    }

    @Override
    public void run(SiteServiceConfiguration siteServiceConfiguration, Environment environment) throws Exception {

    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
