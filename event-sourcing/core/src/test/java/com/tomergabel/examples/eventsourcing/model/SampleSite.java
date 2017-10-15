package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SampleSite {

    public static JsonNode delta1;
    public static JsonNode delta2;
    public static JsonNode delta3;
    public static JsonNode delta4;
    public static JsonNode blob1;
    public static JsonNode blob2;
    public static JsonNode blob3;
    public static JsonNode blob4;
    public static JsonNode finalBlob;

    public final UUID id = UUID.randomUUID();
    public final UUID owner = UUID.randomUUID();
    public final UUID user = UUID.randomUUID();

    public final SiteEvent created0 = new SiteCreated(owner, Instant.now());
    public final SiteEvent updated1 = new SiteUpdated(1, owner, Instant.now(), delta1);
    public final SiteEvent updated2 = new SiteUpdated(2, owner, Instant.now(), delta2);
    public final SiteEvent updated3 = new SiteUpdated(3, owner, Instant.now(), delta3);
    public final SiteEvent restored4 = new SiteRestored(4, user, Instant.now(), 2, delta4);
    public final SiteEvent archived5 = new SiteDeleted(5, user, Instant.now());
    public final List<SiteEvent> allEvents = Arrays.asList(created0, updated1, updated2, updated3, restored4, archived5);

    public final SiteSnapshot intermediateState = new SiteSnapshot(id, 3, owner, blob3, false);
    public final SiteSnapshot finalState = new SiteSnapshot(id, 5, owner, finalBlob, true);

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            delta1 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/name\",\"value\":\"my site\"}]");
            blob1 = mapper.readTree("{\"name\":\"my site\"}");
            delta2 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/url\",\"value\":\"http://www.example.com\"}]");
            blob2 = mapper.readTree("{\"name\":\"my site\",\"url\":\"http://www.example.com\"}");
            delta3 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"other site\"}]");
            blob3 = mapper.readTree("{\"name\":\"other site\", \"url\":\"http://www.example.com\"}");
            delta4 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"my site\"}]");
            blob4 = mapper.readTree("{\"name\":\"my site\", \"url\":\"http://www.example.com\"}");
            finalBlob = mapper.readTree("{\"name\":\"my site\", \"url\":\"http://www.example.com\"}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
