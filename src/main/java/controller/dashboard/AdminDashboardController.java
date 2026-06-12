package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.dao.RoleDAO;
import model.dao.VehicleDAO;
import model.entity.User;
import service.DocumentAlertService;
import service.MaintenanceAlertService;
import service.UserService;

import java.util.List;

public class AdminDashboardController {

    private final UserService userService = new UserService();
    private final RoleDAO roleDAO = new RoleDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertService documentAlertService = new DocumentAlertService();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();
    private Runnable openUserManagementHandler = () -> {
    };
    private Runnable openRoleManagementHandler = () -> {
    };

    @FXML
    private Label lblTotalUsers;
    @FXML
    private Label lblLockedUsers;
    @FXML
    private Label lblSystemAlerts;
    @FXML
    private Label lblUserHint;
    @FXML
    private Label lblVehicleCount;
    @FXML
    private Label lblDocumentAlertCount;

    @FXML
    public void initialize() {
        List<User> users = userService.listUsers();
        long lockedUsers = users.stream()
                .filter(user -> "LOCKED".equalsIgnoreCase(nullToEmpty(user.getAccountStatus())))
                .count();
        int documentAlerts = documentAlertService.countExpired() + documentAlertService.countComingDue();
        int maintenanceAlerts = maintenanceAlertService.listDueAlerts().size();

        lblTotalUsers.setText(String.valueOf(users.size()));
        lblLockedUsers.setText(String.valueOf(lockedUsers));
        lblSystemAlerts.setText(String.valueOf(documentAlerts + maintenanceAlerts));
        lblUserHint.setText(roleDAO.findAllActive().size() + " vai trò hoạt động");
        lblVehicleCount.setText(vehicleDAO.findAll().size() + " phương tiện");
        lblDocumentAlertCount.setText(documentAlerts + " cảnh báo giấy tờ");
    }

    public void setNavigationHandlers(Runnable openUserManagementHandler,
            Runnable openRoleManagementHandler) {
        this.openUserManagementHandler = safeHandler(openUserManagementHandler);
        this.openRoleManagementHandler = safeHandler(openRoleManagementHandler);
    }

    @FXML
    private void handleOpenUserManagement() {
        openUserManagementHandler.run();
    }

    @FXML
    private void handleOpenRoleManagement() {
        openRoleManagementHandler.run();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {
        } : handler;
    }
}
