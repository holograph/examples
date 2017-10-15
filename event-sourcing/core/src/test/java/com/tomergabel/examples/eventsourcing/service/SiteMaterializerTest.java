package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static com.tomergabel.examples.eventsourcing.model.SampleSite.*;
import static org.junit.jupiter.api.Assertions.*;

class SiteMaterializerTest {

    SampleSite site = new SampleSite();

    @Test
    void materializerCorrectlyReconstructsSnapshot() {
        SiteMaterializer mat = new SiteMaterializer(site.id);
        site.allEvents.forEach(mat::append);

        SiteSnapshot snapshot = mat.materialize();
        assertEquals(site.finalState.getVersion(), snapshot.getVersion());
        assertEquals(finalBlob, snapshot.getBlob());
        assertEquals(site.owner, snapshot.getOwner());
        assertTrue(snapshot.getDeleted());
    }

    @Test
    void materializingAnEmptyMaterializerThrows() {
        SiteMaterializer mat = new SiteMaterializer(site.id);
        assertThrows(IllegalEventStreamException.class, mat::materialize);
    }

    @Test
    void appendingAnyButCreatedEventToEmptyMaterializerThrows() {
        SiteMaterializer mat = new SiteMaterializer(site.id);
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteUpdated(0, site.owner, Instant.now(), delta1)));
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteDeleted(0, site.owner, Instant.now())));
        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(new SiteRestored(0, site.owner, Instant.now(), 0, delta1)));
    }

    @Test
    void appendingEventWithWrongVersionThrows() {
        SiteMaterializer mat = new SiteMaterializer(site.id);
        mat.append(site.created0);

        assertThrows(IllegalEventStreamException.class,
                () -> mat.append(site.updated2));
    }

    @Test
    void appendingUpdateEventToArchivedSiteThrows() {
        SiteMaterializer mat = new SiteMaterializer(site.id);
        site.allEvents.forEach(mat::append);

        SiteEvent update = new SiteUpdated(
                site.finalState.getVersion() + 1,
                site.user,
                Instant.now(),
                new ObjectMapper().createArrayNode());

        assertThrows(IllegalEventStreamException.class, () -> mat.append(update));
    }
}
