package com.tomergabel.examples.eventsourcing.resources;

import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.testkit.TestableSiteService;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.testing.junit5.ResourceExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.WebTarget;

@ExtendWith(DropwizardExtensionsSupport.class)
public class SiteResourceTest extends SiteResourceSpec {
    private static SiteService siteService = new TestableSiteService();

    public static final ResourceExtension resources =
            ResourceExtension.builder().addResource(new SiteResource(siteService)).build();

    @Override
    protected WebTarget sites() {
        return resources.target("/sites");
    }
}