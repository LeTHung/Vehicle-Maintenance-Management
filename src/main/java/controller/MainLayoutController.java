package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;

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

    @FXML
    public void initialize() {
        /*
         * Tạm thời test role ở đây.
         * Sau này khi phần Login/UserSession hoàn thiện thì thay dòng này.
         */
        String currentRole = ROLE_MANAGER;
        String currentName = "Lê Tiến Hưng";

        lblUserName.setText(currentName);
        applyRolePermission(currentRole);

        openDashboard();
    }

    private void applyRolePermission(String role) {
        hideAllGroups();

        showGroup(groupDashboard);

        switch (role) {
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
        showPlaceholder(
                "Quản lý người dùng",
                "Màn hình này sẽ do thành viên phụ trách Auth/User hoàn thiện.",
                btnUserManagement);
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
        System.out.println("Đăng xuất hệ thống");

        showPlaceholder(
                "Đăng xuất",
                "Sau này sẽ quay về màn hình đăng nhập.",
                null);
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