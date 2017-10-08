package com.tomergabel.examples.eventsourcing.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.tomergabel.examples.eventsourcing.model.*;
import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.SnapshotStore;

import java.io.IOException;
import java.time.Clock;
import java.util.*;

public class SnapshotEnabledSiteService implements SiteService {

    private Clock clock;
    private EventStore eventStore;
    private SnapshotStore snapshotStore;
    private SnapshotStrategy snapshotStrategy;

    public SnapshotEnabledSiteService(EventStore eventStore,
                                      SnapshotStore snapshotStore,
                                      SnapshotStrategy snapshotStrategy,
                                      Clock clock) {
        this.eventStore = eventStore;
        this.snapshotStore = snapshotStore;
        this.snapshotStrategy = snapshotStrategy;
        this.clock = clock;
    }

    @Override
    public Optional<SiteSnapshot> getSnapshot(UUID siteId, Long atVersion) throws IOException {
        // Read snapshot (if available) and event tail
        Optional<SiteSnapshot> persistedSnapshot = snapshotStore.findLatestSnapshot(siteId, atVersion);
        Long from = persistedSnapshot.map(snapshot -> snapshot.getVersion() + 1).orElse(null);
        List<SiteEvent> tail = eventStore.getEvents(siteId, from, atVersion);

        if (!persistedSnapshot.isPresent() && tail.isEmpty())
            return Optional.empty();

        // Materialize site snapshot
        SiteMaterializer mat = persistedSnapshot.map(SiteMaterializer::new).orElse(new SiteMaterializer(siteId));
        tail.forEach(mat::append);
        SiteSnapshot snapshot = mat.materialize();

        // Optionally persist new snapshot
        if (snapshotStrategy.shouldTakeSnapshot(persistedSnapshot.orElse(null), tail))
            snapshotStore.persistSnapshot(snapshot);

        return Optional.of(snapshot);
    }

    @Override
    public OptionalLong create(UUID siteId, UUID ownerId) throws IOException {
        SiteCreated creationEvent = new SiteCreated(ownerId, clock.instant());
        if (eventStore.addEvents(siteId, Collections.singletonList(creationEvent)))
            return OptionalLong.of(creationEvent.getVersion());
        else
            return OptionalLong.empty();
    }

    @Override
    public OptionalLong update(UUID siteId, UUID userId, long baseVersion, JsonNode delta) throws IOException {
        Optional<SiteSnapshot> current = getSnapshot(siteId, null);
        if (!current.isPresent()
                || current.get().getDeleted()
                || current.get().getVersion() > baseVersion)
            return OptionalLong.empty();

        SiteUpdated updateEvent = new SiteUpdated(baseVersion + 1, userId, clock.instant(), delta);
        if (eventStore.addEvents(siteId, Collections.singletonList(updateEvent)))
            return OptionalLong.of(updateEvent.getVersion());
        else
            return OptionalLong.empty();
    }

    @Override
    public OptionalLong delete(UUID siteId, UUID userId) throws IOException {
        Optional<SiteSnapshot> current = getSnapshot(siteId, null);
        if (!current.isPresent()) return OptionalLong.empty();
        if (current.get().getDeleted()) return OptionalLong.of(current.get().getVersion());

        SiteDeleted deletionEvent = new SiteDeleted(current.get().getVersion() + 1, userId, clock.instant());
        if (eventStore.addEvents(siteId, Collections.singletonList(deletionEvent)))
            return OptionalLong.of(deletionEvent.getVersion());
        else
            return OptionalLong.empty();
    }

    @Override
    public OptionalLong restore(UUID siteId, UUID userId, long toVersion) throws IOException {
        Optional<SiteSnapshot> atSpecified = getSnapshot(siteId, toVersion);
        if (!atSpecified.isPresent() || atSpecified.get().getVersion() != toVersion)
            return OptionalLong.empty();

        List<SiteEvent> tail = eventStore.getEvents(siteId, toVersion + 1, null);
        SiteMaterializer mat = new SiteMaterializer(atSpecified.get());
        tail.forEach(mat::append);
        SiteSnapshot current = mat.materialize();
        JsonNode delta = JsonDiff.asJson(current.getBlob(), atSpecified.get().getBlob());

        SiteRestored restorationEvent =
                new SiteRestored(current.getVersion() + 1, userId, clock.instant(), toVersion, delta);
        if (eventStore.addEvents(siteId, Collections.singletonList(restorationEvent)))
            return OptionalLong.of(restorationEvent.getVersion());
        else
            return OptionalLong.empty();
    }
}
