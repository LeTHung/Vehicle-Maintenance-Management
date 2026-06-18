package controller.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.dao.RoleDAO;
import model.dao.VehicleDAO;
import model.entity.AuditLog;
import model.entity.User;
import service.AuditLogService;
import service.DocumentAlertService;
import service.MaintenanceAlertService;
import service.UserService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class AdminDashboardController {

    private static final DateTimeFormatter ACTIVITY_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    private final UserService userService = new UserService();
    private final RoleDAO roleDAO = new RoleDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DocumentAlertService documentAlertService = new DocumentAlertService();
    private final MaintenanceAlertService maintenanceAlertService = new MaintenanceAlertService();
    private final AuditLogService auditLogService = new AuditLogService();

    private Runnable openUserManagementHandler = () -> {};
    private Runnable createUserHandler = () -> {};
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
    @FXML private Label lblAttentionPrimaryTitle;
    @FXML private Label lblAttentionPrimaryText;
    @FXML private Label lblAttentionAccountTitle;
    @FXML private Label lblAttentionAccountText;
    @FXML private Label lblAttentionDataTitle;
    @FXML private Label lblAttentionDataText;
    @FXML private VBox activityRowOne;
    @FXML private VBox activityRowTwo;
    @FXML private VBox activityRowThree;
    @FXML private Label lblActivityOneTitle;
    @FXML private Label lblActivityOneMeta;
    @FXML private Label lblActivityTwoTitle;
    @FXML private Label lblActivityTwoMeta;
    @FXML private Label lblActivityThreeTitle;
    @FXML private Label lblActivityThreeMeta;

    @FXML
    public void initialize() {
        refreshMetrics();
    }

    public void setNavigationHandlers(Runnable openUserManagementHandler,
                                      Runnable createUserHandler,
                                      Runnable openAuditLogHandler,
                                      Runnable openAlertSettingsHandler) {
        this.openUserManagementHandler = safeHandler(openUserManagementHandler);
        this.createUserHandler = safeHandler(createUserHandler);
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
        int systemAlerts = documentAlerts + maintenanceAlerts;

        lblTotalUsers.setText(String.valueOf(users.size()));
        lblTotalUsersHint.setText(activeRoles + " vai trò hoạt động");
        lblLockedUsers.setText(String.valueOf(lockedUsers));
        lblLockedUsersHint.setText(lockedUsers > 0 ? "Cần rà soát" : "Đang ổn định");
        lblSystemAlerts.setText(String.valueOf(systemAlerts));
        lblSystemAlertsHint.setText("Giấy tờ + bảo dưỡng");

        renderAttentionItems(lockedUsers, documentAlerts, maintenanceAlerts, vehicleCount, activeRoles);

        List<AuditLog> recentLogs = loadRecentLogs();
        renderAuditMetric(recentLogs);
        renderRecentActivity(recentLogs);
    }

    private List<AuditLog> loadRecentLogs() {
        try {
            return auditLogService.listRecentLogs();
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    private void renderAttentionItems(long lockedUsers,
                                      int documentAlerts,
                                      int maintenanceAlerts,
                                      int vehicleCount,
                                      int activeRoles) {
        int systemAlerts = documentAlerts + maintenanceAlerts;
        if (systemAlerts > 0) {
            lblAttentionPrimaryTitle.setText("Cần xử lý cảnh báo");
            lblAttentionPrimaryText.setText(documentAlerts + " cảnh báo giấy tờ và "
                    + maintenanceAlerts + " cảnh báo bảo dưỡng đang cần theo dõi.");
        } else {
            lblAttentionPrimaryTitle.setText("Cảnh báo ổn định");
            lblAttentionPrimaryText.setText("Chưa có giấy tờ hoặc kế hoạch bảo dưỡng cần xử lý ngay.");
        }

        if (lockedUsers > 0) {
            lblAttentionAccountTitle.setText("Rà soát tài khoản bị khóa");
            lblAttentionAccountText.setText(lockedUsers + " tài khoản đang bị khóa. Kiểm tra trước khi bàn giao tài khoản cho người dùng.");
        } else {
            lblAttentionAccountTitle.setText("Tài khoản sẵn sàng");
            lblAttentionAccountText.setText("Không có tài khoản bị khóa. Có thể tiếp tục quản trị user từ màn Quản lý người dùng.");
        }

        lblAttentionDataTitle.setText("Dữ liệu vận hành");
        lblAttentionDataText.setText(vehicleCount + " phương tiện đang được quản lý, "
                + activeRoles + " vai trò đang kích hoạt.");
    }

    private void renderAuditMetric(List<AuditLog> recentLogs) {
        long todayLogs = recentLogs.stream()
                .filter(log -> log.getCreatedAt() != null)
                .filter(log -> LocalDate.now().equals(log.getCreatedAt().toLocalDate()))
                .count();

        lblRecentAuditLogs.setText(String.valueOf(todayLogs));
        lblRecentAuditLogsHint.setText(todayLogs > 0 ? "Hoạt động trong hôm nay" : "Chưa có nhật ký hôm nay");
    }

    private void renderRecentActivity(List<AuditLog> recentLogs) {
        renderActivityRow(activityRowOne, lblActivityOneTitle, lblActivityOneMeta, recentLogs, 0);
        renderActivityRow(activityRowTwo, lblActivityTwoTitle, lblActivityTwoMeta, recentLogs, 1);
        renderActivityRow(activityRowThree, lblActivityThreeTitle, lblActivityThreeMeta, recentLogs, 2);
    }

    private void renderActivityRow(VBox row,
                                   Label title,
                                   Label meta,
                                   List<AuditLog> recentLogs,
                                   int index) {
        boolean hasLog = recentLogs.size() > index;
        row.setVisible(hasLog || index == 0);
        row.setManaged(hasLog || index == 0);
        if (!hasLog) {
            title.setText(index == 0 ? "Chưa có nhật ký gần đây" : "");
            meta.setText(index == 0 ? "Mở Audit logs để theo dõi hoạt động hệ thống." : "");
            return;
        }

        AuditLog log = recentLogs.get(index);
        title.setText(formatActivityTitle(log));
        meta.setText(formatActivityMeta(log));
    }

    private String formatActivityTitle(AuditLog log) {
        String action = nullToEmpty(log.getAction());
        String username = nullToEmpty(log.getUsername());
        if (username.isEmpty()) {
            return action.isEmpty() ? "Hoạt động hệ thống" : action;
        }
        return (action.isEmpty() ? "Hoạt động" : action) + " - " + username;
    }

    private String formatActivityMeta(AuditLog log) {
        String time = log.getCreatedAt() == null
                ? "Chưa rõ thời gian"
                : ACTIVITY_TIME_FORMATTER.format(log.getCreatedAt());
        String description = nullToEmpty(log.getDescription());
        return description.isEmpty() ? time : time + " | " + description;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Runnable safeHandler(Runnable handler) {
        return handler == null ? () -> {} : handler;
    }
}
