package controller.vehicle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.dto.VehicleDocumentViewDTO;
import model.entity.DocumentType;
import model.entity.Vehicle;
import model.entity.VehicleDocument;
import service.VehicleDocumentService;
import util.DetailWindow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleDocumentController {

    private static final String ALL_OPTION = "Tất cả";

    private final VehicleDocumentService vehicleDocumentService = new VehicleDocumentService();
    private final Map<String, Vehicle> vehicleByLabel = new HashMap<>();
    private final Map<String, DocumentType> documentTypeByLabel = new HashMap<>();
    private Long selectedDocumentId;
    private Stage documentDetailStage;

    @FXML
    private VBox documentDetailPanel;
    @FXML
    private Label lblDocumentDetailTitle;

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
    private TextField txtDocumentKeyword;
    @FXML
    private ComboBox<String> cbStatusFilter;

    @FXML
    private TableView<VehicleDocumentViewDTO> tblDocument;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Long> colDocumentId;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, String> colLicensePlate;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, String> colDocumentTypeName;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, String> colDocumentNumber;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, String> colIssuerName;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Object> colIssueDate;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Object> colEffectiveDate;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Object> colExpiryDate;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Object> colFeeAmount;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Object> colPaidDate;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, String> colDocumentStatus;
    @FXML
    private TableColumn<VehicleDocumentViewDTO, Boolean> colIsCurrent;

    @FXML
    public void initialize() {
        configureOptions();
        configureTable();
        loadDocumentData();
    }

    private void configureOptions() {
        cbDocumentStatus.getItems().setAll(
                documentStatusLabel("VALID"),
                documentStatusLabel("EXPIRED"),
                documentStatusLabel("REPLACED"),
                documentStatusLabel("CANCELLED"));
        chkIsCurrent.setSelected(true);

        vehicleByLabel.clear();
        cbVehicle.getItems().clear();
        cbVehicleFilter.getItems().setAll(ALL_OPTION);
        for (Vehicle vehicle : vehicleDocumentService.listVehicles()) {
            String label = vehicleLabel(vehicle);
            vehicleByLabel.put(label, vehicle);
            cbVehicle.getItems().add(label);
            cbVehicleFilter.getItems().add(label);
        }
        cbVehicleFilter.setValue(ALL_OPTION);

        documentTypeByLabel.clear();
        cbDocumentType.getItems().clear();
        cbDocumentTypeFilter.getItems().setAll(ALL_OPTION);
        for (DocumentType type : vehicleDocumentService.listDocumentTypes()) {
            String label = typeLabel(type);
            documentTypeByLabel.put(label, type);
            cbDocumentType.getItems().add(label);
            cbDocumentTypeFilter.getItems().add(label);
        }
        cbDocumentTypeFilter.setValue(ALL_OPTION);

        cbStatusFilter.getItems().setAll(
                ALL_OPTION,
                documentStatusLabel("VALID"),
                documentStatusLabel("EXPIRED"),
                documentStatusLabel("REPLACED"),
                documentStatusLabel("CANCELLED"));
        cbStatusFilter.setValue(ALL_OPTION);
    }

    private void configureTable() {
        colDocumentId.setCellValueFactory(cell -> new SimpleLongProperty(cell.getValue().getDocumentId()).asObject());
        colLicensePlate
                .setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getLicensePlate())));
        colDocumentTypeName.setCellValueFactory(
                cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getDocumentTypeName())));
        colDocumentNumber.setCellValueFactory(
                cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getDocumentNumber())));
        colIssuerName
                .setCellValueFactory(cell -> new SimpleStringProperty(nullToEmpty(cell.getValue().getIssuerName())));
        colIssueDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getIssueDate()));
        colEffectiveDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getEffectiveDate()));
        colExpiryDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getExpiryDate()));
        colFeeAmount.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getFeeAmount()));
        colPaidDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPaidDate()));
        colDocumentStatus.setCellValueFactory(
                cell -> new SimpleStringProperty(documentStatusLabel(cell.getValue().getDocumentStatus())));
        colIsCurrent.setCellValueFactory(cell -> new SimpleBooleanProperty(cell.getValue().isCurrent()));

        tblDocument.setRowFactory(table -> {
            TableRow<VehicleDocumentViewDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    populateForm(row.getItem());
                    showDocumentDetailWindow();
                }
            });
            return row;
        });
    }

    @FXML
    private void handleNewDocument() {
        handleClearDocumentForm();
        lblDocumentDetailTitle.setText("Thêm giấy tờ mới");
        showDocumentDetailWindow();
    }

    @FXML
    private void handleFilterDocument() {
        Vehicle vehicle = vehicleByLabel.get(cbVehicleFilter.getValue());
        DocumentType type = documentTypeByLabel.get(cbDocumentTypeFilter.getValue());

        String keyword = txtDocumentKeyword == null ? "" : txtDocumentKeyword.getText();
        String status = cbStatusFilter == null ? ALL_OPTION : cbStatusFilter.getValue();

        List<VehicleDocumentViewDTO> documents = vehicleDocumentService.searchDocuments(keyword);

        documents = documents.stream()
                .filter(document -> vehicle == null || document.getVehicleId() == vehicle.getVehicleId())
                .filter(document -> type == null || document.getDocumentTypeId() == type.getDocumentTypeId())
                .filter(document -> status == null
                        || ALL_OPTION.equals(status)
                        || documentStatusValue(status).equalsIgnoreCase(nullToEmpty(document.getDocumentStatus())))
                .toList();

        tblDocument.setItems(FXCollections.observableArrayList(documents));
    }

    @FXML
    private void handleRefreshDocument() {
        configureOptions();
        if (txtDocumentKeyword != null) {
            txtDocumentKeyword.clear();
        }
        if (cbStatusFilter != null) {
            cbStatusFilter.setValue(ALL_OPTION);
        }
        handleClearDocumentForm();
        DetailWindow.hide(documentDetailStage);
        loadDocumentData();
    }

    @FXML
    private void handleSaveDocument() {
        try {
            VehicleDocument created = vehicleDocumentService.createDocument(readDocumentFromForm());
            loadDocumentData();
            selectDocument(created.getDocumentId());
            DetailWindow.hide(documentDetailStage);
            showInfo("Đã lưu giấy tờ xe.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleUpdateDocument() {
        if (selectedDocumentId == null) {
            showWarning("Vui lòng chọn giấy tờ cần cập nhật.");
            return;
        }

        try {
            VehicleDocument document = readDocumentFromForm();
            document.setDocumentId(selectedDocumentId);
            VehicleDocument updated = vehicleDocumentService.updateDocument(document);
            loadDocumentData();
            selectDocument(updated.getDocumentId());
            DetailWindow.hide(documentDetailStage);
            showInfo("Đã cập nhật giấy tờ xe.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleClearDocumentForm() {
        selectedDocumentId = null;
        tblDocument.getSelectionModel().clearSelection();
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

    private void loadDocumentData() {
        tblDocument.setItems(FXCollections.observableArrayList(vehicleDocumentService.listDocuments()));
    }

    private VehicleDocument readDocumentFromForm() {
        Vehicle vehicle = vehicleByLabel.get(cbVehicle.getValue());
        DocumentType type = documentTypeByLabel.get(cbDocumentType.getValue());

        VehicleDocument document = new VehicleDocument();
        document.setVehicleId(vehicle == null ? 0 : vehicle.getVehicleId());
        document.setDocumentTypeId(type == null ? 0 : type.getDocumentTypeId());
        document.setDocumentNumber(txtDocumentNumber.getText());
        document.setIssuerName(txtIssuerName.getText());
        document.setIssueDate(dpIssueDate.getValue());
        document.setEffectiveDate(dpEffectiveDate.getValue());
        document.setExpiryDate(dpExpiryDate.getValue());
        document.setFeeAmount(parseBigDecimal(txtFeeAmount.getText()));
        document.setPaidDate(dpPaidDate.getValue());
        document.setDocumentStatus(documentStatusValue(cbDocumentStatus.getValue()));
        document.setCurrent(chkIsCurrent.isSelected());
        document.setNote(txtNote.getText());
        return document;
    }

    private void populateForm(VehicleDocumentViewDTO document) {
        if (document == null) {
            return;
        }

        selectedDocumentId = document.getDocumentId();
        lblDocumentDetailTitle.setText("Chi tiết giấy tờ: " + nullToEmpty(document.getDocumentNumber()));
        cbVehicle.setValue(findVehicleLabel(document.getVehicleId()));
        cbDocumentType.setValue(findTypeLabel(document.getDocumentTypeId()));
        txtDocumentNumber.setText(nullToEmpty(document.getDocumentNumber()));
        txtIssuerName.setText(nullToEmpty(document.getIssuerName()));
        dpIssueDate.setValue(document.getIssueDate());
        dpEffectiveDate.setValue(document.getEffectiveDate());
        dpExpiryDate.setValue(document.getExpiryDate());
        txtFeeAmount.setText(document.getFeeAmount() == null ? "" : document.getFeeAmount().toPlainString());
        dpPaidDate.setValue(document.getPaidDate());
        cbDocumentStatus.setValue(documentStatusLabel(document.getDocumentStatus()));
        chkIsCurrent.setSelected(document.isCurrent());
        txtNote.setText(nullToEmpty(document.getNote()));
    }

    private void showDocumentDetailWindow() {
        documentDetailStage = DetailWindow.show(
                documentDetailStage,
                documentDetailPanel,
                tblDocument,
                "Thông tin giấy tờ xe",
                980,
                500);
    }

    private void selectDocument(long documentId) {
        for (VehicleDocumentViewDTO document : tblDocument.getItems()) {
            if (document.getDocumentId() == documentId) {
                tblDocument.getSelectionModel().select(document);
                tblDocument.scrollTo(document);
                return;
            }
        }
    }

    private String findVehicleLabel(long vehicleId) {
        for (Map.Entry<String, Vehicle> entry : vehicleByLabel.entrySet()) {
            if (entry.getValue().getVehicleId() == vehicleId) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String findTypeLabel(int typeId) {
        for (Map.Entry<String, DocumentType> entry : documentTypeByLabel.entrySet()) {
            if (entry.getValue().getDocumentTypeId() == typeId) {
                return entry.getKey();
            }
        }
        return null;
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Chi phí phải là số.");
        }
    }

    private String vehicleLabel(Vehicle vehicle) {
        return vehicle.getLicensePlate() + " - " + nullToEmpty(vehicle.getVehicleCode());
    }

    private String typeLabel(DocumentType type) {
        return type.getDocumentTypeId() + " - " + nullToEmpty(type.getDocumentTypeName());
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

    private String documentStatusLabel(String status) {
        if (status == null || status.isBlank()) {
            return "";
        }
        return switch (status) {
            case "VALID" -> "Còn hiệu lực";
            case "EXPIRED" -> "Hết hạn";
            case "REPLACED" -> "Đã thay thế";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String documentStatusValue(String label) {
        if (label == null || label.isBlank()) {
            return "";
        }
        return switch (label) {
            case "Còn hiệu lực" -> "VALID";
            case "Hết hạn" -> "EXPIRED";
            case "Đã thay thế" -> "REPLACED";
            case "Đã hủy" -> "CANCELLED";
            default -> label;
        };
    }
}
