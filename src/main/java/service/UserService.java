package service;

import model.entity.User;

import java.util.List;

/**
 * Service quản lý tài khoản người dùng.
 *
 * <p>Đảm nhận các nghiệp vụ: tạo tài khoản (hash mật khẩu trước khi lưu),
 * cập nhật hồ sơ, khoá / mở khoá, liệt kê danh sách user phục vụ trang
 * quản trị.</p>
 *
 * <p>Day 1: skeleton. Day 5 sẽ implement.</p>
 */
public class UserService {

    /**
     * Tạo mới một tài khoản người dùng.
     */
    public User createUser(String username,
                           String rawPassword,
                           String fullName,
                           String email,
                           String phone,
                           Long roleId) {
        // TODO Day 5: hash mật khẩu bằng PasswordUtil -> UserDAO.insert
        throw new UnsupportedOperationException("TODO Day 5: UserService.createUser");
    }

    /**
     * Cập nhật thông tin tài khoản (không đổi mật khẩu ở method này).
     */
    public User updateUser(User user) {
        // TODO Day 5: UserDAO.update + trả về entity sau update
        throw new UnsupportedOperationException("TODO Day 5: UserService.updateUser");
    }

    /**
     * Khoá tài khoản: chuyển {@code account_status} sang {@code "LOCKED"}.
     */
    public void lockUser(Long userId) {
        // TODO Day 5: UserDAO.updateAccountStatus(userId, "LOCKED")
        throw new UnsupportedOperationException("TODO Day 5: UserService.lockUser");
    }

    /**
     * Mở khoá tài khoản: chuyển {@code account_status} sang {@code "ACTIVE"}.
     */
    public void unlockUser(Long userId) {
        // TODO Day 5: UserDAO.updateAccountStatus(userId, "ACTIVE")
        throw new UnsupportedOperationException("TODO Day 5: UserService.unlockUser");
    }

    /**
     * Liệt kê toàn bộ user phục vụ trang quản trị.
     */
    public List<User> listUsers() {
        // TODO Day 5: UserDAO.findAll
        throw new UnsupportedOperationException("TODO Day 5: UserService.listUsers");
    }
}
