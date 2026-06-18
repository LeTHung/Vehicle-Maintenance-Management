package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.dao.MaintenanceRecordDAO;
import model.dao.ReportDAO;
import model.dao.VehicleDAO;
import model.dto.DocumentAlertDTO;
import model.dto.MaintenanceDueAlertDTO;
import model.dto.MonthlyCostReportDTO;
import model.entity.MaintenanceRecord;
import model.entity.Vehicle;
import service.DocumentAlertService;
import service.MaintenanceAlertService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ManagerDashboardController {

    private static final int MAX_ALERT_ROWS = 6;

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final MaintenanceRecordDAO maintenanceRecordDAO = new MaintenanceRecordDAO();
    private final ReportDAO reportDAO = new ReportDAO();
    private final DocumentAlertService documentAlertService = new DocumentAlertService();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();
    private final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.of("vi", "VN"));

    private Runnable openVehicleHandler = () -> {};
    private Runnable openVehicleDocumentHandler = () -> {};
    private Runnable openDocumentAlertHandler = () -> {};
    private Runnable openMaintenanceAlertHandler = () -> {};
    private Runnable openReportHandler = () -> {};

    // Số liệu quyết định hành động của nút spotlight (handler được gán sau initialize).
    private int overdueDocuments;
    private long maintenanceOverdue;

    @FXML private VBox spotlight;
    @FXML private Label lblSpotlightTitle;
    @FXML private Label lblSpotlightText;
    @FXML private Button btnSpotlight;

    @FXML private Label lblTotalVehicles;
    @FXML private Label lblActiveVehicles;
    @FXML private Label lblComingDueDocuments;
    @FXML private Label lblMaintenanceComingDue;
    @FXML private Label lblOpenRecords;
    @FXML private Label lblMaintenanceCostYear;

    @FXML private VBox unifiedAlertList;

    @FXML
    public void initialize() {
        List<Vehicle> vehicles = vehicleDAO.findAll();
        int comingDueDocs = documentAlertService.countComingDue();
        overdueDocuments = documentAlertService.countExpired();

        List<DocumentAlertDTO> documentAlerts = documentAlertService.listAlerts();
        List<MaintenanceDueAlertDTO> dueAlerts = maintenanceAlertService.listDueAlerts();

        maintenanceOverdue = dueAlerts.stream()
                .filter(alert -> "OVERDUE".equalsIgnoreCase(nullToEmpty(alert.getDueStatus())))
                .count();
        long maintenanceComingDue = dueAlerts.size() - maintenanceOverdue;

        long openRecords = maintenanceRecordDAO.findAll().stream()
                .map(MaintenanceRecord::getRecordStatus)
                .map(this::nullToEmpty)
                .filter(status -> "OPEN".equalsIgnoreCase(status) || "IN_PROGRESS".equalsIgnoreCase(status))
                .count();

        String year = String.valueOf(LocalDate.now().getYear());
        BigDecimal maintenanceCostYear = reportDAO.findMonthlyCostsByYear(year).stream()
                .map(MonthlyCostReportDTO::getMaintenanceCost)
                .filter(cost -> cost != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalVehicles.setText(String.valueOf(vehicles.size()));
        lblActiveVehicles.setText(String.valueOf(countByStatus(vehicles, "ACTIVE")));
        lblComingDueDocuments.setText(String.valueOf(comingDueDocs));
        lblMaintenanceComingDue.setText(String.valueOf(maintenanceComingDue));
        lblOpenRecords.setText(String.valueOf(openRecords));
        lblMaintenanceCostYear.setText(currencyFormat.format(maintenanceCostYear));

        renderSpotlight(comingDueDocs, maintenanceComingDue);
        renderUnifiedAlerts(documentAlerts, dueAlerts);
    }

    public void setNavigationHandlers(Runnable openVehicleHandler,
                                      Runnable openVehicleDocumentHandler,
                                      Runnable openDocumentAlertHandler,
                                      Runnable openMaintenanceAlertHandler,
                                      Runnable openReportHandler) {
        this.openVehicleHandler = safeHandler(openVehicleHandler);
        this.openVehicleDocumentHandler = safeHandler(openVehicleDocumentHandler);
        this.openDocumentAlertHandler = safeHandler(openDocumentAlertHandler);
        this.openMaintenanceAlertHandler = safeHandler(openMaintenanceAlertHandler);
        this.openReportHandler = safeHandler(openReportHandler);
    }

    @FXML private void handleOpenVehicle() { openVehicleHandler.run(); }
    @FXML private void handleOpenVehicleDocument() { openVehicleDocumentHandler.run(); }
    @FXML private void handleOpenDocumentAlert() { openDocumentAlertHandler.run(); }
    @FXML private void handleOpenMaintenanceAlert() { openMaintenanceAlertHandler.run(); }
    @FXML private void handleOpenReport() { openReportHandler.run(); }

    @FXML
    private void handleSpotlightAction() {
        if (overdueDocuments > 0) {
            openDocumentAlertHandler.run();
        } else if (maintenanceOverdue > 0) {
            openMaintenanceAlertHandler.run();
        } else {
            openDocumentAlertHandler.run();
        }
    }

    // ── Spotlight ───────────────────────────────────────────────────────────

    private void renderSpotlight(int comingDueDocs, long maintenanceComingDue) {
        spotlight.getStyleClass().removeAll("spotlight-success", "spotlight-warning", "spotlight-danger");

        if (overdueDocuments > 0 || maintenanceOverdue > 0) {
            spotlight.getStyleClass().add("spotlight-danger");
            lblSpotlightTitle.setText(buildOverdueTitle());
            lblSpotlightText.setText("Ưu tiên xử lý các mục đã quá hạn trước khi phát sinh rủi ro vận hành.");
            btnSpotlight.setText("Xử lý ngay");
        } else if (comingDueDocs > 0 || maintenanceComingDue > 0) {
            spotlight.getStyleClass().add("spotlight-warning");
            lblSpotlightTitle.setText(buildComingDueTitle(comingDueDocs, maintenanceComingDue));
            lblSpotlightText.setText("Chủ động lên lịch xử lý trong những ngày tới để tránh quá hạn.");
            btnSpotlight.setText("Xem danh sách");
        } else {
            spotlight.getStyleClass().add("spotlight-success");
            lblSpotlightTitle.setText("Mọi thứ đang trong tầm kiểm soát");
            lblSpotlightText.setText("Hiện chưa có giấy tờ hoặc xe bảo dưỡng nào quá hạn.");
            btnSpotlight.setText("Xem chi tiết");
        }
    }

    private String buildOverdueTitle() {
        List<String> parts = new ArrayList<>();
        if (overdueDocuments > 0) {
            parts.add(overdueDocuments + " giấy tờ quá hạn");
        }
        if (maintenanceOverdue > 0) {
            parts.add(maintenanceOverdue + " xe quá hạn bảo dưỡng");
        }
        return String.join(" · ", parts) + " — cần xử lý ngay";
    }

    private String buildComingDueTitle(int comingDueDocs, long maintenanceComingDue) {
        List<String> parts = new ArrayList<>();
        if (comingDueDocs > 0) {
            parts.add(comingDueDocs + " giấy tờ sắp hạn");
        }
        if (maintenanceComingDue > 0) {
            parts.add(maintenanceComingDue + " xe sắp đến hạn bảo dưỡng");
        }
        return String.join(" · ", parts);
    }

    // ── Danh sách cảnh báo gộp ───────────────────────────────────────────────

    private void renderUnifiedAlerts(List<DocumentAlertDTO> documentAlerts,
                                     List<MaintenanceDueAlertDTO> maintenanceAlerts) {
        unifiedAlertList.getChildren().clear();

        List<UrgentItem> items = new ArrayList<>();
        for (DocumentAlertDTO alert : documentAlerts) {
            items.add(toUrgentItem(alert));
        }
        for (MaintenanceDueAlertDTO alert : maintenanceAlerts) {
            items.add(toUrgentItem(alert));
        }

        if (items.isEmpty()) {
            Label emptyLabel = new Label("Chưa có giấy tờ hoặc xe bảo dưỡng cần xử lý.");
            emptyLabel.getStyleClass().add("item-subtitle");
            unifiedAlertList.getChildren().add(emptyLabel);
            return;
        }

        items.stream()
                .sorted(Comparator.comparingInt((UrgentItem i) -> i.rank).thenComparingLong(i -> i.order))
                .limit(MAX_ALERT_ROWS)
                .map(this::createUnifiedRow)
                .forEach(unifiedAlertList.getChildren()::add);
    }

    private UrgentItem toUrgentItem(DocumentAlertDTO alert) {
        boolean overdue = alert.getDaysToExpiry() < 0;
        return new UrgentItem(
                overdue ? 0 : 2,
                alert.getDaysToExpiry(),
                overdue ? "alert-dot-danger" : "alert-dot-coming",
                nullToEmpty(alert.getLicensePlate()) + " · " + nullToEmpty(alert.getDocumentTypeName()),
                formatAlertText(alert));
    }

    private UrgentItem toUrgentItem(MaintenanceDueAlertDTO alert) {
        boolean overdue = "OVERDUE".equalsIgnoreCase(nullToEmpty(alert.getDueStatus()));
        long order = alert.getNextDueDate() == null
                ? Long.MAX_VALUE
                : ChronoUnit.DAYS.between(LocalDate.now(), alert.getNextDueDate());
        return new UrgentItem(
                overdue ? 1 : 2,
                order,
                overdue ? "alert-dot-overdue" : "alert-dot-coming",
                nullToEmpty(alert.getLicensePlate()) + " · " + nullToEmpty(alert.getMaintenanceName()),
                formatMaintenanceAlertText(alert));
    }

    private Node createUnifiedRow(UrgentItem item) {
        Label dot = new Label("●");
        dot.getStyleClass().addAll("alert-dot", item.dotStyle);

        Label title = new Label(item.title);
        title.getStyleClass().add("item-title");

        Label subtitle = new Label(item.subtitle);
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

    private String formatAlertText(DocumentAlertDTO alert) {
        if (alert.getDaysToExpiry() < 0) {
            return "Đã hết hạn " + Math.abs(alert.getDaysToExpiry()) + " ngày, hạn: " + alert.getExpiryDate();
        }
        if (alert.getDaysToExpiry() == 0) {
            return "Hết hạn hôm nay, hạn: " + alert.getExpiryDate();
        }
        return "Còn " + alert.getDaysToExpiry() + " ngày, hạn: " + alert.getExpiryDate();
    }

    private long countByStatus(List<Vehicle> vehicles, String status) {
        return vehicles.stream()
                .filter(vehicle -> status.equalsIgnoreCase(nullToEmpty(vehicle.getVehicleStatus())))
                .count();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }

    /** Mục cảnh báo đã chuẩn hóa để gộp giấy tờ + bảo dưỡng vào một danh sách. */
    private static final class UrgentItem {
        final int rank;        // 0 = giấy tờ quá hạn, 1 = bảo dưỡng quá hạn, 2 = sắp đến hạn
        final long order;      // số ngày còn lại (âm = quá hạn) để sắp xếp trong cùng nhóm
        final String dotStyle;
        final String title;
        final String subtitle;

        UrgentItem(int rank, long order, String dotStyle, String title, String subtitle) {
            this.rank = rank;
            this.order = order;
            this.dotStyle = dotStyle;
            this.title = title;
            this.subtitle = subtitle;
        }
    }
}
