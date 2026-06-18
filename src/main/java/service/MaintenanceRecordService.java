package service;

import model.dao.MaintenanceRecordDAO;
import model.dao.MaintenancePlanDAO;
import model.dao.VehicleDAO;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenancePlan;
import model.entity.MaintenanceRecord;
import model.entity.Vehicle;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class MaintenanceRecordService {

    private final MaintenanceRecordDAO recordDAO = new MaintenanceRecordDAO();
    private final MaintenancePlanDAO planDAO = new MaintenancePlanDAO();
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
        validate(record, null);
        Long id = recordDAO.insert(record);
        if (id == null) {
            throw new IllegalStateException("Không thể lưu phiếu bảo dưỡng. Vui lòng thử lại.");
        }
        return id;
    }

    public boolean update(MaintenanceRecord record, String previousStatus) {
        if (record == null || record.getRecordId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn phiếu cần cập nhật.");
        }
        validate(record, previousStatus);
        if (!recordDAO.update(record)) {
            throw new IllegalStateException("Không thể cập nhật phiếu bảo dưỡng. Vui lòng thử lại.");
        }
        return true;
    }

    /** Phiếu vừa chuyển sang COMPLETED lần đầu (tạo mới hoặc từ trạng thái khác). */
    public static boolean isNewlyCompleting(String previousStatus, String currentStatus) {
        if (currentStatus == null || currentStatus.isBlank()) {
            return false;
        }
        String normalizedCurrent = currentStatus.trim().toUpperCase(Locale.ROOT);
        if (!"COMPLETED".equals(normalizedCurrent)) {
            return false;
        }
        if (previousStatus == null || previousStatus.isBlank()) {
            return true;
        }
        return !"COMPLETED".equals(previousStatus.trim().toUpperCase(Locale.ROOT));
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

    /** ODO lớn nhất trên các phiếu COMPLETED của xe (dùng khi validate sửa hồ sơ xe). */
    public Optional<Integer> findMaxCompletedOdometer(long vehicleId) {
        return recordDAO.findMaxCompletedOdometer(vehicleId, null);
    }

    private void validate(MaintenanceRecord record, String previousStatus) {
        if (record == null) {
            throw new IllegalArgumentException("Dữ liệu phiếu bảo dưỡng không hợp lệ.");
        }
        if (record.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn xe.");
        }
        validateRelatedPlan(record);
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
        validateStatusTransition(previousStatus, record.getRecordStatus());
        validateOdometerRules(record, previousStatus);
    }

    private void validateStatusTransition(String previousStatus, String currentStatus) {
        if (previousStatus == null || previousStatus.isBlank()) {
            return;
        }
        String previous = previousStatus.trim().toUpperCase(Locale.ROOT);
        String current = currentStatus == null ? "" : currentStatus.trim().toUpperCase(Locale.ROOT);
        if ("COMPLETED".equals(previous) && !"COMPLETED".equals(current)) {
            throw new IllegalArgumentException(
                    "Không thể đổi trạng thái phiếu đã hoàn thành. Liên hệ quản lý nếu cần điều chỉnh.");
        }
    }

    private void validateOdometerRules(MaintenanceRecord record, String previousStatus) {
        boolean newlyCompleting = isNewlyCompleting(previousStatus, record.getRecordStatus());
        boolean editingCompleted = isEditingCompletedRecord(previousStatus, record.getRecordStatus());

        if (newlyCompleting && record.getOdometer() == null) {
            throw new IllegalArgumentException("Vui lòng nhập ODO khi hoàn thành phiếu bảo dưỡng.");
        }

        if (record.getOdometer() == null || editingCompleted) {
            return;
        }

        vehicleDAO.findById(record.getVehicleId()).ifPresent(vehicle -> {
            if (record.getOdometer() < vehicle.getCurrentOdometer()) {
                throw new IllegalArgumentException(
                        "ODO phiếu (" + record.getOdometer() + " km) không được nhỏ hơn "
                                + "ODO hiện tại của xe (" + vehicle.getCurrentOdometer() + " km).");
            }
        });
    }

    private boolean isEditingCompletedRecord(String previousStatus, String currentStatus) {
        if (previousStatus == null || previousStatus.isBlank()) {
            return false;
        }
        return "COMPLETED".equals(previousStatus.trim().toUpperCase(Locale.ROOT))
                && "COMPLETED".equals(currentStatus == null ? "" : currentStatus.trim().toUpperCase(Locale.ROOT));
    }

    private void validateRelatedPlan(MaintenanceRecord record) {
        Long planId = record.getPlanId();
        if (planId == null) {
            return;
        }

        MaintenancePlan plan = planDAO.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Kế hoạch bảo dưỡng liên quan không tồn tại."));

        if (plan.getVehicleId() != record.getVehicleId()) {
            throw new IllegalArgumentException("Kế hoạch bảo dưỡng liên quan không thuộc xe đang chọn.");
        }
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