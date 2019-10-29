package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemorySnapshotStore implements SnapshotStore {

    private Map<UUID, ArrayList<SiteSnapshot>> snapshotMap = new HashMap<>();
    private UUID lastQueriedSite;
    private Long lastQueriedVersion;

    @Override
    public synchronized Optional<SiteSnapshot> findLatestSnapshot(UUID siteId, Long atVersion) {
        lastQueriedSite = siteId;
        lastQueriedVersion = atVersion;

        if (!snapshotMap.containsKey(siteId))
            return Optional.empty();

        Stream<SiteSnapshot> stream = snapshotMap.get(siteId).stream();
        if (atVersion != null) stream = stream.filter(snapshot -> snapshot.getVersion() <= atVersion);
        return stream.max((left, right) -> (int)(left.getVersion() - right.getVersion()));
    }

    @Override
    public synchronized boolean persistSnapshot(SiteSnapshot snapshot) {
        ArrayList<SiteSnapshot> snapshots =
                snapshotMap.computeIfAbsent(snapshot.getSiteId(), id -> new ArrayList<>());

        if (snapshots.stream().anyMatch(existing -> existing.getVersion() == snapshot.getVersion()))
            return false;

        snapshots.add(snapshot);
        return true;
    }

    public synchronized Optional<SiteSnapshot> getPersistedSnapshot(UUID siteId, long atVersion) {
        if (!snapshotMap.containsKey(siteId))
            return Optional.empty();

        return snapshotMap.get(siteId).stream().filter(snapshot -> snapshot.getVersion() == atVersion).findAny();
    }

    public UUID getLastQueriedSite() {
        return lastQueriedSite;
    }

    public Long getLastQueriedVersion() {
        return lastQueriedVersion;
    }

    public synchronized void reset() {
        snapshotMap.clear();
        lastQueriedSite = null;
        lastQueriedVersion = null;
    }

    @Override
    public List<Long> findAvailableSnapshots(UUID siteId) {
        if (!snapshotMap.containsKey(siteId))
            return Collections.emptyList();

        return snapshotMap.get(siteId).stream().map(SiteSnapshot::getVersion).collect(Collectors.toList());
    }
}
