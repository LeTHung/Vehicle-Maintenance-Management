package service;

import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenanceRecord;
import model.entity.Vehicle;

import java.util.List;

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
        if (item == null) throw new IllegalArgumentException("Hạng mục không hợp lệ.");
        if (item.getDescription() == null || item.getDescription().isBlank())
            throw new IllegalArgumentException("Vui lòng nhập mô tả hạng mục.");
        if (item.getQuantity() == null) throw new IllegalArgumentException("Vui lòng nhập số lượng.");
        if (item.getUnitCost() == null) throw new IllegalArgumentException("Vui lòng nhập đơn giá.");
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
        if (record.getServiceDate() == null) {
            throw new IllegalArgumentException("Vui lòng nhập ngày thực hiện.");
        }
    }
}
