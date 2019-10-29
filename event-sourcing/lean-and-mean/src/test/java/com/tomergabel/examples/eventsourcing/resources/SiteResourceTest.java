package com.tomergabel.examples.eventsourcing.resources;

import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.testkit.TestableSiteService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;

import javax.ws.rs.client.WebTarget;

public class SiteResourceTest extends SiteResourceSpec {
    private static SiteService siteService = new TestableSiteService();

    @ClassRule
    public static final ResourceTestRule resources =
            ResourceTestRule.builder().addResource(new SiteResource(siteService)).build();

    @Override
    protected WebTarget sites() {
        return resources.target("/sites");
    }
}