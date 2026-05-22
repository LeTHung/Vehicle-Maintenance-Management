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
    private TableColumn<?, ?> colVehiclePlate;
    @FXML
    private TableColumn<?, ?> colDocumentType;
    @FXML
    private TableColumn<?, ?> colIssueDate;
    @FXML
    private TableColumn<?, ?> colExpiryDate;
    @FXML
    private TableColumn<?, ?> colDaysLeft;
    @FXML
    private TableColumn<?, ?> colAlertStatus;
    @FXML
    private TableColumn<?, ?> colNote;

    @FXML
    public void initialize() {
        cbAlertType.getItems().setAll("Tất cả", "Đã hết hạn", "Sắp hết hạn trong 15 ngày");
        lblExpiredCount.setText("0");
        lblWarningCount.setText("0");
        lblTotalAlertCount.setText("0");
    }

    @FXML
    private void handleSearchAlert() {
        System.out.println("Tìm cảnh báo: " + txtSearch.getText());
    }

    @FXML
    private void handleRefreshAlert() {
        txtSearch.clear();
        cbAlertType.setValue(null);
        System.out.println("Làm mới cảnh báo giấy tờ");
    }
}
