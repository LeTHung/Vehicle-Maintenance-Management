package service;

import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenanceRecord;
import model.entity.Vehicle;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class MaintenanceRecordService {

    private final MaintenanceRecordDAO recordDAO = new MaintenanceRecordDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    public List<MaintenanceRecord> listAll() {
        return recordDAO.findAll();
    }

    public List<MaintenanceRecord> listByVehicle(long vehicleId) {
        return recordDAO.findByVehicleId(vehicleId);
    }

    public List<Vehicle> listVehicles() {
        return vehicleDAO.findAll();
    }

    public Long save(MaintenanceRecord record) {
        validate(record);
        Long id = recordDAO.insert(record);
        if (id == null) {
            throw new IllegalStateException("Không thể lưu phiếu bảo dưỡng. Vui lòng thử lại.");
        }
        return id;
    }

    public boolean update(MaintenanceRecord record) {
        if (record == null || record.getRecordId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn phiếu cần cập nhật.");
        }
        validate(record);
        if (!recordDAO.update(record)) {
            throw new IllegalStateException("Không thể cập nhật phiếu bảo dưỡng. Vui lòng thử lại.");
        }
        return true;
    }

    public List<MaintenanceItemDetail> listItems(long recordId) {
        return recordDAO.findItemsByRecordId(recordId);
    }

    public Long saveItem(MaintenanceItemDetail item) {
        if (item == null) {
            throw new IllegalArgumentException("Hạng mục không hợp lệ.");
        }
        item.setItemType(normalizeItemType(item.getItemType()));
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập mô tả hạng mục.");
        }
        if (item.getQuantity() == null) {
            throw new IllegalArgumentException("Vui lòng nhập số lượng.");
        }
        if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số lượng hạng mục phải lớn hơn 0.");
        }
        if (item.getUnitCost() == null) {
            throw new IllegalArgumentException("Vui lòng nhập đơn giá.");
        }
        if (item.getUnitCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá hạng mục không được âm.");
        }
        return recordDAO.insertItem(item);
    }

    public boolean deleteItems(long recordId) {
        return recordDAO.deleteItemsByRecordId(recordId);
    }

    /** Cập nhật ODO hiện tại của xe theo ODO ghi nhận trên phiếu (chỉ tăng). */
    public boolean updateVehicleOdometer(long vehicleId, int odometer) {
        return vehicleDAO.updateOdometer(vehicleId, odometer);
    }

    private void validate(MaintenanceRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("Dữ liệu phiếu bảo dưỡng không hợp lệ.");
        }
        if (record.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn xe.");
        }
        if (record.getRecordType() == null || record.getRecordType().isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn loại phiếu.");
        }
        record.setRecordType(normalizeRecordType(record.getRecordType()));
        if (record.getServiceDate() == null) {
            throw new IllegalArgumentException("Vui lòng nhập ngày thực hiện.");
        }
        if (record.getOdometer() != null && record.getOdometer() < 0) {
            throw new IllegalArgumentException("ODO bảo dưỡng không được âm.");
        }
        if (record.getTotalCost() != null && record.getTotalCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tổng chi phí bảo dưỡng không được âm.");
        }
        record.setRecordStatus(normalizeRecordStatus(record.getRecordStatus()));
    }

    private String normalizeRecordType(String recordType) {
        String normalized = recordType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "PREVENTIVE", "CORRECTIVE" -> normalized;
            default -> throw new IllegalArgumentException("Loại phiếu bảo dưỡng không hợp lệ.");
        };
    }

    private String normalizeRecordStatus(String status) {
        if (status == null || status.isBlank()) {
            return "OPEN";
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED" -> normalized;
            default -> throw new IllegalArgumentException("Trạng thái phiếu bảo dưỡng không hợp lệ.");
        };
    }

    private String normalizeItemType(String itemType) {
        if (itemType == null || itemType.isBlank()) {
            return "WORK";
        }
        String normalized = itemType.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "WORK", "PART" -> normalized;
            default -> throw new IllegalArgumentException("Loại hạng mục bảo dưỡng không hợp lệ.");
        };
    }
}
