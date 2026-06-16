package model.dao;

import database.DatabaseConnection;
import model.entity.User;

import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;
import java.util.Optional;

/**
 * DAO truy cập bảng {@code users}.
 *
 * <p>Chứa toàn bộ câu truy vấn CRUD liên quan tới tài khoản người dùng.
 * Service ({@code AuthService}, {@code UserService}) gọi DAO này thay vì
 * thao tác trực tiếp với JDBC.</p>
 *
 * <p>Day 2: triển khai JDBC thật cho các thao tác tài khoản cơ bản.</p>
 */
public class UserDAO {

    /**
     * Tìm user theo {@code username}.
     */
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }

        String sql = """
                SELECT user_id, username, password_hash, full_name, email, phone,
                       role_id, account_status, must_change_password,
                       last_login_at, created_at, updated_at
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Tìm user theo khóa chính {@code user_id}.
     */
    public Optional<User> findById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        String sql = """
                SELECT user_id, username, password_hash, full_name, email, phone,
                       role_id, account_status, must_change_password,
                       last_login_at, created_at, updated_at
                FROM users
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Chèn mới một user vào DB, trả về {@code user_id} vừa sinh.
     */
    public Long insert(User user) {
        if (user == null) {
            return null;
        }

        String sql = """
                INSERT INTO users (
                    username, password_hash, full_name, email, phone,
                    role_id, account_status, must_change_password
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            setNullableString(statement, 4, user.getEmail());
            setNullableString(statement, 5, user.getPhone());
            statement.setObject(6, user.getRoleId(), Types.BIGINT);
            statement.setString(7, normalizeStatus(user.getAccountStatus()));
            statement.setBoolean(8, user.isMustChangePassword());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long generatedId = generatedKeys.getLong(1);
                    user.setUserId(generatedId);
                    return generatedId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Cập nhật thông tin user.
     */
    public boolean update(User user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }

        String sql = """
                UPDATE users
                SET username = ?,
                    full_name = ?,
                    email = ?,
                    phone = ?,
                    role_id = ?,
                    account_status = ?,
                    must_change_password = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getFullName());
            setNullableString(statement, 3, user.getEmail());
            setNullableString(statement, 4, user.getPhone());
            statement.setObject(5, user.getRoleId(), Types.BIGINT);
            statement.setString(6, normalizeStatus(user.getAccountStatus()));
            statement.setBoolean(7, user.isMustChangePassword());
            statement.setLong(8, user.getUserId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật trạng thái tài khoản ({@code "ACTIVE"} hoặc {@code "LOCKED"}).
     */
    public boolean updateAccountStatus(Long userId, String status) {
        if (userId == null) {
            return false;
        }

        String sql = """
                UPDATE users
                SET account_status = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizeStatus(status));
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updatePassword(Long userId, String passwordHash, boolean mustChangePassword) {
        if (userId == null || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }

        String sql = """
                UPDATE users
                SET password_hash = ?,
                    must_change_password = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setBoolean(2, mustChangePassword);
            statement.setLong(3, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Cập nhật thời điểm đăng nhập gần nhất.
     */
    public boolean updateLastLogin(Long userId, LocalDateTime ts) {
        if (userId == null) {
            return false;
        }

        String sql = """
                UPDATE users
                SET last_login_at = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            setNullableTimestamp(statement, 1, ts);
            statement.setLong(2, userId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Lấy toàn bộ user trong hệ thống.
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        String sql = """
                SELECT user_id, username, password_hash, full_name, email, phone,
                       role_id, account_status, must_change_password,
                       last_login_at, created_at, updated_at
                FROM users
                ORDER BY created_at DESC, user_id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(mapToUser(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    private User mapToUser(ResultSet resultSet) throws SQLException {
        User user = new User();

        user.setUserId(resultSet.getLong("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setPhone(resultSet.getString("phone"));
        user.setRoleId(resultSet.getLong("role_id"));
        user.setAccountStatus(resultSet.getString("account_status"));
        user.setMustChangePassword(resultSet.getBoolean("must_change_password"));
        user.setLastLoginAt(toLocalDateTime(resultSet.getTimestamp("last_login_at")));
        user.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        user.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));

        return user;
    }

    private String normalizeStatus(String status) {
        return status == null || status.isBlank() ? "ACTIVE" : status.trim().toUpperCase(Locale.ROOT);
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        statement.setString(index, value);
    }

    private void setNullableTimestamp(PreparedStatement statement, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.TIMESTAMP);
            return;
        }

        statement.setTimestamp(index, Timestamp.valueOf(value));
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
