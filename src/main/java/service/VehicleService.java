package service;

import model.dao.VehicleDAO;
import model.entity.Vehicle;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class VehicleService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_UNDER_MAINTENANCE = "UNDER_MAINTENANCE";
    private static final String STATUS_INACTIVE = "INACTIVE";
    private static final String STATUS_DISPOSED = "DISPOSED";

    private final VehicleDAO vehicleDAO = new VehicleDAO();

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
            throw new IllegalStateException("Khong the luu phuong tien. Vui long kiem tra du lieu.");
        }

        return vehicleDAO.findById(generatedId).orElse(vehicle);
    }

    public Vehicle updateVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui long chon phuong tien can cap nhat.");
        }

        normalizeAndValidate(vehicle, vehicle.getVehicleId());

        if (!vehicleDAO.update(vehicle)) {
            throw new IllegalStateException("Khong the cap nhat phuong tien. Vui long thu lai.");
        }

        return vehicleDAO.findById(vehicle.getVehicleId()).orElse(vehicle);
    }

    private void normalizeAndValidate(Vehicle vehicle, Long currentVehicleId) {
        if (vehicle == null) {
            throw new IllegalArgumentException("Du lieu phuong tien khong hop le.");
        }

        vehicle.setVehicleCode(normalizeOptional(vehicle.getVehicleCode()));
        vehicle.setLicensePlate(requireText(vehicle.getLicensePlate(), "Bien so khong duoc de trong.").toUpperCase(Locale.ROOT));
        vehicle.setVehicleType(requireText(vehicle.getVehicleType(), "Loai xe khong duoc de trong."));
        vehicle.setBrand(normalizeOptional(vehicle.getBrand()));
        vehicle.setModel(normalizeOptional(vehicle.getModel()));
        vehicle.setChassisNumber(requireText(vehicle.getChassisNumber(), "So khung khong duoc de trong.").toUpperCase(Locale.ROOT));
        vehicle.setEngineNumber(requireText(vehicle.getEngineNumber(), "So may khong duoc de trong.").toUpperCase(Locale.ROOT));
        vehicle.setColor(normalizeOptional(vehicle.getColor()));
        vehicle.setVehicleStatus(normalizeStatus(vehicle.getVehicleStatus()));
        vehicle.setNotes(normalizeOptional(vehicle.getNotes()));

        validateManufactureYear(vehicle.getManufactureYear());

        if (vehicle.getCurrentOdometer() < 0) {
            throw new IllegalArgumentException("ODO hien tai khong duoc am.");
        }

        if (vehicle.getVehicleCode() != null
                && vehicleDAO.existsByVehicleCode(vehicle.getVehicleCode(), currentVehicleId)) {
            throw new IllegalArgumentException("Ma xe da ton tai.");
        }

        if (vehicleDAO.existsByLicensePlate(vehicle.getLicensePlate(), currentVehicleId)) {
            throw new IllegalArgumentException("Bien so da ton tai.");
        }

        if (vehicleDAO.existsByChassisNumber(vehicle.getChassisNumber(), currentVehicleId)) {
            throw new IllegalArgumentException("So khung da ton tai.");
        }

        if (vehicleDAO.existsByEngineNumber(vehicle.getEngineNumber(), currentVehicleId)) {
            throw new IllegalArgumentException("So may da ton tai.");
        }
    }

    private void validateManufactureYear(Integer year) {
        if (year == null) {
            return;
        }

        int nextYear = LocalDate.now().getYear() + 1;
        if (year < 1900 || year > nextYear) {
            throw new IllegalArgumentException("Nam san xuat khong hop le.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null || status.isBlank()
                ? STATUS_ACTIVE
                : status.trim().toUpperCase(Locale.ROOT);

        return switch (normalizedStatus) {
            case STATUS_ACTIVE, STATUS_UNDER_MAINTENANCE, STATUS_INACTIVE, STATUS_DISPOSED -> normalizedStatus;
            default -> throw new IllegalArgumentException("Trang thai phuong tien khong hop le.");
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
