package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller cho màn hình đăng nhập ({@code login-view.fxml}).
 *
 * <p>Day 1: chỉ khai báo các binding @FXML và stub event handler.
 * Day 4 sẽ gọi {@code AuthService.login} và điều hướng sang
 * {@code main-layout.fxml} khi thành công.</p>
 */
public class LoginController {

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
        // TODO Day 4: reset trạng thái errorLabel, focus usernameField, gắn enter-key shortcut
    }

    @FXML
    private void onLoginClick(ActionEvent event) {
        // TODO Day 4: gọi AuthService.login(usernameField.getText(), passwordField.getText())
        //             -> nếu OK: load main-layout.fxml; nếu lỗi: hiển thị errorLabel
    }
}
