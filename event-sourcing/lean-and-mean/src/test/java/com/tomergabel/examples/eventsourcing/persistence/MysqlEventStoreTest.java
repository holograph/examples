package com.tomergabel.examples.eventsourcing.persistence;

import com.wix.mysql.EmbeddedMysql;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.skife.jdbi.v2.DBI;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.distribution.Version.v5_7_latest;

class MysqlEventStoreTest extends EventStoreSpec {

    static EmbeddedMysql embeddedMysql;
    static DBI database;
    static MysqlEventStore store;

    @BeforeAll
    static void setup() {
        embeddedMysql = anEmbeddedMysql(v5_7_latest).addSchema("events").start();
        String jdbcUrl = "jdbc:mysql://localhost:" + embeddedMysql.getConfig().getPort() + "/events?useSSL=false";
        Flyway flyway = new Flyway();
        flyway.setDataSource(jdbcUrl, embeddedMysql.getConfig().getUsername(), embeddedMysql.getConfig().getPassword());
        flyway.setLocations("classpath:db/migration/events");
        flyway.migrate();
        database = new DBI(jdbcUrl, embeddedMysql.getConfig().getUsername(), embeddedMysql.getConfig().getPassword());
        MysqlEventStore.configureDatabase(database);
        store = new MysqlEventStore(database);
    }

    @AfterAll
    static void tearDown() {
        embeddedMysql.stop();
    }

    @Override
    protected EventStore getStore() {
        return store;
    }
}