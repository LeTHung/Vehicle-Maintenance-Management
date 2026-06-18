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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.entity.MaintenancePlan;
import model.entity.MaintenanceType;
import model.entity.Vehicle;
import service.MaintenancePlanService;
import util.DetailWindow;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaintenancePlanController {

    private final MaintenancePlanService service = new MaintenancePlanService();

    @FXML private VBox planDetailPanel;
    @FXML private Label lblPlanDetailTitle;

    @FXML private ComboBox<Vehicle> cbFilterVehicle;
    @FXML private ComboBox<Vehicle> cbVehicle;
    @FXML private ComboBox<MaintenanceType> cbMaintenanceType;
    @FXML private TextField txtIntervalDays;
    @FXML private TextField txtIntervalKm;
    @FXML private DatePicker dpLastServiceDate;
    @FXML private TextField txtLastServiceOdometer;
    @FXML private DatePicker dpNextDueDate;
    @FXML private TextField txtNextDueOdometer;
    @FXML private TextField txtAlertBeforeDays;
    @FXML private TextField txtAlertBeforeKm;
    @FXML private TextField txtNotes;

    @FXML private TableView<MaintenancePlan> tblPlan;
    @FXML private TableColumn<MaintenancePlan, Long> colPlanId;
    @FXML private TableColumn<MaintenancePlan, String> colPlanLicensePlate;
    @FXML private TableColumn<MaintenancePlan, String> colPlanMaintenanceType;
    @FXML private TableColumn<MaintenancePlan, Integer> colPlanIntervalDays;
    @FXML private TableColumn<MaintenancePlan, Integer> colPlanIntervalKm;
    @FXML private TableColumn<MaintenancePlan, String> colPlanNextDueDate;
    @FXML private TableColumn<MaintenancePlan, Integer> colPlanNextDueOdometer;
    @FXML private TableColumn<MaintenancePlan, String> colPlanActive;

    private Long selectedPlanId;
    private boolean selectedPlanActive = true;
    private Stage planDetailStage;
    private final Map<Long, String> vehicleNameMap = new HashMap<>();
    private final Map<Integer, String> typeNameMap = new HashMap<>();

    @FXML
    public void initialize() {
        setupVehicleComboBoxes();
        setupTypeComboBox();
        configureTable();
        setupFilterListener();
        setupRowSelectionListener();
        loadTable(service.listPlans());
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

    private void setupTypeComboBox() {
        List<MaintenanceType> types = service.listActiveTypes();
        types.forEach(t -> typeNameMap.put(t.getMaintenanceTypeId(), t.getMaintenanceName()));

        cbMaintenanceType.setItems(FXCollections.observableArrayList(types));
        cbMaintenanceType.setConverter(new StringConverter<>() {
            public String toString(MaintenanceType t) { return t == null ? "" : t.getMaintenanceName(); }
            public MaintenanceType fromString(String s) { return null; }
        });
    }

    private void configureTable() {
        colPlanId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getPlanId()).asObject());
        colPlanLicensePlate.setCellValueFactory(c ->
            new SimpleStringProperty(vehicleNameMap.getOrDefault(c.getValue().getVehicleId(), "")));
        colPlanMaintenanceType.setCellValueFactory(c ->
            new SimpleStringProperty(typeNameMap.getOrDefault(c.getValue().getMaintenanceTypeId(), "")));
        colPlanIntervalDays.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getIntervalDays()));
        colPlanIntervalKm.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getIntervalKm()));
        colPlanNextDueDate.setCellValueFactory(c -> {
            LocalDate d = c.getValue().getNextDueDate();
            return new SimpleStringProperty(d == null ? "" : d.toString());
        });
        colPlanNextDueOdometer.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getNextDueOdometer()));
        colPlanActive.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().isActive() ? "Hoạt động" : "Đã tắt"));
    }

    private void setupFilterListener() {
        cbFilterVehicle.setOnAction(e -> {
            Vehicle selected = cbFilterVehicle.getValue();
            if (selected == null) {
                loadTable(service.listPlans());
            } else {
                loadTable(service.listByVehicle(selected.getVehicleId()));
            }
        });
    }

    private void setupRowSelectionListener() {
        tblPlan.setRowFactory(table -> {
            TableRow<MaintenancePlan> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    populateForm(row.getItem());
                    showPlanDetailWindow();
                }
            });
            return row;
        });
    }

    private void loadTable(List<MaintenancePlan> plans) {
        tblPlan.setItems(FXCollections.observableArrayList(plans));
    }

    private void populateForm(MaintenancePlan plan) {
        if (plan == null) return;
        selectedPlanId = plan.getPlanId();
        selectedPlanActive = plan.isActive();
        lblPlanDetailTitle.setText("Chi tiết kế hoạch: " + vehicleNameMap.getOrDefault(plan.getVehicleId(), ""));

        cbVehicle.getItems().stream()
            .filter(v -> v.getVehicleId() == plan.getVehicleId())
            .findFirst().ifPresent(cbVehicle::setValue);

        cbMaintenanceType.getItems().stream()
            .filter(t -> t.getMaintenanceTypeId() == plan.getMaintenanceTypeId())
            .findFirst().ifPresent(cbMaintenanceType::setValue);

        txtIntervalDays.setText(plan.getIntervalDays() == null ? "" : String.valueOf(plan.getIntervalDays()));
        txtIntervalKm.setText(plan.getIntervalKm() == null ? "" : String.valueOf(plan.getIntervalKm()));
        dpLastServiceDate.setValue(plan.getLastServiceDate());
        txtLastServiceOdometer.setText(plan.getLastServiceOdometer() == null ? "" : String.valueOf(plan.getLastServiceOdometer()));
        dpNextDueDate.setValue(plan.getNextDueDate());
        txtNextDueOdometer.setText(plan.getNextDueOdometer() == null ? "" : String.valueOf(plan.getNextDueOdometer()));
        txtAlertBeforeDays.setText(plan.getAlertBeforeDays() == null ? "" : String.valueOf(plan.getAlertBeforeDays()));
        txtAlertBeforeKm.setText(plan.getAlertBeforeKm() == null ? "" : String.valueOf(plan.getAlertBeforeKm()));
        txtNotes.setText(plan.getNotes() == null ? "" : plan.getNotes());
    }

    @FXML
    private void handleNewPlan() {
        handleClear();
        lblPlanDetailTitle.setText("Thêm kế hoạch bảo dưỡng mới");
        showPlanDetailWindow();
    }

    @FXML
    private void handleSave() {
        try {
            MaintenancePlan plan = readFromForm();
            service.save(plan);
            cbFilterVehicle.setValue(null);
            loadTable(service.listPlans());
            handleClear();
            DetailWindow.hide(planDetailStage);
            showInfo("Đã lưu kế hoạch bảo dưỡng.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi lưu: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedPlanId == null) {
            showError("Vui lòng chọn kế hoạch cần cập nhật.");
            return;
        }
        try {
            MaintenancePlan plan = readFromForm();
            plan.setPlanId(selectedPlanId);
            service.update(plan);
            cbFilterVehicle.setValue(null);
            loadTable(service.listPlans());
            handleClear();
            DetailWindow.hide(planDetailStage);
            showInfo("Đã cập nhật kế hoạch bảo dưỡng.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeactivate() {
        if (selectedPlanId == null) {
            showError("Vui lòng chọn kế hoạch cần hủy kích hoạt.");
            return;
        }
        if (!service.deactivate(selectedPlanId)) {
            showError("Không thể hủy kích hoạt kế hoạch. Vui lòng thử lại.");
            return;
        }
        cbFilterVehicle.setValue(null);
        loadTable(service.listPlans());
        handleClear();
        DetailWindow.hide(planDetailStage);
        showInfo("Đã hủy kích hoạt kế hoạch.");
    }

    @FXML
    private void handleClear() {
        cbVehicle.setValue(null);
        cbMaintenanceType.setValue(null);
        txtIntervalDays.clear();
        txtIntervalKm.clear();
        dpLastServiceDate.setValue(null);
        txtLastServiceOdometer.clear();
        dpNextDueDate.setValue(null);
        txtNextDueOdometer.clear();
        txtAlertBeforeDays.clear();
        txtAlertBeforeKm.clear();
        txtNotes.clear();
        selectedPlanId = null;
        selectedPlanActive = true;
        tblPlan.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        cbFilterVehicle.setValue(null);
        handleClear();
        DetailWindow.hide(planDetailStage);
        loadTable(service.listPlans());
    }

    private void showPlanDetailWindow() {
        planDetailStage = DetailWindow.show(
                planDetailStage,
                planDetailPanel,
                tblPlan,
                "Thông tin kế hoạch bảo dưỡng",
                900,
                520);
    }

    private MaintenancePlan readFromForm() {
        Vehicle vehicle = cbVehicle.getValue();
        if (vehicle == null) throw new IllegalArgumentException("Vui lòng chọn xe.");

        MaintenanceType type = cbMaintenanceType.getValue();
        if (type == null) throw new IllegalArgumentException("Vui lòng chọn loại bảo dưỡng.");

        MaintenancePlan plan = new MaintenancePlan();
        plan.setVehicleId(vehicle.getVehicleId());
        plan.setMaintenanceTypeId(type.getMaintenanceTypeId());
        plan.setIntervalDays(parseOptionalInt(txtIntervalDays.getText(), "Chu kỳ ngày"));
        plan.setIntervalKm(parseOptionalInt(txtIntervalKm.getText(), "Chu kỳ km"));
        plan.setLastServiceDate(dpLastServiceDate.getValue());
        plan.setLastServiceOdometer(parseOptionalInt(txtLastServiceOdometer.getText(), "ODO bảo dưỡng cuối"));
        plan.setNextDueDate(dpNextDueDate.getValue());
        plan.setNextDueOdometer(parseOptionalInt(txtNextDueOdometer.getText(), "ODO đến hạn"));
        plan.setAlertBeforeDays(parseOptionalInt(txtAlertBeforeDays.getText(), "Cảnh báo trước (ngày)"));
        plan.setAlertBeforeKm(parseOptionalInt(txtAlertBeforeKm.getText(), "Cảnh báo trước (km)"));
        plan.setNotes(txtNotes.getText().isBlank() ? null : txtNotes.getText().trim());
        plan.setActive(selectedPlanActive);
        return plan;
    }

    private Integer parseOptionalInt(String text, String fieldName) {
        if (text == null || text.isBlank()) return null;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
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
}
