package model.entity;

import java.time.LocalDate;

public class Vehicle {
    private int id;
    private String licensePlate;
    private String vehicleType;
    private String chassisNumber;
    private String engineNumber;
    private int manufactureYear;
    private LocalDate purchaseDate;
    private String status;
    private int currentOdo;

    public Vehicle() {
    }

    public Vehicle(int id, String licensePlate, String vehicleType, String chassisNumber,
            String engineNumber, int manufactureYear, LocalDate purchaseDate,
            String status, int currentOdo) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.chassisNumber = chassisNumber;
        this.engineNumber = engineNumber;
        this.manufactureYear = manufactureYear;
        this.purchaseDate = purchaseDate;
        this.status = status;
        this.currentOdo = currentOdo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCurrentOdo() {
        return currentOdo;
    }

    public void setCurrentOdo(int currentOdo) {
        this.currentOdo = currentOdo;
    }
}