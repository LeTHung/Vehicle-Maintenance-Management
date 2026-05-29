package model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Maps to DB table: maintenance_record_items
public class MaintenanceItemDetail {

    private long recordItemId;
    private long recordId;
    private Long itemId;             // nullable — FK to maintenance_items, null if free-text
    private String itemType;         // WORK | PART
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;    // generated column: quantity * unit_cost (read-only)
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MaintenanceItemDetail() {
    }

    public long getRecordItemId() {
        return recordItemId;
    }

    public void setRecordItemId(long recordItemId) {
        this.recordItemId = recordItemId;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
