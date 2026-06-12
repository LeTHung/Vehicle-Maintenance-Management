package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.dao.MaintenancePlanDAO;
import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;

import java.time.LocalDate;

public class TechnicianDashboardController {

    private final MaintenancePlanDAO maintenancePlanDAO = new MaintenancePlanDAO();
    private final MaintenanceRecordDAO maintenanceRecordDAO = new MaintenanceRecordDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private Runnable openMaintenanceAlertHandler = () -> {
    };
    private Runnable openMaintenanceRecordHandler = () -> {
    };

    @FXML
    private Label lblPendingWorkOrders;
    @FXML
    private Label lblPendingHint;
    @FXML
    private Label lblInProgressRecords;
    @FXML
    private Label lblInProgressHint;
    @FXML
    private Label lblCompletedToday;
    @FXML
    private Label lblCompletedHint;

    @FXML
    public void initialize() {
        var dueAlerts = maintenancePlanDAO.findDueAlerts();
        var records = maintenanceRecordDAO.findAll();

        long openRecords = records.stream()
                .filter(record -> "OPEN".equalsIgnoreCase(nullToEmpty(record.getRecordStatus())))
                .count();
        long inProgress = records.stream()
                .filter(record -> "IN_PROGRESS".equalsIgnoreCase(nullToEmpty(record.getRecordStatus())))
                .count();
        long completedToday = records.stream()
                .filter(record -> "COMPLETED".equalsIgnoreCase(nullToEmpty(record.getRecordStatus())))
                .filter(record -> LocalDate.now().equals(record.getServiceDate()))
                .count();
        long underMaintenanceVehicles = vehicleDAO.findAll().stream()
                .filter(vehicle -> "UNDER_MAINTENANCE".equalsIgnoreCase(nullToEmpty(vehicle.getVehicleStatus())))
                .count();

        lblPendingWorkOrders.setText(String.valueOf(dueAlerts.size() + openRecords));
        lblInProgressRecords.setText(String.valueOf(inProgress));
        lblCompletedToday.setText(String.valueOf(completedToday));
        lblPendingHint.setText(dueAlerts.size() + " kế hoạch đến hạn, " + openRecords + " phiếu chờ");
        lblInProgressHint.setText(underMaintenanceVehicles + " xe đang ở trạng thái bảo dưỡng");
        lblCompletedHint.setText("Lấy từ phiếu đã hoàn thành trong ngày");
    }

    public void setNavigationHandlers(Runnable openMaintenanceAlertHandler,
            Runnable openMaintenanceRecordHandler) {
        this.openMaintenanceAlertHandler = safeHandler(openMaintenanceAlertHandler);
        this.openMaintenanceRecordHandler = safeHandler(openMaintenanceRecordHandler);
    }

    @FXML
    private void handleOpenMaintenanceAlert() {
        openMaintenanceAlertHandler.run();
    }

    @FXML
    private void handleOpenMaintenanceRecord() {
        openMaintenanceRecordHandler.run();
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {
        } : handler;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
