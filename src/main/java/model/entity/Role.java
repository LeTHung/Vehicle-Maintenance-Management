package model.entity;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng {@code roles}.
 *
 * <p>Mô tả vai trò người dùng trong hệ thống. Các vai trò chuẩn trong dự án:
 * {@code ADMIN}, {@code MANAGER}, {@code TECH}. Trường {@code roleCode} là
 * mã định danh dùng cho phân quyền (RBAC), còn {@code roleName} là tên
 * hiển thị bằng tiếng Việt.</p>
 */
public class Role {

    private Long roleId;
    private String roleCode;
    private String roleName;
    private String description;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Role() {
    }

    public Role(Long roleId,
                String roleCode,
                String roleName,
                String description,
                boolean isActive,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
