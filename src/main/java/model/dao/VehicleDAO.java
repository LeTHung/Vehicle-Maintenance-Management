package model.dao;

import database.DatabaseConnection;
import model.entity.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VehicleDAO {

    public List<Vehicle> findAll() {
        List<Vehicle> vehicles = new ArrayList<>();

        String sql = """
                SELECT vehicle_id, vehicle_code, license_plate, vehicle_type,
                       brand, model, manufacture_year, purchase_date,
                       chassis_number, engine_number, color, current_odometer,
                       vehicle_status, notes
                FROM vehicles
                ORDER BY vehicle_id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicles;
    }

    public Optional<Vehicle> findById(long vehicleId) {
        String sql = """
                SELECT vehicle_id, vehicle_code, license_plate, vehicle_type,
                       brand, model, manufacture_year, purchase_date,
                       chassis_number, engine_number, color, current_odometer,
                       vehicle_status, notes
                FROM vehicles
                WHERE vehicle_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vehicleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToVehicle(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<Vehicle> search(String keyword) {
        List<Vehicle> vehicles = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        String sql = """
                SELECT vehicle_id, vehicle_code, license_plate, vehicle_type,
                       brand, model, manufacture_year, purchase_date,
                       chassis_number, engine_number, color, current_odometer,
                       vehicle_status, notes
                FROM vehicles
                WHERE vehicle_code LIKE ?
                   OR license_plate LIKE ?
                   OR brand LIKE ?
                   OR model LIKE ?
                ORDER BY vehicle_id DESC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            String value = "%" + keyword + "%";
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vehicles;
    }

    public Long insert(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        String sql = """
                INSERT INTO vehicles (
                    vehicle_code, license_plate, vehicle_type, brand, model,
                    manufacture_year, purchase_date, chassis_number, engine_number,
                    color, current_odometer, vehicle_status, notes
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillVehicleStatement(ps, vehicle);

            if (ps.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    vehicle.setVehicleId(generatedId);
                    return generatedId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleId() <= 0) {
            return false;
        }

        String sql = """
                UPDATE vehicles
                SET vehicle_code = ?,
                    license_plate = ?,
                    vehicle_type = ?,
                    brand = ?,
                    model = ?,
                    manufacture_year = ?,
                    purchase_date = ?,
                    chassis_number = ?,
                    engine_number = ?,
                    color = ?,
                    current_odometer = ?,
                    vehicle_status = ?,
                    notes = ?
                WHERE vehicle_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            fillVehicleStatement(ps, vehicle);
            ps.setLong(14, vehicle.getVehicleId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật ODO hiện tại của xe sau khi bảo dưỡng.
     * Chỉ tăng (không cho lùi ODO) để tránh ghi đè bằng số nhỏ hơn.
     */
    public boolean updateOdometer(long vehicleId, int odometer) {
        String sql = """
                UPDATE vehicles
                SET current_odometer = ?
                WHERE vehicle_id = ?
                  AND (current_odometer IS NULL OR current_odometer < ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, odometer);
            ps.setLong(2, vehicleId);
            ps.setInt(3, odometer);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsByVehicleCode(String vehicleCode, Long excludedVehicleId) {
        return existsByUniqueColumn("vehicle_code", vehicleCode, excludedVehicleId);
    }

    public boolean existsByLicensePlate(String licensePlate, Long excludedVehicleId) {
        return existsByUniqueColumn("license_plate", licensePlate, excludedVehicleId);
    }

    public boolean existsByChassisNumber(String chassisNumber, Long excludedVehicleId) {
        return existsByUniqueColumn("chassis_number", chassisNumber, excludedVehicleId);
    }

    public boolean existsByEngineNumber(String engineNumber, Long excludedVehicleId) {
        return existsByUniqueColumn("engine_number", engineNumber, excludedVehicleId);
    }

    private boolean existsByUniqueColumn(String column, String value, Long excludedVehicleId) {
        if (value == null || value.isBlank()) {
            return false;
        }

        String normalizedColumn = switch (column) {
            case "vehicle_code", "license_plate", "chassis_number", "engine_number" -> column;
            default -> throw new IllegalArgumentException("Unsupported unique column: " + column);
        };

        String sql = """
                SELECT 1
                FROM vehicles
                WHERE %s = ?
                  AND (? IS NULL OR vehicle_id <> ?)
                LIMIT 1
                """.formatted(normalizedColumn);

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value.trim());
            if (excludedVehicleId == null) {
                ps.setNull(2, Types.BIGINT);
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(2, excludedVehicleId);
                ps.setLong(3, excludedVehicleId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void fillVehicleStatement(PreparedStatement ps, Vehicle vehicle) throws SQLException {
        setNullableString(ps, 1, vehicle.getVehicleCode());
        ps.setString(2, vehicle.getLicensePlate());
        ps.setString(3, vehicle.getVehicleType());
        setNullableString(ps, 4, vehicle.getBrand());
        setNullableString(ps, 5, vehicle.getModel());

        if (vehicle.getManufactureYear() == null) {
            ps.setNull(6, Types.SMALLINT);
        } else {
            ps.setInt(6, vehicle.getManufactureYear());
        }

        if (vehicle.getPurchaseDate() == null) {
            ps.setNull(7, Types.DATE);
        } else {
            ps.setDate(7, Date.valueOf(vehicle.getPurchaseDate()));
        }

        ps.setString(8, vehicle.getChassisNumber());
        ps.setString(9, vehicle.getEngineNumber());
        setNullableString(ps, 10, vehicle.getColor());
        ps.setInt(11, vehicle.getCurrentOdometer());
        ps.setString(12, vehicle.getVehicleStatus());
        setNullableString(ps, 13, vehicle.getNotes());
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
            return;
        }

        ps.setString(index, value.trim());
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleId(rs.getLong("vehicle_id"));
        vehicle.setVehicleCode(rs.getString("vehicle_code"));
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));

        int year = rs.getInt("manufacture_year");
        vehicle.setManufactureYear(rs.wasNull() ? null : year);

        Date purchaseDate = rs.getDate("purchase_date");
        vehicle.setPurchaseDate(purchaseDate != null ? purchaseDate.toLocalDate() : null);

        vehicle.setChassisNumber(rs.getString("chassis_number"));
        vehicle.setEngineNumber(rs.getString("engine_number"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setCurrentOdometer(rs.getInt("current_odometer"));
        vehicle.setVehicleStatus(rs.getString("vehicle_status"));
        vehicle.setNotes(rs.getString("notes"));

        return vehicle;
    }
}
