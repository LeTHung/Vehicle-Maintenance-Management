package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import model.dao.UserDAO;
import model.entity.MaintenanceRecord;
import model.entity.User;
import model.entity.Vehicle;
import service.MaintenanceRecordService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenanceHistoryController {

    private static final String ALL_STATUSES = "Tất cả trạng thái";

    private final MaintenanceRecordService service = new MaintenanceRecordService();
    private final UserDAO userDAO = new UserDAO();
    private final Map<Long, String> vehicleNameMap = new HashMap<>();
    private final Map<Long, String> technicianNameMap = new HashMap<>();

    @FXML private ComboBox<Vehicle> cbFilterVehicle;
    @FXML private ComboBox<String> cbFilterStatus;

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

    @FXML
    public void initialize() {
        loadLookupData();
        setupVehicleFilter();
        setupStatusFilter();
        configureTable();
        setupFilterListeners();
        applyFilters();
    }

    @FXML
    private void handleRefresh() {
        cbFilterVehicle.setValue(null);
        cbFilterStatus.setValue(ALL_STATUSES);
        loadLookupData();
        setupVehicleFilter();
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
            ALL_STATUSES, "OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));
        cbFilterStatus.setValue(ALL_STATUSES);
    }

    private void configureTable() {
        colHistoryId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getRecordId()).asObject());
        colHistoryLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(vehicleNameMap.getOrDefault(c.getValue().getVehicleId(), "")));
        colHistoryType.setCellValueFactory(c ->
            new SimpleStringProperty(nullToEmpty(c.getValue().getRecordType())));
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
            new SimpleStringProperty(nullToEmpty(c.getValue().getRecordStatus())));
        colHistoryWorkSummary.setCellValueFactory(c ->
            new SimpleStringProperty(nullToEmpty(c.getValue().getWorkSummary())));
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
                .filter(record -> selectedStatus.equals(record.getRecordStatus()))
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
}
