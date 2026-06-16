package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.dao.MaintenancePlanDAO;
import model.dao.MaintenanceRecordDAO;
import model.dao.VehicleDAO;
import model.dto.MaintenanceDueAlertDTO;
import service.MaintenanceAlertService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TechnicianDashboardController {

    private static final int MAX_ALERT_ROWS = 5;

    private final MaintenancePlanDAO maintenancePlanDAO = new MaintenancePlanDAO();
    private final MaintenanceRecordDAO maintenanceRecordDAO = new MaintenanceRecordDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();

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
    private VBox maintenanceAlertList;

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

        renderMaintenanceAlertList(maintenanceAlertService.listDueAlerts());
    }

    private void renderMaintenanceAlertList(List<MaintenanceDueAlertDTO> alerts) {
        maintenanceAlertList.getChildren().clear();

        if (alerts.isEmpty()) {
            Label emptyLabel = new Label("Chưa có xe đến hạn bảo dưỡng.");
            emptyLabel.getStyleClass().add("item-subtitle");
            maintenanceAlertList.getChildren().add(emptyLabel);
            return;
        }

        alerts.stream()
                .limit(MAX_ALERT_ROWS)
                .map(this::createMaintenanceAlertRow)
                .forEach(maintenanceAlertList.getChildren()::add);
    }

    private VBox createMaintenanceAlertRow(MaintenanceDueAlertDTO alert) {
        VBox row = new VBox(4);
        row.getStyleClass().add("OVERDUE".equalsIgnoreCase(alert.getDueStatus())
                ? "priority-card-warning" : "priority-card-primary");

        Label title = new Label(alert.getLicensePlate() + " - " + alert.getMaintenanceName());
        title.getStyleClass().add("priority-title");

        Label text = new Label(formatMaintenanceAlertText(alert));
        text.getStyleClass().add("priority-text");
        text.setWrapText(true);

        row.getChildren().addAll(title, text);
        return row;
    }

    private String formatMaintenanceAlertText(MaintenanceDueAlertDTO alert) {
        StringBuilder builder = new StringBuilder();

        if (alert.getNextDueDate() != null) {
            long days = ChronoUnit.DAYS.between(LocalDate.now(), alert.getNextDueDate());
            if (days < 0) {
                builder.append("Quá hạn ").append(Math.abs(days)).append(" ngày");
            } else if (days == 0) {
                builder.append("Đến hạn hôm nay");
            } else {
                builder.append("Còn ").append(days).append(" ngày");
            }
            builder.append(" (hạn ").append(alert.getNextDueDate()).append(")");
        }

        if (alert.getNextDueOdometer() != null && alert.getCurrentOdometer() != null) {
            if (builder.length() > 0) {
                builder.append(" • ");
            }
            int remaining = alert.getNextDueOdometer() - alert.getCurrentOdometer();
            if (remaining <= 0) {
                builder.append("Vượt mốc ").append(Math.abs(remaining)).append(" km");
            } else {
                builder.append("Còn ").append(remaining).append(" km");
            }
            builder.append(" (mốc ").append(alert.getNextDueOdometer()).append(" km)");
        }

        if (builder.length() == 0) {
            return "OVERDUE".equalsIgnoreCase(alert.getDueStatus()) ? "Đã đến hạn bảo dưỡng" : "Sắp đến hạn bảo dưỡng";
        }
        return builder.toString();
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
