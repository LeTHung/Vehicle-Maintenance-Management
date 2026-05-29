package controller.maintenance;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.entity.MaintenancePlan;

public class MaintenancePlanController {

    @FXML private ComboBox<String> cbFilterVehicle;
    @FXML private ComboBox<String> cbVehicle;
    @FXML private ComboBox<String> cbMaintenanceType;
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

    @FXML
    public void initialize() {
        // TODO: load vehicles, maintenance types, bind table columns, load plans
    }

    @FXML
    private void handleSave() {
        // TODO: validate form, create MaintenancePlan, call service.save()
    }

    @FXML
    private void handleUpdate() {
        // TODO: validate selectedPlanId, update record
    }

    @FXML
    private void handleDeactivate() {
        // TODO: call service.deactivate(selectedPlanId)
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
    }

    @FXML
    private void handleRefresh() {
        // TODO: reload plan list from service
    }
}
