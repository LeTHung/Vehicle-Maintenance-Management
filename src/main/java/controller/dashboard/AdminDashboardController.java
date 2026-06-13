package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.AuditLog;
import model.entity.User;
import service.AuditLogService;

import java.time.LocalDate;
import java.util.List;

public class AdminDashboardController {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final AuditLogService auditLogService = new AuditLogService();

    private Runnable openUserManagementHandler = () -> {};
    private Runnable createUserHandler = () -> {};
    private Runnable openAuditLogHandler = () -> {};

    @FXML private Label lblTotalUsers;
    @FXML private Label lblTotalUsersHint;
    @FXML private Label lblLockedUsers;
    @FXML private Label lblLockedUsersHint;
    @FXML private Label lblActiveRoles;
    @FXML private Label lblActiveRolesHint;
    @FXML private Label lblRecentAuditLogs;
    @FXML private Label lblRecentAuditLogsHint;

    @FXML
    public void initialize() {
        refreshMetrics();
    }

    public void setNavigationHandlers(Runnable openUserManagementHandler,
                                      Runnable createUserHandler,
                                      Runnable openAuditLogHandler) {
        this.openUserManagementHandler = safeHandler(openUserManagementHandler);
        this.createUserHandler = safeHandler(createUserHandler);
        this.openAuditLogHandler = safeHandler(openAuditLogHandler);
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
    private void handleOpenAuditLog() {
        openAuditLogHandler.run();
    }

    private void refreshMetrics() {
        List<User> users = userDAO.findAll();
        long lockedUsers = users.stream()
                .filter(user -> "LOCKED".equalsIgnoreCase(nullToEmpty(user.getAccountStatus())))
                .count();

        lblTotalUsers.setText(String.valueOf(users.size()));
        lblTotalUsersHint.setText("Đang quản lý");
        lblLockedUsers.setText(String.valueOf(lockedUsers));
        lblLockedUsersHint.setText(lockedUsers > 0 ? "Cần rà soát" : "Đang ổn định");
        lblActiveRoles.setText(String.valueOf(roleDAO.findAllActive().size()));
        lblActiveRolesHint.setText("Vai trò đang hoạt động");

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
