package model.dto;

import java.time.LocalDate;

public class MaintenanceDueAlertDTO {

    private long planId;
    private long vehicleId;
    private String licensePlate;
    private String vehicleType;
    private String maintenanceName;
    private Integer currentOdometer;
    private LocalDate nextDueDate;
    private Integer nextDueOdometer;
    private Integer effectiveAlertDays;
    private Integer effectiveAlertKm;
    private String dueStatus;

    public long getPlanId() { return planId; }
    public void setPlanId(long planId) { this.planId = planId; }

    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getMaintenanceName() { return maintenanceName; }
    public void setMaintenanceName(String maintenanceName) { this.maintenanceName = maintenanceName; }

    public Integer getCurrentOdometer() { return currentOdometer; }
    public void setCurrentOdometer(Integer currentOdometer) { this.currentOdometer = currentOdometer; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }

    public Integer getNextDueOdometer() { return nextDueOdometer; }
    public void setNextDueOdometer(Integer nextDueOdometer) { this.nextDueOdometer = nextDueOdometer; }

    public Integer getEffectiveAlertDays() { return effectiveAlertDays; }
    public void setEffectiveAlertDays(Integer effectiveAlertDays) { this.effectiveAlertDays = effectiveAlertDays; }

    public Integer getEffectiveAlertKm() { return effectiveAlertKm; }
    public void setEffectiveAlertKm(Integer effectiveAlertKm) { this.effectiveAlertKm = effectiveAlertKm; }

    public String getDueStatus() { return dueStatus; }
    public void setDueStatus(String dueStatus) { this.dueStatus = dueStatus; }
}
