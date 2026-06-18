package model.dao;

import database.DatabaseConnection;
import model.entity.AuditLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {

    private static final Object TABLE_LOCK = new Object();
    private static volatile boolean tableEnsured;

    public Long insert(AuditLog auditLog) {
        if (auditLog == null) {
            return null;
        }

        ensureTableExists();

        String sql = """
                INSERT INTO audit_logs (
                    user_id, username, action, entity_type, entity_id,
                    description, ip_address, device, session_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setNullableLong(statement, 1, auditLog.getUserId());
            setNullableString(statement, 2, auditLog.getUsername());
            statement.setString(3, auditLog.getAction());
            setNullableString(statement, 4, auditLog.getEntityType());
            setNullableString(statement, 5, auditLog.getEntityId());
            setNullableString(statement, 6, auditLog.getDescription());
            setNullableString(statement, 7, auditLog.getIpAddress());
            setNullableString(statement, 8, auditLog.getDevice());
            setNullableString(statement, 9, auditLog.getSessionId());

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long generatedId = generatedKeys.getLong(1);
                    auditLog.setAuditLogId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot insert audit log.", e);
        }

        return null;
    }

    public List<AuditLog> findRecent(int limit) {
        ensureTableExists();

        List<AuditLog> logs = new ArrayList<>();
        int normalizedLimit = Math.max(1, Math.min(limit, 500));

        String sql = """
                SELECT audit_log_id, user_id, username, action, entity_type,
                       entity_id, description, ip_address, device, session_id,
                       created_at
                FROM audit_logs
                ORDER BY created_at DESC, audit_log_id DESC
                LIMIT ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, normalizedLimit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapToAuditLog(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot load audit logs.", e);
        }

        return logs;
    }

    private void ensureTableExists() {
        if (tableEnsured) {
            return;
        }

        synchronized (TABLE_LOCK) {
            if (tableEnsured) {
                return;
            }

            String sql = """
                    CREATE TABLE IF NOT EXISTS audit_logs (
                        audit_log_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                        user_id BIGINT UNSIGNED NULL,
                        username VARCHAR(50) NULL,
                        action VARCHAR(60) NOT NULL,
                        entity_type VARCHAR(60) NULL,
                        entity_id VARCHAR(64) NULL,
                        description VARCHAR(500) NULL,
                        ip_address VARCHAR(45) NULL,
                        device VARCHAR(120) NULL,
                        session_id VARCHAR(120) NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (audit_log_id),
                        KEY idx_audit_logs_created_at (created_at),
                        KEY idx_audit_logs_user_id (user_id),
                        KEY idx_audit_logs_action (action),
                        KEY idx_audit_logs_entity (entity_type, entity_id),
                        CONSTRAINT fk_audit_logs_user
                            FOREIGN KEY (user_id) REFERENCES users (user_id)
                            ON DELETE SET NULL
                            ON UPDATE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                    """;

            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(sql);
                tableEnsured = true;
            } catch (SQLException e) {
                throw new IllegalStateException("Cannot ensure audit_logs table exists.", e);
            }
        }
    }

    private AuditLog mapToAuditLog(ResultSet resultSet) throws SQLException {
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditLogId(resultSet.getLong("audit_log_id"));

        long userId = resultSet.getLong("user_id");
        auditLog.setUserId(resultSet.wasNull() ? null : userId);

        auditLog.setUsername(resultSet.getString("username"));
        auditLog.setAction(resultSet.getString("action"));
        auditLog.setEntityType(resultSet.getString("entity_type"));
        auditLog.setEntityId(resultSet.getString("entity_id"));
        auditLog.setDescription(resultSet.getString("description"));
        auditLog.setIpAddress(resultSet.getString("ip_address"));
        auditLog.setDevice(resultSet.getString("device"));
        auditLog.setSessionId(resultSet.getString("session_id"));
        auditLog.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        return auditLog;
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }

        statement.setLong(index, value);
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        statement.setString(index, value.trim());
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
