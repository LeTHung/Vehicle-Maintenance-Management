package controller.maintenance;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import model.entity.MaintenanceItemDetail;
import model.entity.MaintenanceRecord;
import model.entity.Vehicle;
import service.MaintenanceRecordService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class MaintenanceHistoryController {

    private final MaintenanceRecordService service = new MaintenanceRecordService();

    @FXML private ComboBox<Vehicle> cbVehicle;
    @FXML private Label lblRecordCount;

    @FXML private TableView<MaintenanceRecord> tblHistory;
    @FXML private TableColumn<MaintenanceRecord, Long>   colHistoryId;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryType;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryTitle;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryDate;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryOdo;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryCost;
    @FXML private TableColumn<MaintenanceRecord, String> colHistoryStatus;

    @FXML private TableView<MaintenanceItemDetail> tblItems;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemType;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemDesc;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemQty;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnit;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemUnitCost;
    @FXML private TableColumn<MaintenanceItemDetail, String> colItemLineTotal;

    @FXML
    public void initialize() {
        List<Vehicle> vehicles = service.listVehicles();
        cbVehicle.setItems(FXCollections.observableArrayList(vehicles));
        cbVehicle.setConverter(new StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getLicensePlate(); }
            public Vehicle fromString(String s) { return null; }
        });
        cbVehicle.setOnAction(e -> loadHistory());

        configureHistoryTable();
        configureItemTable();

        tblHistory.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> loadItems(newVal));
    }

    private void configureHistoryTable() {
        colHistoryId.setCellValueFactory(c ->
            new SimpleLongProperty(c.getValue().getRecordId()).asObject());
        colHistoryType.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getRecordType() != null ? c.getValue().getRecordType() : ""));
        colHistoryTitle.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getTitle() != null ? c.getValue().getTitle() : ""));
        colHistoryDate.setCellValueFactory(c -> {
            LocalDate d = c.getValue().getServiceDate();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });
        colHistoryOdo.setCellValueFactory(c -> {
            Integer odo = c.getValue().getOdometer();
            return new SimpleStringProperty(odo != null ? String.format("%,d km", odo) : "");
        });
        colHistoryCost.setCellValueFactory(c ->
            new SimpleStringProperty(formatMoney(c.getValue().getTotalCost()) + " VNĐ"));
        colHistoryStatus.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getRecordStatus() != null ? c.getValue().getRecordStatus() : ""));
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

    @FXML
    private void handleRefresh() {
        loadHistory();
    }

    private void loadHistory() {
        Vehicle v = cbVehicle.getValue();
        if (v == null) {
            tblHistory.getItems().clear();
            tblItems.getItems().clear();
            lblRecordCount.setText("Chưa chọn xe");
            return;
        }
        List<MaintenanceRecord> records = service.listByVehicle(v.getVehicleId());
        tblHistory.setItems(FXCollections.observableArrayList(records));
        tblItems.getItems().clear();
        lblRecordCount.setText(records.size() + " phiếu");
    }

    private void loadItems(MaintenanceRecord record) {
        if (record == null) { tblItems.getItems().clear(); return; }
        List<MaintenanceItemDetail> items = service.listItems(record.getRecordId());
        tblItems.setItems(FXCollections.observableArrayList(items));
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0";
        return NumberFormat.getNumberInstance(Locale.of("vi", "VN")).format(value);
    }
}
