package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;

/**
 * Controller cho trang Dashboard ({@code dashboard-view.fxml}).
 *
 * <p>Dashboard được load vào {@code contentArea} của {@code main-layout.fxml}
 * (thông qua {@code MainLayoutController.openDashboard}). Day 1: skeleton.</p>
 */
public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private MenuBar menuBar;

    @FXML
    public void initialize() {
        // TODO Day 4: hiển thị welcomeLabel theo UserSession.getInstance().getCurrentUser().getFullName()
        //             và ẩn/hiện menu theo UserSession.getInstance().getCurrentRole().getRoleCode()
    }

    @FXML
    private void onLogoutClick() {
        // TODO Day 4: AuthService.logout() -> điều hướng về login-view.fxml
    }
}
