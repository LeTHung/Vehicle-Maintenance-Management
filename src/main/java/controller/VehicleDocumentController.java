package controller;

import javafx.fxml.FXML;
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
    private DatePicker dpIssueDate;
    @FXML
    private DatePicker dpExpiryDate;
    @FXML
    private TextField txtNote;

    @FXML
    private TableView<?> tblDocument;
    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colVehiclePlate;
    @FXML
    private TableColumn<?, ?> colDocumentType;
    @FXML
    private TableColumn<?, ?> colIssueDate;
    @FXML
    private TableColumn<?, ?> colExpiryDate;
    @FXML
    private TableColumn<?, ?> colDocumentStatus;
    @FXML
    private TableColumn<?, ?> colNote;

    @FXML
    public void initialize() {
        cbDocumentType.getItems().setAll("Đăng kiểm", "Bảo hiểm", "Phí đường bộ");
        cbDocumentTypeFilter.getItems().setAll("Tất cả", "Đăng kiểm", "Bảo hiểm", "Phí đường bộ");

        // Tạm thời dùng dữ liệu mẫu. Ngày sau sẽ lấy từ VehicleDAO.
        cbVehicle.getItems().setAll("51A-12345", "51B-67890");
        cbVehicleFilter.getItems().setAll("Tất cả", "51A-12345", "51B-67890");
    }

    @FXML
    private void handleFilterDocument() {
        System.out.println("Lọc giấy tờ xe");
    }

    @FXML
    private void handleRefreshDocument() {
        System.out.println("Làm mới giấy tờ xe");
    }

    @FXML
    private void handleSaveDocument() {
        System.out.println("Lưu giấy tờ xe");
    }

    @FXML
    private void handleUpdateDocument() {
        System.out.println("Cập nhật giấy tờ xe");
    }

    @FXML
    private void handleClearDocumentForm() {
        cbVehicle.setValue(null);
        cbDocumentType.setValue(null);
        dpIssueDate.setValue(null);
        dpExpiryDate.setValue(null);
        txtNote.clear();
    }
}
