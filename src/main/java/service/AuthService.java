package service;

import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.Role;
import model.entity.User;
import session.UserSession;
import util.PasswordUtil;

import java.time.LocalDateTime;

public class AuthService {

    private static final String STATUS_LOCKED = "LOCKED";

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final AuditLogService auditLogService = new AuditLogService();

    public User login(String username, String rawPassword) throws AuthenticationException {
        String attemptedUsername = username == null ? null : username.trim();
        if (username == null || username.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            AuthenticationException exception = new AuthenticationException("Vui lòng nhập tên đăng nhập và mật khẩu.");
            auditLogService.recordLoginFailure(attemptedUsername, exception.getMessage());
            throw exception;
        }

        try {
            User user = userDAO.findByUsername(attemptedUsername)
                    .orElseThrow(() -> new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng."));

            if (STATUS_LOCKED.equalsIgnoreCase(user.getAccountStatus())) {
                throw new AuthenticationException("Tài khoản đã bị khóa.");
            }

            if (!PasswordUtil.verify(rawPassword, user.getPasswordHash())) {
                throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng.");
            }

            Role role = roleDAO.findById(user.getRoleId())
                    .orElseThrow(() -> new AuthenticationException("Tài khoản chưa được gán vai trò hợp lệ."));

            if (!role.isActive()) {
                throw new AuthenticationException("Vai trò của tài khoản đã bị vô hiệu hóa.");
            }

            userDAO.updateLastLogin(user.getUserId(), LocalDateTime.now());
            UserSession.getInstance().login(user, role);
            auditLogService.recordLoginSuccess(user);
            return user;
        } catch (AuthenticationException e) {
            auditLogService.recordLoginFailure(attemptedUsername, e.getMessage());
            throw e;
        }
    }

    public void logout() {
        auditLogService.recordLogout(UserSession.getInstance().getCurrentUser());
        UserSession.getInstance().clear();
    }
}
