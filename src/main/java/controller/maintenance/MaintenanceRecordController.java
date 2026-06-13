package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import model.dao.UserDAO;
import model.entity.MaintenanceRecord;
import model.entity.User;
import model.entity.Vehicle;
import session.UserSession;
import service.MaintenanceRecordService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenanceRecordController {

    private final MaintenanceRecordService service = new MaintenanceRecordService();
    private final UserDAO userDAO = new UserDAO();

    @FXML private ComboBox<Vehicle> cbFilterVehicle;
    @FXML private ComboBox<Vehicle> cbVehicle;
    @FXML private ComboBox<String> cbRecordType;
    @FXML private ComboBox<String> cbRecordStatus;
    @FXML private TextField txtTitle;
    @FXML private DatePicker dpServiceDate;
    @FXML private TextField txtOdometer;
    @FXML private TextField txtTotalCost;
    @FXML private TextField txtServiceProvider;
    @FXML private TextField txtWorkSummary;
    @FXML private TextField txtNotes;

    @FXML private TableView<MaintenanceRecord> tblRecord;
    @FXML private TableColumn<MaintenanceRecord, Long> colRecordId;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordLicensePlate;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordType;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTitle;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordServiceDate;
    @FXML private TableColumn<MaintenanceRecord, Integer> colRecordOdometer;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTechnician;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTotalCost;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordStatus;

    private Long selectedRecordId;
    private Long selectedTechnicianId;
    private Long selectedCreatedBy;
    private final Map<Long, String> vehicleNameMap = new HashMap<>();
    private final Map<Long, String> technicianNameMap = new HashMap<>();

    @FXML
    public void initialize() {
        cbRecordType.getItems().addAll(recordTypeLabel("PREVENTIVE"), recordTypeLabel("CORRECTIVE"));
        cbRecordStatus.getItems().addAll(
                recordStatusLabel("OPEN"),
                recordStatusLabel("IN_PROGRESS"),
                recordStatusLabel("COMPLETED"),
                recordStatusLabel("CANCELLED"));
        loadTechnicianNames();
        setupVehicleComboBoxes();
        configureTable();
        setupFilterListener();
        setupRowSelectionListener();
        loadTable(service.listAll());
    }

    private void setupVehicleComboBoxes() {
        List<Vehicle> vehicles = service.listVehicles();
        vehicles.forEach(v -> vehicleNameMap.put(v.getVehicleId(), v.getLicensePlate()));

        StringConverter<Vehicle> converter = new StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getLicensePlate(); }
            public Vehicle fromString(String s) { return null; }
        };

        cbVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbVehicle.setConverter(converter);

        cbFilterVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbFilterVehicle.setConverter(converter);
    }

    private void configureTable() {
        colRecordId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getRecordId()).asObject());
        colRecordLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(vehicleNameMap.getOrDefault(c.getValue().getVehicleId(), "")));
        colRecordType.setCellValueFactory(c ->
            new SimpleStringProperty(recordTypeLabel(c.getValue().getRecordType())));
        colRecordTitle.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getTitle() == null ? "" : c.getValue().getTitle()));
        colRecordServiceDate.setCellValueFactory(c -> {
            LocalDate d = c.getValue().getServiceDate();
            return new SimpleStringProperty(d == null ? "" : d.toString());
        });
        colRecordOdometer.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getOdometer()));
        colRecordTechnician.setCellValueFactory(c ->
            new SimpleStringProperty(resolveTechnicianName(c.getValue().getTechnicianId())));
        colRecordTotalCost.setCellValueFactory(c -> {
            BigDecimal cost = c.getValue().getTotalCost();
            return new SimpleStringProperty(cost == null ? "" : cost.toPlainString());
        });
        colRecordStatus.setCellValueFactory(c ->
            new SimpleStringProperty(recordStatusLabel(c.getValue().getRecordStatus())));
    }

    private void setupFilterListener() {
        cbFilterVehicle.setOnAction(e -> {
            Vehicle selected = cbFilterVehicle.getValue();
            if (selected == null) {
                loadTable(service.listAll());
            } else {
                loadTable(service.listByVehicle(selected.getVehicleId()));
            }
        });
    }

    private void loadTechnicianNames() {
        technicianNameMap.clear();
        userDAO.findAll().forEach(user -> {
            if (user.getUserId() != null) {
                technicianNameMap.put(user.getUserId(), resolveUserDisplayName(user));
            }
        });
    }

    private void setupRowSelectionListener() {
        tblRecord.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> populateForm(newVal));
    }

    private void loadTable(List<MaintenanceRecord> records) {
        tblRecord.setItems(FXCollections.observableArrayList(records));
    }

    private void populateForm(MaintenanceRecord record) {
        if (record == null) return;
        selectedRecordId = record.getRecordId();
        selectedTechnicianId = record.getTechnicianId();
        selectedCreatedBy = record.getCreatedBy();

        cbVehicle.getItems().stream()
            .filter(v -> v.getVehicleId() == record.getVehicleId())
            .findFirst().ifPresent(cbVehicle::setValue);

        cbRecordType.setValue(recordTypeLabel(record.getRecordType()));
        cbRecordStatus.setValue(recordStatusLabel(record.getRecordStatus()));
        txtTitle.setText(record.getTitle() == null ? "" : record.getTitle());
        dpServiceDate.setValue(record.getServiceDate());
        txtOdometer.setText(record.getOdometer() == null ? "" : String.valueOf(record.getOdometer()));
        txtTotalCost.setText(record.getTotalCost() == null ? "" : record.getTotalCost().toPlainString());
        txtServiceProvider.setText(record.getServiceProviderName() == null ? "" : record.getServiceProviderName());
        txtWorkSummary.setText(record.getWorkSummary() == null ? "" : record.getWorkSummary());
        txtNotes.setText(record.getNotes() == null ? "" : record.getNotes());
    }

    @FXML
    private void handleNewRecord() {
        handleClear();
    }

    @FXML
    private void handleSave() {
        try {
            MaintenanceRecord record = readFromForm();
            Long currentUserId = requireCurrentUserId();
            record.setTechnicianId(currentUserId);
            record.setCreatedBy(currentUserId);
            record.setUpdatedBy(currentUserId);
            service.save(record);
            loadTable(service.listAll());
            handleClear();
            showInfo("Đã lưu phiếu bảo dưỡng.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi lưu: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedRecordId == null) {
            showError("Vui lòng chọn phiếu cần cập nhật.");
            return;
        }
        try {
            MaintenanceRecord record = readFromForm();
            record.setRecordId(selectedRecordId);
            Long currentUserId = requireCurrentUserId();
            record.setTechnicianId(selectedTechnicianId != null ? selectedTechnicianId : currentUserId);
            record.setCreatedBy(selectedCreatedBy);
            record.setUpdatedBy(currentUserId);
            service.update(record);
            loadTable(service.listAll());
            handleClear();
            showInfo("Đã cập nhật phiếu bảo dưỡng.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        cbVehicle.setValue(null);
        cbRecordType.setValue(null);
        cbRecordStatus.setValue(null);
        txtTitle.clear();
        dpServiceDate.setValue(null);
        txtOdometer.clear();
        txtTotalCost.clear();
        txtServiceProvider.clear();
        txtWorkSummary.clear();
        txtNotes.clear();
        selectedRecordId = null;
        selectedTechnicianId = null;
        selectedCreatedBy = null;
        tblRecord.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        cbFilterVehicle.setValue(null);
        loadTable(service.listAll());
    }

    private MaintenanceRecord readFromForm() {
        Vehicle vehicle = cbVehicle.getValue();
        if (vehicle == null) throw new IllegalArgumentException("Vui lòng chọn xe.");

        String recordType = recordTypeValue(cbRecordType.getValue());
        if (recordType == null) throw new IllegalArgumentException("Vui lòng chọn loại phiếu.");

        LocalDate serviceDate = dpServiceDate.getValue();
        if (serviceDate == null) throw new IllegalArgumentException("Vui lòng nhập ngày thực hiện.");

        MaintenanceRecord record = new MaintenanceRecord();
        record.setVehicleId(vehicle.getVehicleId());
        record.setRecordType(recordType);
        record.setRecordStatus(cbRecordStatus.getValue() != null ? recordStatusValue(cbRecordStatus.getValue()) : "OPEN");
        record.setTitle(txtTitle.getText().isBlank() ? null : txtTitle.getText().trim());
        record.setServiceDate(serviceDate);
        record.setOdometer(parseOptionalInt(txtOdometer.getText(), "ODO"));
        record.setTotalCost(parseOptionalBigDecimal(txtTotalCost.getText(), "Tổng chi phí"));
        record.setServiceProviderName(txtServiceProvider.getText().isBlank() ? null : txtServiceProvider.getText().trim());
        record.setWorkSummary(txtWorkSummary.getText().isBlank() ? null : txtWorkSummary.getText().trim());
        record.setNotes(txtNotes.getText().isBlank() ? null : txtNotes.getText().trim());
        return record;
    }

    private Long requireCurrentUserId() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null || currentUser.getUserId() == null || currentUser.getUserId() <= 0) {
            throw new IllegalArgumentException("Không xác định được nhân viên kỹ thuật đang đăng nhập.");
        }
        return currentUser.getUserId();
    }

    private String resolveTechnicianName(Long technicianId) {
        if (technicianId == null) {
            return "";
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

    private Integer parseOptionalInt(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private BigDecimal parseOptionalBigDecimal(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
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

    private String recordTypeValue(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }
        return switch (label) {
            case "Bảo dưỡng định kỳ" -> "PREVENTIVE";
            case "Sửa chữa phát sinh" -> "CORRECTIVE";
            default -> label;
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
