package model.dao;

import database.DatabaseConnection;
import model.dto.DocumentAlertDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentAlertDAO {

    public List<DocumentAlertDTO> findAll() {
        List<DocumentAlertDTO> alerts = new ArrayList<>();

        String sql = """
                SELECT document_id, vehicle_id, license_plate, vehicle_type,
                       document_type_name, document_number, issuer_name,
                       expiry_date, days_to_expiry, due_status
                FROM vw_due_vehicle_documents
                WHERE due_status IN ('OVERDUE', 'COMING_DUE')
                ORDER BY days_to_expiry ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                alerts.add(mapToAlert(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    public List<DocumentAlertDTO> search(String keyword, String dueStatus) {
        List<DocumentAlertDTO> alerts = new ArrayList<>();

        String sql = """
                SELECT document_id, vehicle_id, license_plate, vehicle_type,
                       document_type_name, document_number, issuer_name,
                       expiry_date, days_to_expiry, due_status
                FROM vw_due_vehicle_documents
                WHERE (? IS NULL OR due_status = ?)
                  AND (
                       license_plate LIKE ?
                    OR vehicle_type LIKE ?
                    OR document_type_name LIKE ?
                    OR document_number LIKE ?
                    OR issuer_name LIKE ?
                  )
                ORDER BY days_to_expiry ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            String statusValue = normalizeStatus(dueStatus);
            String keywordValue = "%" + (keyword == null ? "" : keyword.trim()) + "%";

            statement.setString(1, statusValue);
            statement.setString(2, statusValue);
            statement.setString(3, keywordValue);
            statement.setString(4, keywordValue);
            statement.setString(5, keywordValue);
            statement.setString(6, keywordValue);
            statement.setString(7, keywordValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    alerts.add(mapToAlert(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alerts;
    }

    public int countByStatus(String dueStatus) {
        String sql = """
                SELECT COUNT(*) AS total
                FROM vw_due_vehicle_documents
                WHERE due_status = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dueStatus);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private String normalizeStatus(String dueStatus) {
        if (dueStatus == null || dueStatus.isBlank()
                || dueStatus.equalsIgnoreCase("Tat ca")
                || dueStatus.equals("Tất cả")) {
            return null;
        }

        return dueStatus;
    }

    private DocumentAlertDTO mapToAlert(ResultSet resultSet) throws SQLException {
        DocumentAlertDTO alert = new DocumentAlertDTO();

        alert.setDocumentId(resultSet.getLong("document_id"));
        alert.setVehicleId(resultSet.getLong("vehicle_id"));
        alert.setLicensePlate(resultSet.getString("license_plate"));
        alert.setVehicleType(resultSet.getString("vehicle_type"));
        alert.setDocumentTypeName(resultSet.getString("document_type_name"));
        alert.setDocumentNumber(resultSet.getString("document_number"));
        alert.setIssuerName(resultSet.getString("issuer_name"));

        Date expiryDate = resultSet.getDate("expiry_date");
        alert.setExpiryDate(expiryDate == null ? null : expiryDate.toLocalDate());

        alert.setDaysToExpiry(resultSet.getInt("days_to_expiry"));
        alert.setDueStatus(resultSet.getString("due_status"));

        return alert;
    }
}
