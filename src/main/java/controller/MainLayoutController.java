package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entity.Role;
import model.entity.User;
import service.AuthService;
import session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblPageTitle;
    @FXML
    private Label lblUserName;
    @FXML
    private Label lblUserRole;

    @FXML
    private TextField txtQuickSearch;

    @FXML
    private VBox groupDashboard;
    @FXML
    private VBox groupAdmin;
    @FXML
    private VBox groupManager;
    @FXML
    private VBox groupTechnician;
    @FXML
    private VBox groupReport;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnUserManagement;
    @FXML
    private Button btnRoleManagement;

    @FXML
    private Button btnVehicle;
    @FXML
    private Button btnVehicleDocument;
    @FXML
    private Button btnDocumentAlert;

    @FXML
    private Button btnMaintenancePlan;
    @FXML
    private Button btnMaintenanceRecord;
    @FXML
    private Button btnMaintenanceHistory;

    @FXML
    private Button btnReport;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_TECH = "TECH";

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        if (!session.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        User user = session.getCurrentUser();
        Role role = session.getCurrentRole();

        lblUserName.setText(resolveDisplayName(user));
        applyRolePermission(role == null ? null : role.getRoleCode());

        openDashboard();
    }

    private void applyRolePermission(String role) {
        String normalizedRole = normalizeRoleCode(role);

        hideAllGroups();

        showGroup(groupDashboard);

        switch (normalizedRole) {
            case ROLE_ADMIN:
                lblUserRole.setText("Quản trị viên");
                showGroup(groupAdmin);
                showGroup(groupReport);
                break;

            case ROLE_MANAGER:
                lblUserRole.setText("Quản lý đội xe");
                showGroup(groupManager);
                showGroup(groupReport);
                break;

            case ROLE_TECH:
                lblUserRole.setText("Nhân viên kỹ thuật");
                showGroup(groupTechnician);
                break;

            default:
                lblUserRole.setText("Không xác định");
                break;
        }
    }

    private void hideAllGroups() {
        hideGroup(groupAdmin);
        hideGroup(groupManager);
        hideGroup(groupTechnician);
        hideGroup(groupReport);
    }

    private void hideGroup(VBox group) {
        group.setVisible(false);
        group.setManaged(false);
    }

    private void showGroup(VBox group) {
        group.setVisible(true);
        group.setManaged(true);
    }

    @FXML
    private void openDashboard() {
        loadPage("dashboard-view.fxml", "Dashboard", btnDashboard);
    }

    @FXML
    private void openUserManagement() {
        loadPage("user-view.fxml", "Quan ly nguoi dung", btnUserManagement);
    }

    @FXML
    private void openRoleManagement() {
        showPlaceholder(
                "Vai trò & phân quyền",
                "Màn hình này sẽ được tích hợp sau khi có phân quyền.",
                btnRoleManagement);
    }

    @FXML
    private void openVehicle() {
        loadPage("vehicle-view.fxml", "Quản lý phương tiện", btnVehicle);
    }

    @FXML
    private void openVehicleDocument() {
        loadPage("vehicle-document-view.fxml", "Quản lý giấy tờ xe", btnVehicleDocument);
    }

    @FXML
    private void openDocumentAlert() {
        loadPage("document-alert-view.fxml", "Cảnh báo giấy tờ xe", btnDocumentAlert);
    }

    @FXML
    private void openMaintenancePlan() {
        showPlaceholder(
                "Kế hoạch bảo dưỡng",
                "Màn hình này sẽ do thành viên phụ trách bảo dưỡng hoàn thiện.",
                btnMaintenancePlan);
    }

    @FXML
    private void openMaintenanceRecord() {
        showPlaceholder(
                "Cập nhật bảo dưỡng",
                "Màn hình này sẽ do thành viên phụ trách bảo dưỡng hoàn thiện.",
                btnMaintenanceRecord);
    }

    @FXML
    private void openMaintenanceHistory() {
        showPlaceholder(
                "Lịch sử bảo dưỡng",
                "Màn hình này sẽ do thành viên phụ trách bảo dưỡng hoàn thiện.",
                btnMaintenanceHistory);
    }

    @FXML
    private void openReport() {
        showPlaceholder(
                "Báo cáo chi phí",
                "Màn hình báo cáo sẽ được tích hợp sau.",
                btnReport);
    }

    @FXML
    private void handleLogout() {
        authService.logout();
        navigateToLogin();
    }

    private void navigateToLogin() {
        if (contentArea.getScene() == null) {
            Platform.runLater(this::navigateToLogin);
            return;
        }

        try {
            URL fxmlUrl = getClass().getResource("/view/login-view.fxml");

            if (fxmlUrl == null) {
                showPlaceholder(
                        "Lỗi tải màn hình",
                        "Không tìm thấy file: login-view.fxml",
                        null);
                return;
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root);
            applyGlobalStyles(scene);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("FleetCare - Quản lý hồ sơ & bảo dưỡng phương tiện");
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showPlaceholder(
                    "Lỗi tải màn hình",
                    "Không thể quay về màn hình đăng nhập.",
                    null);
        }
    }

    private String resolveDisplayName(User user) {
        if (user == null) {
            return "Người dùng";
        }

        String fullName = user.getFullName();
        if (fullName != null && !fullName.isBlank()) {
            return fullName.trim();
        }

        String username = user.getUsername();
        if (username != null && !username.isBlank()) {
            return username.trim();
        }

        return "Người dùng";
    }

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return "";
        }

        String normalized = roleCode.trim().toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "FLEET_MANAGER" -> ROLE_MANAGER;
            case "TECHNICIAN" -> ROLE_TECH;
            default -> normalized;
        };
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

    private void loadPage(String fxmlFile, String title, Button activeButton) {
        try {
            URL fxmlUrl = getClass().getResource("/view/" + fxmlFile);

            if (fxmlUrl == null) {
                showPlaceholder(
                        "Lỗi tải màn hình",
                        "Không tìm thấy file: " + fxmlFile,
                        activeButton);
                return;
            }

            Parent page = FXMLLoader.load(fxmlUrl);

            contentArea.getChildren().setAll(page);
            lblPageTitle.setText(title);
            setActiveButton(activeButton);

        } catch (Exception e) {
            e.printStackTrace();

            showPlaceholder(
                    "Lỗi tải màn hình",
                    "Không thể tải file: " + fxmlFile,
                    activeButton);
        }
    }

    private void showPlaceholder(String title, String message, Button activeButton) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("placeholder-box");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("placeholder-title");

        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("placeholder-message");

        box.getChildren().addAll(titleLabel, messageLabel);

        contentArea.getChildren().setAll(box);
        lblPageTitle.setText(title);

        setActiveButton(activeButton);
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
                btnDashboard,
                btnUserManagement,
                btnRoleManagement,
                btnVehicle,
                btnVehicleDocument,
                btnDocumentAlert,
                btnMaintenancePlan,
                btnMaintenanceRecord,
                btnMaintenanceHistory,
                btnReport
        };

        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("active");
            }
        }

        if (activeButton != null && !activeButton.getStyleClass().contains("active")) {
            activeButton.getStyleClass().add("active");
        }
    }
}
