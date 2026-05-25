package model.dao;

import database.DatabaseConnection;
import model.dto.VehicleDocumentViewDTO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDocumentDAO {

    public List<VehicleDocumentViewDTO> findAllForTable() {
        List<VehicleDocumentViewDTO> documents = new ArrayList<>();

        String sql = """
                SELECT vd.document_id,
                       vd.vehicle_id,
                       v.license_plate,
                       vd.document_type_id,
                       dt.document_type_name,
                       vd.document_number,
                       vd.issuer_name,
                       vd.issue_date,
                       vd.effective_date,
                       vd.expiry_date,
                       vd.fee_amount,
                       vd.paid_date,
                       vd.document_status,
                       vd.is_current,
                       vd.note
                FROM vehicle_documents vd
                JOIN vehicles v ON v.vehicle_id = vd.vehicle_id
                JOIN document_types dt ON dt.document_type_id = vd.document_type_id
                ORDER BY vd.expiry_date ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                documents.add(mapToDocumentView(resultSet));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documents;
    }

    public List<VehicleDocumentViewDTO> search(String keyword) {
        List<VehicleDocumentViewDTO> documents = new ArrayList<>();

        String sql = """
                SELECT vd.document_id,
                       vd.vehicle_id,
                       v.license_plate,
                       vd.document_type_id,
                       dt.document_type_name,
                       vd.document_number,
                       vd.issuer_name,
                       vd.issue_date,
                       vd.effective_date,
                       vd.expiry_date,
                       vd.fee_amount,
                       vd.paid_date,
                       vd.document_status,
                       vd.is_current,
                       vd.note
                FROM vehicle_documents vd
                JOIN vehicles v ON v.vehicle_id = vd.vehicle_id
                JOIN document_types dt ON dt.document_type_id = vd.document_type_id
                WHERE v.license_plate LIKE ?
                   OR dt.document_type_name LIKE ?
                   OR vd.document_number LIKE ?
                   OR vd.issuer_name LIKE ?
                   OR vd.document_status LIKE ?
                ORDER BY vd.expiry_date ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            String value = "%" + keyword + "%";

            for (int i = 1; i <= 5; i++) {
                statement.setString(i, value);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    documents.add(mapToDocumentView(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documents;
    }

    private VehicleDocumentViewDTO mapToDocumentView(ResultSet resultSet) throws SQLException {
        VehicleDocumentViewDTO document = new VehicleDocumentViewDTO();

        document.setDocumentId(resultSet.getLong("document_id"));
        document.setVehicleId(resultSet.getLong("vehicle_id"));
        document.setLicensePlate(resultSet.getString("license_plate"));
        document.setDocumentTypeId(resultSet.getInt("document_type_id"));
        document.setDocumentTypeName(resultSet.getString("document_type_name"));
        document.setDocumentNumber(resultSet.getString("document_number"));
        document.setIssuerName(resultSet.getString("issuer_name"));

        Date issueDate = resultSet.getDate("issue_date");
        document.setIssueDate(issueDate == null ? null : issueDate.toLocalDate());

        Date effectiveDate = resultSet.getDate("effective_date");
        document.setEffectiveDate(effectiveDate == null ? null : effectiveDate.toLocalDate());

        Date expiryDate = resultSet.getDate("expiry_date");
        document.setExpiryDate(expiryDate == null ? null : expiryDate.toLocalDate());

        document.setFeeAmount(resultSet.getBigDecimal("fee_amount"));

        Date paidDate = resultSet.getDate("paid_date");
        document.setPaidDate(paidDate == null ? null : paidDate.toLocalDate());

        document.setDocumentStatus(resultSet.getString("document_status"));
        document.setCurrent(resultSet.getBoolean("is_current"));
        document.setNote(resultSet.getString("note"));

        return document;
    }
}