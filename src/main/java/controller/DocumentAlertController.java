package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.dto.DocumentAlertDTO;
import service.DocumentAlertService;

public class DocumentAlertController {

    private final DocumentAlertService documentAlertService = new DocumentAlertService();

    @FXML private ComboBox<String> cbAlertType;
    @FXML private TextField txtSearch;
    @FXML private Label lblExpiredCount;
    @FXML private Label lblWarningCount;
    @FXML private Label lblTotalAlertCount;

    @FXML private TableView<DocumentAlertDTO> tblAlert;
    @FXML private TableColumn<DocumentAlertDTO, Long> colDocumentId;
    @FXML private TableColumn<DocumentAlertDTO, Long> colVehicleId;
    @FXML private TableColumn<DocumentAlertDTO, String> colLicensePlate;
    @FXML private TableColumn<DocumentAlertDTO, String> colVehicleType;
    @FXML private TableColumn<DocumentAlertDTO, String> colDocumentTypeName;
    @FXML private TableColumn<DocumentAlertDTO, String> colDocumentNumber;
    @FXML private TableColumn<DocumentAlertDTO, String> colIssuerName;
    @FXML private TableColumn<DocumentAlertDTO, Object> colExpiryDate;
    @FXML private TableColumn<DocumentAlertDTO, Integer> colDaysToExpiry;
    @FXML private TableColumn<DocumentAlertDTO, String> colDueStatus;

    @FXML
    public void initialize() {
        cbAlertType.getItems().setAll("Tất cả", "OVERDUE", "COMING_DUE", "NORMAL");
        cbAlertType.setValue("Tất cả");
        configureTable();
        loadAlertData();
    }

    private void configureTable() {
        colDocumentId.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getDocumentId()).asObject());
        colVehicleId.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getVehicleId()).asObject());
        colLicensePlate.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getLicensePlate())));
        colVehicleType.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getVehicleType())));
        colDocumentTypeName.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getDocumentTypeName())));
        colDocumentNumber.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getDocumentNumber())));
        colIssuerName.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getIssuerName())));
        colExpiryDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getExpiryDate()));
        colDaysToExpiry.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getDaysToExpiry()).asObject());
        colDueStatus.setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getDueStatus())));
    }

    @FXML
    private void handleSearchAlert() {
        tblAlert.setItems(FXCollections.observableArrayList(
                documentAlertService.searchAlerts(txtSearch.getText(), cbAlertType.getValue())));
        updateSummary();
    }

    @FXML
    private void handleRefreshAlert() {
        txtSearch.clear();
        cbAlertType.setValue("Tất cả");
        loadAlertData();
    }

    private void loadAlertData() {
        tblAlert.setItems(FXCollections.observableArrayList(documentAlertService.listAlerts()));
        updateSummary();
    }

    private void updateSummary() {
        int expired = documentAlertService.countExpired();
        int comingDue = documentAlertService.countComingDue();
        lblExpiredCount.setText(String.valueOf(expired));
        lblWarningCount.setText(String.valueOf(comingDue));
        lblTotalAlertCount.setText(String.valueOf(expired + comingDue));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
