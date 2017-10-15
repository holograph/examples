package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.persistence.MysqlEventStore;
import com.tomergabel.examples.eventsourcing.resources.SiteResourceSpec;
import com.tomergabel.examples.eventsourcing.server.SiteServiceApplication;
import com.tomergabel.examples.eventsourcing.server.SiteServiceConfiguration;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.Sources;
import com.wix.mysql.config.MysqldConfig;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import javax.ws.rs.client.WebTarget;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;

public class SiteServiceAcceptanceTest extends SiteResourceSpec {

    @ClassRule
    public static final DropwizardAppRule<SiteServiceConfiguration> applicationRule =
            new DropwizardAppRule<>(SiteServiceApplication.class, ResourceHelpers.resourceFilePath("test-config.yaml"));

    static EmbeddedMysql embeddedMysql;

    @BeforeAll
    static void setup() {
        MysqldConfig config = aMysqldConfig(v5_7_latest)
                .withUser("test", "test")
                .withPort(3310)
                .build();
        embeddedMysql = anEmbeddedMysql(config)
                .addSchema("events", Sources.fromString(MysqlEventStore.SCHEMA_DDL))
                .start();
    }

    @AfterAll
    static void tearDown() {
        embeddedMysql.stop();
    }

    @Override
    protected WebTarget sites() {
        return applicationRule.client().target("/sites");
    }
}
