-- FleetCare audit logs schema and demo data
-- Run after the base schema creates the users table.

CREATE DATABASE IF NOT EXISTS vehicle_maintenance_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE vehicle_maintenance_management;

CREATE TABLE IF NOT EXISTS audit_logs (
    audit_log_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id BIGINT UNSIGNED NULL,
    username VARCHAR(50) NULL,
    action VARCHAR(60) NOT NULL,
    entity_type VARCHAR(60) NULL,
    entity_id VARCHAR(64) NULL,
    description VARCHAR(500) NULL,
    ip_address VARCHAR(45) NULL,
    device VARCHAR(120) NULL,
    session_id VARCHAR(120) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_log_id),
    KEY idx_audit_logs_created_at (created_at),
    KEY idx_audit_logs_user_id (user_id),
    KEY idx_audit_logs_action (action),
    KEY idx_audit_logs_entity (entity_type, entity_id),
    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id) REFERENCES users (user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, description, created_at)
SELECT u.user_id,
       u.username,
       'LOGIN_SUCCESS',
       'AUTH',
       NULL,
       'Demo: quản trị viên đăng nhập thành công.',
       NOW() - INTERVAL 3 HOUR
FROM users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM audit_logs
      WHERE action = 'LOGIN_SUCCESS'
        AND description = 'Demo: quản trị viên đăng nhập thành công.'
  );

INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, description, created_at)
SELECT u.user_id,
       u.username,
       'USER_UPDATED',
       'USER',
       CAST(u.user_id AS CHAR),
       'Demo: cập nhật thông tin tài khoản admin.',
       NOW() - INTERVAL 2 HOUR
FROM users u
WHERE u.username = 'admin'
  AND NOT EXISTS (
      SELECT 1 FROM audit_logs
      WHERE action = 'USER_UPDATED'
        AND description = 'Demo: cập nhật thông tin tài khoản admin.'
  );

INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, description, created_at)
SELECT NULL,
       'unknown',
       'LOGIN_FAILED',
       'AUTH',
       NULL,
       'Demo: đăng nhập thất bại cho tài khoản không tồn tại.',
       NOW() - INTERVAL 1 HOUR
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs
    WHERE action = 'LOGIN_FAILED'
      AND description = 'Demo: đăng nhập thất bại cho tài khoản không tồn tại.'
);

SELECT audit_log_id, username, action, entity_type, entity_id, description, created_at
FROM audit_logs
ORDER BY created_at DESC, audit_log_id DESC
LIMIT 20;
