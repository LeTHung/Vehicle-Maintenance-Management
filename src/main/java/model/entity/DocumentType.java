package model.entity;

public class DocumentType {

    private int documentTypeId;
    private String documentTypeCode;
    private String documentTypeName;
    private int defaultAlertDays;
    private boolean active;

    public DocumentType() {
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(String documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getDocumentTypeName() {
        return documentTypeName;
    }

    public void setDocumentTypeName(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }

    public int getDefaultAlertDays() {
        return defaultAlertDays;
    }

    public void setDefaultAlertDays(int defaultAlertDays) {
        this.defaultAlertDays = defaultAlertDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}