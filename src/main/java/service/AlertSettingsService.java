package service;

import model.dao.AlertSettingsDAO;
import model.entity.AlertSettings;
import model.entity.User;
import session.UserSession;

public class AlertSettingsService {

    private static final int MIN_DAYS = 1;
    private static final int MAX_DAYS = 365;
    private static final int MIN_KM = 1;
    private static final int MAX_KM = 100_000;

    private final AlertSettingsDAO alertSettingsDAO = new AlertSettingsDAO();
    private final AuditLogService auditLogService = new AuditLogService();

    public AlertSettings getSettings() {
        return alertSettingsDAO.findOrCreateDefault();
    }

    public AlertSettings updateSettings(int documentAlertDays,
                                        int maintenanceAlertDays,
                                        int maintenanceAlertKm,
                                        boolean active) {
        validateRange(documentAlertDays, MIN_DAYS, MAX_DAYS, "Số ngày cảnh báo giấy tờ");
        validateRange(maintenanceAlertDays, MIN_DAYS, MAX_DAYS, "Số ngày cảnh báo bảo dưỡng");
        validateRange(maintenanceAlertKm, MIN_KM, MAX_KM, "Số km cảnh báo bảo dưỡng");

        AlertSettings settings = new AlertSettings();
        settings.setSettingId(1);
        settings.setDocumentAlertDays(documentAlertDays);
        settings.setMaintenanceAlertDays(maintenanceAlertDays);
        settings.setMaintenanceAlertKm(maintenanceAlertKm);
        settings.setActive(active);
        settings.setUpdatedBy(resolveCurrentUserId());

        AlertSettings updatedSettings = alertSettingsDAO.update(settings);
        auditLogService.recordSettingsUpdated(updatedSettings);
        return updatedSettings;
    }

    private void validateRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " phải nằm trong khoảng " + min + " - " + max + ".");
        }
    }

    private Long resolveCurrentUserId() {
        User currentUser = UserSession.getInstance().getCurrentUser();
        return currentUser == null ? null : currentUser.getUserId();
    }
}
