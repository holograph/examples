package com.tomergabel.examples.eventsourcing.persistence;

import com.tomergabel.examples.eventsourcing.model.SiteEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryEventStore implements EventStore {

    private Map<UUID, ArrayList<SiteEvent>> eventMap = new HashMap<>();
    private UUID lastQueriedSite = null;
    private Long lastQueriedFromVersion = null;
    private Long lastQueriedToVersion = null;

    @Override
    public synchronized List<SiteEvent> getEvents(UUID siteId, Long from, Long to) {
        lastQueriedSite = siteId;
        lastQueriedFromVersion = from;
        lastQueriedToVersion = to;

        if (!eventMap.containsKey(siteId)) return Collections.emptyList();
        Stream<SiteEvent> events = eventMap.get(siteId).stream();
        if (from != null) events = events.filter(event -> event.getVersion() >= from);
        if (to != null) events = events.filter(event -> event.getVersion() <= to);
        return events.collect(Collectors.toList());
    }

    @Override
    public synchronized boolean addEvents(UUID siteId, List<SiteEvent> events) {
        ArrayList<SiteEvent> eventStream = eventMap.computeIfAbsent(siteId, uuid -> new ArrayList<>());

        Set<Long> existing = eventStream.stream().map(SiteEvent::getVersion).collect(Collectors.toSet());
        if (events.stream().map(SiteEvent::getVersion).anyMatch(existing::contains))
            return false;

        eventStream.addAll(events);
        return true;
    }

    public synchronized void reset() {
        eventMap.clear();
        lastQueriedFromVersion = null;
        lastQueriedToVersion = null;
    }

    public UUID getLastQueriedSite() {
        return lastQueriedSite;
    }

    public Long getLastQueriedFromVersion() {
        return lastQueriedFromVersion;
    }

    public Long getLastQueriedToVersion() {
        return lastQueriedToVersion;
    }
}
