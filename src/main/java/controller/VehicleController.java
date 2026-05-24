package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class VehicleController {

    @FXML
    private TextField txtSearch;

    @FXML
    private TextField txtVehicleCode;
    @FXML
    private TextField txtLicensePlate;
    @FXML
    private ComboBox<String> cbVehicleType;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtManufactureYear;
    @FXML
    private DatePicker dpPurchaseDate;
    @FXML
    private TextField txtChassisNumber;
    @FXML
    private TextField txtEngineNumber;
    @FXML
    private TextField txtColor;
    @FXML
    private TextField txtCurrentOdometer;
    @FXML
    private ComboBox<String> cbVehicleStatus;
    @FXML
    private TextField txtNotes;

    @FXML
    private TableView<?> tblVehicle;
    @FXML
    private TableColumn<?, ?> colVehicleId;
    @FXML
    private TableColumn<?, ?> colVehicleCode;
    @FXML
    private TableColumn<?, ?> colLicensePlate;
    @FXML
    private TableColumn<?, ?> colVehicleType;
    @FXML
    private TableColumn<?, ?> colBrand;
    @FXML
    private TableColumn<?, ?> colModel;
    @FXML
    private TableColumn<?, ?> colManufactureYear;
    @FXML
    private TableColumn<?, ?> colPurchaseDate;
    @FXML
    private TableColumn<?, ?> colChassisNumber;
    @FXML
    private TableColumn<?, ?> colEngineNumber;
    @FXML
    private TableColumn<?, ?> colColor;
    @FXML
    private TableColumn<?, ?> colCurrentOdometer;
    @FXML
    private TableColumn<?, ?> colVehicleStatus;

    @FXML
    public void initialize() {
        initComboBoxes();
    }

    private void initComboBoxes() {
        cbVehicleType.getItems().setAll(
                "Xe tải",
                "Xe khách",
                "Xe con",
                "Xe bán tải",
                "Xe chuyên dụng");

        cbVehicleStatus.getItems().setAll(
                "ACTIVE",
                "UNDER_MAINTENANCE",
                "INACTIVE");
    }

    @FXML
    private void handleSearchVehicle() {
        String keyword = txtSearch.getText();
        System.out.println("Tìm phương tiện: " + keyword);

        // Ngày 4 sẽ nối VehicleDAO.search(keyword)
    }

    @FXML
    private void handleRefreshVehicle() {
        txtSearch.clear();
        handleClearVehicleForm();

        System.out.println("Làm mới danh sách phương tiện");

        // Ngày 4 sẽ gọi loadVehicleData()
    }

    @FXML
    private void handleSaveVehicle() {
        System.out.println("Lưu phương tiện mới");

        // Ngày sau sẽ lấy dữ liệu từ form -> Vehicle -> VehicleService.save()
    }

    @FXML
    private void handleUpdateVehicle() {
        System.out.println("Cập nhật phương tiện");

        // Ngày sau sẽ lấy dòng đang chọn -> cập nhật Vehicle
    }

    @FXML
    private void handleClearVehicleForm() {
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
}