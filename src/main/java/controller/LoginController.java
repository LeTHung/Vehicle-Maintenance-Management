package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AuthService;
import service.AuthenticationException;

import java.io.IOException;

/**
 * Controller cho màn hình đăng nhập ({@code login-view.fxml}).
 *
 * <p>Day 1: chỉ khai báo các binding @FXML và stub event handler.
 * Day 4 sẽ gọi {@code AuthService.login} và điều hướng sang
 * {@code main-layout.fxml} khi thành công.</p>
 */
public class LoginController {

    private final AuthService authService = new AuthService();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

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

        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            authService.login(username, password);
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

    private void loadMainLayout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main-layout.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 760);
        applyGlobalStyles(scene);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("FleetCare - Quản lý hồ sơ & bảo dưỡng phương tiện");
        stage.centerOnScreen();
    }

    private void applyGlobalStyles(Scene scene) {
        String[] globalCssFiles = {
                "/css/global/theme.css",
                "/css/global/layout.css",
                "/css/global/pages.css",
                "/css/global/cards.css",
                "/css/global/forms.css",
                "/css/global/buttons.css",
                "/css/global/tables.css"
        };

        for (String css : globalCssFiles) {
            scene.getStylesheets().add(
                    getClass().getResource(css).toExternalForm());
        }
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
