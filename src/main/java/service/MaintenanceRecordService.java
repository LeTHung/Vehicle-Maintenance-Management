package service;

import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;
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
