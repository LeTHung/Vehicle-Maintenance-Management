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
            throw new IllegalStateException("Không thể lưu giấy tờ xe. Vui lòng kiểm tra dữ liệu.");
        }

        return vehicleDocumentDAO.findById(generatedId).orElse(document);
    }

    public VehicleDocument updateDocument(VehicleDocument document) {
        if (document == null || document.getDocumentId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn giấy tờ cần cập nhật.");
        }

        normalizeAndValidate(document, document.getDocumentId());

        if (!vehicleDocumentDAO.update(document)) {
            throw new IllegalStateException("Không thể cập nhật giấy tờ xe. Vui lòng thử lại.");
        }

        return vehicleDocumentDAO.findById(document.getDocumentId()).orElse(document);
    }

    private void normalizeAndValidate(VehicleDocument document, Long currentDocumentId) {
        if (document == null) {
            throw new IllegalArgumentException("Dữ liệu giấy tờ không hợp lệ.");
        }

        if (document.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn phương tiện.");
        }

        if (document.getDocumentTypeId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn loại giấy tờ.");
        }

        document.setDocumentNumber(normalizeOptional(document.getDocumentNumber()));
        document.setIssuerName(normalizeOptional(document.getIssuerName()));
        document.setDocumentStatus(normalizeStatus(document.getDocumentStatus()));
        document.setNote(normalizeOptional(document.getNote()));

        if (document.getExpiryDate() == null) {
            throw new IllegalArgumentException("Ngày hết hạn không được để trống.");
        }

        validateDateRange(document.getIssueDate(), document.getEffectiveDate(), document.getExpiryDate());

        if (document.getFeeAmount() == null) {
            document.setFeeAmount(BigDecimal.ZERO);
        }

        if (document.getFeeAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Chi phí không được âm.");
        }

        if (document.isCurrent()
                && vehicleDocumentDAO.existsCurrentDocument(
                        document.getVehicleId(),
                        document.getDocumentTypeId(),
                        currentDocumentId)) {
            throw new IllegalArgumentException("Xe đã có giấy tờ hiện hành cho loại này.");
        }
    }

    private void validateDateRange(LocalDate issueDate, LocalDate effectiveDate, LocalDate expiryDate) {
        if (effectiveDate != null && expiryDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("Ngày hết hạn phải lớn hơn hoặc bằng ngày hiệu lực.");
        }

        if (issueDate != null && effectiveDate != null && effectiveDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Ngày hiệu lực phải lớn hơn hoặc bằng ngày cấp.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null || status.isBlank()
                ? STATUS_VALID
                : status.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedStatus) {
            case STATUS_VALID, STATUS_EXPIRED, STATUS_REPLACED, STATUS_CANCELLED -> normalizedStatus;
            default -> throw new IllegalArgumentException("Trạng thái giấy tờ không hợp lệ.");
        };
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
