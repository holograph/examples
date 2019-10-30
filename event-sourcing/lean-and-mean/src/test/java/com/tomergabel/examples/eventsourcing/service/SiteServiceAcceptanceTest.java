package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.resources.SiteResourceSpec;
import com.tomergabel.examples.eventsourcing.server.SiteServiceApplication;
import com.tomergabel.examples.eventsourcing.server.SiteServiceConfiguration;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.ws.rs.client.WebTarget;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

@ExtendWith(DropwizardExtensionsSupport.class)
class SiteServiceAcceptanceTest extends SiteResourceSpec {

    static EmbeddedMysql embeddedMysql;

    public DropwizardAppExtension<SiteServiceConfiguration> applicationRule =
            new DropwizardAppExtension<>(SiteServiceApplication.class, resourceFilePath("test-config.yaml"));

    @BeforeAll
    public static void setup() {
        MysqldConfig config = aMysqldConfig(v5_7_latest)
                .withUser("test", "test")
                .withPort(3310)
                .build();
        embeddedMysql = anEmbeddedMysql(config)
                .addSchema("events")
                .addSchema("snapshots")
                .start();
    }

    @AfterAll
    public static void tearDown() {
        embeddedMysql.stop();
    }

    @Override
    protected WebTarget siteResource() {
        // TODO gotta be a cleaner way to do this, but client() returns relative URI?! --TG
        return applicationRule
                .client()
                .property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true)
                .target("http://localhost:" + applicationRule.getLocalPort() + "/sites");
    }
}
