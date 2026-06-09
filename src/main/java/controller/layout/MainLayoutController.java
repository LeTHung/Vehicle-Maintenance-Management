package controller.layout;

import controller.dashboard.ManagerDashboardController;
import controller.dashboard.TechnicianDashboardController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

    @FXML private StackPane contentArea;
    @FXML private Label lblPageTitle;
    @FXML private Label lblUserName;
    @FXML private Label lblUserRole;
    @FXML private Label lblRoleBadge;
    @FXML private TextField txtQuickSearch;
    @FXML private ComboBox<String> cbRolePreview;

    @FXML private VBox groupAdmin;
    @FXML private VBox groupManager;
    @FXML private VBox groupTechnician;
    @FXML private VBox groupReport;

    @FXML private Button btnDashboard;
    @FXML private Button btnAdminUsers;
    @FXML private Button btnAdminRoles;
    @FXML private Button btnAuditLog;
    @FXML private Button btnVehicle;
    @FXML private Button btnVehicleDocument;
    @FXML private Button btnDocumentAlert;
    @FXML private Button btnMaintenancePlan;
    @FXML private Button btnMaintenanceAlert;
    @FXML private Button btnMaintenanceRecord;
    @FXML private Button btnMaintenanceHistory;
    @FXML private Button btnReport;
    @FXML private Button btnTopbarNotification;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_TECH = "TECH";
    private static final String ROLE_UNKNOWN = "UNKNOWN";

    private String currentRole = ROLE_UNKNOWN;

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
            return ROLE_UNKNOWN;
        }

        return switch (roleCode.trim().toUpperCase(Locale.ROOT)) {
            case ROLE_ADMIN -> ROLE_ADMIN;
            case "FLEET_MANAGER", ROLE_MANAGER -> ROLE_MANAGER;
            case "TECHNICIAN", ROLE_TECH -> ROLE_TECH;
            default -> ROLE_UNKNOWN;
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
        setNodeVisible(btnTopbarNotification, false);

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
                setNodeVisible(btnTopbarNotification, true);
            }
            default -> {
                lblUserRole.setText("Chưa phân quyền");
                lblRoleBadge.setText(ROLE_UNKNOWN);
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

    private void setNodeVisible(Node node, boolean visible) {
        if (node != null) {
            node.setVisible(visible);
            node.setManaged(visible);
            node.setDisable(!visible);
        }
    }

    @FXML
    private void openDashboard() {
        if (ROLE_ADMIN.equals(currentRole)) {
            loadPage("dashboard/admin-dashboard-view.fxml", "Dashboard quản trị", btnDashboard);
        } else if (ROLE_TECH.equals(currentRole)) {
            loadPage("dashboard/technician-dashboard-view.fxml", "Dashboard kỹ thuật", btnDashboard);
        } else if (ROLE_MANAGER.equals(currentRole)) {
            loadPage("dashboard/manager-dashboard-view.fxml", "Dashboard đội xe", btnDashboard);
        } else {
            showPlaceholder("Không xác định quyền truy cập",
                    "Không thể xác định vai trò người dùng. Vui lòng đăng xuất và đăng nhập lại.",
                    btnDashboard);
        }
    }

    @FXML
    private void openAdminUsers() {
        loadPage("user/user-view.fxml", "Quản lý người dùng", btnAdminUsers);
    }

    @FXML
    private void openAdminRoles() {
        showPlaceholder("Vai trò & phân quyền",
                "Màn hình này sẽ nối với Role/Permission sau khi có module phân quyền.",
                btnAdminRoles);
    }

    @FXML
    private void openAuditLog() {
        showPlaceholder("Audit logs",
                "Khu vực theo dõi thao tác hệ thống và lịch sử hoạt động.",
                btnAuditLog);
    }

    @FXML
    private void openVehicle() {
        loadPage("vehicle/vehicle-view.fxml", "Quản lý phương tiện", btnVehicle);
    }

    @FXML
    private void openVehicleDocument() {
        if (!ROLE_MANAGER.equals(currentRole)) {
            showAccessDenied("Giấy tờ xe", btnVehicleDocument);
            return;
        }
        loadPage("vehicle/vehicle-document-view.fxml", "Giấy tờ xe", btnVehicleDocument);
    }

    @FXML
    private void openDocumentAlert() {
        if (!ROLE_MANAGER.equals(currentRole)) {
            showAccessDenied("Cảnh báo giấy tờ", btnDocumentAlert);
            return;
        }
        loadPage("vehicle/document-alert-view.fxml", "Cảnh báo giấy tờ", btnDocumentAlert);
    }

    @FXML
    private void openMaintenancePlan() {
        loadPage("maintenance/maintenance-plan-view.fxml", "Kế hoạch bảo dưỡng", btnMaintenancePlan);
    }

    @FXML
    private void openMaintenanceAlert() {
        loadPage("maintenance/maintenance-alert-view.fxml", "Cảnh báo bảo dưỡng", btnMaintenanceAlert);
    }

    @FXML
    private void openMaintenanceRecord() {
        loadPage("maintenance/maintenance-record-view.fxml", "Cập nhật bảo dưỡng", btnMaintenanceRecord);
    }

    @FXML
    private void openMaintenanceHistory() {
        showPlaceholder("Lịch sử bảo dưỡng",
                "Màn hình lịch sử bảo dưỡng theo từng xe — sẽ hoàn thiện ở ngày 12.",
                btnMaintenanceHistory);
    }

    @FXML
    private void openReport() {
        loadPage("maintenance/report-view.fxml", "Báo cáo chi phí", btnReport);
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/auth/login-view.fxml"));
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
            FXMLLoader loader = new FXMLLoader(url);
            Parent page = loader.load();
            configureLoadedController(loader.getController());
            if (page instanceof ScrollPane sp) {
                sp.setFitToWidth(true);
                sp.setFitToHeight(false);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
            contentArea.getChildren().setAll(page);
            lblPageTitle.setText(title);
            setActiveButton(activeButton);
        } catch (Exception e) {
            e.printStackTrace();
            showPlaceholder("Lỗi tải màn hình", "Không thể tải file: " + fxmlFile, activeButton);
        }
    }

    private void configureLoadedController(Object controller) {
        if (controller instanceof ManagerDashboardController managerDashboardController) {
            managerDashboardController.setNavigationHandlers(
                    this::openVehicle,
                    this::openVehicleDocument,
                    this::openDocumentAlert,
                    this::openReport);
        } else if (controller instanceof TechnicianDashboardController technicianDashboardController) {
            technicianDashboardController.setNavigationHandlers(
                    this::openMaintenanceAlert,
                    this::openMaintenanceRecord);
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

    private void showAccessDenied(String featureName, Button activeButton) {
        showPlaceholder("Không có quyền truy cập",
                "Vai trò hiện tại không được phép mở màn hình " + featureName + ".",
                activeButton);
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
                btnDashboard, btnAdminUsers, btnAdminRoles, btnAuditLog,
                btnVehicle, btnVehicleDocument, btnDocumentAlert,
                btnMaintenancePlan, btnMaintenanceAlert, btnMaintenanceRecord, btnMaintenanceHistory, btnReport
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
