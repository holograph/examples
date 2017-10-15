package com.tomergabel.examples.eventsourcing.resources;

import com.tomergabel.examples.eventsourcing.persistence.InMemoryEventStore;
import com.tomergabel.examples.eventsourcing.persistence.InMemorySnapshotStore;
import com.tomergabel.examples.eventsourcing.service.MockSnapshotStrategy;
import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.service.SnapshotEnabledSiteService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.ClassRule;

import javax.ws.rs.client.WebTarget;
import java.time.Clock;

public class SiteResourceTest extends SiteResourceSpec {
    private static SiteService siteService =
            new SnapshotEnabledSiteService(
                    new InMemoryEventStore(),
                    new InMemorySnapshotStore(),
                    new MockSnapshotStrategy(),
                    Clock.systemUTC());

    @ClassRule
    public static final ResourceTestRule resources =
            ResourceTestRule.builder().addResource(new SiteResource(siteService)).build();

    @Override
    protected WebTarget sites() {
        return resources.target("/sites");
    }
}