-- Seed roles and sample users for FleetCare
-- Password for all sample accounts: 123456
-- BCrypt hash generated with cost 12

USE vehicle_maintenance_management;

INSERT INTO roles (role_id, role_code, role_name, description, is_active, created_at, updated_at)
VALUES
    (1, 'ADMIN', 'Quan tri he thong', 'Quan tri he thong, tai khoan, cau hinh', 1, NOW(), NOW()),
    (2, 'FLEET_MANAGER', 'Quan ly doi xe', 'Quan ly ho so xe, giay to, ke hoach, bao cao', 1, NOW(), NOW()),
    (3, 'TECHNICIAN', 'Nhan vien ky thuat', 'Cap nhat bao duong, sua chua, phu tung, ODO', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    role_code = VALUES(role_code),
    role_name = VALUES(role_name),
    description = VALUES(description),
    is_active = VALUES(is_active);

INSERT INTO users (
    username,
    password_hash,
    full_name,
    email,
    phone,
    role_id,
    account_status,
    must_change_password
) VALUES
    (
        'admin',
        '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
        'Quan tri vien',
        'admin@fleetcare.local',
        '0900000001',
        (SELECT role_id FROM roles WHERE role_code = 'ADMIN' LIMIT 1),
        'ACTIVE',
        0
    ),
    (
        'manager',
        '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
        'Quan ly doi xe',
        'manager@fleetcare.local',
        '0900000002',
        (SELECT role_id FROM roles WHERE role_code = 'FLEET_MANAGER' LIMIT 1),
        'ACTIVE',
        0
    ),
    (
        'tech',
        '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
        'Nhan vien ky thuat',
        'tech@fleetcare.local',
        '0900000003',
        (SELECT role_id FROM roles WHERE role_code = 'TECHNICIAN' LIMIT 1),
        'ACTIVE',
        0
    )
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    full_name = VALUES(full_name),
    email = VALUES(email),
    phone = VALUES(phone),
    role_id = VALUES(role_id),
    account_status = VALUES(account_status),
    must_change_password = VALUES(must_change_password);
