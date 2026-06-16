package model.dao;

import database.DatabaseConnection;
import model.entity.MaintenanceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceTypeDAO {

    public List<MaintenanceType> findAll() {
        List<MaintenanceType> list = new ArrayList<>();

        String sql = """
                SELECT maintenance_type_id, maintenance_code, maintenance_name,
                       description, default_interval_days, default_interval_km,
                       is_active, created_at, updated_at
                FROM maintenance_types
                ORDER BY maintenance_type_id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<MaintenanceType> findAllActive() {
        List<MaintenanceType> list = new ArrayList<>();

        String sql = """
                SELECT maintenance_type_id, maintenance_code, maintenance_name,
                       description, default_interval_days, default_interval_km,
                       is_active, created_at, updated_at
                FROM maintenance_types
                WHERE is_active = 1
                ORDER BY maintenance_type_id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Optional<MaintenanceType> findById(int id) {
        String sql = """
                SELECT maintenance_type_id, maintenance_code, maintenance_name,
                       description, default_interval_days, default_interval_km,
                       is_active, created_at, updated_at
                FROM maintenance_types
                WHERE maintenance_type_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private MaintenanceType mapRow(ResultSet rs) throws SQLException {
        MaintenanceType t = new MaintenanceType();
        t.setMaintenanceTypeId(rs.getInt("maintenance_type_id"));
        t.setMaintenanceCode(rs.getString("maintenance_code"));
        t.setMaintenanceName(rs.getString("maintenance_name"));
        t.setDescription(rs.getString("description"));

        int days = rs.getInt("default_interval_days");
        t.setDefaultIntervalDays(rs.wasNull() ? null : days);

        int km = rs.getInt("default_interval_km");
        t.setDefaultIntervalKm(rs.wasNull() ? null : km);

        t.setActive(rs.getBoolean("is_active"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) t.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) t.setUpdatedAt(updatedAt.toLocalDateTime());

        return t;
    }
}
