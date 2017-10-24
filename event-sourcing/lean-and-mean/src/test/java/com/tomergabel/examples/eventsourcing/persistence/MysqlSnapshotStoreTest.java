package com.tomergabel.examples.eventsourcing.persistence;

import com.wix.mysql.EmbeddedMysql;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.skife.jdbi.v2.DBI;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.distribution.Version.v5_7_latest;

public class MysqlSnapshotStoreTest extends SnapshotStoreSpec {

    static EmbeddedMysql embeddedMysql;
    static DBI database;
    static SnapshotStore store;

    @BeforeAll
    static void setup() {
        embeddedMysql = anEmbeddedMysql(v5_7_latest).addSchema("snapshots").start();
        String jdbcUrl = "jdbc:mysql://localhost:" + embeddedMysql.getConfig().getPort() + "/snapshots?useSSL=false";
        Flyway flyway = new Flyway();
        flyway.setDataSource(jdbcUrl, embeddedMysql.getConfig().getUsername(), embeddedMysql.getConfig().getPassword());
        flyway.setLocations("classpath:db/migration/snapshots");
        flyway.migrate();
        database = new DBI(jdbcUrl, embeddedMysql.getConfig().getUsername(), embeddedMysql.getConfig().getPassword());
        MysqlSnapshotStore.configureDatabase(database);
        store = new MysqlSnapshotStore(database);
    }

    @AfterAll
    static void tearDown() {
        embeddedMysql.stop();
    }

    @Override
    protected SnapshotStore getStore() {
        return store;
    }
}
