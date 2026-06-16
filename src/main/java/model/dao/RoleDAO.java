package model.dao;

import model.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * DAO truy cập bảng {@code roles}.
 *
 * <p>Phục vụ cho việc tra cứu vai trò khi đăng nhập (gắn role vào
 * {@code UserSession}) và khi tạo / sửa người dùng. Day 2 sẽ implement.</p>
 */
public class RoleDAO {

    /**
     * Tìm role theo khóa chính {@code role_id}.
     */
    public Optional<Role> findById(Long roleId) {
        // TODO Day 2: SELECT * FROM roles WHERE role_id = ?
        throw new UnsupportedOperationException("TODO Day 2: RoleDAO.findById");
    }

    /**
     * Tìm role theo mã ({@code ADMIN}, {@code MANAGER}, {@code TECH}, ...).
     */
    public Optional<Role> findByCode(String roleCode) {
        // TODO Day 2: SELECT * FROM roles WHERE role_code = ?
        throw new UnsupportedOperationException("TODO Day 2: RoleDAO.findByCode");
    }

    /**
     * Lấy danh sách các role đang còn hiệu lực ({@code is_active = TRUE}).
     */
    public List<Role> findAllActive() {
        // TODO Day 2: SELECT * FROM roles WHERE is_active = TRUE
        throw new UnsupportedOperationException("TODO Day 2: RoleDAO.findAllActive");
    }
}
