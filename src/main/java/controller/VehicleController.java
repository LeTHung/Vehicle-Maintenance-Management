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
    private TextField txtLicensePlate;
    @FXML
    private ComboBox<String> cbVehicleType;
    @FXML
    private TextField txtChassisNumber;
    @FXML
    private TextField txtEngineNumber;
    @FXML
    private TextField txtManufactureYear;
    @FXML
    private DatePicker dpPurchaseDate;
    @FXML
    private ComboBox<String> cbStatus;
    @FXML
    private TextField txtCurrentOdo;

    @FXML
    private TableView<?> tblVehicle;
    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colLicensePlate;
    @FXML
    private TableColumn<?, ?> colVehicleType;
    @FXML
    private TableColumn<?, ?> colChassisNumber;
    @FXML
    private TableColumn<?, ?> colEngineNumber;
    @FXML
    private TableColumn<?, ?> colManufactureYear;
    @FXML
    private TableColumn<?, ?> colPurchaseDate;
    @FXML
    private TableColumn<?, ?> colStatus;
    @FXML
    private TableColumn<?, ?> colCurrentOdo;

    @FXML
    public void initialize() {
        cbVehicleType.getItems().setAll("Xe tải", "Xe khách", "Xe con", "Xe chuyên dụng");
        cbStatus.getItems().setAll("Đang hoạt động", "Đang bảo dưỡng", "Ngừng hoạt động");
    }

    @FXML
    private void handleSearchVehicle() {
        System.out.println("Tìm phương tiện: " + txtSearch.getText());
    }

    @FXML
    private void handleRefreshVehicle() {
        txtSearch.clear();
        System.out.println("Làm mới danh sách phương tiện");
    }

    @FXML
    private void handleSaveVehicle() {
        System.out.println("Lưu phương tiện mới");
    }

    @FXML
    private void handleUpdateVehicle() {
        System.out.println("Cập nhật phương tiện");
    }

    @FXML
    private void handleClearVehicleForm() {
        txtLicensePlate.clear();
        cbVehicleType.setValue(null);
        txtChassisNumber.clear();
        txtEngineNumber.clear();
        txtManufactureYear.clear();
        dpPurchaseDate.setValue(null);
        cbStatus.setValue(null);
        txtCurrentOdo.clear();
    }
}
