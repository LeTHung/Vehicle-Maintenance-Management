package service;

import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;
import model.entity.Vehicle;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class VehicleService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_UNDER_MAINTENANCE = "UNDER_MAINTENANCE";
    private static final String STATUS_INACTIVE = "INACTIVE";
    private static final String STATUS_DISPOSED = "DISPOSED";

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MaintenanceRecordDAO maintenanceRecordDAO = new MaintenanceRecordDAO();

    public List<Vehicle> listVehicles() {
        return vehicleDAO.findAll();
    }

    public List<Vehicle> searchVehicles(String keyword) {
        return vehicleDAO.search(keyword);
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        normalizeAndValidate(vehicle, null);

        Long generatedId = vehicleDAO.insert(vehicle);
        if (generatedId == null) {
            throw new IllegalStateException("Không thể lưu phương tiện. Vui lòng kiểm tra dữ liệu.");
        }

        return vehicleDAO.findById(generatedId).orElse(vehicle);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn phương tiện cần cập nhật.");
        }

        normalizeAndValidate(vehicle, vehicle.getVehicleId());

        if (!vehicleDAO.update(vehicle)) {
            throw new IllegalStateException("Không thể cập nhật phương tiện. Vui lòng thử lại.");
        }

        return vehicleDAO.findById(vehicle.getVehicleId()).orElse(vehicle);
    }

    private void normalizeAndValidate(Vehicle vehicle, Long currentVehicleId) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Dữ liệu phương tiện không hợp lệ.");
        }

        vehicle.setVehicleCode(normalizeOptional(vehicle.getVehicleCode()));
        vehicle.setLicensePlate(requireText(vehicle.getLicensePlate(), "Biển số không được để trống.").toUpperCase(Locale.ROOT));
        vehicle.setVehicleType(requireText(vehicle.getVehicleType(), "Loại xe không được để trống."));
        vehicle.setBrand(normalizeOptional(vehicle.getBrand()));
        vehicle.setModel(normalizeOptional(vehicle.getModel()));
        vehicle.setChassisNumber(requireText(vehicle.getChassisNumber(), "Số khung không được để trống.").toUpperCase(Locale.ROOT));
        vehicle.setEngineNumber(requireText(vehicle.getEngineNumber(), "Số máy không được để trống.").toUpperCase(Locale.ROOT));
        vehicle.setColor(normalizeOptional(vehicle.getColor()));
        vehicle.setVehicleStatus(normalizeStatus(vehicle.getVehicleStatus()));
        vehicle.setNotes(normalizeOptional(vehicle.getNotes()));

        validateManufactureYear(vehicle.getManufactureYear());

        if (vehicle.getCurrentOdometer() < 0) {
            throw new IllegalArgumentException("ODO hiện tại không được âm.");
        }

        if (currentVehicleId != null) {
            Optional<Integer> maxCompletedOdometer =
                    maintenanceRecordDAO.findMaxCompletedOdometer(currentVehicleId, null);
            if (maxCompletedOdometer.isPresent()
                    && vehicle.getCurrentOdometer() < maxCompletedOdometer.get()) {
                throw new IllegalArgumentException(
                        "ODO hiện tại không được nhỏ hơn ODO trên phiếu bảo dưỡng đã hoàn thành ("
                                + maxCompletedOdometer.get() + " km).");
            }
        }

        if (vehicle.getVehicleCode() != null
                && vehicleDAO.existsByVehicleCode(vehicle.getVehicleCode(), currentVehicleId)) {
            throw new IllegalArgumentException("Mã xe đã tồn tại.");
        }

        if (vehicleDAO.existsByLicensePlate(vehicle.getLicensePlate(), currentVehicleId)) {
            throw new IllegalArgumentException("Biển số đã tồn tại.");
        }

        if (vehicleDAO.existsByChassisNumber(vehicle.getChassisNumber(), currentVehicleId)) {
            throw new IllegalArgumentException("Số khung đã tồn tại.");
        }

        if (vehicleDAO.existsByEngineNumber(vehicle.getEngineNumber(), currentVehicleId)) {
            throw new IllegalArgumentException("Số máy đã tồn tại.");
        }
    }

    private void validateManufactureYear(Integer year) {
        if (year == null) {
            return;
        }

        int nextYear = LocalDate.now().getYear() + 1;
        if (year < 1900 || year > nextYear) {
            throw new IllegalArgumentException("Năm sản xuất không hợp lệ.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null || status.isBlank()
                ? STATUS_ACTIVE
                : status.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedStatus) {
            case STATUS_ACTIVE, STATUS_UNDER_MAINTENANCE, STATUS_INACTIVE, STATUS_DISPOSED -> normalizedStatus;
            default -> throw new IllegalArgumentException("Trạng thái phương tiện không hợp lệ.");
        };
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
