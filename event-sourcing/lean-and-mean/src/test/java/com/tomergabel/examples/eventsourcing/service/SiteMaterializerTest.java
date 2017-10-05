package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.tomergabel.examples.eventsourcing.model.SampleSite.*;
import static org.junit.jupiter.api.Assertions.*;

class SiteMaterializerTest {

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

        SiteEvent update = new SiteUpdated(finalVersion + 1, user, Instant.now(), new ObjectMapper().createArrayNode());

        assertThrows(IllegalEventStreamException.class, () -> mat.append(update));
    }
}
