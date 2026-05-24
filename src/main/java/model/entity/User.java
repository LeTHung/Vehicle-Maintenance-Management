package model.entity;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng {@code users}.
 *
 * <p>Lưu thông tin tài khoản người dùng đăng nhập vào hệ thống FleetCare:
 * thông tin đăng nhập, hồ sơ cá nhân, vai trò, trạng thái tài khoản và
 * các mốc thời gian hệ thống (lần đăng nhập gần nhất, ngày tạo, ngày cập nhật).</p>
 *
 * <p>Cột {@code account_status} nhận hai giá trị ENUM: {@code "ACTIVE"} và
 * {@code "LOCKED"}.</p>
 */
public class User {

    private Long userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private Long roleId;
    private String accountStatus;
    private boolean mustChangePassword;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(Long userId,
                String username,
                String passwordHash,
                String fullName,
                String email,
                String phone,
                Long roleId,
                String accountStatus,
                boolean mustChangePassword,
                LocalDateTime lastLoginAt,
                LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.accountStatus = accountStatus;
        this.mustChangePassword = mustChangePassword;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
