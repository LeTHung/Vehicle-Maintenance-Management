package model.dao;

import database.DatabaseConnection;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenanceRecord;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenanceRecordDAO {

    public List<MaintenanceRecord> findAll() {
        List<MaintenanceRecord> list = new ArrayList<>();

        String sql = """
                SELECT record_id, vehicle_id, plan_id, record_type, title,
                       scheduled_date, service_date, started_at, completed_at,
                       odometer, work_summary, service_provider_name,
                       technician_id, total_cost, record_status, notes,
                       created_by, updated_by, created_at, updated_at
                FROM maintenance_records
                ORDER BY record_id DESC
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

    public List<MaintenanceRecord> findByVehicleId(long vehicleId) {
        List<MaintenanceRecord> list = new ArrayList<>();

        String sql = """
                SELECT record_id, vehicle_id, plan_id, record_type, title,
                       scheduled_date, service_date, started_at, completed_at,
                       odometer, work_summary, service_provider_name,
                       technician_id, total_cost, record_status, notes,
                       created_by, updated_by, created_at, updated_at
                FROM maintenance_records
                WHERE vehicle_id = ?
                ORDER BY service_date DESC, record_id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vehicleId);

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

    /** ODO lớn nhất trên các phiếu COMPLETED của xe (có thể loại trừ một phiếu). */
    public Optional<Integer> findMaxCompletedOdometer(long vehicleId, Long excludeRecordId) {
        String sql = """
                SELECT MAX(odometer) AS max_odometer
                FROM maintenance_records
                WHERE vehicle_id = ?
                  AND record_status = 'COMPLETED'
                  AND odometer IS NOT NULL
                """ + (excludeRecordId != null ? " AND record_id <> ?" : "");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vehicleId);
            if (excludeRecordId != null) {
                ps.setLong(2, excludeRecordId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int max = rs.getInt("max_odometer");
                    return rs.wasNull() ? Optional.empty() : Optional.of(max);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<MaintenanceRecord> findById(long recordId) {
        String sql = """
                SELECT record_id, vehicle_id, plan_id, record_type, title,
                       scheduled_date, service_date, started_at, completed_at,
                       odometer, work_summary, service_provider_name,
                       technician_id, total_cost, record_status, notes,
                       created_by, updated_by, created_at, updated_at
                FROM maintenance_records
                WHERE record_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, recordId);

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

    public Long insert(MaintenanceRecord record) {
        if (record == null) return null;

        String sql = """
                INSERT INTO maintenance_records (
                    vehicle_id, plan_id, record_type, title,
                    scheduled_date, service_date, started_at, completed_at,
                    odometer, work_summary, service_provider_name,
                    technician_id, total_cost, record_status, notes,
                    created_by, updated_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, record);

            if (ps.executeUpdate() == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    record.setRecordId(id);
                    return id;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(MaintenanceRecord record) {
        if (record == null || record.getRecordId() <= 0) return false;

        String sql = """
                UPDATE maintenance_records
                SET vehicle_id = ?,
                    plan_id = ?,
                    record_type = ?,
                    title = ?,
                    scheduled_date = ?,
                    service_date = ?,
                    started_at = ?,
                    completed_at = ?,
                    odometer = ?,
                    work_summary = ?,
                    service_provider_name = ?,
                    technician_id = ?,
                    total_cost = ?,
                    record_status = ?,
                    notes = ?,
                    created_by = ?,
                    updated_by = ?
                WHERE record_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, record);
            ps.setLong(18, record.getRecordId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ─── Line items ───────────────────────────────────────────────────────────

    public List<MaintenanceItemDetail> findItemsByRecordId(long recordId) {
        List<MaintenanceItemDetail> items = new ArrayList<>();

        String sql = """
                SELECT record_item_id, record_id, item_id, item_type,
                       description, quantity, unit, unit_cost, line_total,
                       notes, created_at, updated_at
                FROM maintenance_record_items
                WHERE record_id = ?
                ORDER BY record_item_id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, recordId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItemRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

    public Long insertItem(MaintenanceItemDetail item) {
        if (item == null) return null;

        String sql = """
                INSERT INTO maintenance_record_items (
                    record_id, item_id, item_type, description,
                    quantity, unit, unit_cost, notes
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, item.getRecordId());
            setNullableLong(ps, 2, item.getItemId());
            ps.setString(3, item.getItemType());
            ps.setString(4, item.getDescription());
            ps.setBigDecimal(5, item.getQuantity());
            setNullableString(ps, 6, item.getUnit());
            ps.setBigDecimal(7, item.getUnitCost());
            setNullableString(ps, 8, item.getNotes());

            if (ps.executeUpdate() == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    item.setRecordItemId(id);
                    return id;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean deleteItemsByRecordId(long recordId) {
        String sql = "DELETE FROM maintenance_record_items WHERE record_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, recordId);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void fillStatement(PreparedStatement ps, MaintenanceRecord r) throws SQLException {
        ps.setLong(1, r.getVehicleId());
        setNullableLong(ps, 2, r.getPlanId());
        ps.setString(3, r.getRecordType());
        setNullableString(ps, 4, r.getTitle());

        if (r.getScheduledDate() == null) ps.setNull(5, Types.DATE);
        else ps.setDate(5, Date.valueOf(r.getScheduledDate()));

        if (r.getServiceDate() == null) ps.setNull(6, Types.DATE);
        else ps.setDate(6, Date.valueOf(r.getServiceDate()));

        if (r.getStartedAt() == null) ps.setNull(7, Types.TIMESTAMP);
        else ps.setTimestamp(7, Timestamp.valueOf(r.getStartedAt()));

        if (r.getCompletedAt() == null) ps.setNull(8, Types.TIMESTAMP);
        else ps.setTimestamp(8, Timestamp.valueOf(r.getCompletedAt()));

        if (r.getOdometer() == null) ps.setNull(9, Types.INTEGER);
        else ps.setInt(9, r.getOdometer());

        ps.setString(10, r.getWorkSummary() != null ? r.getWorkSummary() : "");
        setNullableString(ps, 11, r.getServiceProviderName());
        setNullableLong(ps, 12, r.getTechnicianId());

        BigDecimal cost = r.getTotalCost() != null ? r.getTotalCost() : BigDecimal.ZERO;
        ps.setBigDecimal(13, cost);

        String status = r.getRecordStatus() != null ? r.getRecordStatus() : "OPEN";
        ps.setString(14, status);

        setNullableString(ps, 15, r.getNotes());
        setNullableLong(ps, 16, r.getCreatedBy());
        setNullableLong(ps, 17, r.getUpdatedBy());
    }

    private MaintenanceRecord mapRow(ResultSet rs) throws SQLException {
        MaintenanceRecord r = new MaintenanceRecord();
        r.setRecordId(rs.getLong("record_id"));
        r.setVehicleId(rs.getLong("vehicle_id"));

        long planId = rs.getLong("plan_id");
        r.setPlanId(rs.wasNull() ? null : planId);

        r.setRecordType(rs.getString("record_type"));
        r.setTitle(rs.getString("title"));

        Date scheduled = rs.getDate("scheduled_date");
        r.setScheduledDate(scheduled != null ? scheduled.toLocalDate() : null);

        Date serviceDate = rs.getDate("service_date");
        r.setServiceDate(serviceDate != null ? serviceDate.toLocalDate() : null);

        Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null) r.setStartedAt(startedAt.toLocalDateTime());

        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) r.setCompletedAt(completedAt.toLocalDateTime());

        int odo = rs.getInt("odometer");
        r.setOdometer(rs.wasNull() ? null : odo);

        r.setWorkSummary(rs.getString("work_summary"));
        r.setServiceProviderName(rs.getString("service_provider_name"));

        long techId = rs.getLong("technician_id");
        r.setTechnicianId(rs.wasNull() ? null : techId);

        r.setTotalCost(rs.getBigDecimal("total_cost"));
        r.setRecordStatus(rs.getString("record_status"));
        r.setNotes(rs.getString("notes"));

        long createdBy = rs.getLong("created_by");
        r.setCreatedBy(rs.wasNull() ? null : createdBy);

        long updatedBy = rs.getLong("updated_by");
        r.setUpdatedBy(rs.wasNull() ? null : updatedBy);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) r.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) r.setUpdatedAt(updatedAt.toLocalDateTime());

        return r;
    }

    private MaintenanceItemDetail mapItemRow(ResultSet rs) throws SQLException {
        MaintenanceItemDetail d = new MaintenanceItemDetail();
        d.setRecordItemId(rs.getLong("record_item_id"));
        d.setRecordId(rs.getLong("record_id"));

        long itemId = rs.getLong("item_id");
        d.setItemId(rs.wasNull() ? null : itemId);

        d.setItemType(rs.getString("item_type"));
        d.setDescription(rs.getString("description"));
        d.setQuantity(rs.getBigDecimal("quantity"));
        d.setUnit(rs.getString("unit"));
        d.setUnitCost(rs.getBigDecimal("unit_cost"));
        d.setLineTotal(rs.getBigDecimal("line_total"));
        d.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) d.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) d.setUpdatedAt(updatedAt.toLocalDateTime());

        return d;
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) ps.setNull(index, Types.BIGINT);
        else ps.setLong(index, value);
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) ps.setNull(index, Types.VARCHAR);
        else ps.setString(index, value.trim());
    }
}
