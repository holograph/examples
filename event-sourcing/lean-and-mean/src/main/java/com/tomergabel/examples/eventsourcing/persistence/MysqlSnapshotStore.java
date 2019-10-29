package com.tomergabel.examples.eventsourcing.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomergabel.examples.eventsourcing.model.SiteSnapshot;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.tomergabel.examples.eventsourcing.persistence.JDBIHelpers.isPKViolation;
import static com.tomergabel.examples.eventsourcing.persistence.UUIDMapper.readUUID;

public class MysqlSnapshotStore implements SnapshotStore {

    private DBI database;

    @Inject
    public MysqlSnapshotStore(@Named("snapshots") DBI database) {
        this.database = database;
    }

    public static void configureDatabase(DBI database) {
        database.registerColumnMapper(new UUIDMapper());
        database.registerArgumentFactory(new UUIDMapper());
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<SiteSnapshot> findLatestSnapshot(UUID siteId, Long atVersion) {
        return database.withHandle(handle -> {
            StringBuilder sql =
                    new StringBuilder("select version, owner, `blob`, deleted from snapshots where site_id=:id");
            if (atVersion != null) sql.append(" and version<:atVersion");
            sql.append(" order by version desc limit 1");
            Query<Map<String, Object>> query = handle.createQuery(sql.toString()).bind("id", siteId);
            if (atVersion != null) query.bind("atVersion", atVersion);

            ResultSetMapper<SiteSnapshot> mapper = new ResultSetMapper<SiteSnapshot>() {
                @Override
                public SiteSnapshot map(int index, ResultSet rs, StatementContext ctx) throws SQLException {
                    try {
                        return new SiteSnapshot(
                                siteId,
                                rs.getLong("version"),
                                readUUID(rs.getBytes("owner")),
                                objectMapper.readTree(rs.getBytes("blob")),
                                rs.getBoolean("deleted"));
                    } catch (IOException e) {
                        throw new SQLException("Failed to parse snapshot blob", e);
                    }
                }
            };

            return Optional.ofNullable(query.map(mapper).first());
        });
    }

    @Override
    public boolean persistSnapshot(SiteSnapshot snapshot) {
        return database.withHandle(handle -> {
            try {
                int inserted = handle.insert(
                        "insert into snapshots (site_id, version, owner, `blob`, deleted) values (?, ?, ?, ?, ?)",
                        snapshot.getSiteId(),
                        snapshot.getVersion(),
                        snapshot.getOwner(),
                        objectMapper.writeValueAsBytes(snapshot.getBlob()),
                        snapshot.getDeleted()
                );
                return inserted == 1;
            }
            catch (Exception e) {
                if (isPKViolation(e))
                    return false;
                else
                    throw e;
            }
        });
    }

    @Override
    public List<Long> findAvailableSnapshots(UUID siteId) {
        return database.withHandle(handle -> {
            String sql =  "select version from snapshots where site_id=:id order by version desc";
            return handle
                    .createQuery(sql).bind("id", siteId)
                    .mapTo(Long.class)
                    .list();
        });
    }
}
