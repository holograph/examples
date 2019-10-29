package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SnapshotStore {
    Optional<SiteSnapshot> findLatestSnapshot(UUID siteId, Long atVersion) throws IOException;
    List<Long> findAvailableSnapshots(UUID siteId) throws IOException;
    boolean persistSnapshot(SiteSnapshot snapshot) throws IOException;
}
