package com.tomergabel.examples.eventsourcing.testkit;

import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.service.SiteServiceSpec;

public class TestableSiteServiceTest extends SiteServiceSpec {
    @Override
    protected SiteService instantiateService() {
        return new TestableSiteService();
    }
}