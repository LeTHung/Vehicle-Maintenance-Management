package service;

import model.dao.MaintenancePlanDAO;
import model.dao.MaintenanceTypeDAO;
import model.dao.VehicleDAO;
import model.entity.MaintenancePlan;
import model.entity.MaintenanceType;
import model.entity.Vehicle;

import java.util.List;

public class MaintenancePlanService {

    private final MaintenancePlanDAO planDAO = new MaintenancePlanDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MaintenanceTypeDAO typeDAO = new MaintenanceTypeDAO();

    public List<MaintenancePlan> listPlans() {
        return planDAO.findAll();
    }

    public List<MaintenancePlan> listByVehicle(long vehicleId) {
        return planDAO.findByVehicleId(vehicleId);
    }

    public List<Vehicle> listVehicles() {
        return vehicleDAO.findAll();
    }

    public List<MaintenanceType> listActiveTypes() {
        return typeDAO.findAllActive();
    }

    public Long save(MaintenancePlan plan) {
        validate(plan);
        Long id = planDAO.insert(plan);
        if (id == null) {
            throw new IllegalStateException("Không thể lưu kế hoạch bảo dưỡng. Vui lòng thử lại.");
        }
        return id;
    }

    public boolean update(MaintenancePlan plan) {
        if (plan == null || plan.getPlanId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn kế hoạch cần cập nhật.");
        }
        validate(plan);
        if (!planDAO.update(plan)) {
            throw new IllegalStateException("Không thể cập nhật kế hoạch. Vui lòng thử lại.");
        }
        return true;
    }

    public boolean deactivate(long planId) {
        return planDAO.deactivate(planId);
    }

    private void validate(MaintenancePlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Dữ liệu kế hoạch không hợp lệ.");
        }
        if (plan.getVehicleId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn xe.");
        }
        if (plan.getMaintenanceTypeId() <= 0) {
            throw new IllegalArgumentException("Vui lòng chọn loại bảo dưỡng.");
        }
        if (plan.getIntervalDays() == null && plan.getIntervalKm() == null) {
            throw new IllegalArgumentException("Phải nhập ít nhất một trong hai: chu kỳ ngày hoặc chu kỳ km.");
        }
        if (plan.getIntervalDays() != null && plan.getIntervalDays() <= 0) {
            throw new IllegalArgumentException("Chu kỳ ngày phải lớn hơn 0.");
        }
        if (plan.getIntervalKm() != null && plan.getIntervalKm() <= 0) {
            throw new IllegalArgumentException("Chu kỳ km phải lớn hơn 0.");
        }
    }
}
