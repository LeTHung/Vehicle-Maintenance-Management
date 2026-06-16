package service;

import model.dao.RoleDAO;
import model.dao.UserDAO;
import model.entity.Role;
import model.entity.User;
import session.UserSession;
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
    private static final String ROLE_ADMIN = "ADMIN";
    private static final int MIN_PASSWORD_LENGTH = 8;

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
        validateNewPassword(normalizedPassword, normalizedPassword, null);

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

    public User changeOwnPassword(String currentPassword,
                                  String newPassword,
                                  String confirmPassword) {
        User sessionUser = UserSession.getInstance().getCurrentUser();
        if (sessionUser == null || sessionUser.getUserId() == null) {
            throw new IllegalStateException("Phiên đăng nhập không hợp lệ. Vui lòng đăng nhập lại.");
        }

        User currentUser = userDAO.findById(sessionUser.getUserId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy tài khoản hiện tại."));

        if (!PasswordUtil.verify(currentPassword, currentUser.getPasswordHash())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }

        validateNewPassword(newPassword, confirmPassword, currentUser.getPasswordHash());
        String passwordHash = PasswordUtil.hash(newPassword);

        if (!userDAO.updatePassword(currentUser.getUserId(), passwordHash, false)) {
            throw new IllegalStateException("Không thể cập nhật mật khẩu. Vui lòng thử lại.");
        }

        User updatedUser = userDAO.findById(currentUser.getUserId()).orElse(currentUser);
        sessionUser.setPasswordHash(updatedUser.getPasswordHash());
        sessionUser.setMustChangePassword(false);
        auditLogService.recordPasswordChanged(updatedUser);
        return updatedUser;
    }

    public User adminResetPassword(Long targetUserId,
                                   String newPassword,
                                   String confirmPassword) {
        requireCurrentAdmin();
        if (targetUserId == null) {
            throw new IllegalArgumentException("Vui lòng chọn tài khoản cần reset mật khẩu.");
        }

        User currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser != null && targetUserId.equals(currentUser.getUserId())) {
            throw new IllegalArgumentException("Không reset mật khẩu của chính mình tại đây. Vui lòng dùng chức năng Đổi mật khẩu của tôi.");
        }

        User targetUser = userDAO.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản cần reset mật khẩu không tồn tại."));

        validateNewPassword(newPassword, confirmPassword, targetUser.getPasswordHash());
        String passwordHash = PasswordUtil.hash(newPassword);

        if (!userDAO.updatePassword(targetUserId, passwordHash, true)) {
            throw new IllegalStateException("Không thể reset mật khẩu. Vui lòng thử lại.");
        }

        User updatedUser = userDAO.findById(targetUserId).orElse(targetUser);
        auditLogService.recordPasswordResetByAdmin(updatedUser);
        return updatedUser;
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

    private void requireCurrentAdmin() {
        Role currentRole = UserSession.getInstance().getCurrentRole();
        String roleCode = currentRole == null ? null : currentRole.getRoleCode();
        if (!ROLE_ADMIN.equalsIgnoreCase(roleCode == null ? "" : roleCode.trim())) {
            throw new IllegalStateException("Chỉ quản trị viên mới được reset mật khẩu tài khoản khác.");
        }
    }

    private void validateNewPassword(String newPassword,
                                     String confirmPassword,
                                     String currentPasswordHash) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        if (confirmPassword == null || !newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Xác nhận mật khẩu mới không khớp.");
        }
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
        }
        if (!containsLetter(newPassword) || !containsDigit(newPassword)) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất một chữ cái và một chữ số.");
        }
        if (PasswordUtil.verify(newPassword, currentPasswordHash)) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng mật khẩu hiện tại.");
        }
    }

    private boolean containsLetter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetter(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                return true;
            }
        }
        return false;
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
