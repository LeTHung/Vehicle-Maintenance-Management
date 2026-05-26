package model.dao;

import database.DatabaseConnection;
import model.entity.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO truy cập bảng {@code roles}.
 *
 * <p>Phục vụ cho việc tra cứu vai trò khi đăng nhập (gắn role vào
 * {@code UserSession}) và khi tạo / sửa người dùng.</p>
 */
public class RoleDAO {

    /**
     * Tìm role theo khóa chính {@code role_id}.
     */
    public Optional<Role> findById(Long roleId) {
        if (roleId == null) {
            return Optional.empty();
        }

        String sql = """
                SELECT role_id, role_code, role_name, description,
                       is_active, created_at, updated_at
                FROM roles
                WHERE role_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, roleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToRole(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Tìm role theo mã ({@code ADMIN}, {@code MANAGER}, {@code TECH}, ...).
     */
    public Optional<Role> findByCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return Optional.empty();
        }

        String sql = """
                SELECT role_id, role_code, role_name, description,
                       is_active, created_at, updated_at
                FROM roles
                WHERE role_code = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, roleCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapToRole(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Lấy danh sách các role đang còn hiệu lực ({@code is_active = TRUE}).
     */
    public List<Role> findAllActive() {
        List<Role> roles = new ArrayList<>();

        String sql = """
                SELECT role_id, role_code, role_name, description,
                       is_active, created_at, updated_at
                FROM roles
                WHERE is_active = 1
                ORDER BY role_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                roles.add(mapToRole(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roles;
    }

    private Role mapToRole(ResultSet resultSet) throws SQLException {
        Role role = new Role();

        role.setRoleId(resultSet.getLong("role_id"));
        role.setRoleCode(resultSet.getString("role_code"));
        role.setRoleName(resultSet.getString("role_name"));
        role.setDescription(resultSet.getString("description"));
        role.setActive(resultSet.getBoolean("is_active"));
        role.setCreatedAt(toLocalDateTime(resultSet.getTimestamp("created_at")));
        role.setUpdatedAt(toLocalDateTime(resultSet.getTimestamp("updated_at")));

        return role;
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
