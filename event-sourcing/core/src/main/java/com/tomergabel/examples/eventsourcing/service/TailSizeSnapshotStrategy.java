package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.util.List;

public class TailSizeSnapshotStrategy implements SnapshotStrategy {
    private final int maximumTailSize;

    public TailSizeSnapshotStrategy(int maximumTailSize) {
        this.maximumTailSize = maximumTailSize;
    }

    @Override
    public boolean shouldTakeSnapshot(SiteSnapshot baseSnapshot, List<SiteEvent> tail) {
        return tail.size() >= maximumTailSize;
    }
}
