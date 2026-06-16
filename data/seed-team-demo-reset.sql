-- FleetCare team demo reset seed
-- Run after data/Dump20260524.sql.
-- Demo accounts: admin / manager / tech, password: 123456

SET NAMES utf8mb4;

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

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM audit_logs;
DELETE FROM alerts;
DELETE FROM maintenance_record_items;
DELETE FROM maintenance_records;
DELETE FROM maintenance_plans;
DELETE FROM vehicle_documents;
DELETE FROM vehicles;
DELETE FROM users;
DELETE FROM role_permissions;
DELETE FROM permissions;
DELETE FROM roles;
DELETE FROM maintenance_items;
DELETE FROM maintenance_types;
DELETE FROM document_types;
DELETE FROM alert_settings;

ALTER TABLE audit_logs AUTO_INCREMENT = 1;
ALTER TABLE alerts AUTO_INCREMENT = 1;
ALTER TABLE maintenance_record_items AUTO_INCREMENT = 1;
ALTER TABLE maintenance_records AUTO_INCREMENT = 1;
ALTER TABLE maintenance_plans AUTO_INCREMENT = 1;
ALTER TABLE vehicle_documents AUTO_INCREMENT = 1;
ALTER TABLE vehicles AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;
ALTER TABLE permissions AUTO_INCREMENT = 1;
ALTER TABLE roles AUTO_INCREMENT = 1;
ALTER TABLE maintenance_items AUTO_INCREMENT = 1;
ALTER TABLE maintenance_types AUTO_INCREMENT = 1;
ALTER TABLE document_types AUTO_INCREMENT = 1;
ALTER TABLE alert_settings AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO roles (role_id, role_code, role_name, description, is_active, created_at, updated_at) VALUES
(1, 'ADMIN', 'Quản trị hệ thống', 'Quản trị tài khoản, phân quyền, cấu hình và nhật ký hệ thống', 1, NOW(), NOW()),
(2, 'FLEET_MANAGER', 'Quản lý đội xe', 'Quản lý hồ sơ xe, giấy tờ, kế hoạch, cảnh báo và báo cáo', 1, NOW(), NOW()),
(3, 'TECHNICIAN', 'Nhân viên kỹ thuật', 'Theo dõi xe đến hạn, cập nhật bảo dưỡng, sửa chữa, phụ tùng và ODO', 1, NOW(), NOW());

INSERT INTO permissions (permission_code, permission_name, module_name, description, created_at, updated_at) VALUES
('USER_MANAGE', 'Quản lý tài khoản', 'AUTH', 'Tạo, sửa, khóa và mở khóa tài khoản', NOW(), NOW()),
('ROLE_ASSIGN', 'Phân quyền vai trò', 'AUTH', 'Gán vai trò cho người dùng', NOW(), NOW()),
('SETTINGS_UPDATE', 'Cấu hình hệ thống', 'SYSTEM', 'Cập nhật cấu hình cảnh báo', NOW(), NOW()),
('VEHICLE_VIEW', 'Xem phương tiện', 'VEHICLE', 'Xem danh sách và chi tiết phương tiện', NOW(), NOW()),
('VEHICLE_CREATE', 'Thêm phương tiện', 'VEHICLE', 'Tạo hồ sơ phương tiện', NOW(), NOW()),
('VEHICLE_UPDATE', 'Sửa phương tiện', 'VEHICLE', 'Cập nhật hồ sơ phương tiện', NOW(), NOW()),
('DOCUMENT_VIEW', 'Xem giấy tờ', 'DOCUMENT', 'Xem giấy tờ pháp lý', NOW(), NOW()),
('DOCUMENT_UPDATE', 'Cập nhật giấy tờ', 'DOCUMENT', 'Cập nhật đăng kiểm, bảo hiểm và phí đường bộ', NOW(), NOW()),
('MAINTENANCE_PLAN_VIEW', 'Xem kế hoạch bảo dưỡng', 'PLAN', 'Xem kế hoạch bảo dưỡng', NOW(), NOW()),
('MAINTENANCE_PLAN_CREATE', 'Lập kế hoạch bảo dưỡng', 'PLAN', 'Tạo kế hoạch bảo dưỡng', NOW(), NOW()),
('MAINTENANCE_PLAN_UPDATE', 'Sửa kế hoạch bảo dưỡng', 'PLAN', 'Cập nhật kế hoạch bảo dưỡng', NOW(), NOW()),
('MAINTENANCE_RECORD_VIEW', 'Xem hồ sơ bảo dưỡng', 'MAINTENANCE', 'Xem lịch sử bảo dưỡng và sửa chữa', NOW(), NOW()),
('MAINTENANCE_RECORD_CREATE', 'Tạo phiếu bảo dưỡng', 'MAINTENANCE', 'Tạo phiếu bảo dưỡng và sửa chữa', NOW(), NOW()),
('MAINTENANCE_RECORD_UPDATE', 'Cập nhật phiếu bảo dưỡng', 'MAINTENANCE', 'Cập nhật phụ tùng, chi phí và ODO', NOW(), NOW()),
('ALERT_VIEW', 'Xem cảnh báo', 'ALERT', 'Xem các cảnh báo đến hạn', NOW(), NOW()),
('COST_REPORT_VIEW', 'Xem báo cáo chi phí', 'REPORT', 'Xem báo cáo chi phí bảo dưỡng và giấy tờ', NOW(), NOW());

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.role_id, p.permission_id
FROM roles r
JOIN permissions p
WHERE r.role_code = 'ADMIN'
   OR (r.role_code = 'FLEET_MANAGER' AND p.permission_code IN (
        'VEHICLE_VIEW', 'VEHICLE_CREATE', 'VEHICLE_UPDATE',
        'DOCUMENT_VIEW', 'DOCUMENT_UPDATE',
        'MAINTENANCE_PLAN_VIEW', 'MAINTENANCE_PLAN_CREATE', 'MAINTENANCE_PLAN_UPDATE',
        'MAINTENANCE_RECORD_VIEW', 'ALERT_VIEW', 'COST_REPORT_VIEW'
   ))
   OR (r.role_code = 'TECHNICIAN' AND p.permission_code IN (
        'VEHICLE_VIEW', 'MAINTENANCE_PLAN_VIEW',
        'MAINTENANCE_RECORD_VIEW', 'MAINTENANCE_RECORD_CREATE', 'MAINTENANCE_RECORD_UPDATE',
        'ALERT_VIEW'
   ));

INSERT INTO users (
    username, password_hash, full_name, email, phone,
    role_id, account_status, must_change_password, created_at, updated_at
) VALUES
('admin', '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
 'Quản trị viên', 'admin@fleetcare.local', '0900000001',
 (SELECT role_id FROM roles WHERE role_code = 'ADMIN'), 'ACTIVE', 0, NOW(), NOW()),
('manager', '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
 'Quản lý đội xe', 'manager@fleetcare.local', '0900000002',
 (SELECT role_id FROM roles WHERE role_code = 'FLEET_MANAGER'), 'ACTIVE', 0, NOW(), NOW()),
('tech', '$2a$12$IyrUX3XVzB8ACyisk4xJvOh97BxoJs0Wm7YK3IU7ZFzbxDp4rwRf2',
 'Nhân viên kỹ thuật', 'tech@fleetcare.local', '0900000003',
 (SELECT role_id FROM roles WHERE role_code = 'TECHNICIAN'), 'ACTIVE', 0, NOW(), NOW());

INSERT INTO alert_settings (
    setting_id, document_alert_days, maintenance_alert_days,
    maintenance_alert_km, is_active, updated_by, created_at, updated_at
) VALUES (
    1, 15, 7, 500, 1,
    (SELECT user_id FROM users WHERE username = 'admin'),
    NOW(), NOW()
);

INSERT INTO document_types (document_type_id, document_type_code, document_type_name, default_alert_days, is_active, created_at, updated_at) VALUES
(1, 'REGISTRATION_INSPECTION', 'Đăng kiểm', 15, 1, NOW(), NOW()),
(2, 'INSURANCE', 'Bảo hiểm', 15, 1, NOW(), NOW()),
(3, 'ROAD_FEE', 'Phí đường bộ', 15, 1, NOW(), NOW());

INSERT INTO maintenance_types (
    maintenance_type_id, maintenance_code, maintenance_name, description,
    default_interval_days, default_interval_km, is_active, created_at, updated_at
) VALUES
(1, 'PERIODIC_SERVICE', 'Bảo dưỡng định kỳ', 'Bảo dưỡng tổng quát theo chu kỳ', 180, 5000, 1, NOW(), NOW()),
(2, 'OIL_CHANGE', 'Thay dầu', 'Thay dầu động cơ', 180, 5000, 1, NOW(), NOW()),
(3, 'BRAKE_CHECK', 'Kiểm tra phanh', 'Kiểm tra và bảo dưỡng hệ thống phanh', 180, 10000, 1, NOW(), NOW()),
(4, 'TIRE_SERVICE', 'Lốp', 'Đảo lốp, thay lốp hoặc cân bằng lốp', 365, 20000, 1, NOW(), NOW()),
(5, 'COOLING_SYSTEM', 'Hệ thống làm mát', 'Kiểm tra nước làm mát và két nước', 180, 10000, 1, NOW(), NOW());

INSERT INTO maintenance_items (item_code, item_name, item_type, unit, default_unit_cost, notes) VALUES
('W001', 'Thay dầu động cơ', 'WORK', 'Lần', 150000, 'Công thay dầu động cơ'),
('W002', 'Thay lọc dầu', 'WORK', 'Lần', 50000, 'Công thay lọc dầu'),
('W003', 'Thay lọc gió động cơ', 'WORK', 'Lần', 60000, 'Công thay lọc gió'),
('W004', 'Kiểm tra hệ thống phanh', 'WORK', 'Lần', 90000, 'Kiểm tra má phanh, dầu phanh'),
('W005', 'Đảo lốp và cân bằng động', 'WORK', 'Lần', 180000, 'Đảo lốp, cân chỉnh bánh'),
('W006', 'Kiểm tra hệ thống làm mát', 'WORK', 'Lần', 80000, 'Kiểm tra nước làm mát, két nước'),
('P001', 'Dầu động cơ 10W-40 4L', 'PART', 'Can', 420000, 'Dầu động cơ xe tải nhẹ'),
('P002', 'Lọc dầu động cơ', 'PART', 'Cái', 85000, 'Phụ tùng thay định kỳ'),
('P003', 'Lọc gió động cơ', 'PART', 'Cái', 120000, 'Phụ tùng thay định kỳ'),
('P004', 'Má phanh trước', 'PART', 'Bộ', 520000, 'Má phanh trước theo bộ'),
('P005', 'Dầu phanh DOT4', 'PART', 'Chai', 95000, 'Dầu phanh'),
('P006', 'Nước làm mát động cơ', 'PART', 'Lít', 45000, 'Nước làm mát');

CREATE OR REPLACE VIEW vw_due_vehicle_documents AS
SELECT vd.document_id,
       v.vehicle_id,
       v.license_plate,
       v.vehicle_type,
       dt.document_type_name,
       vd.document_number,
       vd.issuer_name,
       vd.expiry_date,
       DATEDIFF(vd.expiry_date, CURDATE()) AS days_to_expiry,
       CASE
           WHEN vd.expiry_date < CURDATE() THEN 'OVERDUE'
           WHEN aset.is_active = 1
                AND DATEDIFF(vd.expiry_date, CURDATE()) <= aset.document_alert_days THEN 'COMING_DUE'
           ELSE 'NORMAL'
       END AS due_status
FROM vehicle_documents vd
JOIN vehicles v ON v.vehicle_id = vd.vehicle_id
JOIN document_types dt ON dt.document_type_id = vd.document_type_id
JOIN alert_settings aset ON aset.setting_id = 1
WHERE vd.is_current = 1
  AND vd.document_status IN ('VALID', 'EXPIRED');

CREATE OR REPLACE VIEW vw_due_maintenance_plans AS
SELECT mp.plan_id,
       v.vehicle_id,
       v.license_plate,
       v.vehicle_type,
       mt.maintenance_name,
       v.current_odometer,
       mp.next_due_date,
       mp.next_due_odometer,
       COALESCE(mp.alert_before_days, aset.maintenance_alert_days) AS effective_alert_days,
       COALESCE(mp.alert_before_km, aset.maintenance_alert_km) AS effective_alert_km,
       CASE
           WHEN mp.next_due_date IS NOT NULL AND mp.next_due_date < CURDATE() THEN 'OVERDUE'
           WHEN mp.next_due_odometer IS NOT NULL AND v.current_odometer >= mp.next_due_odometer THEN 'OVERDUE'
           WHEN aset.is_active = 1
                AND mp.next_due_date IS NOT NULL
                AND DATEDIFF(mp.next_due_date, CURDATE()) <= COALESCE(mp.alert_before_days, aset.maintenance_alert_days) THEN 'COMING_DUE'
           WHEN aset.is_active = 1
                AND mp.next_due_odometer IS NOT NULL
                AND (mp.next_due_odometer - v.current_odometer) <= COALESCE(mp.alert_before_km, aset.maintenance_alert_km) THEN 'COMING_DUE'
           ELSE 'NORMAL'
       END AS due_status
FROM maintenance_plans mp
JOIN vehicles v ON v.vehicle_id = mp.vehicle_id
JOIN maintenance_types mt ON mt.maintenance_type_id = mp.maintenance_type_id
JOIN alert_settings aset ON aset.setting_id = 1
WHERE mp.is_active = 1;

INSERT INTO vehicles
(vehicle_code, license_plate, vehicle_type, brand, model, manufacture_year,
 purchase_date, chassis_number, engine_number, color, current_odometer,
 vehicle_status, notes, created_by, updated_by)
VALUES
('VH-FC-001', '51C-256.89', 'Xe tải', 'Isuzu', 'QKR 270', 2022, '2023-02-10', 'RL4QKR77NN100001', '4JH1E500001', 'Trắng', 48200, 'ACTIVE', 'Xe tải giao hàng nội thành TP.HCM', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
('VH-FC-002', '50H-112.35', 'Xe tải', 'Hyundai', 'Mighty EX8', 2021, '2022-06-18', 'KMFWBX7KCMU00002', 'D4CCM200002', 'Xanh', 73000, 'UNDER_MAINTENANCE', 'Đang sửa hệ thống phanh tại xưởng', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
('VH-FC-003', '51B-345.67', 'Xe khách', 'Thaco', 'County TB85S', 2020, '2021-11-05', 'RN2TB85S0L000003', 'D4DBL300003', 'Vàng', 96500, 'ACTIVE', 'Xe đưa rước nhân viên tuyến Bình Dương', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
('VH-FC-004', '60C-990.12', 'Xe bán tải', 'Ford', 'Ranger XLS', 2023, '2024-01-22', 'MPBUMFF60PX00004', 'P4ATPX00004', 'Xám', 31500, 'ACTIVE', 'Xe hỗ trợ kỹ thuật hiện trường', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
('VH-FC-005', '51F-888.66', 'Xe con', 'Toyota', 'Vios G', 2022, '2022-09-12', 'RL4VIO22NN000005', '2NRFE500005', 'Đen', 27800, 'ACTIVE', 'Xe điều hành văn phòng', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
('VH-FC-006', '51LD-123.45', 'Xe chuyên dụng', 'Hino', 'XZU650L', 2021, '2021-08-30', 'JHHDXZU65M000006', 'N04CM600006', 'Trắng', 68800, 'INACTIVE', 'Xe nâng thùng tạm ngưng khai thác để kiểm tra tổng quát', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager'));

INSERT INTO vehicle_documents
(vehicle_id, document_type_id, document_number, issuer_name,
 issue_date, effective_date, expiry_date, fee_amount, paid_date,
 document_status, is_current, note, created_by, updated_by)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 1, 'DK-50-01S-240612-001', 'Trung tâm Đăng kiểm 50-01S', DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 173 DAY), 'VALID', 1, 'Đăng kiểm sắp hết hạn trong 7 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 2, 'BH-BVI-51C25689-2026', 'Bảo Việt Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_ADD(CURDATE(), INTERVAL 120 DAY), 1580000, DATE_SUB(CURDATE(), INTERVAL 245 DAY), 'VALID', 1, 'Bảo hiểm TNDS còn hiệu lực', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 3, 'PDB-51C25689-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 367 DAY), 'EXPIRED', 1, 'Phí đường bộ đã quá hạn 2 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 1, 'DK-50-03V-231201-014', 'Trung tâm Đăng kiểm 50-03V', DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 194 DAY), 'EXPIRED', 1, 'Đăng kiểm đã quá hạn, cần xử lý trước khi khai thác', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 2, 'BH-PVI-50H11235-2026', 'PVI Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1760000, DATE_SUB(CURDATE(), INTERVAL 351 DAY), 'VALID', 1, 'Bảo hiểm sắp hết hạn trong 14 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 3, 'PDB-50H11235-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_ADD(CURDATE(), INTERVAL 240 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 125 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 1, 'DK-50-02S-260301-008', 'Trung tâm Đăng kiểm 50-02S', DATE_SUB(CURDATE(), INTERVAL 95 DAY), DATE_SUB(CURDATE(), INTERVAL 95 DAY), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 370000, DATE_SUB(CURDATE(), INTERVAL 95 DAY), 'VALID', 1, 'Đăng kiểm còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 2, 'BH-PTI-51B34567-2026', 'PTI Hồ Chí Minh', DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY), 2450000, DATE_SUB(CURDATE(), INTERVAL 369 DAY), 'EXPIRED', 1, 'Bảo hiểm đã quá hạn 4 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 3, 'PDB-51B34567-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 3240000, DATE_SUB(CURDATE(), INTERVAL 360 DAY), 'VALID', 1, 'Phí đường bộ sắp hết hạn trong 5 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 1, 'DK-60-01S-260501-022', 'Trung tâm Đăng kiểm 60-01S', DATE_SUB(CURDATE(), INTERVAL 42 DAY), DATE_SUB(CURDATE(), INTERVAL 42 DAY), DATE_ADD(CURDATE(), INTERVAL 140 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 42 DAY), 'VALID', 1, 'Đăng kiểm còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 2, 'BH-MIC-60C99012-2026', 'MIC Đồng Nai', DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_ADD(CURDATE(), INTERVAL 185 DAY), 1320000, DATE_SUB(CURDATE(), INTERVAL 180 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 3, 'PDB-60C99012-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 305 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 1, 'DK-50-05V-260612-017', 'Trung tâm Đăng kiểm 50-05V', DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 170 DAY), 'VALID', 1, 'Đăng kiểm sắp hết hạn trong 12 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 2, 'BH-BSH-51F88866-2026', 'BSH Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 335 DAY), 980000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 3, 'PDB-51F88866-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1560000, DATE_SUB(CURDATE(), INTERVAL 370 DAY), 'EXPIRED', 1, 'Phí đường bộ quá hạn 1 ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 1, 'DK-50-04D-250101-033', 'Trung tâm Đăng kiểm 50-04D', DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 165 DAY), 370000, DATE_SUB(CURDATE(), INTERVAL 530 DAY), 'EXPIRED', 1, 'Xe tạm ngưng khai thác, cần kiểm định lại trước khi hoạt động', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 2, 'BH-PJICO-51LD12345-2026', 'PJICO Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_ADD(CURDATE(), INTERVAL 205 DAY), 1850000, DATE_SUB(CURDATE(), INTERVAL 160 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 3, 'PDB-51LD12345-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_ADD(CURDATE(), INTERVAL 285 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 80 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager'));

INSERT INTO maintenance_plans
(vehicle_id, maintenance_type_id, interval_days, interval_km, last_service_date, last_service_odometer,
 next_due_date, next_due_odometer, alert_before_days, alert_before_km, is_active, notes, created_by, updated_by)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 2, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 176 DAY), 43200, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 48700, 15, 700, 1, 'Thay dầu sắp đến hạn theo ngày và ODO', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 3, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 205 DAY), 63000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 73000, 15, 500, 1, 'Kiểm tra phanh đã quá hạn, xe đang ở xưởng', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 1, 90, 10000, DATE_SUB(CURDATE(), INTERVAL 105 DAY), 86000, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 96500, 15, 700, 1, 'Bảo dưỡng định kỳ đến hạn theo ODO', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 5, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 26000, DATE_ADD(CURDATE(), INTERVAL 120 DAY), 36000, 15, 500, 1, 'Kiểm tra hệ thống làm mát còn hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 4, 365, 20000, DATE_SUB(CURDATE(), INTERVAL 350 DAY), 9000, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 29000, 15, 500, 1, 'Đảo lốp/cân bằng động sắp đến hạn theo ngày', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 1, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 210 DAY), 63800, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 68800, 15, 500, 1, 'Xe tạm ngưng nhưng vẫn có kế hoạch bảo dưỡng quá hạn', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager'));

INSERT INTO maintenance_records
(vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes,
 created_by, updated_by)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89') AND maintenance_type_id=2 LIMIT 1), 'PREVENTIVE', 'Thay dầu định kỳ 48.000 km', DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_SUB(CURDATE(), INTERVAL 31 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '10:15:00'), 48050, 'Thay dầu động cơ, lọc dầu và kiểm tra tổng quát.', 'Xưởng FleetCare Quận 7', (SELECT user_id FROM users WHERE username='tech'), 705000, 'COMPLETED', 'Hoàn tất, xe vận hành ổn định', (SELECT user_id FROM users WHERE username='tech'), (SELECT user_id FROM users WHERE username='tech')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35') AND maintenance_type_id=3 LIMIT 1), 'CORRECTIVE', 'Sửa hệ thống phanh trước', CURDATE(), NULL, TIMESTAMP(CURDATE(), '09:00:00'), NULL, 73000, 'Kiểm tra phanh, thay má phanh trước và dầu phanh.', 'Xưởng FleetCare Bình Tân', (SELECT user_id FROM users WHERE username='tech'), 0, 'IN_PROGRESS', 'Đang chờ phụ tùng má phanh trước', (SELECT user_id FROM users WHERE username='tech'), (SELECT user_id FROM users WHERE username='tech')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35') AND maintenance_type_id=3 LIMIT 1), 'PREVENTIVE', 'Kiểm tra phanh sau sửa chữa', DATE_ADD(CURDATE(), INTERVAL 1 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra lại hệ thống phanh sau khi hoàn tất sửa chữa.', 'Xưởng FleetCare Bình Tân', (SELECT user_id FROM users WHERE username='tech'), 0, 'OPEN', 'Phiếu chờ xử lý ngày mai', (SELECT user_id FROM users WHERE username='manager'), (SELECT user_id FROM users WHERE username='manager')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67') AND maintenance_type_id=1 LIMIT 1), 'PREVENTIVE', 'Bảo dưỡng định kỳ xe khách 95.000 km', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 59 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '15:30:00'), 95000, 'Bảo dưỡng tổng quát, thay dầu, lọc gió, kiểm tra phanh.', 'Xưởng Thaco Bus Miền Nam', (SELECT user_id FROM users WHERE username='tech'), 2380000, 'COMPLETED', 'Hoàn tất bảo dưỡng định kỳ', (SELECT user_id FROM users WHERE username='tech'), (SELECT user_id FROM users WHERE username='tech')),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66') AND maintenance_type_id=4 LIMIT 1), 'PREVENTIVE', 'Đảo lốp và cân bằng động', CURDATE(), CURDATE(), TIMESTAMP(CURDATE(), '07:45:00'), TIMESTAMP(CURDATE(), '09:10:00'), 27800, 'Đảo lốp, cân bằng động, kiểm tra áp suất lốp.', 'Xưởng FleetCare Quận 7', (SELECT user_id FROM users WHERE username='tech'), 280000, 'COMPLETED', 'Hoàn tất trong ngày', (SELECT user_id FROM users WHERE username='tech'), (SELECT user_id FROM users WHERE username='tech'));

INSERT INTO maintenance_record_items
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W001'), 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P001'), 'PART', 'Dầu động cơ 10W-40 4L', 1, 'Can', 420000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P002'), 'PART', 'Lọc dầu động cơ', 1, 'Cái', 85000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Sửa hệ thống phanh trước' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W004'), 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Sửa hệ thống phanh trước' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P004'), 'PART', 'Má phanh trước', 1, 'Bộ', 520000, 'Chờ nhập kho'),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W001'), 'WORK', 'Công thay dầu', 1, 'Lần', 150000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W003'), 'WORK', 'Công thay lọc gió', 1, 'Lần', 60000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P001'), 'PART', 'Dầu động cơ 10W-40 4L', 4, 'Can', 420000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P003'), 'PART', 'Lọc gió động cơ', 2, 'Cái', 120000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Đảo lốp và cân bằng động' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W005'), 'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL);

INSERT INTO audit_logs (user_id, username, action, entity_type, entity_id, description, created_at) VALUES
((SELECT user_id FROM users WHERE username='admin'), 'admin', 'SEED_RESET', 'DATABASE', 'seed-team-demo-reset.sql', 'Seed dữ liệu demo đồng bộ cho nhóm.', NOW());

SELECT 'users' AS table_name, COUNT(*) AS total FROM users
UNION ALL SELECT 'vehicles', COUNT(*) FROM vehicles
UNION ALL SELECT 'vehicle_documents', COUNT(*) FROM vehicle_documents
UNION ALL SELECT 'maintenance_plans', COUNT(*) FROM maintenance_plans
UNION ALL SELECT 'maintenance_records', COUNT(*) FROM maintenance_records
UNION ALL SELECT 'maintenance_record_items', COUNT(*) FROM maintenance_record_items
UNION ALL SELECT 'document_alerts', COUNT(*) FROM vw_due_vehicle_documents WHERE due_status IN ('OVERDUE', 'COMING_DUE')
UNION ALL SELECT 'maintenance_due_alerts', COUNT(*) FROM vw_due_maintenance_plans WHERE due_status IN ('OVERDUE', 'COMING_DUE');
