package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("WeakerAccess")
class SiteMaterializerTest {

    ObjectMapper mapper = new ObjectMapper();

    UUID siteId = UUID.randomUUID();
    UUID owner = UUID.randomUUID();
    UUID user = UUID.randomUUID();
    SiteEvent created0 = new SiteCreated(owner, Instant.now());
    JsonNode delta1 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/name\",\"value\":\"my site\"}]");
    SiteEvent updated1 = new SiteUpdated(1, owner, Instant.now(), delta1);
    JsonNode delta2 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/url\",\"value\":\"http://www.example.com\"}]");
    SiteEvent updated2 = new SiteUpdated(2, owner, Instant.now(), delta2);
    JsonNode delta3 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"other site\"}]");
    SiteEvent updated3 = new SiteUpdated(3, siteId, Instant.now(), delta3);
    JsonNode delta4 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"my site\"}]");
    SiteEvent restored4 = new SiteRestored(4, user, Instant.now(), 2, delta4);
    SiteEvent archived5 = new SiteDeleted(5, user, Instant.now());

    List<SiteEvent> allEvents = Arrays.asList(created0, updated1, updated2, updated3, restored4, archived5);
    long finalVersion = 5;
    JsonNode finalBlob = mapper.readTree("{\"name\":\"my site\", \"url\":\"http://www.example.com\"}");

    SiteMaterializerTest() throws IOException {
    }

    @Test
    void materializerCorrectlyReconstructsSnapshot() {
        SiteMaterializer mat = new SiteMaterializer(siteId);
        allEvents.forEach(mat::append);

        SiteSnapshot snapshot = mat.materialize();
        assertEquals(snapshot.getVersion(), finalVersion);
        assertEquals(snapshot.getBlob(), finalBlob);
        assertEquals(snapshot.getOwner(), owner);
        assertTrue(snapshot.getDeleted());
    }

    @Test
    void materializingAnEmptyMaterializerThrows() {
        SiteMaterializer mat = new SiteMaterializer(siteId);
        assertThrows(IllegalEventStreamException.class, mat::materialize);
    }

    @Test
    void appendingAnyButCreatedEventToEmptyMaterializerThrows() {
        SiteMaterializer mat = new SiteMaterializer(siteId);
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteUpdated(0, owner, Instant.now(), delta1)));
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteDeleted(0, owner, Instant.now())));
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteRestored(0, owner, Instant.now(), 0, delta1)));
    }

    @Test
    void appendingEventWithWrongVersionThrows() {
        SiteMaterializer mat = new SiteMaterializer(siteId);
        mat.append(created0);

        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(updated2));
    }

    @Test
    void appendingUpdateEventToArchivedSiteThrows() {
        SiteMaterializer mat = new SiteMaterializer(siteId);
        allEvents.forEach(mat::append);

        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteUpdated(finalVersion + 1, user, Instant.now(), mapper.createArrayNode())));
    }
}
