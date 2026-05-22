package model.entity;

import java.time.LocalDate;

public class VehicleDocument {
    private int id;
    private int vehicleId;
    private String documentType;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String note;

    public VehicleDocument() {
    }

    public VehicleDocument(int id, int vehicleId, String documentType,
            LocalDate issueDate, LocalDate expiryDate, String note) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.documentType = documentType;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}