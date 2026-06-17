package controller.auth;

import controller.common.PasswordDialogHelper;
import controller.common.PasswordDialogHelper.PasswordChangeData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import model.entity.User;
import javafx.stage.Stage;
import service.AuthService;
import service.AuthenticationException;
import service.UserService;
import util.StylesheetLoader;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        loginButton.setDefaultButton(true);

        passwordField.setOnAction(this::onLoginClick);
        usernameField.requestFocus();
    }

    @FXML
    private void onLoginClick(ActionEvent event) {
        hideError();

        try {
            User user = authService.login(usernameField.getText(), passwordField.getText());
            if (user.isMustChangePassword() && !forcePasswordChange()) {
                authService.logout();
                passwordField.clear();
                showError("Bạn cần đổi mật khẩu trước khi vào hệ thống.");
                return;
            }
            loadMainLayout();
        } catch (AuthenticationException e) {
            showError(e.getMessage());
            passwordField.clear();
            passwordField.requestFocus();
        } catch (IOException e) {
            showError("Không thể mở màn hình chính. Vui lòng thử lại.");
            e.printStackTrace();
        }
    }

    private boolean forcePasswordChange() {
        Optional<PasswordChangeData> formData = PasswordDialogHelper.showOwnPasswordDialog(
                "Đổi mật khẩu bắt buộc",
                "Tài khoản của bạn đang dùng mật khẩu tạm. Vui lòng đổi mật khẩu mới.",
                data -> userService.changeOwnPassword(data.currentPassword(), data.newPassword(), data.confirmPassword()));
        return formData.isPresent();
    }

    private void loadMainLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/layout/main-layout.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);
        applyAppStyles(scene);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
        stage.setScene(scene);
        stage.setTitle("FleetCare - Quản lý hồ sơ & bảo dưỡng phương tiện");

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        if (screen.getWidth() < 1280 || screen.getHeight() < 820) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(1280);
            stage.setHeight(800);
            stage.centerOnScreen();
        }
    }

    private void applyAppStyles(Scene scene) {
        StylesheetLoader.addBaseStyles(scene);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setText("");
    }

}
