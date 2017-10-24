package com.tomergabel.examples.eventsourcing.server;

import com.google.inject.name.Names;
import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.MysqlEventStore;
import com.tomergabel.examples.eventsourcing.persistence.MysqlSnapshotStore;
import com.tomergabel.examples.eventsourcing.persistence.SnapshotStore;
import com.tomergabel.examples.eventsourcing.service.SiteService;
import com.tomergabel.examples.eventsourcing.service.SnapshotEnabledSiteService;
import com.tomergabel.examples.eventsourcing.service.SnapshotStrategy;
import com.tomergabel.examples.eventsourcing.service.TailSizeSnapshotStrategy;
import io.dropwizard.jdbi.DBIFactory;
import org.skife.jdbi.v2.DBI;
import ru.vyarus.dropwizard.guice.module.support.DropwizardAwareModule;

import java.time.Clock;

public class SiteServiceModule extends DropwizardAwareModule<SiteServiceConfiguration> {

    @Override
    protected void configure() {

        bind(DBI.class)
                .annotatedWith(Names.named("events"))
                .toProvider(() -> {
                    DBI eventDB = new DBIFactory().build(
                            environment(),
                            configuration().getEventsDatabaseFactory(),
                            "events");
                    MysqlEventStore.configureDatabase(eventDB);
                    return eventDB;
                });

        bind(DBI.class)
                .annotatedWith(Names.named("snapshots"))
                .toProvider(() -> {
                    DBI snapshotDB = new DBIFactory().build(
                            environment(),
                            configuration().getSnapshotsDatabaseFactory(),
                            "snapshots");
                    MysqlSnapshotStore.configureDatabase(snapshotDB);
                    return snapshotDB;
                });

        bind(EventStore.class).to(MysqlEventStore.class);
        bind(SnapshotStore.class).to(MysqlSnapshotStore.class);
        bind(SnapshotStrategy.class).toProvider(() -> new TailSizeSnapshotStrategy(configuration().getMaxTailSize()));
        bind(Clock.class).toProvider(Clock::systemUTC);
        bind(SiteService.class).to(SnapshotEnabledSiteService.class);
    }

}
