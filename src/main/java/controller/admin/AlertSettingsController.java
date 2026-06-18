package controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entity.AlertSettings;
import service.AlertSettingsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertSettingsController {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AlertSettingsService alertSettingsService = new AlertSettingsService();

    @FXML private TextField txtDocumentAlertDays;
    @FXML private TextField txtMaintenanceAlertDays;
    @FXML private TextField txtMaintenanceAlertKm;
    @FXML private CheckBox chkActive;
    @FXML private Label lblUpdatedAt;
    @FXML private Label lblStatus;
    @FXML private Label lblError;

    @FXML
    public void initialize() {
        hideError();
        clearStatus();
        loadSettings();
    }

    @FXML
    private void onSaveClick() {
        try {
            hideError();
            AlertSettings settings = alertSettingsService.updateSettings(
                    parseInt(txtDocumentAlertDays, "Số ngày cảnh báo giấy tờ"),
                    parseInt(txtMaintenanceAlertDays, "Số ngày cảnh báo bảo dưỡng"),
                    parseInt(txtMaintenanceAlertKm, "Số km cảnh báo bảo dưỡng"),
                    chkActive.isSelected());

            renderSettings(settings);
            showStatus("Đã lưu cấu hình cảnh báo.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void onRefreshClick() {
        loadSettings();
    }

    @FXML
    private void onResetDefaultsClick() {
        txtDocumentAlertDays.setText("15");
        txtMaintenanceAlertDays.setText("7");
        txtMaintenanceAlertKm.setText("500");
        chkActive.setSelected(true);
        hideError();
        clearStatus();
    }

    private void loadSettings() {
        try {
            hideError();
            renderSettings(alertSettingsService.getSettings());
            showStatus("Đã tải cấu hình hiện tại.");
        } catch (RuntimeException e) {
            showError("Không thể tải cấu hình cảnh báo. " + nullToEmpty(e.getMessage()));
        }
    }

    private void renderSettings(AlertSettings settings) {
        txtDocumentAlertDays.setText(String.valueOf(settings.getDocumentAlertDays()));
        txtMaintenanceAlertDays.setText(String.valueOf(settings.getMaintenanceAlertDays()));
        txtMaintenanceAlertKm.setText(String.valueOf(settings.getMaintenanceAlertKm()));
        chkActive.setSelected(settings.isActive());
        lblUpdatedAt.setText(formatUpdatedAt(settings.getUpdatedAt()));
    }

    private int parseInt(TextField field, String fieldName) {
        String value = field == null ? "" : field.getText();
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " không được để trống.");
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên.");
        }
    }

    private String formatUpdatedAt(LocalDateTime value) {
        return value == null ? "Chưa cập nhật" : DATE_TIME_FORMATTER.format(value);
    }

    private void showError(String message) {
        lblError.setText(message == null || message.isBlank() ? "Đã có lỗi xảy ra." : message);
        lblError.setVisible(true);
        lblError.setManaged(true);
        clearStatus();
    }

    private void hideError() {
        lblError.setText("");
        lblError.setVisible(false);
        lblError.setManaged(false);
    }

    private void showStatus(String message) {
        lblStatus.setText(message);
        lblStatus.setVisible(true);
        lblStatus.setManaged(true);
    }

    private void clearStatus() {
        lblStatus.setText("");
        lblStatus.setVisible(false);
        lblStatus.setManaged(false);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
