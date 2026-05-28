package model.dao;

import database.DatabaseConnection;
import model.dto.VehicleDocumentViewDTO;
import model.entity.VehicleDocument;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<VehicleDocument> findById(long documentId) {
        String sql = """
                SELECT document_id, vehicle_id, document_type_id, document_number,
                       issuer_name, issue_date, effective_date, expiry_date,
                       fee_amount, paid_date, document_status, is_current, note
                FROM vehicle_documents
                WHERE document_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, documentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToVehicleDocument(resultSet));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public List<VehicleDocumentViewDTO> search(String keyword) {
        List<VehicleDocumentViewDTO> documents = new ArrayList<>();

        if (keyword == null || keyword.isBlank()) {
            return findAllForTable();
        }

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

    public List<VehicleDocumentViewDTO> filter(Long vehicleId, Integer documentTypeId) {
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
                WHERE (? IS NULL OR vd.vehicle_id = ?)
                  AND (? IS NULL OR vd.document_type_id = ?)
                ORDER BY vd.expiry_date ASC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableLong(statement, 1, vehicleId);
            setNullableLong(statement, 2, vehicleId);
            setNullableInteger(statement, 3, documentTypeId);
            setNullableInteger(statement, 4, documentTypeId);

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

    public Long insert(VehicleDocument document) {
        if (document == null) {
            return null;
        }

        String sql = """
                INSERT INTO vehicle_documents (
                    vehicle_id, document_type_id, document_number, issuer_name,
                    issue_date, effective_date, expiry_date, fee_amount, paid_date,
                    document_status, is_current, note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillDocumentStatement(statement, document);

            if (statement.executeUpdate() == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    document.setDocumentId(generatedId);
                    return generatedId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean update(VehicleDocument document) {
        if (document == null || document.getDocumentId() <= 0) {
            return false;
        }

        String sql = """
                UPDATE vehicle_documents
                SET vehicle_id = ?,
                    document_type_id = ?,
                    document_number = ?,
                    issuer_name = ?,
                    issue_date = ?,
                    effective_date = ?,
                    expiry_date = ?,
                    fee_amount = ?,
                    paid_date = ?,
                    document_status = ?,
                    is_current = ?,
                    note = ?
                WHERE document_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            fillDocumentStatement(statement, document);
            statement.setLong(13, document.getDocumentId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existsCurrentDocument(long vehicleId, int documentTypeId, Long excludedDocumentId) {
        String sql = """
                SELECT 1
                FROM vehicle_documents
                WHERE vehicle_id = ?
                  AND document_type_id = ?
                  AND is_current = 1
                  AND (? IS NULL OR document_id <> ?)
                LIMIT 1
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, vehicleId);
            statement.setInt(2, documentTypeId);
            setNullableLong(statement, 3, excludedDocumentId);
            setNullableLong(statement, 4, excludedDocumentId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void fillDocumentStatement(PreparedStatement statement, VehicleDocument document) throws SQLException {
        statement.setLong(1, document.getVehicleId());
        statement.setInt(2, document.getDocumentTypeId());
        setNullableString(statement, 3, document.getDocumentNumber());
        setNullableString(statement, 4, document.getIssuerName());
        setNullableDate(statement, 5, document.getIssueDate());
        setNullableDate(statement, 6, document.getEffectiveDate());
        statement.setDate(7, Date.valueOf(document.getExpiryDate()));
        statement.setBigDecimal(8, document.getFeeAmount());
        setNullableDate(statement, 9, document.getPaidDate());
        statement.setString(10, document.getDocumentStatus());
        statement.setBoolean(11, document.isCurrent());
        setNullableString(statement, 12, document.getNote());
    }

    private VehicleDocument mapToVehicleDocument(ResultSet resultSet) throws SQLException {
        VehicleDocument document = new VehicleDocument();

        document.setDocumentId(resultSet.getLong("document_id"));
        document.setVehicleId(resultSet.getLong("vehicle_id"));
        document.setDocumentTypeId(resultSet.getInt("document_type_id"));
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

    private void setNullableDate(PreparedStatement statement, int index, java.time.LocalDate value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.DATE);
            return;
        }

        statement.setDate(index, Date.valueOf(value));
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        statement.setString(index, value.trim());
    }

    private void setNullableLong(PreparedStatement statement, int index, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.BIGINT);
            return;
        }

        statement.setLong(index, value);
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
            return;
        }

        statement.setInt(index, value);
    }
}
