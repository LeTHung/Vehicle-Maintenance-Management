package controller.maintenance;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.entity.MaintenanceRecord;

public class MaintenanceRecordController {

    @FXML private ComboBox<String> cbFilterVehicle;
    @FXML private ComboBox<String> cbVehicle;
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
    @FXML private TableColumn<MaintenanceRecord, String> colRecordTotalCost;
    @FXML private TableColumn<MaintenanceRecord, String> colRecordStatus;

    private Long selectedRecordId;

    @FXML
    public void initialize() {
        cbRecordType.getItems().addAll("PREVENTIVE", "CORRECTIVE");
        cbRecordStatus.getItems().addAll("OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED");
        // TODO: load vehicles, bind table columns, load records
    }

    @FXML
    private void handleNewRecord() {
        handleClear();
    }

    @FXML
    private void handleSave() {
        // TODO: validate form, create MaintenanceRecord, call service.save()
    }

    @FXML
    private void handleUpdate() {
        // TODO: validate selectedRecordId, update record
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
    }

    @FXML
    private void handleRefresh() {
        // TODO: reload record list from service
    }
}
