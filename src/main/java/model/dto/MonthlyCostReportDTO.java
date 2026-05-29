package model.dto;

import java.math.BigDecimal;

// Projection từ view vw_vehicle_cost_monthly
public class MonthlyCostReportDTO {

    private long vehicleId;
    private String licensePlate;
    private String periodYm;           // định dạng YYYY-MM
    private BigDecimal maintenanceCost;
    private BigDecimal documentCost;
    private BigDecimal totalCost;

    public MonthlyCostReportDTO() {
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getPeriodYm() {
        return periodYm;
    }

    public void setPeriodYm(String periodYm) {
        this.periodYm = periodYm;
    }

    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public BigDecimal getDocumentCost() {
        return documentCost;
    }

    public void setDocumentCost(BigDecimal documentCost) {
        this.documentCost = documentCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
