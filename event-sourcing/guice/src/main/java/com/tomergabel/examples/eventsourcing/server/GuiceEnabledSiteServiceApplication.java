package com.tomergabel.examples.eventsourcing.server;

import com.tomergabel.examples.eventsourcing.resources.SiteResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import ru.vyarus.dropwizard.guice.GuiceBundle;

public class GuiceEnabledSiteServiceApplication extends Application<SiteServiceConfiguration> {

    @Override
    public void initialize(Bootstrap<SiteServiceConfiguration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.builder()
//                .useWebInstallers()
                .modules(new SiteServiceModule())
                .extensions(SiteResource.class)
                .build());
    }

    @Override
    public String getName() {
        return "Guice-Enabled Site Service";
    }

    @Override
    public void run(SiteServiceConfiguration configuration, Environment environment) {
    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
