package controller.auth;

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
import util.StylesheetLoader;

import java.io.IOException;

public class LoginController {

    private final AuthService authService = new AuthService();

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
            authService.login(usernameField.getText(), passwordField.getText());
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/layout/main-layout.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 800);
        applyAppStyles(scene);

        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setResizable(true);
        stage.setMinWidth(0);
        stage.setMinHeight(0);
        stage.setMaxWidth(Double.MAX_VALUE);
        stage.setMaxHeight(Double.MAX_VALUE);
        stage.setScene(scene);
        stage.setTitle("FleetCare - Quản lý hồ sơ & bảo dưỡng phương tiện");
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.setWidth(1280);
        stage.setHeight(800);
        stage.centerOnScreen();
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
