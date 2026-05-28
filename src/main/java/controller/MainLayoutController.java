package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entity.Role;
import model.entity.User;
import session.UserSession;
import util.StylesheetLoader;

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
    private Label lblRoleBadge;
    @FXML
    private TextField txtQuickSearch;
    @FXML
    private ComboBox<String> cbRolePreview;

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
    private Button btnAdminUsers;
    @FXML
    private Button btnAdminRoles;
    @FXML
    private Button btnAuditLog;
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

    private String currentRole = ROLE_MANAGER;

    @FXML
    public void initialize() {
        currentRole = resolveCurrentRole();
        bindCurrentUser();
        hideRolePreview();
        applyRolePermission(currentRole);
        openDashboard();
    }

    private void bindCurrentUser() {
        User user = UserSession.getInstance().getCurrentUser();
        lblUserName.setText(resolveDisplayName(user));
    }

    private String resolveCurrentRole() {
        Role role = UserSession.getInstance().getCurrentRole();
        String roleCode = role == null ? null : role.getRoleCode();
        if (roleCode == null || roleCode.isBlank()) {
            return ROLE_MANAGER;
        }

        return switch (roleCode.trim().toUpperCase(Locale.ROOT)) {
            case ROLE_ADMIN -> ROLE_ADMIN;
            case "FLEET_MANAGER", ROLE_MANAGER -> ROLE_MANAGER;
            case "TECHNICIAN", ROLE_TECH -> ROLE_TECH;
            default -> ROLE_MANAGER;
        };
    }

    private void hideRolePreview() {
        if (cbRolePreview != null) {
            cbRolePreview.setVisible(false);
            cbRolePreview.setManaged(false);
        }
    }

    private void applyRolePermission(String role) {
        hideGroup(groupAdmin);
        hideGroup(groupManager);
        hideGroup(groupTechnician);
        hideGroup(groupReport);

        switch (role) {
            case ROLE_ADMIN -> {
                lblUserRole.setText("Quản trị viên");
                lblRoleBadge.setText("ADMIN");
                showGroup(groupAdmin);
                showGroup(groupReport);
            }
            case ROLE_TECH -> {
                lblUserRole.setText("Nhân viên kỹ thuật");
                lblRoleBadge.setText("TECH");
                showGroup(groupTechnician);
            }
            case ROLE_MANAGER -> {
                lblUserRole.setText("Quản lý đội xe");
                lblRoleBadge.setText("MANAGER");
                showGroup(groupManager);
                showGroup(groupReport);
            }
            default -> {
                lblUserRole.setText("Chưa phân quyền");
                lblRoleBadge.setText("UNKNOWN");
            }
        }
    }

    private void hideGroup(VBox group) {
        if (group != null) {
            group.setVisible(false);
            group.setManaged(false);
        }
    }

    private void showGroup(VBox group) {
        if (group != null) {
            group.setVisible(true);
            group.setManaged(true);
        }
    }

    @FXML
    private void openDashboard() {
        if (ROLE_ADMIN.equals(currentRole)) {
            loadPage("admin-dashboard-view.fxml", "Dashboard quản trị", btnDashboard);
        } else if (ROLE_TECH.equals(currentRole)) {
            loadPage("technician-dashboard-view.fxml", "Dashboard kỹ thuật", btnDashboard);
        } else {
            loadPage("manager-dashboard-view.fxml", "Dashboard đội xe", btnDashboard);
        }
    }

    @FXML
    private void openAdminUsers() {
        loadPage("user-view.fxml", "Quản lý người dùng", btnAdminUsers);
    }

    @FXML
    private void openAdminRoles() {
        showPlaceholder("Vai trò & phân quyền",
                "Màn hình này sẽ nối với Role/Permission sau khi có module phân quyền.",
                btnAdminRoles);
    }

    @FXML
    private void openAuditLog() {
        showPlaceholder("Audit logs", "Khu vực theo dõi thao tác hệ thống và lịch sử hoạt động.", btnAuditLog);
    }

    @FXML
    private void openVehicle() {
        loadPage("vehicle-view.fxml", "Quản lý phương tiện", btnVehicle);
    }

    @FXML
    private void openVehicleDocument() {
        loadPage("vehicle-document-view.fxml", "Giấy tờ xe", btnVehicleDocument);
    }

    @FXML
    private void openDocumentAlert() {
        loadPage("document-alert-view.fxml", "Cảnh báo giấy tờ", btnDocumentAlert);
    }

    @FXML
    private void openMaintenancePlan() {
        showPlaceholder("Kế hoạch bảo dưỡng",
                "Màn hình này do thành viên phụ trách kỹ thuật/bảo dưỡng hoàn thiện.",
                btnMaintenancePlan);
    }

    @FXML
    private void openMaintenanceRecord() {
        showPlaceholder("Cập nhật bảo dưỡng",
                "Màn hình phiếu bảo dưỡng, chi phí và phụ tùng sẽ được tích hợp sau.",
                btnMaintenanceRecord);
    }

    @FXML
    private void openMaintenanceHistory() {
        showPlaceholder("Lịch sử bảo dưỡng",
                "Màn hình này sẽ hiển thị lịch sử bảo dưỡng theo từng xe.",
                btnMaintenanceHistory);
    }

    @FXML
    private void openReport() {
        showPlaceholder("Báo cáo chi phí",
                "Báo cáo chi phí bảo dưỡng và giấy tờ sẽ được tích hợp sau.",
                btnReport);
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login-view.fxml"));
            Scene scene = new Scene(root, 420, 360);
            applyAppStyles(scene);

            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setMaximized(false);
            stage.setMinWidth(0);
            stage.setMinHeight(0);
            stage.setMaxWidth(Double.MAX_VALUE);
            stage.setMaxHeight(Double.MAX_VALUE);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.setTitle("FleetCare - Đăng nhập");
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showPlaceholder("Lỗi đăng xuất", "Không thể quay về màn hình đăng nhập.", null);
        }
    }

    private void loadPage(String fxmlFile, String title, Button activeButton) {
        try {
            URL url = getClass().getResource("/view/" + fxmlFile);
            if (url == null) {
                showPlaceholder("Lỗi tải màn hình", "Không tìm thấy file: " + fxmlFile, activeButton);
                return;
            }
            Parent page = FXMLLoader.load(url);
            contentArea.getChildren().setAll(page);
            lblPageTitle.setText(title);
            setActiveButton(activeButton);
        } catch (Exception e) {
            e.printStackTrace();
            showPlaceholder("Lỗi tải màn hình", "Không thể tải file: " + fxmlFile, activeButton);
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
        messageLabel.setWrapText(true);

        box.getChildren().addAll(titleLabel, messageLabel);
        contentArea.getChildren().setAll(box);
        lblPageTitle.setText(title);
        setActiveButton(activeButton);
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
                btnDashboard, btnAdminUsers, btnAdminRoles, btnAuditLog,
                btnVehicle, btnVehicleDocument, btnDocumentAlert,
                btnMaintenancePlan, btnMaintenanceRecord, btnMaintenanceHistory, btnReport
        };
        for (Button button : buttons) {
            if (button != null) {
                button.getStyleClass().remove("nav-button-active");
            }
        }
        if (activeButton != null && !activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private void applyAppStyles(Scene scene) {
        StylesheetLoader.addBaseStyles(scene);
    }

    private String resolveDisplayName(User user) {
        if (user == null) {
            return "Người dùng";
        }
        if (user.getFullName() != null && !user.getFullName().isBlank()) {
            return user.getFullName().trim();
        }
        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername().trim();
        }
        return "Người dùng";
    }
}
