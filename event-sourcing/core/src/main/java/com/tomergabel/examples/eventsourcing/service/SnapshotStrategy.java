package com.tomergabel.examples.eventsourcing.service;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;

import java.util.List;

public interface SnapshotStrategy {
    boolean shouldTakeSnapshot(SiteSnapshot baseSnapshot, List<SiteEvent> tail);
}
