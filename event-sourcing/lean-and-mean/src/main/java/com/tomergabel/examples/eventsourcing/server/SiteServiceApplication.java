package com.tomergabel.examples.eventsourcing.server;

import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.MysqlEventStore;
import com.tomergabel.examples.eventsourcing.persistence.MysqlSnapshotStore;
import com.tomergabel.examples.eventsourcing.persistence.SnapshotStore;
import com.tomergabel.examples.eventsourcing.resources.SiteResource;
import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.service.SnapshotEnabledSiteService;
import com.tomergabel.examples.eventsourcing.service.SnapshotStrategy;
import com.tomergabel.examples.eventsourcing.service.TailSizeSnapshotStrategy;
import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.time.Clock;

public class SiteServiceApplication extends Application<SiteServiceConfiguration> {

    @Override
    public String getName() {
        return "Site Service";
    }

    @Override
    public void run(SiteServiceConfiguration configuration, Environment environment) {

        DBI eventDB = new DBIFactory().build(environment, configuration.getEventsDatabaseFactory(), "events");
        MysqlEventStore.configureDatabase(eventDB);
        EventStore eventStore = new MysqlEventStore(eventDB);

        DBI snapshotDB = new DBIFactory().build(environment, configuration.getSnapshotsDatabaseFactory(), "snapshots");
        MysqlSnapshotStore.configureDatabase(snapshotDB);
        SnapshotStore snapshotStore = new MysqlSnapshotStore(snapshotDB);

        SnapshotStrategy snapshotStrategy = new TailSizeSnapshotStrategy(configuration.getMaxTailSize());

        SiteService siteService =
                new SnapshotEnabledSiteService(eventStore, snapshotStore, snapshotStrategy, Clock.systemUTC());

        environment.jersey().register(new SiteResource(siteService));
    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
