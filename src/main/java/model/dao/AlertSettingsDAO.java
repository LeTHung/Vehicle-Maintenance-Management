package model.dao;

import database.DatabaseConnection;
import model.entity.AlertSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Optional;

public class AlertSettingsDAO {

    private static final int DEFAULT_SETTING_ID = 1;

    public AlertSettings findOrCreateDefault() {
        return findById(DEFAULT_SETTING_ID).orElseGet(this::insertDefault);
    }

    public Optional<AlertSettings> findById(int settingId) {
        String sql = """
                SELECT setting_id, document_alert_days, maintenance_alert_days,
                       maintenance_alert_km, is_active, updated_by,
                       created_at, updated_at
                FROM alert_settings
                WHERE setting_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, settingId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot load alert settings.", e);
        }

        return Optional.empty();
    }

    public AlertSettings update(AlertSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Alert settings is required.");
        }

        String updateSettingsSql = """
                UPDATE alert_settings
                SET document_alert_days = ?,
                    maintenance_alert_days = ?,
                    maintenance_alert_km = ?,
                    is_active = ?,
                    updated_by = ?
                WHERE setting_id = ?
                """;
        String updateDocumentTypesSql = """
                UPDATE document_types
                SET default_alert_days = ?
                WHERE is_active = 1
                """;

        try (Connection connection = DatabaseConnection.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement settingsStatement = connection.prepareStatement(updateSettingsSql);
                 PreparedStatement documentTypesStatement = connection.prepareStatement(updateDocumentTypesSql)) {

                settingsStatement.setInt(1, settings.getDocumentAlertDays());
                settingsStatement.setInt(2, settings.getMaintenanceAlertDays());
                settingsStatement.setInt(3, settings.getMaintenanceAlertKm());
                settingsStatement.setBoolean(4, settings.isActive());
                setNullableLong(settingsStatement, 5, settings.getUpdatedBy());
                settingsStatement.setInt(6, DEFAULT_SETTING_ID);

                if (settingsStatement.executeUpdate() == 0) {
                    insertDefault(connection);
                    settingsStatement.executeUpdate();
                }

                documentTypesStatement.setInt(1, settings.getDocumentAlertDays());
                documentTypesStatement.executeUpdate();

                connection.commit();
                connection.setAutoCommit(originalAutoCommit);
                return findById(DEFAULT_SETTING_ID).orElse(settings);
            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(originalAutoCommit);
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot update alert settings.", e);
        }
    }

    private AlertSettings insertDefault() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            insertDefault(connection);
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot create default alert settings.", e);
        }

        return findById(DEFAULT_SETTING_ID).orElseGet(AlertSettings::defaults);
    }

    private void insertDefault(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO alert_settings (
                    setting_id, document_alert_days, maintenance_alert_days,
                    maintenance_alert_km, is_active
                )
                VALUES (1, 15, 7, 500, 1)
                ON DUPLICATE KEY UPDATE
                    document_alert_days = document_alert_days
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    private AlertSettings mapRow(ResultSet resultSet) throws SQLException {
        AlertSettings settings = new AlertSettings();
        settings.setSettingId(resultSet.getInt("setting_id"));
        settings.setDocumentAlertDays(resultSet.getInt("document_alert_days"));
        settings.setMaintenanceAlertDays(resultSet.getInt("maintenance_alert_days"));
        settings.setMaintenanceAlertKm(resultSet.getInt("maintenance_alert_km"));
        settings.setActive(resultSet.getBoolean("is_active"));

        long updatedBy = resultSet.getLong("updated_by");
        settings.setUpdatedBy(resultSet.wasNull() ? null : updatedBy);

        settings.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        settings.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));
        return settings;
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }

        statement.setLong(index, value);
    }

    private java.time.LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
