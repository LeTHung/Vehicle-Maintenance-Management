package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DocumentAlertController {

    @FXML
    private ComboBox<String> cbAlertType;
    @FXML
    private TextField txtSearch;

    @FXML
    private Label lblExpiredCount;
    @FXML
    private Label lblWarningCount;
    @FXML
    private Label lblTotalAlertCount;

    @FXML
    private TableView<?> tblAlert;
    @FXML
    private TableColumn<?, ?> colDocumentId;
    @FXML
    private TableColumn<?, ?> colVehicleId;
    @FXML
    private TableColumn<?, ?> colLicensePlate;
    @FXML
    private TableColumn<?, ?> colVehicleType;
    @FXML
    private TableColumn<?, ?> colDocumentTypeName;
    @FXML
    private TableColumn<?, ?> colDocumentNumber;
    @FXML
    private TableColumn<?, ?> colIssuerName;
    @FXML
    private TableColumn<?, ?> colExpiryDate;
    @FXML
    private TableColumn<?, ?> colDaysToExpiry;
    @FXML
    private TableColumn<?, ?> colDueStatus;

    @FXML
    public void initialize() {
        initComboBoxes();
        resetSummary();
    }

    private void initComboBoxes() {
        cbAlertType.getItems().setAll(
                "Tất cả",
                "OVERDUE",
                "COMING_DUE",
                "NORMAL");
    }

    private void resetSummary() {
        lblExpiredCount.setText("0");
        lblWarningCount.setText("0");
        lblTotalAlertCount.setText("0");
    }

    @FXML
    private void handleSearchAlert() {
        String keyword = txtSearch.getText();
        String alertType = cbAlertType.getValue();

        System.out.println("Tìm cảnh báo: " + keyword);
        System.out.println("Loại cảnh báo: " + alertType);

        // Ngày sau sẽ nối DocumentAlertDAO.search()
    }

    @FXML
    private void handleRefreshAlert() {
        txtSearch.clear();
        cbAlertType.setValue(null);
        resetSummary();

        System.out.println("Làm mới cảnh báo giấy tờ");

        // Ngày sau sẽ gọi loadAlertData()
    }
}