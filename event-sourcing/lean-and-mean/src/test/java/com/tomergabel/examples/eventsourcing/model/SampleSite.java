package com.tomergabel.examples.eventsourcing.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("WeakerAccess")
public class SampleSite {

    public static JsonNode delta1;
    public static JsonNode delta2;
    public static JsonNode delta3;
    public static JsonNode delta4;
    public static JsonNode finalBlob;

    public static UUID siteId = UUID.randomUUID();
    public static UUID owner = UUID.randomUUID();
    public static UUID user = UUID.randomUUID();

    public static SiteEvent created0;
    public static SiteEvent updated1;
    public static SiteEvent updated2;
    public static SiteEvent updated3;
    public static SiteEvent restored4;
    public static SiteEvent archived5;
    public static List<SiteEvent> allEvents;

    public static long finalVersion;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            delta1 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/name\",\"value\":\"my site\"}]");
            delta2 = mapper.readTree("[{\"op\":\"add\",\"path\":\"/url\",\"value\":\"http://www.example.com\"}]");
            delta3 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"other site\"}]");
            delta4 = mapper.readTree("[{\"op\":\"replace\",\"path\":\"/name\",\"value\":\"my site\"}]");
            finalBlob = mapper.readTree("{\"name\":\"my site\", \"url\":\"http://www.example.com\"}");

            created0 = new SiteCreated(owner, Instant.now());
            updated1 = new SiteUpdated(1, owner, Instant.now(), delta1);
            updated2 = new SiteUpdated(2, owner, Instant.now(), delta2);
            updated3 = new SiteUpdated(3, siteId, Instant.now(), delta3);
            restored4 = new SiteRestored(4, user, Instant.now(), 2, delta4);
            archived5 = new SiteDeleted(5, user, Instant.now());
            allEvents = Arrays.asList(created0, updated1, updated2, updated3, restored4, archived5);
            finalVersion = 5;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
