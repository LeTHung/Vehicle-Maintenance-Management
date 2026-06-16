package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.dao.RoleDAO;
import model.dao.VehicleDAO;
import model.entity.AuditLog;
import model.entity.User;
import service.AuditLogService;
import service.DocumentAlertService;
import service.MaintenanceAlertService;
import service.UserService;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {

    private final UserService userService = new UserService();
    private final RoleDAO roleDAO = new RoleDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertService documentAlertService = new DocumentAlertService();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();
    private final AuditLogService auditLogService = new AuditLogService();

    private Runnable openUserManagementHandler = () -> {};
    private Runnable createUserHandler = () -> {};
    private Runnable openRoleManagementHandler = () -> {};
    private Runnable openAuditLogHandler = () -> {};
    private Runnable openAlertSettingsHandler = () -> {};

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalUsersHint;
    @FXML private Label lblLockedUsers;
    @FXML private Label lblLockedUsersHint;
    @FXML private Label lblSystemAlerts;
    @FXML private Label lblSystemAlertsHint;
    @FXML private Label lblRecentAuditLogs;
    @FXML private Label lblRecentAuditLogsHint;
    @FXML private Label lblVehicleCount;
    @FXML private Label lblDocumentAlertCount;

    @FXML
    public void initialize() {
        refreshMetrics();
    }

    public void setNavigationHandlers(Runnable openUserManagementHandler,
                                      Runnable createUserHandler,
                                      Runnable openRoleManagementHandler,
                                      Runnable openAuditLogHandler,
                                      Runnable openAlertSettingsHandler) {
        this.openUserManagementHandler = safeHandler(openUserManagementHandler);
        this.createUserHandler = safeHandler(createUserHandler);
        this.openRoleManagementHandler = safeHandler(openRoleManagementHandler);
        this.openAuditLogHandler = safeHandler(openAuditLogHandler);
        this.openAlertSettingsHandler = safeHandler(openAlertSettingsHandler);
    }

    @FXML
    private void handleOpenUsers() {
        openUserManagementHandler.run();
    }

    @FXML
    private void handleCreateUser() {
        createUserHandler.run();
    }

    @FXML
    private void handleOpenRoleManagement() {
        openRoleManagementHandler.run();
    }

    @FXML
    private void handleOpenAuditLog() {
        openAuditLogHandler.run();
    }

    @FXML
    private void handleOpenAlertSettings() {
        openAlertSettingsHandler.run();
    }

    private void refreshMetrics() {
        List<User> users = userService.listUsers();
        long lockedUsers = users.stream()
                .filter(user -> "LOCKED".equalsIgnoreCase(nullToEmpty(user.getAccountStatus())))
                .count();
        int activeRoles = roleDAO.findAllActive().size();
        int vehicleCount = vehicleDAO.findAll().size();
        int documentAlerts = documentAlertService.countExpired() + documentAlertService.countComingDue();
        int maintenanceAlerts = maintenanceAlertService.listDueAlerts().size();

        lblTotalUsers.setText(String.valueOf(users.size()));
        lblTotalUsersHint.setText(activeRoles + " vai trò hoạt động");
        lblLockedUsers.setText(String.valueOf(lockedUsers));
        lblLockedUsersHint.setText(lockedUsers > 0 ? "Cần rà soát" : "Đang ổn định");
        lblSystemAlerts.setText(String.valueOf(documentAlerts + maintenanceAlerts));
        lblSystemAlertsHint.setText("Giấy tờ + bảo dưỡng");
        lblVehicleCount.setText(vehicleCount + " phương tiện");
        lblDocumentAlertCount.setText(documentAlerts + " cảnh báo giấy tờ");

        renderAuditMetric();
    }

    private void renderAuditMetric() {
        try {
            List<AuditLog> recentLogs = auditLogService.listRecentLogs();
            long todayLogs = recentLogs.stream()
                    .filter(log -> log.getCreatedAt() != null)
                    .filter(log -> LocalDate.now().equals(log.getCreatedAt().toLocalDate()))
                    .count();

            lblRecentAuditLogs.setText(String.valueOf(todayLogs));
            lblRecentAuditLogsHint.setText(todayLogs > 0 ? "Hoạt động trong hôm nay" : "Chưa có nhật ký hôm nay");
        } catch (RuntimeException e) {
            lblRecentAuditLogs.setText("-");
            lblRecentAuditLogsHint.setText("Chưa tải được nhật ký");
        }
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }
}
