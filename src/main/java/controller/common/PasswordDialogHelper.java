package controller.common;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import util.AlertUtil;
import util.StylesheetLoader;

import java.util.Optional;

public final class PasswordDialogHelper {

    private PasswordDialogHelper() {
    }

    public static Optional<PasswordChangeData> showOwnPasswordDialog(String title, String header) {
        return showDialog(title, header, true, null);
    }

    public static Optional<PasswordChangeData> showOwnPasswordDialog(String title,
                                                                     String header,
                                                                     PasswordSubmitHandler submitHandler) {
        return showDialog(title, header, true, submitHandler);
    }

    public static Optional<PasswordChangeData> showAdminResetDialog(String title, String header) {
        return showDialog(title, header, false, null);
    }

    public static Optional<PasswordChangeData> showAdminResetDialog(String title,
                                                                    String header,
                                                                    PasswordSubmitHandler submitHandler) {
        return showDialog(title, header, false, submitHandler);
    }

    private static Optional<PasswordChangeData> showDialog(String title,
                                                           String header,
                                                           boolean requireCurrentPassword,
                                                           PasswordSubmitHandler submitHandler) {
        Dialog<PasswordChangeData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        AlertUtil.applyFleetCareIcon(dialog);
        styleDialogPane(dialog.getDialogPane());

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        PasswordField currentPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        currentPasswordField.setPromptText("Mật khẩu hiện tại");
        newPasswordField.setPromptText("Tối thiểu 8 ký tự, có chữ và số");
        confirmPasswordField.setPromptText("Nhập lại mật khẩu mới");
        currentPasswordField.getStyleClass().add("password-dialog-field");
        newPasswordField.getStyleClass().add("password-dialog-field");
        confirmPasswordField.getStyleClass().add("password-dialog-field");

        GridPane form = new GridPane();
        form.getStyleClass().add("password-dialog-form");
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(12));

        int row = 0;
        if (requireCurrentPassword) {
            form.add(formLabel("Mật khẩu hiện tại"), 0, row);
            form.add(currentPasswordField, 1, row++);
        }
        form.add(formLabel("Mật khẩu mới"), 0, row);
        form.add(newPasswordField, 1, row++);
        form.add(formLabel("Xác nhận mật khẩu mới"), 0, row);
        form.add(confirmPasswordField, 1, row);

        currentPasswordField.setPrefWidth(320);
        newPasswordField.setPrefWidth(320);
        confirmPasswordField.setPrefWidth(320);

        dialog.getDialogPane().setContent(form);
        styleDialogButtons(dialog.getDialogPane(), saveButtonType, cancelButtonType);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        if (saveButton != null) {
            saveButton.addEventFilter(ActionEvent.ACTION, event -> {
                if (submitHandler == null) {
                    return;
                }

                try {
                    submitHandler.submit(readPasswordData(
                            requireCurrentPassword,
                            currentPasswordField,
                            newPasswordField,
                            confirmPasswordField));
                } catch (RuntimeException e) {
                    showDialogError(e.getMessage());
                    event.consume();
                }
            });
        }

        dialog.setResultConverter(buttonType -> {
            if (buttonType != saveButtonType) {
                return null;
            }
            return readPasswordData(
                    requireCurrentPassword,
                    currentPasswordField,
                    newPasswordField,
                    confirmPasswordField);
        });

        return dialog.showAndWait();
    }

    private static PasswordChangeData readPasswordData(boolean requireCurrentPassword,
                                                       PasswordField currentPasswordField,
                                                       PasswordField newPasswordField,
                                                       PasswordField confirmPasswordField) {
        return new PasswordChangeData(
                requireCurrentPassword ? currentPasswordField.getText() : null,
                newPasswordField.getText(),
                confirmPasswordField.getText());
    }

    private static Label formLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    public static void styleDialogPane(DialogPane pane) {
        StylesheetLoader.addBaseStyles(pane);
        pane.getStyleClass().add("password-dialog-pane");
    }

    public static void styleDialogButtons(DialogPane pane, ButtonType saveButtonType, ButtonType cancelButtonType) {
        Button saveButton = (Button) pane.lookupButton(saveButtonType);
        if (saveButton != null) {
            saveButton.getStyleClass().add("btn-primary");
        }
        Button cancelButton = (Button) pane.lookupButton(cancelButtonType);
        if (cancelButton != null) {
            cancelButton.getStyleClass().add("btn-soft");
        }
    }

    private static void showDialogError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message == null || message.isBlank() ? "Đã có lỗi xảy ra." : message);
        AlertUtil.applyFleetCareIcon(alert);
        alert.showAndWait();
    }

    @FunctionalInterface
    public interface PasswordSubmitHandler {
        void submit(PasswordChangeData data);
    }

    public record PasswordChangeData(String currentPassword,
                                     String newPassword,
                                     String confirmPassword) {
    }
}
