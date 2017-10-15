package com.tomergabel.examples.eventsourcing.persistence;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;
import org.skife.jdbi.v2.tweak.ResultColumnMapper;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UUIDMapper implements ArgumentFactory<UUID>, ResultColumnMapper<UUID> {
    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof UUID;
    }

    @Override
    public Argument build(Class<?> expectedType, UUID value, StatementContext ctx) {
        return new Argument() {
            @Override
            public void apply(int position, PreparedStatement statement, StatementContext ctx) throws SQLException {
                ByteBuffer buffer = ByteBuffer.allocate(16);
                buffer.putLong(value.getMostSignificantBits());
                buffer.putLong(value.getLeastSignificantBits());
                statement.setBytes(position, buffer.array());
            }
        };
    }

    public static UUID readUUID(byte[] bytes) throws SQLException {
        if (bytes == null) throw new SQLException("Unexpected null instead of UUID");
        if (bytes.length != 16) throw new SQLException("Unexpected UUID blob of size " + bytes.length);

        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return new UUID(buffer.getLong(), buffer.getLong());
    }

    @Override
    public UUID mapColumn(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return readUUID(r.getBytes(columnNumber));
    }

    @Override
    public UUID mapColumn(ResultSet r, String columnLabel, StatementContext ctx) throws SQLException {
        return readUUID(r.getBytes(columnLabel));
    }
}
