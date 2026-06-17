package controller.layout;

import controller.common.PasswordDialogHelper;
import controller.common.PasswordDialogHelper.PasswordChangeData;
import controller.dashboard.AdminDashboardController;
import controller.dashboard.ManagerDashboardController;
import controller.dashboard.TechnicianDashboardController;
import controller.maintenance.MaintenanceAlertController;
import controller.maintenance.MaintenanceRecordController;
import controller.user.UserController;
import javafx.beans.value.ChangeListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.dto.MaintenanceDueAlertDTO;
import model.entity.Role;
import model.entity.User;
import service.AuthService;
import service.UserService;
import session.UserSession;
import util.AlertUtil;
import util.SmoothScrollUtil;
import util.StylesheetLoader;

import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

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

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnAdminUsers;
    @FXML
    private Button btnAlertSettings;
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
    private Button btnMaintenanceAlert;
    @FXML
    private Button btnTechMaintenanceAlert;
    @FXML
    private Button btnMaintenanceRecord;
    @FXML
    private Button btnMaintenanceHistory;
    @FXML
    private Button btnTechMaintenanceHistory;
    @FXML
    private Button btnReport;
    @FXML
    private Button btnTopbarNotification;

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_TECH = "TECH";
    private static final String ROLE_UNKNOWN = "UNKNOWN";

    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();

    private String currentRole = ROLE_UNKNOWN;
    private ChangeListener<String> quickSearchListener;

    @FXML
    public void initialize() {
        currentRole = resolveCurrentRole();
        bindCurrentUser();
        hideRolePreview();
        applyRolePermission(currentRole);
        installSmoothScrollingWhenReady();
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
                setRoleBadge("QUẢN TRỊ");
                showGroup(groupAdmin);
            }
            case ROLE_TECH -> {
                lblUserRole.setText("Nhân viên kỹ thuật");
                setRoleBadge("KỸ THUẬT");
                showGroup(groupTechnician);
            }
            case ROLE_MANAGER -> {
                lblUserRole.setText("Quản lý đội xe");
                setRoleBadge("QUẢN LÝ");
                showGroup(groupManager);
                showGroup(groupReport);
                setNodeVisible(btnTopbarNotification, true);
            }
            default -> {
                lblUserRole.setText("Chưa phân quyền");
                setRoleBadge("CHƯA RÕ");
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

    private void setRoleBadge(String text) {
        if (lblRoleBadge != null) {
            lblRoleBadge.setText(text);
        }
    }

    private void setPageTitle(String title) {
        if (lblPageTitle != null) {
            lblPageTitle.setText(title);
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
            loadPage("dashboard/admin-dashboard-view.fxml", "Tổng quan quản trị", btnDashboard);
        } else if (ROLE_TECH.equals(currentRole)) {
            loadPage("dashboard/technician-dashboard-view.fxml", "Tổng quan kỹ thuật", btnDashboard);
        } else if (ROLE_MANAGER.equals(currentRole)) {
            loadPage("dashboard/manager-dashboard-view.fxml", "Tổng quan đội xe", btnDashboard);
        } else {
            showPlaceholder("Không xác định quyền truy cập",
                    "Không thể xác định vai trò người dùng. Vui lòng đăng xuất và đăng nhập lại.",
                    btnDashboard);
        }
    }

    @FXML
    private void openAdminUsers() {
        if (!ROLE_ADMIN.equals(currentRole)) {
            showAccessDenied("Quản lý người dùng", btnAdminUsers);
            return;
        }
        loadPage("user/user-view.fxml", "Quản lý người dùng", btnAdminUsers);
    }

    private void openAdminUserCreateForm() {
        if (!ROLE_ADMIN.equals(currentRole)) {
            showAccessDenied("Quản lý người dùng", btnAdminUsers);
            return;
        }
        loadPage("user/user-view.fxml", "Quản lý người dùng", btnAdminUsers, controller -> {
            if (controller instanceof UserController userController) {
                userController.openCreateUserDialog();
            }
        });
    }

    @FXML
    private void openAuditLog() {
        if (!ROLE_ADMIN.equals(currentRole)) {
            showAccessDenied("Audit logs", btnAuditLog);
            return;
        }
        loadPage("admin/audit-log-view.fxml", "Audit logs", btnAuditLog);
    }

    @FXML
    private void openAlertSettings() {
        if (!ROLE_ADMIN.equals(currentRole)) {
            showAccessDenied("Cấu hình cảnh báo", btnAlertSettings);
            return;
        }
        loadPage("admin/alert-settings-view.fxml", "Cấu hình cảnh báo", btnAlertSettings);
    }

    @FXML
    private void openVehicle() {
        if (!ROLE_MANAGER.equals(currentRole)) {
            showAccessDenied("Hồ sơ phương tiện", btnVehicle);
            return;
        }
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
        if (!ROLE_MANAGER.equals(currentRole)) {
            showAccessDenied("Kế hoạch bảo dưỡng", btnMaintenancePlan);
            return;
        }
        loadPage("maintenance/maintenance-plan-view.fxml", "Kế hoạch bảo dưỡng", btnMaintenancePlan);
    }

    @FXML
    private void openMaintenanceAlert() {
        if (!canOpenMaintenanceAlert()) {
            showAccessDenied("Cảnh báo bảo dưỡng", resolveMaintenanceAlertButton());
            return;
        }
        loadPage("maintenance/maintenance-alert-view.fxml", "Cảnh báo bảo dưỡng", resolveMaintenanceAlertButton());
    }

    @FXML
    private void openMaintenanceRecord() {
        if (!ROLE_TECH.equals(currentRole)) {
            showAccessDenied("Cập nhật bảo dưỡng", btnMaintenanceRecord);
            return;
        }
        loadPage("maintenance/maintenance-record-view.fxml", "Cập nhật bảo dưỡng", btnMaintenanceRecord);
    }

    private void openMaintenanceRecordForVehicle(MaintenanceDueAlertDTO alert) {
        if (!ROLE_TECH.equals(currentRole)) {
            showAccessDenied("Cập nhật bảo dưỡng", btnMaintenanceRecord);
            return;
        }
        loadPage("maintenance/maintenance-record-view.fxml", "Cập nhật bảo dưỡng", btnMaintenanceRecord, controller -> {
            if (controller instanceof MaintenanceRecordController maintenanceRecordController && alert != null) {
                maintenanceRecordController.filterByVehicleId(alert.getVehicleId());
            }
        });
    }

    @FXML
    private void openMaintenanceHistory() {
        if (!canOpenMaintenanceHistory()) {
            showAccessDenied("Lịch sử bảo dưỡng", resolveMaintenanceHistoryButton());
            return;
        }
        loadPage("maintenance/maintenance-history-view.fxml", "Lịch sử bảo dưỡng", resolveMaintenanceHistoryButton());
    }

    private boolean canOpenMaintenanceAlert() {
        return ROLE_MANAGER.equals(currentRole) || ROLE_TECH.equals(currentRole);
    }

    private Button resolveMaintenanceAlertButton() {
        return ROLE_TECH.equals(currentRole) ? btnTechMaintenanceAlert : btnMaintenanceAlert;
    }

    private boolean canOpenMaintenanceHistory() {
        return ROLE_MANAGER.equals(currentRole) || ROLE_TECH.equals(currentRole);
    }

    private Button resolveMaintenanceHistoryButton() {
        return ROLE_TECH.equals(currentRole) ? btnTechMaintenanceHistory : btnMaintenanceHistory;
    }

    @FXML
    private void openReport() {
        if (!ROLE_MANAGER.equals(currentRole)) {
            showAccessDenied("Báo cáo chi phí", btnReport);
            return;
        }
        loadPage("maintenance/report-view.fxml", "Báo cáo chi phí", btnReport);
    }

    @FXML
    private void handleLogout() {
        authService.logout();
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

    @FXML
    private void handleChangeOwnPassword() {
        Optional<PasswordChangeData> formData = PasswordDialogHelper.showOwnPasswordDialog(
                "Đổi mật khẩu",
                "Cập nhật mật khẩu đăng nhập của bạn",
                data -> userService.changeOwnPassword(data.currentPassword(), data.newPassword(), data.confirmPassword()));
        if (formData.isEmpty()) {
            return;
        }

        bindCurrentUser();
        showInfo("Đã đổi mật khẩu thành công.");
    }

    private void loadPage(String fxmlFile, String title, Button activeButton) {
        loadPage(fxmlFile, title, activeButton, controller -> {});
    }

    private void loadPage(String fxmlFile, String title, Button activeButton, Consumer<Object> controllerCallback) {
        try {
            URL url = getClass().getResource("/view/" + fxmlFile);
            if (url == null) {
                showPlaceholder("Lỗi tải màn hình", "Không tìm thấy file: " + fxmlFile, activeButton);
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent page = loader.load();
            configureLoadedController(loader.getController());
            bindQuickSearchController(loader.getController());
            if (page instanceof ScrollPane sp) {
                sp.setFitToWidth(true);
                sp.setFitToHeight(false);
                sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            }
            contentArea.getChildren().setAll(page);
            SmoothScrollUtil.install(page);
            Platform.runLater(() -> SmoothScrollUtil.install(page));
            setPageTitle(title);
            setActiveButton(activeButton);
            controllerCallback.accept(loader.getController());
        } catch (Exception e) {
            e.printStackTrace();
            showPlaceholder("Lỗi tải màn hình", "Không thể tải file: " + fxmlFile, activeButton);
        }
    }

    private void installSmoothScrollingWhenReady() {
        if (contentArea.getScene() != null) {
            SmoothScrollUtil.install(contentArea.getScene().getRoot());
            return;
        }

        contentArea.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                SmoothScrollUtil.install(newScene.getRoot());
            }
        });
    }

    private void configureLoadedController(Object controller) {
        if (controller instanceof AdminDashboardController adminDashboardController) {
            adminDashboardController.setNavigationHandlers(
                    this::openAdminUsers,
                    this::openAdminUserCreateForm,
                    this::openAuditLog,
                    this::openAlertSettings);
        } else if (controller instanceof ManagerDashboardController managerDashboardController) {
            managerDashboardController.setNavigationHandlers(
                    this::openVehicle,
                    this::openVehicleDocument,
                    this::openDocumentAlert,
                    this::openMaintenanceAlert,
                    this::openReport);
        } else if (controller instanceof TechnicianDashboardController technicianDashboardController) {
            technicianDashboardController.setNavigationHandlers(
                    this::openMaintenanceAlert,
                    this::openMaintenanceRecord);
        } else if (controller instanceof MaintenanceAlertController maintenanceAlertController) {
            if (ROLE_TECH.equals(currentRole)) {
                maintenanceAlertController.setOpenMaintenanceRecordHandler(this::openMaintenanceRecordForVehicle);
            }
        }
    }

    private void bindQuickSearchController(Object controller) {
        clearQuickSearchBinding();
        if (controller instanceof QuickSearchAware quickSearchAware && txtQuickSearch != null) {
            txtQuickSearch.setPromptText(quickSearchAware.getQuickSearchPrompt());
            setNodeVisible(txtQuickSearch, true);
            quickSearchListener = (observable, oldValue, newValue) -> quickSearchAware.applyQuickSearch(newValue);
            txtQuickSearch.textProperty().addListener(quickSearchListener);
            quickSearchAware.applyQuickSearch(txtQuickSearch.getText());
        }
    }

    private void clearQuickSearchBinding() {
        if (txtQuickSearch == null) {
            return;
        }
        if (quickSearchListener != null) {
            txtQuickSearch.textProperty().removeListener(quickSearchListener);
            quickSearchListener = null;
        }
        if (!txtQuickSearch.getText().isEmpty()) {
            txtQuickSearch.clear();
        }
        txtQuickSearch.setPromptText("Tìm kiếm nhanh...");
        setNodeVisible(txtQuickSearch, false);
    }

    private void showPlaceholder(String title, String message, Button activeButton) {
        clearQuickSearchBinding();

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
        setPageTitle(title);
        setActiveButton(activeButton);
    }

    private void showAccessDenied(String featureName, Button activeButton) {
        showPlaceholder("Không có quyền truy cập",
                "Vai trò hiện tại không được phép mở màn hình " + featureName + ".",
                activeButton);
    }

    private void setActiveButton(Button activeButton) {
        Button[] buttons = {
                btnDashboard, btnAdminUsers, btnAlertSettings, btnAuditLog,
                btnVehicle, btnVehicleDocument, btnDocumentAlert,
                btnMaintenancePlan, btnMaintenanceAlert, btnTechMaintenanceAlert,
                btnMaintenanceRecord, btnMaintenanceHistory, btnTechMaintenanceHistory, btnReport
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

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", message);
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Lỗi", message == null || message.isBlank() ? "Đã có lỗi xảy ra." : message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        AlertUtil.applyFleetCareIcon(alert);
        alert.showAndWait();
    }
}
