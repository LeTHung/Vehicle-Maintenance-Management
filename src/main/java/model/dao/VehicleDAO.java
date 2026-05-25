package model.dao;

import database.DatabaseConnection;
import model.entity.Vehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<Vehicle> search(String keyword) {
        List<Vehicle> vehicles = new ArrayList<>();

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