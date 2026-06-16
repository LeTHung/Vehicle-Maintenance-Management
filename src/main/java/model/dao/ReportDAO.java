package model.dao;

import database.DatabaseConnection;
import model.dto.MonthlyCostReportDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    /**
     * Lấy toàn bộ chi phí theo tháng và xe từ view vw_vehicle_cost_monthly.
     * Dùng cho màn hình báo cáo — lọc theo năm hoặc xe ở tầng Service/Controller.
     */
    public List<MonthlyCostReportDTO> findMonthlyCosts() {
        List<MonthlyCostReportDTO> list = new ArrayList<>();

        String sql = """
                SELECT vehicle_id, license_plate, period_ym,
                       maintenance_cost, document_cost, total_cost
                FROM vw_vehicle_cost_monthly
                ORDER BY period_ym DESC, vehicle_id ASC
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

    /** Lọc theo năm, ví dụ periodYearPrefix = "2026" */
    public List<MonthlyCostReportDTO> findMonthlyCostsByYear(String year) {
        List<MonthlyCostReportDTO> list = new ArrayList<>();

        String sql = """
                SELECT vehicle_id, license_plate, period_ym,
                       maintenance_cost, document_cost, total_cost
                FROM vw_vehicle_cost_monthly
                WHERE period_ym LIKE ?
                ORDER BY period_ym ASC, vehicle_id ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, year + "-%");

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

    /** Lọc theo xe và năm */
    public List<MonthlyCostReportDTO> findMonthlyCostsByVehicleAndYear(long vehicleId, String year) {
        List<MonthlyCostReportDTO> list = new ArrayList<>();

        String sql = """
                SELECT vehicle_id, license_plate, period_ym,
                       maintenance_cost, document_cost, total_cost
                FROM vw_vehicle_cost_monthly
                WHERE vehicle_id = ?
                  AND period_ym LIKE ?
                ORDER BY period_ym ASC
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, vehicleId);
            ps.setString(2, year + "-%");

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

    private MonthlyCostReportDTO mapRow(ResultSet rs) throws SQLException {
        MonthlyCostReportDTO dto = new MonthlyCostReportDTO();
        dto.setVehicleId(rs.getLong("vehicle_id"));
        dto.setLicensePlate(rs.getString("license_plate"));
        dto.setPeriodYm(rs.getString("period_ym"));
        dto.setMaintenanceCost(rs.getBigDecimal("maintenance_cost"));
        dto.setDocumentCost(rs.getBigDecimal("document_cost"));
        dto.setTotalCost(rs.getBigDecimal("total_cost"));
        return dto;
    }
}
