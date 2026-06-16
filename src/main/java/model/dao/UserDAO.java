package model.dao;

import model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO truy cập bảng {@code users}.
 *
 * <p>Chứa toàn bộ câu truy vấn CRUD liên quan tới tài khoản người dùng.
 * Service ({@code AuthService}, {@code UserService}) gọi DAO này thay vì
 * thao tác trực tiếp với JDBC.</p>
 *
 * <p>Day 1: mới chỉ tạo skeleton method signature. Day 2 sẽ implement.</p>
 */
public class UserDAO {

    /**
     * Tìm user theo {@code username}.
     */
    public Optional<User> findByUsername(String username) {
        // TODO Day 2: SELECT * FROM users WHERE username = ?
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.findByUsername");
    }

    /**
     * Tìm user theo khóa chính {@code user_id}.
     */
    public Optional<User> findById(Long userId) {
        // TODO Day 2: SELECT * FROM users WHERE user_id = ?
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.findById");
    }

    /**
     * Chèn mới một user vào DB, trả về {@code user_id} vừa sinh.
     */
    public Long insert(User user) {
        // TODO Day 2: INSERT INTO users (...) VALUES (...)
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.insert");
    }

    /**
     * Cập nhật thông tin user.
     */
    public boolean update(User user) {
        // TODO Day 2: UPDATE users SET ... WHERE user_id = ?
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.update");
    }

    /**
     * Cập nhật trạng thái tài khoản ({@code "ACTIVE"} hoặc {@code "LOCKED"}).
     */
    public boolean updateAccountStatus(Long userId, String status) {
        // TODO Day 2: UPDATE users SET account_status = ? WHERE user_id = ?
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.updateAccountStatus");
    }

    /**
     * Cập nhật thời điểm đăng nhập gần nhất.
     */
    public boolean updateLastLogin(Long userId, LocalDateTime ts) {
        // TODO Day 2: UPDATE users SET last_login_at = ? WHERE user_id = ?
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.updateLastLogin");
    }

    /**
     * Lấy toàn bộ user trong hệ thống.
     */
    public List<User> findAll() {
        // TODO Day 2: SELECT * FROM users
        throw new UnsupportedOperationException("TODO Day 2: UserDAO.findAll");
    }
}
