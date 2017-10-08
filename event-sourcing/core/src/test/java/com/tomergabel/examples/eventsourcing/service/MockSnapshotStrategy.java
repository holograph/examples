package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.util.List;

public class MockSnapshotStrategy implements SnapshotStrategy {
    SiteSnapshot lastQueriedSnapshot = null;
    List<SiteEvent> lastQueriedTail = null;
    boolean response = false;

    @Override
    public boolean shouldTakeSnapshot(SiteSnapshot baseSnapshot, List<SiteEvent> tail) {
        lastQueriedSnapshot = baseSnapshot;
        lastQueriedTail = tail;
        return response;
    }

    boolean queried() {
        return lastQueriedSnapshot != null || lastQueriedTail != null;
    }

    void reset() {
        lastQueriedTail = null;
        lastQueriedSnapshot = null;
        response = false;
    }
}
