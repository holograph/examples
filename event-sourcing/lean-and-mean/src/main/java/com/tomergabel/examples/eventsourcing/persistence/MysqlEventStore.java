package com.tomergabel.examples.eventsourcing.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tomergabel.examples.eventsourcing.model.*;
import io.dropwizard.jdbi.args.InstantArgumentFactory;
import io.dropwizard.jdbi.args.InstantMapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.PreparedBatch;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.tomergabel.examples.eventsourcing.persistence.JDBIHelpers.isPKViolation;
import static com.tomergabel.examples.eventsourcing.persistence.UUIDMapper.readUUID;

public class MysqlEventStore implements EventStore {

    private DBI database;
    private static ObjectMapper mapper = new ObjectMapper();

    public MysqlEventStore(DBI database) {
        this.database = database;
    }

    private static byte[] encodeEventPayload(SiteEvent event) {
        ObjectNode payload = mapper.createObjectNode();
        String type;
        if (event instanceof SiteCreated) {
            type = "created";
        } else if (event instanceof SiteUpdated) {
            type = "updated";
            payload.set("delta", ((SiteUpdated) event).getDelta());
        } else if (event instanceof SiteDeleted) {
            type = "deleted";
        } else if (event instanceof SiteRestored) {
            type = "restored";
            payload.put("restoredVersion", ((SiteRestored) event).getRestoredVersion());
            payload.set("delta", ((SiteRestored) event).getDelta());
        } else
            throw new IllegalStateException("Cannot encode unknown site event type " + event.getClass().getSimpleName());
        payload.put("type", type);

        try {
            return mapper.writeValueAsBytes(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot encode event payload", e);
        }
    }

    private static SiteEvent decodeEvent(UUID userId, long version, Instant timestamp, JsonNode payload) throws SQLException {
        JsonNode type = payload.get("type");
        if (type == null) throw new SQLException("Invalid payload: missing \"type\" field");
        if (!type.isTextual()) throw new SQLException("Invalid payload: \"type\" field is not a text node");

        switch (type.textValue()) {
            case "created":
                if (version != SiteEvent.INITIAL_VERSION)
                    throw new SQLException("Unexpected version " + version + " on creation event");
                return new SiteCreated(userId, timestamp);

            case "updated":
                return new SiteUpdated(version, userId, timestamp, payload.get("delta"));

            case "deleted":
                return new SiteDeleted(version, userId, timestamp);

            case "restored":
                JsonNode restoredVersion = payload.get("restoredVersion");
                if (restoredVersion == null)
                    throw new SQLException("Invalid payload: missing \"restoredVersion\" field");
                if (!restoredVersion.canConvertToLong())
                    throw new SQLException("Unexpected non-numeric restored version " + restoredVersion.toString());
                JsonNode delta = payload.get("delta");
                return new SiteRestored(version, userId, timestamp, restoredVersion.longValue(), delta);

            default:
                throw new SQLException("Unrecognized event type " + type.textValue());
        }
    }

    private static ResultSetMapper<SiteEvent> eventRowMapper = new ResultSetMapper<SiteEvent>() {
        @Override
        public SiteEvent map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
            UUID userId = readUUID(resultSet.getBytes("user"));
            long version = resultSet.getLong("version");
            Instant timestamp = resultSet.getTimestamp("timestamp").toInstant();
            JsonNode payload;
            try {
                payload = mapper.readTree(resultSet.getBinaryStream("payload"));
            } catch (IOException e) {
                throw new SQLException("Failed to read event payload", e);
            }

            return decodeEvent(userId, version, timestamp, payload);
        }
    };

    @Override
    public List<SiteEvent> getEvents(UUID siteId, Long from, Long to) {
        return database.withHandle(handle -> {
            StringBuilder q = new StringBuilder()
                    .append("select version, user, timestamp, payload from events ")
                    .append("where site_id=:id");
            if (from != null) q.append(" and version >= :from");
            if (to != null) q.append(" and version <= :to");
            q.append(" order by version asc");

            return handle
                    .createQuery(q.toString())
                    .bind("id", siteId)
                    .bind("from", from)
                    .bind("to", to)
                    .map(eventRowMapper)
                    .list();
        });
    }

    @Override
    public boolean addEvents(UUID siteId, List<SiteEvent> events) {
        return database.inTransaction((handle, tx) -> {

            PreparedBatch batch = handle.prepareBatch(
                    "insert into events (site_id, version, user, timestamp, payload) " +
                            "values (?, ?, ?, ?, ?)");

            events.forEach(event -> batch.add(
                    siteId,
                    event.getVersion(),
                    event.getUserId(),
                    event.getTimestamp(),
                    encodeEventPayload(event)));

            try {
                batch.execute();
                return true;
            } catch (Exception e) {
                if (isPKViolation(e))
                    return false;
                else
                    throw e;
            }
        });
    }

    public static String SCHEMA_DDL =
            "create table events (                 " +
            "   site_id binary(16),                " +
            "   version int,                       " +
            "   user binary(16),                   " +
            "   timestamp timestamp,               " +
            "   payload blob,                      " +
            "   primary key (site_id, version desc)" +
            ")                                     " +
            "engine=innodb;                        ";

    public static void configureDatabase(DBI database) {
        database.registerArgumentFactory(new InstantArgumentFactory());
        database.registerColumnMapper(new InstantMapper());
        database.registerArgumentFactory(new UUIDMapper());
        database.registerColumnMapper(new UUIDMapper());
    }
}
