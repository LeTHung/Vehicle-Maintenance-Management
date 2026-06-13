package service;

import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.Role;
import model.entity.User;
import util.PasswordUtil;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Service quản lý tài khoản người dùng.
 *
 * <p>Day 5: implement CRUD cơ bản cho màn hình quản trị user.</p>
 */
public class UserService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_LOCKED = "LOCKED";

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final AuditLogService auditLogService = new AuditLogService();

    /**
     * Tạo mới một tài khoản người dùng.
     */
    public User createUser(String username,
                           String rawPassword,
                           String fullName,
                           String email,
                           String phone,
                           Long roleId) {
        String normalizedUsername = requireText(username, "Tên đăng nhập không được để trống.");
        String normalizedPassword = requireText(rawPassword, "Mật khẩu không được để trống.");
        String normalizedFullName = requireText(fullName, "Họ tên không được để trống.");

        validateUsernameAvailable(normalizedUsername, null);
        validateRole(roleId);

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPasswordHash(PasswordUtil.hash(normalizedPassword));
        user.setFullName(normalizedFullName);
        user.setEmail(normalizeOptional(email));
        user.setPhone(normalizeOptional(phone));
        user.setRoleId(roleId);
        user.setAccountStatus(STATUS_ACTIVE);
        user.setMustChangePassword(false);

        Long generatedId = userDAO.insert(user);
        if (generatedId == null) {
            throw new IllegalStateException("Không thể tạo tài khoản. Vui lòng kiểm tra dữ liệu và thử lại.");
        }

        User createdUser = userDAO.findById(generatedId).orElse(user);
        auditLogService.recordUserCreated(createdUser);
        return createdUser;
    }

    /**
     * Cập nhật thông tin tài khoản, không đổi mật khẩu ở method này.
     */
    public User updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn tài khoản cần cập nhật.");
        }

        String normalizedUsername = requireText(user.getUsername(), "Tên đăng nhập không được để trống.");
        String normalizedFullName = requireText(user.getFullName(), "Họ tên không được để trống.");
        String normalizedStatus = normalizeStatus(user.getAccountStatus());

        validateUsernameAvailable(normalizedUsername, user.getUserId());
        validateRole(user.getRoleId());

        user.setUsername(normalizedUsername);
        user.setFullName(normalizedFullName);
        user.setEmail(normalizeOptional(user.getEmail()));
        user.setPhone(normalizeOptional(user.getPhone()));
        user.setAccountStatus(normalizedStatus);

        if (!userDAO.update(user)) {
            throw new IllegalStateException("Không thể cập nhật tài khoản. Vui lòng thử lại.");
        }

        User updatedUser = userDAO.findById(user.getUserId()).orElse(user);
        auditLogService.recordUserUpdated(updatedUser);
        return updatedUser;
    }

    /**
     * Khóa tài khoản: chuyển account_status sang LOCKED.
     */
    public void lockUser(Long userId) {
        User updatedUser = updateAccountStatus(userId, STATUS_LOCKED);
        auditLogService.recordUserLocked(updatedUser);
    }

    /**
     * Mở khóa tài khoản: chuyển account_status sang ACTIVE.
     */
    public void unlockUser(Long userId) {
        User updatedUser = updateAccountStatus(userId, STATUS_ACTIVE);
        auditLogService.recordUserUnlocked(updatedUser);
    }

    /**
     * Liệt kê toàn bộ user phục vụ trang quản trị.
     */
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    /**
     * Liệt kê role đang active để hiển thị trong form thêm/sửa user.
     */
    public List<Role> listActiveRoles() {
        return roleDAO.findAllActive();
    }

    private User updateAccountStatus(Long userId, String status) {
        if (userId == null) {
            throw new IllegalArgumentException("Vui lòng chọn tài khoản.");
        }

        if (!userDAO.updateAccountStatus(userId, status)) {
            throw new IllegalStateException("Không thể cập nhật trạng thái tài khoản. Vui lòng thử lại.");
        }

        return userDAO.findById(userId).orElseGet(() -> {
            User user = new User();
            user.setUserId(userId);
            user.setAccountStatus(status);
            return user;
        });
    }

    private void validateUsernameAvailable(String username, Long currentUserId) {
        Optional<User> existing = userDAO.findByUsername(username);
        if (existing.isEmpty()) {
            return;
        }

        Long existingId = existing.get().getUserId();
        if (currentUserId == null || !currentUserId.equals(existingId)) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
    }

    private void validateRole(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("Vui lòng chọn vai trò.");
        }

        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại."));

        if (!role.isActive()) {
            throw new IllegalArgumentException("Vai trò đã bị vô hiệu hóa.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null ? STATUS_ACTIVE : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_LOCKED.equals(normalizedStatus)) {
            throw new IllegalArgumentException("Trạng thái tài khoản không hợp lệ.");
        }

        return normalizedStatus;
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }

        return value.trim();
    }

    private String normalizeOptional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
