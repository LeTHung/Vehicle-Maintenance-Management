package model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MaintenancePlan {

    private long planId;
    private long vehicleId;
    private int maintenanceTypeId;
    private Integer intervalDays;
    private Integer intervalKm;
    private LocalDate lastServiceDate;
    private Integer lastServiceOdometer;
    private LocalDate nextDueDate;
    private Integer nextDueOdometer;
    private Integer alertBeforeDays;
    private Integer alertBeforeKm;
    private boolean isActive;
    private String notes;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MaintenancePlan() {
    }

    public long getPlanId() {
        return planId;
    }

    public void setPlanId(long planId) {
        this.planId = planId;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getMaintenanceTypeId() {
        return maintenanceTypeId;
    }

    public void setMaintenanceTypeId(int maintenanceTypeId) {
        this.maintenanceTypeId = maintenanceTypeId;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Integer getIntervalKm() {
        return intervalKm;
    }

    public void setIntervalKm(Integer intervalKm) {
        this.intervalKm = intervalKm;
    }

    public LocalDate getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(LocalDate lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Integer getLastServiceOdometer() {
        return lastServiceOdometer;
    }

    public void setLastServiceOdometer(Integer lastServiceOdometer) {
        this.lastServiceOdometer = lastServiceOdometer;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Integer getNextDueOdometer() {
        return nextDueOdometer;
    }

    public void setNextDueOdometer(Integer nextDueOdometer) {
        this.nextDueOdometer = nextDueOdometer;
    }

    public Integer getAlertBeforeDays() {
        return alertBeforeDays;
    }

    public void setAlertBeforeDays(Integer alertBeforeDays) {
        this.alertBeforeDays = alertBeforeDays;
    }

    public Integer getAlertBeforeKm() {
        return alertBeforeKm;
    }

    public void setAlertBeforeKm(Integer alertBeforeKm) {
        this.alertBeforeKm = alertBeforeKm;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
