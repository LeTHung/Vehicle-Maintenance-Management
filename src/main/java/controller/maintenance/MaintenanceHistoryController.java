package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.dao.UserDAO;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenanceRecord;
import model.entity.User;
import model.entity.Vehicle;
import service.MaintenanceRecordService;
import util.DetailWindow;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MaintenanceHistoryController {

    private static final String ALL_STATUSES = "Tất cả trạng thái";

    private final MaintenanceRecordService service = new MaintenanceRecordService();
    private final UserDAO userDAO = new UserDAO();
    private final Map<Long, String> vehicleNameMap = new HashMap<>();
    private final Map<Long, String> technicianNameMap = new HashMap<>();
    private Stage historyDetailStage;

    @FXML private ComboBox<Vehicle> cbFilterVehicle;
    @FXML private ComboBox<String> cbFilterStatus;
    @FXML private VBox historyDetailPanel;

    @FXML private TableView<MaintenanceRecord> tblHistory;
    @FXML private TableColumn<MaintenanceRecord, Long> colHistoryId;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryLicensePlate;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryType;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryTitle;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryServiceDate;
    @FXML private TableColumn<MaintenanceRecord, Integer> colHistoryOdometer;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryTechnician;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryProvider;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryTotalCost;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryStatus;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryWorkSummary;

    @FXML private TableView<MaintenanceItemDetail> tblItems;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemType;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemDesc;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemQty;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnit;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnitCost;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemLineTotal;

    @FXML
    public void initialize() {
        loadLookupData();
        setupVehicleFilter();
        setupStatusFilter();
        configureTable();
        configureItemTable();
        setupFilterListeners();
        tblHistory.setRowFactory(table -> {
            TableRow<MaintenanceRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    loadItems(row.getItem());
                    showHistoryDetailWindow();
                }
            });
            return row;
        });
        applyFilters();
    }

    @FXML
    private void handleRefresh() {
        cbFilterVehicle.setValue(null);
        cbFilterStatus.setValue(ALL_STATUSES);
        loadLookupData();
        setupVehicleFilter();
        DetailWindow.hide(historyDetailStage);
        applyFilters();
    }

    private void loadLookupData() {
        vehicleNameMap.clear();
        service.listVehicles().forEach(vehicle ->
            vehicleNameMap.put(vehicle.getVehicleId(), vehicle.getLicensePlate()));

        technicianNameMap.clear();
        userDAO.findAll().forEach(user -> {
            if (user.getUserId() != null) {
                technicianNameMap.put(user.getUserId(), resolveUserDisplayName(user));
            }
        });
    }

    private void setupVehicleFilter() {
        List<Vehicle> vehicles = service.listVehicles();
        StringConverter<Vehicle> converter = new StringConverter<>() {
            public String toString(Vehicle vehicle) {
                return vehicle == null ? "" : vehicle.getLicensePlate();
            }

            public Vehicle fromString(String value) {
                return null;
            }
        };

        cbFilterVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbFilterVehicle.setConverter(converter);
    }

    private void setupStatusFilter() {
        cbFilterStatus.setItems(FXCollections.observableArrayList(
            ALL_STATUSES,
            recordStatusLabel("OPEN"),
            recordStatusLabel("IN_PROGRESS"),
            recordStatusLabel("COMPLETED"),
            recordStatusLabel("CANCELLED")
        ));
        cbFilterStatus.setValue(ALL_STATUSES);
    }

    private void configureTable() {
        colHistoryId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getRecordId()).asObject());
        colHistoryLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(vehicleNameMap.getOrDefault(c.getValue().getVehicleId(), "")));
        colHistoryType.setCellValueFactory(c ->
            new SimpleStringProperty(recordTypeLabel(c.getValue().getRecordType())));
        colHistoryTitle.setCellValueFactory(c ->
            new SimpleStringProperty(nullToEmpty(c.getValue().getTitle())));
        colHistoryServiceDate.setCellValueFactory(c ->
            new SimpleStringProperty(formatDate(c.getValue().getServiceDate())));
        colHistoryOdometer.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getOdometer()));
        colHistoryTechnician.setCellValueFactory(c ->
            new SimpleStringProperty(resolveTechnicianName(c.getValue().getTechnicianId())));
        colHistoryProvider.setCellValueFactory(c ->
            new SimpleStringProperty(nullToEmpty(c.getValue().getServiceProviderName())));
        colHistoryTotalCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatCost(c.getValue().getTotalCost())));
        colHistoryStatus.setCellValueFactory(c ->
            new SimpleStringProperty(recordStatusLabel(c.getValue().getRecordStatus())));
        colHistoryWorkSummary.setCellValueFactory(c ->
            new SimpleStringProperty(nullToEmpty(c.getValue().getWorkSummary())));
    }

    private void configureItemTable() {
        colItemType.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getItemType() != null ? c.getValue().getItemType() : ""));
        colItemDesc.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getDescription() != null ? c.getValue().getDescription() : ""));
        colItemQty.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getQuantity() != null ? c.getValue().getQuantity().toPlainString() : ""));
        colItemUnit.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getUnit() != null ? c.getValue().getUnit() : ""));
        colItemUnitCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getUnitCost())));
        colItemLineTotal.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getLineTotal())));
    }

    private void loadItems(MaintenanceRecord record) {
        if (record == null) {
            tblItems.getItems().clear();
            return;
        }
        List<MaintenanceItemDetail> items = service.listItems(record.getRecordId());
        tblItems.setItems(FXCollections.observableArrayList(items));
    }

    private void showHistoryDetailWindow() {
        historyDetailStage = DetailWindow.show(
                historyDetailStage,
                historyDetailPanel,
                tblHistory,
                "Chi tiết hạng mục bảo dưỡng",
                760,
                360);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getNumberInstance(Locale.of("vi", "VN")).format(value);
    }

    private void setupFilterListeners() {
        cbFilterVehicle.setOnAction(e -> applyFilters());
        cbFilterStatus.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        Vehicle selectedVehicle = cbFilterVehicle.getValue();
        String selectedStatus = cbFilterStatus.getValue();

        List<MaintenanceRecord> records = selectedVehicle == null
            ? service.listAll()
            : service.listByVehicle(selectedVehicle.getVehicleId());

        if (selectedStatus != null && !ALL_STATUSES.equals(selectedStatus)) {
            records = records.stream()
                .filter(record -> recordStatusValue(selectedStatus).equals(record.getRecordStatus()))
                .toList();
        }

        tblHistory.setItems(FXCollections.observableArrayList(sortForHistory(records)));
    }

    private List<MaintenanceRecord> sortForHistory(List<MaintenanceRecord> records) {
        return records.stream()
            .sorted(this::compareHistoryRows)
            .toList();
    }

    private int compareHistoryRows(MaintenanceRecord left, MaintenanceRecord right) {
        LocalDate leftDate = left.getServiceDate();
        LocalDate rightDate = right.getServiceDate();

        if (leftDate == null && rightDate != null) return 1;
        if (leftDate != null && rightDate == null) return -1;
        if (leftDate != null) {
            int dateCompare = rightDate.compareTo(leftDate);
            if (dateCompare != 0) return dateCompare;
        }

        return Long.compare(right.getRecordId(), left.getRecordId());
    }

    private String resolveTechnicianName(Long technicianId) {
        if (technicianId == null) {
            return "Chưa gán";
        }
        return technicianNameMap.getOrDefault(technicianId, "#" + technicianId);
    }

    private String resolveUserDisplayName(User user) {
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername().trim();
        }
        return "#" + user.getUserId();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : date.toString();
    }

    private String formatCost(BigDecimal cost) {
        return cost == null ? "" : cost.toPlainString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String recordTypeLabel(String type) {
        if (type == null || type.isBlank()) {
            return "";
        }
        return switch (type) {
            case "PREVENTIVE" -> "Bảo dưỡng định kỳ";
            case "CORRECTIVE" -> "Sửa chữa phát sinh";
            default -> type;
        };
    }

    private String recordStatusLabel(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }
        return switch (status) {
            case "OPEN" -> "Chờ xử lý";
            case "IN_PROGRESS" -> "Đang xử lý";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String recordStatusValue(String label) {
        if (label == null || label.isBlank()) {
            return "";
        }
        return switch (label) {
            case "Chờ xử lý" -> "OPEN";
            case "Đang xử lý" -> "IN_PROGRESS";
            case "Hoàn thành" -> "COMPLETED";
            case "Đã hủy" -> "CANCELLED";
            default -> label;
        };
    }
}
