package model.dao;

import database.DatabaseConnection;
import model.entity.DocumentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DocumentTypeDAO {

    public List<DocumentType> findAllActive() {
        List<DocumentType> documentTypes = new ArrayList<>();

        String sql = """
                SELECT document_type_id, document_type_code, document_type_name,
                       default_alert_days, is_active
                FROM document_types
                WHERE is_active = 1
                ORDER BY document_type_id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DocumentType type = new DocumentType();

                type.setDocumentTypeId(rs.getInt("document_type_id"));
                type.setDocumentTypeCode(rs.getString("document_type_code"));
                type.setDocumentTypeName(rs.getString("document_type_name"));
                type.setDefaultAlertDays(rs.getInt("default_alert_days"));
                type.setActive(rs.getBoolean("is_active"));

                documentTypes.add(type);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentTypes;
    }
}