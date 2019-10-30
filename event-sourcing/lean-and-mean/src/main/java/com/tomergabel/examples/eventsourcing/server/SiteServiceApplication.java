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
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.time.Clock;
import java.util.Collections;
import java.util.EnumSet;

public class SiteServiceApplication extends Application<SiteServiceConfiguration> {

    @Override
    public String getName() {
        return "Site Service";
    }

    @Override
    public void initialize(Bootstrap<SiteServiceConfiguration> bootstrap) {
    }


    @Override
    public void run(SiteServiceConfiguration configuration, Environment environment) {

        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,PATCH");

        DBI eventDB = new DBIFactory().build(environment, configuration.getEventsDatabaseFactory(), "events");
        MysqlEventStore.configureDatabase(eventDB);
        migrateDatabase(configuration.getEventsDatabaseFactory(), "events");
        EventStore eventStore = new MysqlEventStore(eventDB);

        DBI snapshotDB = new DBIFactory().build(environment, configuration.getSnapshotsDatabaseFactory(), "snapshots");
        MysqlSnapshotStore.configureDatabase(snapshotDB);
        migrateDatabase(configuration.getSnapshotsDatabaseFactory(), "snapshots");
        SnapshotStore snapshotStore = new MysqlSnapshotStore(snapshotDB);

        SnapshotStrategy snapshotStrategy = new TailSizeSnapshotStrategy(configuration.getMaxTailSize());

        SiteService siteService =
                new SnapshotEnabledSiteService(eventStore, snapshotStore, snapshotStrategy, Clock.systemUTC());

        environment.jersey().register(new SiteResource(siteService));
    }

    private void migrateDatabase(DataSourceFactory factory, String schema) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(factory.getUrl(), factory.getUser(), factory.getPassword());
        flyway.configure(Collections.singletonMap("flyway.connectRetries", "10"));
        flyway.setLocations("classpath:db/migration/" + schema);
        flyway.migrate();
    }

    public static void main(String[] args) throws Exception {
        new SiteServiceApplication().run(args);
    }
}
