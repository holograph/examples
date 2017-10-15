package com.tomergabel.examples.eventsourcing.server;

import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.MysqlEventStore;
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

        DBI eventDB = new DBIFactory().build(environment, siteServiceConfiguration.getDataSourceFactory(), "events");
        MysqlEventStore.configureDatabase(eventDB);
        EventStore eventStore = new MysqlEventStore(eventDB);

//        DBI snapshotDB = new DBIFactory().build(environment, siteServiceConfiguration.getDataSourceFactory(), "snapshots");
//        SnapshotStore snapshotStore = new MysqlSnapshotStore(database);
//        SnapshotStore snapshotStore = new InMemorySn

//        SiteService siteService =
//                new SnapshotEnabledSiteService(eventStore, snapshotStore, snapshotStrategy, Clock.systemUTC());
//        environment.jersey().register(new SiteResource(siteService));
    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
