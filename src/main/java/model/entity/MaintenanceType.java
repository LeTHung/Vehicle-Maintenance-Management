package model.entity;

import java.time.LocalDateTime;

public class MaintenanceType {

    private int maintenanceTypeId;
    private String maintenanceCode;
    private String maintenanceName;
    private String description;
    private Integer defaultIntervalDays;
    private Integer defaultIntervalKm;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MaintenanceType() {
    }

    public int getMaintenanceTypeId() {
        return maintenanceTypeId;
    }

    public void setMaintenanceTypeId(int maintenanceTypeId) {
        this.maintenanceTypeId = maintenanceTypeId;
    }

    public String getMaintenanceCode() {
        return maintenanceCode;
    }

    public void setMaintenanceCode(String maintenanceCode) {
        this.maintenanceCode = maintenanceCode;
    }

    public String getMaintenanceName() {
        return maintenanceName;
    }

    public void setMaintenanceName(String maintenanceName) {
        this.maintenanceName = maintenanceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDefaultIntervalDays() {
        return defaultIntervalDays;
    }

    public void setDefaultIntervalDays(Integer defaultIntervalDays) {
        this.defaultIntervalDays = defaultIntervalDays;
    }

    public Integer getDefaultIntervalKm() {
        return defaultIntervalKm;
    }

    public void setDefaultIntervalKm(Integer defaultIntervalKm) {
        this.defaultIntervalKm = defaultIntervalKm;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return maintenanceName;
    }
}
