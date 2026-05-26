package service;

import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.Role;
import model.entity.User;
import session.UserSession;
import util.PasswordUtil;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Service xử lý nghiệp vụ đăng nhập / đăng xuất hệ thống FleetCare.
 *
 * <p>Phối hợp giữa {@code UserDAO}, {@code RoleDAO}, {@code PasswordUtil}
 * và {@code UserSession}: xác thực mật khẩu bằng BCrypt, kiểm tra trạng thái
 * tài khoản, ghi nhận thời điểm đăng nhập, và nạp {@code currentUser} +
 * {@code currentRole} vào session.</p>
 *
 * <p>Day 1: skeleton. Day 4 sẽ implement đầy đủ.</p>
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    /**
     * Đăng nhập bằng cặp ({@code username}, {@code rawPassword}).
     *
     * @return entity {@link User} đã đăng nhập thành công
     * @throws AuthenticationException khi sai thông tin hoặc tài khoản bị khóa
     */
    public User login(String username, String rawPassword) throws AuthenticationException {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedPassword = rawPassword == null ? "" : rawPassword.trim();

        if (normalizedUsername.isEmpty() || normalizedPassword.isEmpty()) {
            throw new AuthenticationException("Vui lòng nhập tên đăng nhập và mật khẩu.");
        }

        User user = userDAO.findByUsername(normalizedUsername)
                .orElseThrow(() -> new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng."));

        if (!PasswordUtil.verify(normalizedPassword, user.getPasswordHash())) {
            throw new AuthenticationException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        if (isLocked(user.getAccountStatus())) {
            throw new AuthenticationException("Tài khoản đã bị khóa.");
        }

        Role role = roleDAO.findById(user.getRoleId())
                .orElseThrow(() -> new AuthenticationException("Không tìm thấy vai trò của tài khoản."));

        if (!role.isActive()) {
            throw new AuthenticationException("Vai trò hiện tại đang bị vô hiệu hóa.");
        }

        role.setRoleCode(normalizeRoleCode(role.getRoleCode()));

        UserSession session = UserSession.getInstance();
        session.setCurrentUser(user);
        session.setCurrentRole(role);

        userDAO.updateLastLogin(user.getUserId(), LocalDateTime.now());

        return user;
    }

    /**
     * Đăng xuất phiên hiện tại: xoá thông tin trong {@code UserSession}.
     */
    public void logout() {
        UserSession.getInstance().clear();
    }

    private boolean isLocked(String accountStatus) {
        if (accountStatus == null || accountStatus.isBlank()) {
            return false;
        }

        return "LOCKED".equalsIgnoreCase(accountStatus.trim());
    }

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return roleCode;
        }

        String normalized = roleCode.trim().toUpperCase(Locale.ROOT);

        return switch (normalized) {
            case "FLEET_MANAGER" -> "MANAGER";
            case "TECHNICIAN" -> "TECH";
            default -> normalized;
        };
    }
}
