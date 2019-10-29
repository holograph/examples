package com.tomergabel.examples.eventsourcing.siterestorer;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.persistence.EventStore;
import com.tomergabel.examples.eventsourcing.persistence.SnapshotStore;
import com.tomergabel.examples.eventsourcing.service.SiteService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.stream.Collectors;

public class SiteRestorer {
    private final EventStore eventStore;
    private final SnapshotStore snapshotStore;
    private final SiteService siteService;

    public SiteRestorer(EventStore eventStore, SnapshotStore snapshotStore, SiteService siteService) {
        this.eventStore = eventStore;
        this.snapshotStore = snapshotStore;
        this.siteService = siteService;
    }

    public List<Long> findVersions(UUID siteId) throws IOException {
        OptionalLong currentVersion =
                // TODO optimize this (will require opportunistic feature in EventStore though)
                eventStore.getEvents(siteId).stream().mapToLong(SiteEvent::getVersion).max();
        if (!currentVersion.isPresent()) return Collections.emptyList();

        List<Long> allVersions = snapshotStore.findAvailableSnapshots(siteId);
        return allVersions
                .stream()
                .filter(version -> version != currentVersion.getAsLong())
                .collect(Collectors.toList());
    }

    public OptionalLong restoreSite(UUID siteId, UUID userId, long toVersion) throws IOException {
        return siteService.restore(siteId, userId, toVersion);
    }
}
