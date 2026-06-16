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
        applyAutoCalculation(plan);
        checkNoDuplicateActive(plan.getVehicleId(), plan.getMaintenanceTypeId(), -1L);
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
        applyAutoCalculation(plan);
        if (plan.isActive()) {
            checkNoDuplicateActive(plan.getVehicleId(), plan.getMaintenanceTypeId(), plan.getPlanId());
        }
        if (!planDAO.update(plan)) {
            throw new IllegalStateException("Không thể cập nhật kế hoạch. Vui lòng thử lại.");
        }
        return true;
    }

    /**
     * Tự tính ngày/ODO đến hạn và điền ngưỡng cảnh báo trước.
     * - next_due_date  = ngày bảo dưỡng gần nhất + chu kỳ ngày (chỉ tính khi người dùng để trống)
     * - next_due_odometer = ODO gần nhất + chu kỳ km (chỉ tính khi người dùng để trống)
     * - alert_before_days/km: dùng giá trị người dùng nhập; nếu trống thì lấy mặc định từ bảng alert_settings
     */
    private void applyAutoCalculation(MaintenancePlan plan) {
        if (plan.getNextDueDate() == null
                && plan.getIntervalDays() != null
                && plan.getLastServiceDate() != null) {
            plan.setNextDueDate(plan.getLastServiceDate().plusDays(plan.getIntervalDays()));
        }

        if (plan.getNextDueOdometer() == null
                && plan.getIntervalKm() != null
                && plan.getLastServiceOdometer() != null) {
            plan.setNextDueOdometer(plan.getLastServiceOdometer() + plan.getIntervalKm());
        }

        if (plan.getAlertBeforeDays() == null || plan.getAlertBeforeKm() == null) {
            int[] defaults = planDAO.findMaintenanceAlertDefaults();
            if (plan.getAlertBeforeDays() == null) {
                plan.setAlertBeforeDays(defaults[0]);
            }
            if (plan.getAlertBeforeKm() == null) {
                plan.setAlertBeforeKm(defaults[1]);
            }
        }
    }

    private void checkNoDuplicateActive(long vehicleId, int typeId, long excludePlanId) {
        boolean duplicate = planDAO.findByVehicleId(vehicleId).stream()
                .anyMatch(p -> p.isActive()
                        && p.getMaintenanceTypeId() == typeId
                        && p.getPlanId() != excludePlanId);
        if (duplicate) {
            throw new IllegalArgumentException(
                "Xe này đã có kế hoạch bảo dưỡng loại này đang hoạt động.\n" +
                "Hủy kích hoạt kế hoạch cũ trước khi tạo kế hoạch mới.");
        }
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
        // Bắt buộc nhập mốc nền để tự tính được ngày/ODO đến hạn
        if (plan.getIntervalDays() != null
                && plan.getLastServiceDate() == null
                && plan.getNextDueDate() == null) {
            throw new IllegalArgumentException(
                "Đã nhập 'Chu kỳ ngày' → bắt buộc nhập 'Ngày bảo dưỡng gần nhất' (để tự tính ngày đến hạn) "
                + "hoặc nhập trực tiếp 'Ngày đến hạn kế tiếp'.");
        }
        if (plan.getIntervalKm() != null
                && plan.getLastServiceOdometer() == null
                && plan.getNextDueOdometer() == null) {
            throw new IllegalArgumentException(
                "Đã nhập 'Chu kỳ km' → bắt buộc nhập 'ODO bảo dưỡng gần nhất' (để tự tính ODO đến hạn) "
                + "hoặc nhập trực tiếp 'ODO đến hạn kế tiếp'.");
        }
    }
}
