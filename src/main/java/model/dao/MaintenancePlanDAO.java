package model.dao;

import database.DatabaseConnection;
import model.dto.MaintenanceDueAlertDTO;
import model.entity.MaintenancePlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaintenancePlanDAO {

    public List<MaintenancePlan> findAll() {
        List<MaintenancePlan> list = new ArrayList<>();

        String sql = """
                SELECT plan_id, vehicle_id, maintenance_type_id,
                       interval_days, interval_km,
                       last_service_date, last_service_odometer,
                       next_due_date, next_due_odometer,
                       alert_before_days, alert_before_km,
                       is_active, notes, created_by, updated_by,
                       created_at, updated_at
                FROM maintenance_plans
                ORDER BY plan_id DESC
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

    public List<MaintenancePlan> findByVehicleId(long vehicleId) {
        List<MaintenancePlan> list = new ArrayList<>();

        String sql = """
                SELECT plan_id, vehicle_id, maintenance_type_id,
                       interval_days, interval_km,
                       last_service_date, last_service_odometer,
                       next_due_date, next_due_odometer,
                       alert_before_days, alert_before_km,
                       is_active, notes, created_by, updated_by,
                       created_at, updated_at
                FROM maintenance_plans
                WHERE vehicle_id = ?
                ORDER BY plan_id DESC
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

    public Optional<MaintenancePlan> findById(long planId) {
        String sql = """
                SELECT plan_id, vehicle_id, maintenance_type_id,
                       interval_days, interval_km,
                       last_service_date, last_service_odometer,
                       next_due_date, next_due_odometer,
                       alert_before_days, alert_before_km,
                       is_active, notes, created_by, updated_by,
                       created_at, updated_at
                FROM maintenance_plans
                WHERE plan_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, planId);

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

    public Long insert(MaintenancePlan plan) {
        if (plan == null) return null;

        String sql = """
                INSERT INTO maintenance_plans (
                    vehicle_id, maintenance_type_id,
                    interval_days, interval_km,
                    last_service_date, last_service_odometer,
                    next_due_date, next_due_odometer,
                    alert_before_days, alert_before_km,
                    is_active, notes, created_by, updated_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, plan);

            if (ps.executeUpdate() == 0) return null;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    plan.setPlanId(id);
                    return id;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(MaintenancePlan plan) {
        if (plan == null || plan.getPlanId() <= 0) return false;

        String sql = """
                UPDATE maintenance_plans
                SET vehicle_id = ?,
                    maintenance_type_id = ?,
                    interval_days = ?,
                    interval_km = ?,
                    last_service_date = ?,
                    last_service_odometer = ?,
                    next_due_date = ?,
                    next_due_odometer = ?,
                    alert_before_days = ?,
                    alert_before_km = ?,
                    is_active = ?,
                    notes = ?,
                    created_by = ?,
                    updated_by = ?
                WHERE plan_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, plan);
            ps.setLong(15, plan.getPlanId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<MaintenanceDueAlertDTO> findDueAlerts() {
        List<MaintenanceDueAlertDTO> list = new ArrayList<>();

        String sql = """
                SELECT plan_id, vehicle_id, license_plate, vehicle_type,
                       maintenance_name, current_odometer,
                       next_due_date, next_due_odometer,
                       effective_alert_days, effective_alert_km, due_status
                FROM vw_due_maintenance_plans
                WHERE due_status IN ('OVERDUE', 'COMING_DUE')
                ORDER BY
                    CASE due_status WHEN 'OVERDUE' THEN 0 ELSE 1 END,
                    next_due_date ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapAlertRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Đọc ngưỡng cảnh báo bảo dưỡng mặc định từ bảng alert_settings (setting_id = 1).
     * Trả về [maintenance_alert_days, maintenance_alert_km]; fallback [7, 500] nếu không có cấu hình.
     */
    public int[] findMaintenanceAlertDefaults() {
        int days = 7;
        int km = 500;

        String sql = "SELECT maintenance_alert_days, maintenance_alert_km FROM alert_settings WHERE setting_id = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                days = rs.getInt("maintenance_alert_days");
                km = rs.getInt("maintenance_alert_km");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new int[]{days, km};
    }

    public boolean deactivate(long planId) {
        String sql = "UPDATE maintenance_plans SET is_active = 0 WHERE plan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, planId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private MaintenanceDueAlertDTO mapAlertRow(ResultSet rs) throws SQLException {
        MaintenanceDueAlertDTO dto = new MaintenanceDueAlertDTO();
        dto.setPlanId(rs.getLong("plan_id"));
        dto.setVehicleId(rs.getLong("vehicle_id"));
        dto.setLicensePlate(rs.getString("license_plate"));
        dto.setVehicleType(rs.getString("vehicle_type"));
        dto.setMaintenanceName(rs.getString("maintenance_name"));

        int odo = rs.getInt("current_odometer");
        dto.setCurrentOdometer(rs.wasNull() ? null : odo);

        Date nextDate = rs.getDate("next_due_date");
        dto.setNextDueDate(nextDate != null ? nextDate.toLocalDate() : null);

        int nextOdo = rs.getInt("next_due_odometer");
        dto.setNextDueOdometer(rs.wasNull() ? null : nextOdo);

        int alertDays = rs.getInt("effective_alert_days");
        dto.setEffectiveAlertDays(rs.wasNull() ? null : alertDays);

        int alertKm = rs.getInt("effective_alert_km");
        dto.setEffectiveAlertKm(rs.wasNull() ? null : alertKm);

        dto.setDueStatus(rs.getString("due_status"));
        return dto;
    }

    private void fillStatement(PreparedStatement ps, MaintenancePlan plan) throws SQLException {
        ps.setLong(1, plan.getVehicleId());
        ps.setInt(2, plan.getMaintenanceTypeId());

        setNullableInt(ps, 3, plan.getIntervalDays());
        setNullableInt(ps, 4, plan.getIntervalKm());

        if (plan.getLastServiceDate() == null) {
            ps.setNull(5, Types.DATE);
        } else {
            ps.setDate(5, Date.valueOf(plan.getLastServiceDate()));
        }

        setNullableInt(ps, 6, plan.getLastServiceOdometer());

        if (plan.getNextDueDate() == null) {
            ps.setNull(7, Types.DATE);
        } else {
            ps.setDate(7, Date.valueOf(plan.getNextDueDate()));
        }

        setNullableInt(ps, 8, plan.getNextDueOdometer());
        setNullableInt(ps, 9, plan.getAlertBeforeDays());
        setNullableInt(ps, 10, plan.getAlertBeforeKm());
        ps.setBoolean(11, plan.isActive());
        setNullableString(ps, 12, plan.getNotes());
        setNullableLong(ps, 13, plan.getCreatedBy());
        setNullableLong(ps, 14, plan.getUpdatedBy());
    }

    private MaintenancePlan mapRow(ResultSet rs) throws SQLException {
        MaintenancePlan p = new MaintenancePlan();
        p.setPlanId(rs.getLong("plan_id"));
        p.setVehicleId(rs.getLong("vehicle_id"));
        p.setMaintenanceTypeId(rs.getInt("maintenance_type_id"));

        int intervalDays = rs.getInt("interval_days");
        p.setIntervalDays(rs.wasNull() ? null : intervalDays);

        int intervalKm = rs.getInt("interval_km");
        p.setIntervalKm(rs.wasNull() ? null : intervalKm);

        Date lastDate = rs.getDate("last_service_date");
        p.setLastServiceDate(lastDate != null ? lastDate.toLocalDate() : null);

        int lastOdo = rs.getInt("last_service_odometer");
        p.setLastServiceOdometer(rs.wasNull() ? null : lastOdo);

        Date nextDate = rs.getDate("next_due_date");
        p.setNextDueDate(nextDate != null ? nextDate.toLocalDate() : null);

        int nextOdo = rs.getInt("next_due_odometer");
        p.setNextDueOdometer(rs.wasNull() ? null : nextOdo);

        int alertDays = rs.getInt("alert_before_days");
        p.setAlertBeforeDays(rs.wasNull() ? null : alertDays);

        int alertKm = rs.getInt("alert_before_km");
        p.setAlertBeforeKm(rs.wasNull() ? null : alertKm);

        p.setActive(rs.getBoolean("is_active"));
        p.setNotes(rs.getString("notes"));

        long createdBy = rs.getLong("created_by");
        p.setCreatedBy(rs.wasNull() ? null : createdBy);

        long updatedBy = rs.getLong("updated_by");
        p.setUpdatedBy(rs.wasNull() ? null : updatedBy);

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) p.setUpdatedAt(updatedAt.toLocalDateTime());

        return p;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void setNullableLong(PreparedStatement ps, int index, Long value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.BIGINT);
        } else {
            ps.setLong(index, value);
        }
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }
}
