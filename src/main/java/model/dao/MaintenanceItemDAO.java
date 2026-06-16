package model.dao;

import database.DatabaseConnection;
import model.entity.MaintenanceItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceItemDAO {

    public List<MaintenanceItem> findAll() {
        List<MaintenanceItem> list = new ArrayList<>();

        String sql = """
                SELECT item_id, item_code, item_name, item_type, unit,
                       default_unit_cost, is_active, notes, created_at, updated_at
                FROM maintenance_items
                WHERE is_active = 1
                ORDER BY item_type ASC, item_name ASC
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

    public List<MaintenanceItem> findByType(String itemType) {
        List<MaintenanceItem> list = new ArrayList<>();

        String sql = """
                SELECT item_id, item_code, item_name, item_type, unit,
                       default_unit_cost, is_active, notes, created_at, updated_at
                FROM maintenance_items
                WHERE item_type = ? AND is_active = 1
                ORDER BY item_name ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, itemType);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public Optional<MaintenanceItem> findById(long itemId) {
        String sql = """
                SELECT item_id, item_code, item_name, item_type, unit,
                       default_unit_cost, is_active, notes, created_at, updated_at
                FROM maintenance_items
                WHERE item_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, itemId);

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

    private MaintenanceItem mapRow(ResultSet rs) throws SQLException {
        MaintenanceItem item = new MaintenanceItem();
        item.setItemId(rs.getLong("item_id"));
        item.setItemCode(rs.getString("item_code"));
        item.setItemName(rs.getString("item_name"));
        item.setItemType(rs.getString("item_type"));
        item.setUnit(rs.getString("unit"));
        item.setDefaultUnitCost(rs.getBigDecimal("default_unit_cost"));
        item.setActive(rs.getBoolean("is_active"));
        item.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) item.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) item.setUpdatedAt(updatedAt.toLocalDateTime());

        return item;
    }
}
