package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.dao.VehicleDAO;
import model.dto.DocumentAlertDTO;
import model.entity.Vehicle;
import service.DocumentAlertService;

import java.util.List;

public class ManagerDashboardController {

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertService documentAlertService = new DocumentAlertService();
    private Runnable openVehicleHandler = () -> {};
    private Runnable openVehicleDocumentHandler = () -> {};
    private Runnable openDocumentAlertHandler = () -> {};
    private Runnable openReportHandler = () -> {};

    @FXML private Label lblTotalVehicles;
    @FXML private Label lblActiveVehicles;
    @FXML private Label lblComingDueDocuments;
    @FXML private Label lblOverdueDocuments;
    @FXML private VBox alertList;
    @FXML private Label lblPriorityText;

    @FXML
    public void initialize() {
        List<Vehicle> vehicles = vehicleDAO.findAll();
        int comingDue = documentAlertService.countComingDue();
        int overdue = documentAlertService.countExpired();

        lblTotalVehicles.setText(String.valueOf(vehicles.size()));
        lblActiveVehicles.setText(String.valueOf(countByStatus(vehicles, "ACTIVE")));
        lblComingDueDocuments.setText(String.valueOf(comingDue));
        lblOverdueDocuments.setText(String.valueOf(overdue));

        lblPriorityText.setText(resolvePriorityText(overdue, comingDue));
        renderAlertList(documentAlertService.listAlerts());
    }

    public void setNavigationHandlers(Runnable openVehicleHandler,
                                      Runnable openVehicleDocumentHandler,
                                      Runnable openDocumentAlertHandler,
                                      Runnable openReportHandler) {
        this.openVehicleHandler = safeHandler(openVehicleHandler);
        this.openVehicleDocumentHandler = safeHandler(openVehicleDocumentHandler);
        this.openDocumentAlertHandler = safeHandler(openDocumentAlertHandler);
        this.openReportHandler = safeHandler(openReportHandler);
    }

    @FXML
    private void handleOpenVehicle() {
        openVehicleHandler.run();
    }

    @FXML
    private void handleOpenVehicleDocument() {
        openVehicleDocumentHandler.run();
    }

    @FXML
    private void handleOpenDocumentAlert() {
        openDocumentAlertHandler.run();
    }

    @FXML
    private void handleOpenReport() {
        openReportHandler.run();
    }

    private long countByStatus(List<Vehicle> vehicles, String status) {
        return vehicles.stream()
                .filter(vehicle -> status.equalsIgnoreCase(nullToEmpty(vehicle.getVehicleStatus())))
                .count();
    }

    private String resolvePriorityText(int overdue, int comingDue) {
        if (overdue > 0) {
            return "Ưu tiên xử lý giấy tờ đã hết hạn trước, sau đó cập nhật các giấy tờ sắp hết hạn.";
        }
        if (comingDue > 0) {
            return "Theo dõi giấy tờ sắp hết hạn trong 15 ngày tới và chuẩn bị cập nhật hồ sơ.";
        }
        return "Hiện chưa có giấy tờ quá hạn hoặc sắp hết hạn.";
    }

    private void renderAlertList(List<DocumentAlertDTO> alerts) {
        alertList.getChildren().clear();

        if (alerts.isEmpty()) {
            Label emptyLabel = new Label("Chưa có cảnh báo giấy tờ cần xử lý.");
            emptyLabel.getStyleClass().add("item-subtitle");
            alertList.getChildren().add(emptyLabel);
            return;
        }

        alerts.stream()
                .limit(3)
                .map(this::createAlertRow)
                .forEach(alertList.getChildren()::add);
    }

    private VBox createAlertRow(DocumentAlertDTO alert) {
        VBox row = new VBox(4);
        row.getStyleClass().add(alert.getDaysToExpiry() < 0 ? "priority-card-warning" : "priority-card-primary");

        Label title = new Label(alert.getLicensePlate() + " - " + alert.getDocumentTypeName());
        title.getStyleClass().add("priority-title");

        Label text = new Label(formatAlertText(alert));
        text.getStyleClass().add("priority-text");
        text.setWrapText(true);

        row.getChildren().addAll(title, text);
        return row;
    }

    private String formatAlertText(DocumentAlertDTO alert) {
        if (alert.getDaysToExpiry() < 0) {
            return "Đã hết hạn " + Math.abs(alert.getDaysToExpiry()) + " ngày, hạn: " + alert.getExpiryDate();
        }
        if (alert.getDaysToExpiry() == 0) {
            return "Hết hạn hôm nay, hạn: " + alert.getExpiryDate();
        }
        return "Còn " + alert.getDaysToExpiry() + " ngày, hạn: " + alert.getExpiryDate();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }
}
