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
 * Service quan ly tai khoan nguoi dung.
 *
 * <p>Day 5: implement CRUD co ban cho man hinh quan tri user.</p>
 */
public class UserService {

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_LOCKED = "LOCKED";

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    /**
     * Tao moi mot tai khoan nguoi dung.
     */
    public User createUser(String username,
                           String rawPassword,
                           String fullName,
                           String email,
                           String phone,
                           Long roleId) {
        String normalizedUsername = requireText(username, "Ten dang nhap khong duoc de trong.");
        String normalizedPassword = requireText(rawPassword, "Mat khau khong duoc de trong.");
        String normalizedFullName = requireText(fullName, "Ho ten khong duoc de trong.");

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
            throw new IllegalStateException("Khong the tao tai khoan. Vui long kiem tra du lieu va thu lai.");
        }

        return userDAO.findById(generatedId).orElse(user);
    }

    /**
     * Cap nhat thong tin tai khoan, khong doi mat khau o method nay.
     */
    public User updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            throw new IllegalArgumentException("Vui long chon tai khoan can cap nhat.");
        }

        String normalizedUsername = requireText(user.getUsername(), "Ten dang nhap khong duoc de trong.");
        String normalizedFullName = requireText(user.getFullName(), "Ho ten khong duoc de trong.");
        String normalizedStatus = normalizeStatus(user.getAccountStatus());

        validateUsernameAvailable(normalizedUsername, user.getUserId());
        validateRole(user.getRoleId());

        user.setUsername(normalizedUsername);
        user.setFullName(normalizedFullName);
        user.setEmail(normalizeOptional(user.getEmail()));
        user.setPhone(normalizeOptional(user.getPhone()));
        user.setAccountStatus(normalizedStatus);

        if (!userDAO.update(user)) {
            throw new IllegalStateException("Khong the cap nhat tai khoan. Vui long thu lai.");
        }

        return userDAO.findById(user.getUserId()).orElse(user);
    }

    /**
     * Khoa tai khoan: chuyen account_status sang LOCKED.
     */
    public void lockUser(Long userId) {
        updateAccountStatus(userId, STATUS_LOCKED);
    }

    /**
     * Mo khoa tai khoan: chuyen account_status sang ACTIVE.
     */
    public void unlockUser(Long userId) {
        updateAccountStatus(userId, STATUS_ACTIVE);
    }

    /**
     * Liet ke toan bo user phuc vu trang quan tri.
     */
    public List<User> listUsers() {
        return userDAO.findAll();
    }

    /**
     * Liet ke role dang active de hien thi trong form them/sua user.
     */
    public List<Role> listActiveRoles() {
        return roleDAO.findAllActive();
    }

    private void updateAccountStatus(Long userId, String status) {
        if (userId == null) {
            throw new IllegalArgumentException("Vui long chon tai khoan.");
        }

        if (!userDAO.updateAccountStatus(userId, status)) {
            throw new IllegalStateException("Khong the cap nhat trang thai tai khoan. Vui long thu lai.");
        }
    }

    private void validateUsernameAvailable(String username, Long currentUserId) {
        Optional<User> existing = userDAO.findByUsername(username);
        if (existing.isEmpty()) {
            return;
        }

        Long existingId = existing.get().getUserId();
        if (currentUserId == null || !currentUserId.equals(existingId)) {
            throw new IllegalArgumentException("Ten dang nhap da ton tai.");
        }
    }

    private void validateRole(Long roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("Vui long chon vai tro.");
        }

        Role role = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Vai tro khong ton tai."));

        if (!role.isActive()) {
            throw new IllegalArgumentException("Vai tro da bi vo hieu hoa.");
        }
    }

    private String normalizeStatus(String status) {
        String normalizedStatus = status == null ? STATUS_ACTIVE : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUS_ACTIVE.equals(normalizedStatus) && !STATUS_LOCKED.equals(normalizedStatus)) {
            throw new IllegalArgumentException("Trang thai tai khoan khong hop le.");
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
