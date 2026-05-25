package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class VehicleDocumentController {

    @FXML
    private ComboBox<String> cbVehicleFilter;
    @FXML
    private ComboBox<String> cbDocumentTypeFilter;

    @FXML
    private ComboBox<String> cbVehicle;
    @FXML
    private ComboBox<String> cbDocumentType;
    @FXML
    private TextField txtDocumentNumber;
    @FXML
    private TextField txtIssuerName;
    @FXML
    private DatePicker dpIssueDate;
    @FXML
    private DatePicker dpEffectiveDate;
    @FXML
    private DatePicker dpExpiryDate;
    @FXML
    private TextField txtFeeAmount;
    @FXML
    private DatePicker dpPaidDate;
    @FXML
    private ComboBox<String> cbDocumentStatus;
    @FXML
    private CheckBox chkIsCurrent;
    @FXML
    private TextField txtNote;

    @FXML
    private TableView<?> tblDocument;
    @FXML
    private TableColumn<?, ?> colDocumentId;
    @FXML
    private TableColumn<?, ?> colLicensePlate;
    @FXML
    private TableColumn<?, ?> colDocumentTypeName;
    @FXML
    private TableColumn<?, ?> colDocumentNumber;
    @FXML
    private TableColumn<?, ?> colIssuerName;
    @FXML
    private TableColumn<?, ?> colIssueDate;
    @FXML
    private TableColumn<?, ?> colEffectiveDate;
    @FXML
    private TableColumn<?, ?> colExpiryDate;
    @FXML
    private TableColumn<?, ?> colFeeAmount;
    @FXML
    private TableColumn<?, ?> colPaidDate;
    @FXML
    private TableColumn<?, ?> colDocumentStatus;
    @FXML
    private TableColumn<?, ?> colIsCurrent;

    @FXML
    public void initialize() {
        initComboBoxes();
    }

    private void initComboBoxes() {
        cbDocumentType.getItems().setAll(
                "Đăng kiểm",
                "Bảo hiểm",
                "Phí đường bộ");

        cbDocumentTypeFilter.getItems().setAll(
                "Tất cả",
                "Đăng kiểm",
                "Bảo hiểm",
                "Phí đường bộ");

        cbDocumentStatus.getItems().setAll(
                "VALID",
                "EXPIRED",
                "REPLACED",
                "CANCELLED");

        // Tạm thời dùng dữ liệu mẫu.
        // Ngày 4-5 sẽ lấy từ VehicleDAO.
        cbVehicle.getItems().setAll(
                "51A-12345",
                "51B-67890",
                "51C-24680");

        cbVehicleFilter.getItems().setAll(
                "Tất cả",
                "51A-12345",
                "51B-67890",
                "51C-24680");

        chkIsCurrent.setSelected(true);
    }

    @FXML
    private void handleFilterDocument() {
        System.out.println("Lọc giấy tờ xe");

        // Ngày sau sẽ nối VehicleDocumentDAO.filter()
    }

    @FXML
    private void handleRefreshDocument() {
        cbVehicleFilter.setValue(null);
        cbDocumentTypeFilter.setValue(null);
        handleClearDocumentForm();

        System.out.println("Làm mới giấy tờ xe");

        // Ngày sau sẽ gọi loadDocumentData()
    }

    @FXML
    private void handleSaveDocument() {
        System.out.println("Lưu giấy tờ xe");

        // Ngày sau sẽ lấy dữ liệu từ form -> VehicleDocument -> service.save()
    }

    @FXML
    private void handleUpdateDocument() {
        System.out.println("Cập nhật giấy tờ xe");

        // Ngày sau sẽ cập nhật giấy tờ đang chọn
    }

    @FXML
    private void handleClearDocumentForm() {
        cbVehicle.setValue(null);
        cbDocumentType.setValue(null);
        txtDocumentNumber.clear();
        txtIssuerName.clear();
        dpIssueDate.setValue(null);
        dpEffectiveDate.setValue(null);
        dpExpiryDate.setValue(null);
        txtFeeAmount.clear();
        dpPaidDate.setValue(null);
        cbDocumentStatus.setValue(null);
        chkIsCurrent.setSelected(true);
        txtNote.clear();
    }
}