package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.dao.MaintenanceRecordDAO;
import model.dto.MaintenanceDueAlertDTO;
import service.MaintenanceAlertService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TechnicianDashboardController {

    private static final int MAX_ALERT_ROWS = 6;

    private final MaintenanceRecordDAO maintenanceRecordDAO = new MaintenanceRecordDAO();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();

    private Runnable openMaintenanceAlertHandler = () -> {};
    private Runnable openMaintenanceRecordHandler = () -> {};

    // Quyết định hành động nút spotlight (handler được gán sau initialize).
    private long maintenanceOverdue;

    @FXML private VBox spotlight;
    @FXML private Label lblSpotlightTitle;
    @FXML private Label lblSpotlightText;
    @FXML private Button btnSpotlight;

    @FXML private Label lblPendingWorkOrders;
    @FXML private Label lblInProgressRecords;
    @FXML private Label lblCompletedToday;
    @FXML private Label lblDueVehicles;

    @FXML private VBox maintenanceAlertList;

    @FXML
    public void initialize() {
        List<MaintenanceDueAlertDTO> dueAlerts = maintenanceAlertService.listDueAlerts();
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

        maintenanceOverdue = dueAlerts.stream()
                .filter(alert -> "OVERDUE".equalsIgnoreCase(nullToEmpty(alert.getDueStatus())))
                .count();
        long maintenanceComingDue = dueAlerts.size() - maintenanceOverdue;

        lblPendingWorkOrders.setText(String.valueOf(dueAlerts.size() + openRecords));
        lblInProgressRecords.setText(String.valueOf(inProgress));
        lblCompletedToday.setText(String.valueOf(completedToday));
        lblDueVehicles.setText(String.valueOf(dueAlerts.size()));

        renderSpotlight(maintenanceComingDue);
        renderMaintenanceAlertList(dueAlerts);
    }

    public void setNavigationHandlers(Runnable openMaintenanceAlertHandler,
                                      Runnable openMaintenanceRecordHandler) {
        this.openMaintenanceAlertHandler = safeHandler(openMaintenanceAlertHandler);
        this.openMaintenanceRecordHandler = safeHandler(openMaintenanceRecordHandler);
    }

    @FXML private void handleOpenMaintenanceAlert() { openMaintenanceAlertHandler.run(); }
    @FXML private void handleOpenMaintenanceRecord() { openMaintenanceRecordHandler.run(); }

    @FXML
    private void handleSpotlightAction() {
        if (maintenanceOverdue > 0) {
            openMaintenanceRecordHandler.run();
        } else {
            openMaintenanceAlertHandler.run();
        }
    }

    // ── Spotlight ───────────────────────────────────────────────────────────

    private void renderSpotlight(long maintenanceComingDue) {
        spotlight.getStyleClass().removeAll("spotlight-success", "spotlight-warning", "spotlight-danger");

        if (maintenanceOverdue > 0) {
            spotlight.getStyleClass().add("spotlight-danger");
            lblSpotlightTitle.setText(maintenanceOverdue + " xe quá hạn bảo dưỡng — cần lên phiếu ngay");
            lblSpotlightText.setText("Tạo phiếu bảo dưỡng/sửa chữa cho các xe đã quá hạn để xử lý kịp thời.");
            btnSpotlight.setText("Tạo phiếu ngay");
        } else if (maintenanceComingDue > 0) {
            spotlight.getStyleClass().add("spotlight-warning");
            lblSpotlightTitle.setText(maintenanceComingDue + " xe sắp đến hạn bảo dưỡng");
            lblSpotlightText.setText("Chủ động sắp xếp công việc cho các xe sắp đến hạn theo ngày/ODO.");
            btnSpotlight.setText("Xem danh sách");
        } else {
            spotlight.getStyleClass().add("spotlight-success");
            lblSpotlightTitle.setText("Chưa có xe nào quá hạn bảo dưỡng");
            lblSpotlightText.setText("Theo dõi danh sách xe đến hạn để chủ động xử lý.");
            btnSpotlight.setText("Xem lịch hôm nay");
        }
    }

    // ── Danh sách xe đến hạn ──────────────────────────────────────────────────

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

    private Node createMaintenanceAlertRow(MaintenanceDueAlertDTO alert) {
        boolean overdue = "OVERDUE".equalsIgnoreCase(nullToEmpty(alert.getDueStatus()));

        Label dot = new Label("●");
        dot.getStyleClass().addAll("alert-dot", overdue ? "alert-dot-overdue" : "alert-dot-coming");

        Label title = new Label(nullToEmpty(alert.getLicensePlate()) + " · " + nullToEmpty(alert.getMaintenanceName()));
        title.getStyleClass().add("item-title");

        Label subtitle = new Label(formatMaintenanceAlertText(alert));
        subtitle.getStyleClass().add("item-subtitle");
        subtitle.setWrapText(true);

        VBox texts = new VBox(2, title, subtitle);
        HBox.setHgrow(texts, Priority.ALWAYS);

        HBox row = new HBox(dot, texts);
        row.getStyleClass().add("alert-row");
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

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
