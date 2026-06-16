package controller.common;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public final class PasswordDialogHelper {

    private PasswordDialogHelper() {
    }

    public static Optional<PasswordChangeData> showOwnPasswordDialog(String title, String header) {
        return showDialog(title, header, true);
    }

    public static Optional<PasswordChangeData> showAdminResetDialog(String title, String header) {
        return showDialog(title, header, false);
    }

    private static Optional<PasswordChangeData> showDialog(String title, String header, boolean requireCurrentPassword) {
        Dialog<PasswordChangeData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ButtonType saveButtonType = new ButtonType("Cập nhật", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);

        PasswordField currentPasswordField = new PasswordField();
        PasswordField newPasswordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();

        currentPasswordField.setPromptText("Mật khẩu hiện tại");
        newPasswordField.setPromptText("Tối thiểu 8 ký tự, có chữ và số");
        confirmPasswordField.setPromptText("Nhập lại mật khẩu mới");

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(12));

        int row = 0;
        if (requireCurrentPassword) {
            form.add(new Label("Mật khẩu hiện tại"), 0, row);
            form.add(currentPasswordField, 1, row++);
        }
        form.add(new Label("Mật khẩu mới"), 0, row);
        form.add(newPasswordField, 1, row++);
        form.add(new Label("Xác nhận mật khẩu mới"), 0, row);
        form.add(confirmPasswordField, 1, row);

        currentPasswordField.setPrefWidth(320);
        newPasswordField.setPrefWidth(320);
        confirmPasswordField.setPrefWidth(320);

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(buttonType -> {
            if (buttonType != saveButtonType) {
                return null;
            }
            return new PasswordChangeData(
                    requireCurrentPassword ? currentPasswordField.getText() : null,
                    newPasswordField.getText(),
                    confirmPasswordField.getText());
        });

        return dialog.showAndWait();
    }

    public record PasswordChangeData(String currentPassword,
                                     String newPassword,
                                     String confirmPassword) {
    }
}
