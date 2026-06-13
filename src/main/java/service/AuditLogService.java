package service;

import model.dao.AuditLogDAO;
import model.entity.AuditLog;
import model.entity.User;
import session.UserSession;

import java.util.List;

public class AuditLogService {

    private static final int DEFAULT_RECENT_LIMIT = 200;

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public List<AuditLog> listRecentLogs() {
        return auditLogDAO.findRecent(DEFAULT_RECENT_LIMIT);
    }

    public void recordLoginSuccess(User user) {
        record(
                resolveUserId(user),
                resolveUsername(user),
                "LOGIN_SUCCESS",
                "AUTH",
                null,
                "Đăng nhập thành công.");
    }

    public void recordLoginFailure(String username, String reason) {
        String attemptedUsername = normalizeOptional(username);
        String message = "Đăng nhập thất bại";
        if (attemptedUsername != null) {
            message += " cho tài khoản '" + attemptedUsername + "'";
        }
        String normalizedReason = trimTrailingPeriods(reason);
        if (normalizedReason != null) {
            message += ". Lý do: " + normalizedReason;
        }
        message += ".";

        record(null, attemptedUsername, "LOGIN_FAILED", "AUTH", null, message);
    }

    public void recordLogout(User user) {
        record(
                resolveUserId(user),
                resolveUsername(user),
                "LOGOUT",
                "AUTH",
                null,
                "Đăng xuất khỏi hệ thống.");
    }

    public void recordUserCreated(User targetUser) {
        recordActorEvent(
                "USER_CREATED",
                "USER",
                resolveEntityId(targetUser),
                "Tạo tài khoản '" + resolveDisplayName(targetUser) + "'.");
    }

    public void recordUserUpdated(User targetUser) {
        recordActorEvent(
                "USER_UPDATED",
                "USER",
                resolveEntityId(targetUser),
                "Cập nhật tài khoản '" + resolveDisplayName(targetUser) + "'.");
    }

    public void recordUserLocked(User targetUser) {
        recordActorEvent(
                "USER_LOCKED",
                "USER",
                resolveEntityId(targetUser),
                "Khóa tài khoản '" + resolveDisplayName(targetUser) + "'.");
    }

    public void recordUserUnlocked(User targetUser) {
        recordActorEvent(
                "USER_UNLOCKED",
                "USER",
                resolveEntityId(targetUser),
                "Mở khóa tài khoản '" + resolveDisplayName(targetUser) + "'.");
    }

    private void recordActorEvent(String action, String entityType, String entityId, String description) {
        User actor = UserSession.getInstance().getCurrentUser();
        record(resolveUserId(actor), resolveUsername(actor), action, entityType, entityId, description);
    }

    private void record(Long userId,
                        String username,
                        String action,
                        String entityType,
                        String entityId,
                        String description) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setUsername(username);
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDescription(description);
            auditLogDAO.insert(auditLog);
        } catch (RuntimeException e) {
            System.out.println("Khong the ghi audit log: " + e.getMessage());
        }
    }

    private Long resolveUserId(User user) {
        return user == null ? null : user.getUserId();
    }

    private String resolveUsername(User user) {
        if (user == null) {
            return null;
        }
        return normalizeOptional(user.getUsername());
    }

    private String resolveEntityId(User user) {
        return user == null || user.getUserId() == null ? null : String.valueOf(user.getUserId());
    }

    private String resolveDisplayName(User user) {
        if (user == null) {
            return "không xác định";
        }
        String username = normalizeOptional(user.getUsername());
        if (username != null) {
            return username;
        }
        Long userId = user.getUserId();
        return userId == null ? "không xác định" : "#" + userId;
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String trimTrailingPeriods(String value) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            return null;
        }

        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }
        return normalized.isBlank() ? null : normalized;
    }
}
