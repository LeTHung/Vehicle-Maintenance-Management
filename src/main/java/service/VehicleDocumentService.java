package service;

import model.dao.DocumentTypeDAO;
import model.dao.VehicleDAO;
import model.dao.VehicleDocumentDAO;
import model.dto.VehicleDocumentViewDTO;
import model.entity.DocumentType;
import model.entity.Vehicle;
import model.entity.VehicleDocument;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class VehicleDocumentService {

    private static final String STATUS_VALID = "VALID";
    private static final String STATUS_EXPIRED = "EXPIRED";
    private static final String STATUS_REPLACED = "REPLACED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final VehicleDocumentDAO vehicleDocumentDAO = new VehicleDocumentDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentTypeDAO documentTypeDAO = new DocumentTypeDAO();

    public List<VehicleDocumentViewDTO> listDocuments() {
        return vehicleDocumentDAO.findAllForTable();
    }

    public List<VehicleDocumentViewDTO> searchDocuments(String keyword) {
        return vehicleDocumentDAO.search(keyword);
    }

    public List<VehicleDocumentViewDTO> filterDocuments(Long vehicleId, Integer documentTypeId) {
        return vehicleDocumentDAO.filter(vehicleId, documentTypeId);
    }

    public List<Vehicle> listVehicles() {
        return vehicleDAO.findAll();
    }

    public List<DocumentType> listDocumentTypes() {
        return documentTypeDAO.findAllActive();
    }

    public VehicleDocument createDocument(VehicleDocument document) {
        normalizeAndValidate(document, null);

        Long generatedId = vehicleDocumentDAO.insert(document);
        if (generatedId == null) {
            throw new IllegalStateException("Khong the luu giay to xe. Vui long kiem tra du lieu.");
        }

        return vehicleDocumentDAO.findById(generatedId).orElse(document);
    }

    public VehicleDocument updateDocument(VehicleDocument document) {
        if (document == null || document.getDocumentId() <= 0) {
            throw new IllegalArgumentException("Vui long chon giay to can cap nhat.");
        }

        normalizeAndValidate(document, document.getDocumentId());

        if (!vehicleDocumentDAO.update(document)) {
            throw new IllegalStateException("Khong the cap nhat giay to xe. Vui long thu lai.");
        }

        return vehicleDocumentDAO.findById(document.getDocumentId()).orElse(document);
    }

    private void normalizeAndValidate(VehicleDocument document, Long currentDocumentId) {
        if (document == null) {
            throw new IllegalArgumentException("Du lieu giay to khong hop le.");
        }

        if (document.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui long chon phuong tien.");
        }

        if (document.getDocumentTypeId() <= 0) {
            throw new IllegalArgumentException("Vui long chon loai giay to.");
        }

        document.setDocumentNumber(normalizeOptional(document.getDocumentNumber()));
        document.setIssuerName(normalizeOptional(document.getIssuerName()));
        document.setDocumentStatus(normalizeStatus(document.getDocumentStatus()));
        document.setNote(normalizeOptional(document.getNote()));

        if (document.getExpiryDate() == null) {
            throw new IllegalArgumentException("Ngay het han khong duoc de trong.");
        }

        validateDateRange(document.getIssueDate(), document.getEffectiveDate(), document.getExpiryDate());

        if (document.getFeeAmount() == null) {
            document.setFeeAmount(BigDecimal.ZERO);
        }

        if (document.getFeeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chi phi khong duoc am.");
        }

        if (document.isCurrent()
                && vehicleDocumentDAO.existsCurrentDocument(
                        document.getVehicleId(),
                        document.getDocumentTypeId(),
                        currentDocumentId)) {
            throw new IllegalArgumentException("Xe da co giay to hien hanh cho loai nay.");
        }
    }

    private void validateDateRange(LocalDate issueDate, LocalDate effectiveDate, LocalDate expiryDate) {
        if (effectiveDate != null && expiryDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("Ngay het han phai lon hon hoac bang ngay hieu luc.");
        }

        if (issueDate != null && effectiveDate != null && effectiveDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Ngay hieu luc phai lon hon hoac bang ngay cap.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null || status.isBlank()
                ? STATUS_VALID
                : status.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedStatus) {
            case STATUS_VALID, STATUS_EXPIRED, STATUS_REPLACED, STATUS_CANCELLED -> normalizedStatus;
            default -> throw new IllegalArgumentException("Trang thai giay to khong hop le.");
        };
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
