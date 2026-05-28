package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.entity.Vehicle;
import service.VehicleService;

public class VehicleController {

    private final VehicleService vehicleService = new VehicleService();
    private Long selectedVehicleId;

    @FXML private TextField txtSearch;
    @FXML private TextField txtVehicleCode;
    @FXML private TextField txtLicensePlate;
    @FXML private ComboBox<String> cbVehicleType;
    @FXML private TextField txtBrand;
    @FXML private TextField txtModel;
    @FXML private TextField txtManufactureYear;
    @FXML private DatePicker dpPurchaseDate;
    @FXML private TextField txtChassisNumber;
    @FXML private TextField txtEngineNumber;
    @FXML private TextField txtColor;
    @FXML private TextField txtCurrentOdometer;
    @FXML private ComboBox<String> cbVehicleStatus;
    @FXML private TextField txtNotes;

    @FXML private TableView<Vehicle> tblVehicle;
    @FXML private TableColumn<Vehicle, Long> colVehicleId;
    @FXML private TableColumn<Vehicle, String> colVehicleCode;
    @FXML private TableColumn<Vehicle, String> colLicensePlate;
    @FXML private TableColumn<Vehicle, String> colVehicleType;
    @FXML private TableColumn<Vehicle, String> colBrand;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, Integer> colManufactureYear;
    @FXML private TableColumn<Vehicle, Object> colPurchaseDate;
    @FXML private TableColumn<Vehicle, String> colChassisNumber;
    @FXML private TableColumn<Vehicle, String> colEngineNumber;
    @FXML private TableColumn<Vehicle, String> colColor;
    @FXML private TableColumn<Vehicle, Integer> colCurrentOdometer;
    @FXML private TableColumn<Vehicle, String> colVehicleStatus;

    @FXML
    public void initialize() {
        cbVehicleType.getItems().setAll("Xe tai", "Xe khach", "Xe con", "Xe ban tai", "Xe chuyen dung");
        cbVehicleStatus.getItems().setAll("ACTIVE", "UNDER_MAINTENANCE", "INACTIVE", "DISPOSED");
        configureTable();
        loadVehicleData();
    }

    private void configureTable() {
        colVehicleId.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getVehicleId()).asObject());
        colVehicleCode.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getVehicleCode())));
        colLicensePlate.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getLicensePlate())));
        colVehicleType.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getVehicleType())));
        colBrand.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getBrand())));
        colModel.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getModel())));
        colManufactureYear.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getManufactureYear()));
        colPurchaseDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPurchaseDate()));
        colChassisNumber.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getChassisNumber())));
        colEngineNumber.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getEngineNumber())));
        colColor.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getColor())));
        colCurrentOdometer.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCurrentOdometer()).asObject());
        colVehicleStatus.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getVehicleStatus())));

        tblVehicle.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> populateForm(newValue));
    }

    @FXML
    private void handleSearchVehicle() {
        tblVehicle.setItems(FXCollections.observableArrayList(vehicleService.searchVehicles(txtSearch.getText())));
    }

    @FXML
    private void handleRefreshVehicle() {
        txtSearch.clear();
        handleClearVehicleForm();
        loadVehicleData();
    }

    @FXML
    private void handleSaveVehicle() {
        try {
            Vehicle created = vehicleService.createVehicle(readVehicleFromForm());
            loadVehicleData();
            selectVehicle(created.getVehicleId());
            showInfo("Đã lưu phương tiện.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateVehicle() {
        if (selectedVehicleId == null) {
            showWarning("Vui lòng chọn phương tiện cần cập nhật.");
            return;
        }

        try {
            Vehicle vehicle = readVehicleFromForm();
            vehicle.setVehicleId(selectedVehicleId);
            Vehicle updated = vehicleService.updateVehicle(vehicle);
            loadVehicleData();
            selectVehicle(updated.getVehicleId());
            showInfo("Đã cập nhật phương tiện.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleClearVehicleForm() {
        selectedVehicleId = null;
        tblVehicle.getSelectionModel().clearSelection();
        txtVehicleCode.clear();
        txtLicensePlate.clear();
        cbVehicleType.setValue(null);
        txtBrand.clear();
        txtModel.clear();
        txtManufactureYear.clear();
        dpPurchaseDate.setValue(null);
        txtChassisNumber.clear();
        txtEngineNumber.clear();
        txtColor.clear();
        txtCurrentOdometer.clear();
        cbVehicleStatus.setValue(null);
        txtNotes.clear();
    }

    private void loadVehicleData() {
        tblVehicle.setItems(FXCollections.observableArrayList(vehicleService.listVehicles()));
    }

    private Vehicle readVehicleFromForm() {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleCode(txtVehicleCode.getText());
        vehicle.setLicensePlate(txtLicensePlate.getText());
        vehicle.setVehicleType(cbVehicleType.getValue());
        vehicle.setBrand(txtBrand.getText());
        vehicle.setModel(txtModel.getText());
        vehicle.setManufactureYear(parseInteger(txtManufactureYear.getText(), "Năm sản xuất phải là số."));
        vehicle.setPurchaseDate(dpPurchaseDate.getValue());
        vehicle.setChassisNumber(txtChassisNumber.getText());
        vehicle.setEngineNumber(txtEngineNumber.getText());
        vehicle.setColor(txtColor.getText());
        vehicle.setCurrentOdometer(parseInteger(txtCurrentOdometer.getText(), "ODO phải là số."));
        vehicle.setVehicleStatus(cbVehicleStatus.getValue());
        vehicle.setNotes(txtNotes.getText());
        return vehicle;
    }

    private void populateForm(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }

        selectedVehicleId = vehicle.getVehicleId();
        txtVehicleCode.setText(nullToEmpty(vehicle.getVehicleCode()));
        txtLicensePlate.setText(nullToEmpty(vehicle.getLicensePlate()));
        cbVehicleType.setValue(vehicle.getVehicleType());
        txtBrand.setText(nullToEmpty(vehicle.getBrand()));
        txtModel.setText(nullToEmpty(vehicle.getModel()));
        txtManufactureYear.setText(vehicle.getManufactureYear() == null ? "" : String.valueOf(vehicle.getManufactureYear()));
        dpPurchaseDate.setValue(vehicle.getPurchaseDate());
        txtChassisNumber.setText(nullToEmpty(vehicle.getChassisNumber()));
        txtEngineNumber.setText(nullToEmpty(vehicle.getEngineNumber()));
        txtColor.setText(nullToEmpty(vehicle.getColor()));
        txtCurrentOdometer.setText(String.valueOf(vehicle.getCurrentOdometer()));
        cbVehicleStatus.setValue(vehicle.getVehicleStatus());
        txtNotes.setText(nullToEmpty(vehicle.getNotes()));
    }

    private void selectVehicle(long vehicleId) {
        for (Vehicle vehicle : tblVehicle.getItems()) {
            if (vehicle.getVehicleId() == vehicleId) {
                tblVehicle.getSelectionModel().select(vehicle);
                tblVehicle.scrollTo(vehicle);
                return;
            }
        }
    }

    private Integer parseInteger(String value, String message) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", message);
    }

    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Cảnh báo", message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Lỗi", message == null || message.isBlank() ? "Đã có lỗi xảy ra." : message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
