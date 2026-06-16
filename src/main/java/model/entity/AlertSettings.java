package model.entity;

import java.time.LocalDateTime;

public class AlertSettings {

    private int settingId;
    private int documentAlertDays;
    private int maintenanceAlertDays;
    private int maintenanceAlertKm;
    private boolean active;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlertSettings() {
    }

    public static AlertSettings defaults() {
        AlertSettings settings = new AlertSettings();
        settings.setSettingId(1);
        settings.setDocumentAlertDays(15);
        settings.setMaintenanceAlertDays(7);
        settings.setMaintenanceAlertKm(500);
        settings.setActive(true);
        return settings;
    }

    public int getSettingId() {
        return settingId;
    }

    public void setSettingId(int settingId) {
        this.settingId = settingId;
    }

    public int getDocumentAlertDays() {
        return documentAlertDays;
    }

    public void setDocumentAlertDays(int documentAlertDays) {
        this.documentAlertDays = documentAlertDays;
    }

    public int getMaintenanceAlertDays() {
        return maintenanceAlertDays;
    }

    public void setMaintenanceAlertDays(int maintenanceAlertDays) {
        this.maintenanceAlertDays = maintenanceAlertDays;
    }

    public int getMaintenanceAlertKm() {
        return maintenanceAlertKm;
    }

    public void setMaintenanceAlertKm(int maintenanceAlertKm) {
        this.maintenanceAlertKm = maintenanceAlertKm;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
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
}
