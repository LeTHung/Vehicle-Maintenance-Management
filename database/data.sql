SET NAMES utf8mb4;
USE `vehicle_maintenance_management`;

-- --- Reset dữ liệu 
-- Tắt safe-update mode để DELETE chạy được trong MySQL Workbench.
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `alerts`;
TRUNCATE TABLE `maintenance_record_items`;
TRUNCATE TABLE `maintenance_records`;
TRUNCATE TABLE `maintenance_plans`;
TRUNCATE TABLE `maintenance_items`;
TRUNCATE TABLE `vehicle_documents`;
TRUNCATE TABLE `vehicles`;
TRUNCATE TABLE `role_permissions`;
TRUNCATE TABLE `alert_settings`;
TRUNCATE TABLE `users`;
TRUNCATE TABLE `permissions`;
TRUNCATE TABLE `maintenance_types`;
TRUNCATE TABLE `document_types`;
TRUNCATE TABLE `roles`;
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- =====================================================================
-- 1. DỮ LIỆU THAM CHIẾU (bắt buộc để ứng dụng hoạt động)
-- =====================================================================

-- 1.1 Vai trò
INSERT INTO `roles` (role_id, role_code, role_name, description, is_active) VALUES
(1, 'ADMIN',         'Quản trị hệ thống', 'Quản trị hệ thống, tài khoản, cấu hình', 1),
(2, 'FLEET_MANAGER', 'Quản lý đội xe',    'Quản lý hồ sơ xe, giấy tờ, kế hoạch, báo cáo', 1),
(3, 'TECHNICIAN',    'Nhân viên kỹ thuật','Cập nhật bảo dưỡng, sửa chữa, phụ tùng, ODO', 1);

-- 1.2 Quyền
INSERT INTO `permissions` (permission_id, permission_code, permission_name, module_name, description) VALUES
(1,  'USER_MANAGE',              'Quản lý tài khoản',        'AUTH',        'Tạo/sửa/khóa tài khoản'),
(2,  'ROLE_ASSIGN',             'Phân quyền vai trò',        'AUTH',        'Gán vai trò cho người dùng'),
(3,  'SETTINGS_UPDATE',         'Cấu hình hệ thống',         'SYSTEM',      'Cập nhật cấu hình cảnh báo'),
(4,  'VEHICLE_VIEW',            'Xem phương tiện',           'VEHICLE',     'Xem danh sách và chi tiết phương tiện'),
(5,  'VEHICLE_CREATE',          'Thêm phương tiện',          'VEHICLE',     'Tạo hồ sơ phương tiện'),
(6,  'VEHICLE_UPDATE',          'Sửa phương tiện',           'VEHICLE',     'Cập nhật hồ sơ phương tiện'),
(7,  'DOCUMENT_VIEW',           'Xem giấy tờ',               'DOCUMENT',    'Xem giấy tờ pháp lý'),
(8,  'DOCUMENT_UPDATE',         'Cập nhật giấy tờ',          'DOCUMENT',    'Cập nhật đăng kiểm/bảo hiểm/phí đường bộ'),
(9,  'MAINTENANCE_PLAN_VIEW',   'Xem kế hoạch bảo dưỡng',    'PLAN',        'Xem kế hoạch bảo dưỡng'),
(10, 'MAINTENANCE_PLAN_CREATE', 'Lập kế hoạch bảo dưỡng',    'PLAN',        'Tạo kế hoạch bảo dưỡng'),
(11, 'MAINTENANCE_PLAN_UPDATE', 'Sửa kế hoạch bảo dưỡng',    'PLAN',        'Cập nhật kế hoạch bảo dưỡng'),
(12, 'MAINTENANCE_RECORD_VIEW', 'Xem hồ sơ bảo dưỡng',       'MAINTENANCE', 'Xem lịch sử bảo dưỡng/sửa chữa'),
(13, 'MAINTENANCE_RECORD_CREATE','Tạo phiếu bảo dưỡng',      'MAINTENANCE', 'Tạo phiếu bảo dưỡng/sửa chữa'),
(14, 'MAINTENANCE_RECORD_UPDATE','Cập nhật phiếu bảo dưỡng', 'MAINTENANCE', 'Cập nhật phụ tùng, chi phí, ODO'),
(15, 'ALERT_VIEW',              'Xem cảnh báo',              'ALERT',       'Xem các cảnh báo đến hạn'),
(16, 'COST_REPORT_VIEW',        'Xem báo cáo chi phí',       'REPORT',      'Xem báo cáo chi phí bảo dưỡng và giấy tờ');

-- 1.3 Gán quyền cho vai trò
--   ADMIN: toàn bộ; FLEET_MANAGER: xe/giấy tờ/kế hoạch/cảnh báo/báo cáo; TECHNICIAN: bảo dưỡng
INSERT INTO `role_permissions` (role_id, permission_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),
(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,12),(2,15),(2,16),
(3,4),(3,9),(3,12),(3,13),(3,14),(3,15);

-- 1.4 Loại giấy tờ
INSERT INTO `document_types` (document_type_id, document_type_code, document_type_name, default_alert_days, is_active) VALUES
(1, 'REGISTRATION_INSPECTION', 'Đăng kiểm',     15, 1),
(2, 'INSURANCE',               'Bảo hiểm',      15, 1),
(3, 'ROAD_FEE',                'Phí đường bộ',  15, 1);

-- 1.5 Loại bảo dưỡng
INSERT INTO `maintenance_types` (maintenance_type_id, maintenance_code, maintenance_name, description, default_interval_days, default_interval_km, is_active) VALUES
(1, 'PERIODIC_SERVICE', 'Bảo dưỡng định kỳ',  'Bảo dưỡng tổng quát theo chu kỳ',       180, 5000,  1),
(2, 'OIL_CHANGE',       'Thay dầu',           'Thay dầu động cơ',                      180, 5000,  1),
(3, 'BRAKE_CHECK',      'Kiểm tra phanh',     'Kiểm tra và bảo dưỡng hệ thống phanh',  180, 10000, 1),
(4, 'TIRE_SERVICE',     'Lốp',                'Đảo lốp / thay lốp / cân bằng lốp',     365, 20000, 1),
(5, 'COOLING_SYSTEM',   'Hệ thống làm mát',   'Kiểm tra nước làm mát và két nước',     180, 10000, 1);

-- 1.6 Danh mục hạng mục / phụ tùng
INSERT INTO `maintenance_items` (item_id, item_code, item_name, item_type, unit, default_unit_cost, notes) VALUES
(1,  'W001', 'Thay dầu động cơ',          'WORK', 'Lần',  150000, 'Công thay dầu động cơ'),
(2,  'W002', 'Thay lọc dầu',              'WORK', 'Lần',   50000, 'Công thay lọc dầu'),
(3,  'W003', 'Thay lọc gió động cơ',      'WORK', 'Lần',   60000, 'Công thay lọc gió'),
(4,  'W004', 'Kiểm tra hệ thống phanh',   'WORK', 'Lần',   90000, 'Kiểm tra má phanh, dầu phanh'),
(5,  'W005', 'Đảo lốp và cân bằng động',  'WORK', 'Lần',  180000, 'Đảo lốp, cân chỉnh bánh'),
(6,  'W006', 'Kiểm tra hệ thống làm mát', 'WORK', 'Lần',   80000, 'Kiểm tra nước làm mát, két nước'),
(7,  'P001', 'Dầu động cơ 10W-40 4L',     'PART', 'Can',  420000, 'Dầu động cơ xe tải nhẹ'),
(8,  'P002', 'Lọc dầu động cơ',           'PART', 'Cái',   85000, 'Phụ tùng thay định kỳ'),
(9,  'P003', 'Lọc gió động cơ',           'PART', 'Cái',  120000, 'Phụ tùng thay định kỳ'),
(10, 'P004', 'Má phanh trước',            'PART', 'Bộ',   520000, 'Má phanh trước theo bộ'),
(11, 'P005', 'Dầu phanh DOT4',            'PART', 'Chai',  95000, 'Dầu phanh'),
(12, 'P006', 'Nước làm mát động cơ',      'PART', 'Lít',   45000, 'Nước làm mát'),
(13, 'W007', 'Sửa hệ thống điều hòa',     'WORK', 'Lần',  250000, 'Kiểm tra gas, máy nén, quạt'),
(14, 'W008', 'Thay ắc quy',               'WORK', 'Lần',   80000, 'Thay và kiểm tra hệ thống sạc'),
(15, 'P007', 'Ắc quy 12V 65Ah',           'PART', 'Cái',  1850000, 'Ắc quy xe tải/khách'),
(16, 'P008', 'Lốp 195/65R15',             'PART', 'Cái', 2100000, 'Lốp xe con'),
(17, 'W009', 'Sửa hệ thống điện',         'WORK', 'Lần',  180000, 'Kiểm tra cầu chì, dây điện, đèn'),
(18, 'P009', 'Bugi đánh lửa',             'PART', 'Bộ',   320000, 'Bộ 4 bugi'),
(19, 'W010', 'Vệ sinh két nước',          'WORK', 'Lần',  120000, 'Xả và nạp lại nước làm mát'),
(20, 'P010', 'Dây curoa động cơ',         'PART', 'Cái',  450000, 'Dây curoa tổng');

-- 1.7 Cấu hình cảnh báo (1 bản ghi duy nhất)
INSERT INTO `alert_settings` (setting_id, document_alert_days, maintenance_alert_days, maintenance_alert_km, is_active, updated_by) VALUES
(1, 15, 7, 500, 1, NULL);

-- =====================================================================
-- 2. DỮ LIỆU DEMO
-- =====================================================================

-- 2.1 Tài khoản (mật khẩu = tên tài khoản + 1234)
INSERT INTO `users` (user_id, username, password_hash, full_name, email, phone, role_id, account_status, must_change_password) VALUES
(1, 'admin',   '$2a$12$kzZVDQDziMLUnlZcknGVNe4QfREN6Zsn31i2oh1/akXxWEc66RhYW', 'Quản trị viên',       'admin@fleetcare.local',   '0900000001', 1, 'ACTIVE', 0),
(2, 'manager', '$2a$12$BkCVgSYlAc.83A9Kz3LL/eYMKdHwmjqM1oOhWG3q4v/9ZK6JXm3BW', 'Quản lý đội xe',      'manager@fleetcare.local', '0900000002', 2, 'ACTIVE', 0),
(3, 'tech',    '$2a$12$KPJnY29JhT1jCp9trAD89O3AI2lfVUi1XOVCNHnsNx1ELNGhvK9sW', 'Nguyễn Văn An',         'tech@fleetcare.local',    '0900000003', 3, 'ACTIVE', 0),
(4, 'tech2',   '$2a$12$b1qphGseJd7rVMsEJOIg.u52yMoIlWD1i/GO.Tg8gAUvHzyu2AoUS', 'Nguyễn Văn Minh',       'tech2@fleetcare.local',   '0900000004', 3, 'ACTIVE', 0),
(5, 'tech3',   '$2a$12$vmr/ziTkrQkSG/Y2erMomObZi7J56xvO9OKb73hWVD.KjOZMuTFsi', 'Trần Quốc Hùng',        'tech3@fleetcare.local',   '0900000005', 3, 'ACTIVE', 0),
(6, 'tech4',   '$2a$12$07ZDCRX5LSVlc8abVHvyBuUMqo9fL5PIqnIcgH/OhxZtI2tzJzXkW', 'Lê Hoàng Phúc',         'tech4@fleetcare.local',   '0900000006', 3, 'ACTIVE', 0);

-- 2.2 Phương tiện (created_by/updated_by = manager)
INSERT INTO `vehicles`
(vehicle_id, vehicle_code, license_plate, vehicle_type, brand, model, manufacture_year,
 purchase_date, chassis_number, engine_number, color, current_odometer, vehicle_status, notes, created_by, updated_by)
VALUES
(1, 'VH-FC-001', '51C-256.89',  'Xe tải',        'Isuzu',   'QKR 270',      2022, '2023-02-10', 'RL4QKR77NN100001', '4JH1E500001', 'Trắng', 48200, 'ACTIVE',             'Xe tải giao hàng nội thành TP.HCM', 2, 2),
(2, 'VH-FC-002', '50H-112.35',  'Xe tải',        'Hyundai', 'Mighty EX8',   2021, '2022-06-18', 'KMFWBX7KCMU00002', 'D4CCM200002', 'Xanh',  73000, 'UNDER_MAINTENANCE',  'Đang sửa hệ thống phanh tại xưởng', 2, 2),
(3, 'VH-FC-003', '51B-345.67',  'Xe khách',      'Thaco',   'County TB85S', 2020, '2021-11-05', 'RN2TB85S0L000003', 'D4DBL300003', 'Vàng',  96500, 'ACTIVE',             'Xe đưa rước nhân viên tuyến Bình Dương', 2, 2),
(4, 'VH-FC-004', '60C-990.12',  'Xe bán tải',    'Ford',    'Ranger XLS',   2023, '2024-01-22', 'MPBUMFF60PX00004', 'P4ATPX00004', 'Xám',   31500, 'ACTIVE',             'Xe hỗ trợ kỹ thuật hiện trường', 2, 2),
(5, 'VH-FC-005', '51F-888.66',  'Xe con',        'Toyota',  'Vios G',       2022, '2022-09-12', 'RL4VIO22NN000005', '2NRFE500005', 'Đen',   27800, 'ACTIVE',             'Xe điều hành văn phòng', 2, 2),
(6, 'VH-FC-006', '51LD-123.45', 'Xe chuyên dụng','Hino',    'XZU650L',      2021, '2021-08-30', 'JHHDXZU65M000006', 'N04CM600006', 'Trắng', 68800, 'INACTIVE',           'Xe nâng thùng tạm ngưng khai thác để kiểm tra tổng quát', 2, 2),
(7, 'VH-FC-007', '43C-111.22',  'Xe tải',        'Mitsubishi','Fuso Canter',  2019, '2020-03-15', 'MEC1343CXL000007', '4D34T700007', 'Trắng', 85200, 'ACTIVE',             'Xe tải tuyến Đồng Nai — Bình Dương', 2, 2),
(8, 'VH-FC-008', '51G-222.33',  'Xe con',        'Honda',   'City RS',      2023, '2023-08-01', 'MRHGC1580P000008', 'L15Z900008',  'Bạc',   41800, 'ACTIVE',             'Xe đi lại nội bộ chi nhánh', 2, 2),
(9, 'VH-FC-009', '50E-444.55',  'Xe khách',      'Ford',    'Transit 16 chỗ',2022,'2022-11-20', 'WF0EXXTTRE000009', 'Duratorq00009','Xanh', 112400,'ACTIVE',            'Xe đưa đón nhân viên ca 2–3', 2, 2),
(10,'VH-FC-010', '72A-777.88',  'Xe chuyên dụng','Mercedes','Sprinter 316', 2020, '2021-02-14', 'WDB906633N000010', 'OM651A00010', 'Trắng', 54800, 'ACTIVE',             'Xe cứu thương / vận chuyển y tế', 2, 2);

-- 2.3 Giấy tờ pháp lý (đủ OVERDUE / COMING_DUE / NORMAL) — document_id cố định 1–30
INSERT INTO `vehicle_documents`
(document_id, vehicle_id, document_type_id, document_number, issuer_name,
 issue_date, effective_date, expiry_date, fee_amount, paid_date,
 document_status, is_current, note, created_by, updated_by)
VALUES
-- Xe 1: 51C-256.89
(1,  1, 1, 'DK-50-01S-001', 'Trung tâm Đăng kiểm 50-01S', DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY),   340000,  DATE_SUB(CURDATE(), INTERVAL 173 DAY), 'VALID',   1, 'Đăng kiểm sắp hết hạn trong 7 ngày', 2, 2),
(2,  1, 2, 'BH-BVI-51C25689',   'Bảo Việt Sài Gòn',       DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_ADD(CURDATE(), INTERVAL 120 DAY), 1580000, DATE_SUB(CURDATE(), INTERVAL 245 DAY), 'VALID',   1, 'Bảo hiểm TNDS còn hiệu lực', 2, 2),
(3,  1, 3, 'PDB-51C25689',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY),   2160000, DATE_SUB(CURDATE(), INTERVAL 367 DAY), 'EXPIRED', 1, 'Phí đường bộ đã quá hạn 2 ngày', 2, 2),
-- Xe 2: 50H-112.35
(4,  2, 1, 'DK-50-03V-014', 'Trung tâm Đăng kiểm 50-03V', DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY),  340000,  DATE_SUB(CURDATE(), INTERVAL 194 DAY), 'EXPIRED', 1, 'Đăng kiểm đã quá hạn, cần xử lý trước khi khai thác', 2, 2),
(5,  2, 2, 'BH-PVI-50H11235',   'PVI Sài Gòn',            DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY),  1760000, DATE_SUB(CURDATE(), INTERVAL 351 DAY), 'VALID',   1, 'Bảo hiểm sắp hết hạn trong 14 ngày', 2, 2),
(6,  2, 3, 'PDB-50H11235',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_ADD(CURDATE(), INTERVAL 240 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 125 DAY), 'VALID',   1, 'Phí đường bộ còn hạn', 2, 2),
-- Xe 3: 51B-345.67
(7,  3, 1, 'DK-50-02S-008', 'Trung tâm Đăng kiểm 50-02S', DATE_SUB(CURDATE(), INTERVAL 95 DAY),  DATE_SUB(CURDATE(), INTERVAL 95 DAY),  DATE_ADD(CURDATE(), INTERVAL 90 DAY),  370000,  DATE_SUB(CURDATE(), INTERVAL 95 DAY),  'VALID',   1, 'Đăng kiểm còn hạn', 2, 2),
(8,  3, 2, 'BH-PTI-51B34567',   'PTI Hồ Chí Minh',        DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY),   2450000, DATE_SUB(CURDATE(), INTERVAL 369 DAY), 'EXPIRED', 1, 'Bảo hiểm đã quá hạn 4 ngày', 2, 2),
(9,  3, 3, 'PDB-51B34567',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY),   3240000, DATE_SUB(CURDATE(), INTERVAL 360 DAY), 'VALID',   1, 'Phí đường bộ sắp hết hạn trong 5 ngày', 2, 2),
-- Xe 4: 60C-990.12
(10, 4, 1, 'DK-60-01S-022', 'Trung tâm Đăng kiểm 60-01S', DATE_SUB(CURDATE(), INTERVAL 42 DAY),  DATE_SUB(CURDATE(), INTERVAL 42 DAY),  DATE_ADD(CURDATE(), INTERVAL 140 DAY), 340000,  DATE_SUB(CURDATE(), INTERVAL 42 DAY),  'VALID',   1, 'Đăng kiểm còn hạn', 2, 2),
(11, 4, 2, 'BH-MIC-60C99012',   'MIC Đồng Nai',           DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_ADD(CURDATE(), INTERVAL 185 DAY), 1320000, DATE_SUB(CURDATE(), INTERVAL 180 DAY), 'VALID',   1, 'Bảo hiểm còn hạn', 2, 2),
(12, 4, 3, 'PDB-60C99012',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 60 DAY),  DATE_SUB(CURDATE(), INTERVAL 60 DAY),  DATE_ADD(CURDATE(), INTERVAL 305 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 60 DAY),  'VALID',   1, 'Phí đường bộ còn hạn', 2, 2),
-- Xe 5: 51F-888.66
(13, 5, 1, 'DK-50-05V-017', 'Trung tâm Đăng kiểm 50-05V', DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY),  340000,  DATE_SUB(CURDATE(), INTERVAL 170 DAY), 'VALID',   1, 'Đăng kiểm sắp hết hạn trong 12 ngày', 2, 2),
(14, 5, 2, 'BH-BSH-51F88866',   'BSH Sài Gòn',            DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_ADD(CURDATE(), INTERVAL 335 DAY), 980000,  DATE_SUB(CURDATE(), INTERVAL 30 DAY),  'VALID',   1, 'Bảo hiểm còn hạn', 2, 2),
(15, 5, 3, 'PDB-51F88866',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY),   1560000, DATE_SUB(CURDATE(), INTERVAL 370 DAY), 'EXPIRED', 1, 'Phí đường bộ quá hạn 1 ngày', 2, 2),
-- Xe 6: 51LD-123.45
(16, 6, 1, 'DK-50-04D-033', 'Trung tâm Đăng kiểm 50-04D', DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 165 DAY), 370000,  DATE_SUB(CURDATE(), INTERVAL 530 DAY), 'EXPIRED', 1, 'Xe tạm ngưng khai thác, cần kiểm định lại trước khi hoạt động', 2, 2),
(17, 6, 2, 'BH-PJICO-51LD12345','PJICO Sài Gòn',          DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_ADD(CURDATE(), INTERVAL 205 DAY), 1850000, DATE_SUB(CURDATE(), INTERVAL 160 DAY), 'VALID',   1, 'Bảo hiểm còn hạn', 2, 2),
(18, 6, 3, 'PDB-51LD12345',     'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 80 DAY),  DATE_SUB(CURDATE(), INTERVAL 80 DAY),  DATE_ADD(CURDATE(), INTERVAL 285 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 80 DAY),  'VALID',   1, 'Phí đường bộ còn hạn', 2, 2),
-- Xe 7: 43C-111.22
(19, 7, 1, 'DK-43-01S-041', 'Trung tâm Đăng kiểm 43-01S', DATE_SUB(CURDATE(), INTERVAL 355 DAY), DATE_SUB(CURDATE(), INTERVAL 355 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY),  340000,  DATE_SUB(CURDATE(), INTERVAL 355 DAY), 'EXPIRED', 1, 'Đăng kiểm quá hạn 8 ngày', 2, 2),
(20, 7, 2, 'BH-GIC-43C11122',   'GIC Đồng Nai',           DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_ADD(CURDATE(), INTERVAL 165 DAY), 1420000, DATE_SUB(CURDATE(), INTERVAL 200 DAY), 'VALID',   1, 'Bảo hiểm còn hạn', 2, 2),
(21, 7, 3, 'PDB-43C11122',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_SUB(CURDATE(), INTERVAL 30 DAY),  DATE_ADD(CURDATE(), INTERVAL 10 DAY),  2160000, DATE_SUB(CURDATE(), INTERVAL 30 DAY),  'VALID',   1, 'Phí đường bộ sắp hết hạn trong 10 ngày', 2, 2),
-- Xe 8: 51G-222.33
(22, 8, 1, 'DK-51-02S-055', 'Trung tâm Đăng kiểm 51-02S', DATE_SUB(CURDATE(), INTERVAL 120 DAY), DATE_SUB(CURDATE(), INTERVAL 120 DAY), DATE_ADD(CURDATE(), INTERVAL 65 DAY),  340000,  DATE_SUB(CURDATE(), INTERVAL 120 DAY), 'VALID',   1, 'Đăng kiểm còn hạn', 2, 2),
(23, 8, 2, 'BH-VNI-51G22233',   'VNI Sài Gòn',            DATE_SUB(CURDATE(), INTERVAL 90 DAY),  DATE_SUB(CURDATE(), INTERVAL 90 DAY),  DATE_ADD(CURDATE(), INTERVAL 275 DAY), 890000,  DATE_SUB(CURDATE(), INTERVAL 90 DAY),  'VALID',   1, 'Bảo hiểm còn hạn', 2, 2),
(24, 8, 3, 'PDB-51G22233',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 365 DAY), DATE_SUB(CURDATE(), INTERVAL 365 DAY), DATE_ADD(CURDATE(), INTERVAL 3 DAY),   1560000, DATE_SUB(CURDATE(), INTERVAL 365 DAY), 'VALID',   1, 'Phí đường bộ sắp hết hạn trong 3 ngày', 2, 2),
-- Xe 9: 50E-444.55
(25, 9, 1, 'DK-50-06V-028', 'Trung tâm Đăng kiểm 50-06V', DATE_SUB(CURDATE(), INTERVAL 88 DAY),  DATE_SUB(CURDATE(), INTERVAL 88 DAY),  DATE_ADD(CURDATE(), INTERVAL 92 DAY),  370000,  DATE_SUB(CURDATE(), INTERVAL 88 DAY),  'VALID',   1, 'Đăng kiểm còn hạn', 2, 2),
(26, 9, 2, 'BH-AAA-50E44455',   'AAA Hồ Chí Minh',        DATE_SUB(CURDATE(), INTERVAL 15 DAY),  DATE_SUB(CURDATE(), INTERVAL 15 DAY),  DATE_ADD(CURDATE(), INTERVAL 11 DAY),  2280000, DATE_SUB(CURDATE(), INTERVAL 15 DAY),  'VALID',   1, 'Bảo hiểm sắp hết hạn trong 11 ngày', 2, 2),
(27, 9, 3, 'PDB-50E44455',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_ADD(CURDATE(), INTERVAL 165 DAY), 3240000, DATE_SUB(CURDATE(), INTERVAL 200 DAY), 'VALID',   1, 'Phí đường bộ còn hạn', 2, 2),
-- Xe 10: 72A-777.88
(28, 10, 1, 'DK-72-01S-009', 'Trung tâm Đăng kiểm 72-01S', DATE_SUB(CURDATE(), INTERVAL 150 DAY), DATE_SUB(CURDATE(), INTERVAL 150 DAY), DATE_ADD(CURDATE(), INTERVAL 35 DAY),  340000,  DATE_SUB(CURDATE(), INTERVAL 150 DAY), 'VALID',   1, 'Đăng kiểm còn hạn', 2, 2),
(29, 10, 2, 'BH-LIB-72A77788',   'Liberty Sài Gòn',        DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY),   2650000, DATE_SUB(CURDATE(), INTERVAL 300 DAY), 'EXPIRED', 1, 'Bảo hiểm quá hạn 6 ngày', 2, 2),
(30, 10, 3, 'PDB-72A77788',      'Cục Đường bộ Việt Nam',  DATE_SUB(CURDATE(), INTERVAL 45 DAY),  DATE_SUB(CURDATE(), INTERVAL 45 DAY),  DATE_ADD(CURDATE(), INTERVAL 320 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 45 DAY),  'VALID',   1, 'Phí đường bộ còn hạn', 2, 2);

ALTER TABLE `vehicle_documents` AUTO_INCREMENT = 31;

-- 2.4 Kế hoạch bảo dưỡng (có OVERDUE theo ngày/ODO, COMING_DUE, NORMAL)
INSERT INTO `maintenance_plans`
(plan_id, vehicle_id, maintenance_type_id, interval_days, interval_km, last_service_date, last_service_odometer,
 next_due_date, next_due_odometer, alert_before_days, alert_before_km, is_active, notes, created_by, updated_by)
VALUES
(1, 1, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 31 DAY),  48200, DATE_ADD(CURDATE(), INTERVAL 149 DAY), 53200, 15, 700, 1, 'Đã thay dầu gần đây — kỳ tiếp theo tính từ phiếu #1', 2, 2),
(2, 2, 3, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 205 DAY), 63000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 73000, 15, 500, 1, 'Kiểm tra phanh đã quá hạn, xe đang ở xưởng', 2, 2),
(3, 3, 1, 90,  10000, DATE_SUB(CURDATE(), INTERVAL 59 DAY), 95000, DATE_ADD(CURDATE(), INTERVAL 31 DAY), 105000, 15, 700, 1, 'Bảo dưỡng định kỳ đến hạn theo ODO', 2, 2),
(4, 4, 5, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 60 DAY),  26000, DATE_ADD(CURDATE(), INTERVAL 120 DAY),36000, 15, 500, 1, 'Kiểm tra hệ thống làm mát còn hạn', 2, 2),
(5, 5, 4, 365, 20000, DATE_SUB(CURDATE(), INTERVAL 350 DAY), 9000,  DATE_ADD(CURDATE(), INTERVAL 10 DAY), 29000, 15, 500, 1, 'Đảo lốp/cân bằng động sắp đến hạn theo ngày', 2, 2),
(6, 6, 1, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 210 DAY), 63800, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 68800, 15, 500, 1, 'Xe tạm ngưng nhưng vẫn có kế hoạch bảo dưỡng quá hạn', 2, 2);

-- 2.5 Phiếu bảo dưỡng (total_cost = tổng thành tiền hạng mục đối với phiếu có hạng mục)
INSERT INTO `maintenance_records`
(record_id, vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes, created_by, updated_by)
VALUES
(1, 1, 1, 'PREVENTIVE', 'Thay dầu định kỳ 48.200 km', DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_SUB(CURDATE(), INTERVAL 31 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '10:15:00'), 48200, 'Thay dầu động cơ, lọc dầu và kiểm tra tổng quát.', 'Xưởng FleetCare Quận 7', 3, 655000,  'COMPLETED',  'Hoàn tất, xe vận hành ổn định', 3, 3),
(2, 2, 2, 'CORRECTIVE', 'Sửa hệ thống phanh trước', CURDATE(), NULL, TIMESTAMP(CURDATE(), '09:00:00'), NULL, 73000, 'Kiểm tra phanh, thay má phanh trước và dầu phanh.', 'Xưởng FleetCare Bình Tân', 3, 610000, 'IN_PROGRESS', 'Đang chờ phụ tùng má phanh trước', 3, 3),
(3, 2, 2, 'PREVENTIVE', 'Kiểm tra phanh sau sửa chữa', DATE_ADD(CURDATE(), INTERVAL 1 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra lại hệ thống phanh sau khi hoàn tất sửa chữa.', 'Xưởng FleetCare Bình Tân', 3, 0, 'OPEN', 'Phiếu chờ xử lý ngày mai', 2, 2),
(4, 3, 3, 'PREVENTIVE', 'Bảo dưỡng định kỳ xe khách 95.000 km', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 59 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '15:30:00'), 95000, 'Bảo dưỡng tổng quát, thay dầu, lọc gió, kiểm tra phanh.', 'Xưởng Thaco Bus Miền Nam', 3, 2130000, 'COMPLETED', 'Hoàn tất bảo dưỡng định kỳ', 3, 3),
(5, 5, 5, 'PREVENTIVE', 'Đảo lốp và cân bằng động', CURDATE(), CURDATE(), TIMESTAMP(CURDATE(), '07:45:00'), TIMESTAMP(CURDATE(), '09:10:00'), 27800, 'Đảo lốp, cân bằng động, kiểm tra áp suất lốp.', 'Xưởng FleetCare Quận 7', 3, 180000, 'COMPLETED', 'Hoàn tất trong ngày', 3, 3),
-- Bổ sung lịch sử / báo cáo đa tháng (xe 4 chưa có kế hoạch Thay dầu — phục vụ demo tạo mới)
(6, 4, 4, 'PREVENTIVE', 'Kiểm tra làm mát định kỳ 26.500 km', DATE_SUB(CURDATE(), INTERVAL 91 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '11:30:00'), 26500, 'Kiểm tra nước làm mát, bổ sung dung dịch.', 'Xưởng FleetCare Quận 7', 3, 125000, 'COMPLETED', 'Xe 60C-990.12 — lịch sử làm mát', 3, 3),
(7, 1, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 40.000 km', DATE_SUB(CURDATE(), INTERVAL 201 DAY), DATE_SUB(CURDATE(), INTERVAL 200 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 200 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 200 DAY), '09:45:00'), 40000, 'Thay dầu và lọc dầu định kỳ.', 'Xưởng FleetCare Quận 7', 3, 655000, 'COMPLETED', 'Lịch sử bảo dưỡng xe tải 51C-256.89', 3, 3),
(8, 4, NULL, 'CORRECTIVE', 'Thay dầu + kiểm tra nhanh 30.800 km', DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '14:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '15:20:00'), 30800, 'Thay dầu + kiểm tra phanh, lọc dầu.', 'Xưởng FleetCare Quận 7', 3, 570000, 'COMPLETED', 'Mẫu phiếu tương tự kịch bản demo bước 3', 3, 3),
(9, 5, NULL, 'PREVENTIVE', 'Kiểm tra lốp và áp suất 26.500 km', DATE_SUB(CURDATE(), INTERVAL 46 DAY), DATE_SUB(CURDATE(), INTERVAL 45 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 45 DAY), '09:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 45 DAY), '10:00:00'), 26500, 'Kiểm tra lốp, bơm hơi, kiểm tra má phanh.', 'Xưởng FleetCare Quận 7', 3, 100000, 'COMPLETED', 'Bổ sung chi phí báo cáo xe 51F-888.66', 3, 3),
(10, 3, NULL, 'CORRECTIVE', 'Sửa điều hòa và kiểm tra ắc quy 92.000 km', DATE_SUB(CURDATE(), INTERVAL 121 DAY), DATE_SUB(CURDATE(), INTERVAL 120 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 120 DAY), '13:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 120 DAY), '17:00:00'), 92000, 'Sửa quạt điều hòa, kiểm tra hệ thống điện.', 'Xưởng Thaco Bus Miền Nam', 3, 350000, 'COMPLETED', 'Lịch sử sửa chữa xe khách 51B-345.67', 3, 3);

-- 2.6 Chi tiết hạng mục / phụ tùng của phiếu
INSERT INTO `maintenance_record_items`
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
-- Phiếu 1
(1, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(1, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(1, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 2
(2, 4,  'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần',  90000, NULL),
(2, 10, 'PART', 'Má phanh trước',          1, 'Bộ',  520000, 'Chờ nhập kho'),
-- Phiếu 4
(4, 1, 'WORK', 'Công thay dầu',          1, 'Lần', 150000, NULL),
(4, 3, 'WORK', 'Công thay lọc gió',      1, 'Lần',  60000, NULL),
(4, 7, 'PART', 'Dầu động cơ 10W-40 4L',  4, 'Can', 420000, NULL),
(4, 9, 'PART', 'Lọc gió động cơ',        2, 'Cái', 120000, NULL),
-- Phiếu 5
(5, 5, 'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL),
-- Phiếu 6 (xe 60C-990.12)
(6, 6, 'WORK', 'Kiểm tra hệ thống làm mát', 1, 'Lần', 80000, NULL),
(6, 12, 'PART', 'Nước làm mát động cơ',      1, 'Lít', 45000, NULL),
-- Phiếu 7 (lịch sử xe 51C-256.89)
(7, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(7, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(7, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 8 (mẫu demo xe 60C-990.12)
(8, 1, 'WORK', 'Công thay dầu',           1, 'Lần', 150000, NULL),
(8, 7, 'PART', 'Dầu động cơ 10W-40 4L',   1, 'Can', 420000, NULL),
-- Phiếu 9 (xe 51F-888.66)
(9, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 100000, NULL),
-- Phiếu 10 (xe 51B-345.67)
(10, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(10, 11, 'PART', 'Dầu phanh DOT4',         2, 'Chai', 95000, 'Thay dầu phanh + kiểm tra điện');

-- 2.7 Kế hoạch bảo dưỡng bổ sung
INSERT INTO `maintenance_plans`
(plan_id, vehicle_id, maintenance_type_id, interval_days, interval_km, last_service_date, last_service_odometer,
 next_due_date, next_due_odometer, alert_before_days, alert_before_km, is_active, notes, created_by, updated_by)
VALUES
-- Xe 60C-990.12: thêm kế hoạch thay dầu (demo tạo kế hoạch mới / cảnh báo theo km)
(7, 4, 2, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 16 DAY), 30800, DATE_ADD(CURDATE(), INTERVAL 164 DAY), 35800, 15, 500, 1, 'Thay dầu sau phiếu #8 — kỳ tiếp theo tính từ 30.800 km', 2, 2),
-- Xe 51B-345.67: thêm kế hoạch thay dầu (COMING_DUE theo ODO: 96.500 km, next 97.000 km)
(8, 3, 2, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 59 DAY), 95000, DATE_ADD(CURDATE(), INTERVAL 121 DAY), 97000, 15, 700, 1, 'Thay dầu sắp đến hạn theo ODO (còn ~500 km)', 2, 2),
-- Xe 51F-888.66: bảo dưỡng định kỳ
(9, 5, 1, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 46 DAY), 26500, DATE_ADD(CURDATE(), INTERVAL 134 DAY), 31500, 15, 500, 1, 'Bảo dưỡng định kỳ xe con — còn hạn', 2, 2);

-- 2.8 Phiếu bảo dưỡng bổ sung (lịch sử đa tháng + báo cáo chi phí)
INSERT INTO `maintenance_records`
(record_id, vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes, created_by, updated_by)
VALUES
(11, 3, 8, 'PREVENTIVE', 'Thay dầu định kỳ 88.000 km', DATE_SUB(CURDATE(), INTERVAL 136 DAY), DATE_SUB(CURDATE(), INTERVAL 135 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 135 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 135 DAY), '10:00:00'), 88000, 'Thay dầu động cơ và lọc dầu định kỳ.', 'Xưởng Thaco Bus Miền Nam', 3, 655000, 'COMPLETED', 'Bổ sung chi phí báo cáo tháng trước — xe 51B-345.67', 3, 3),
(12, 1, NULL, 'CORRECTIVE', 'Sửa hệ thống phanh sau 45.500 km', DATE_SUB(CURDATE(), INTERVAL 76 DAY), DATE_SUB(CURDATE(), INTERVAL 75 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 75 DAY), '13:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 75 DAY), '16:30:00'), 45500, 'Thay má phanh sau, bổ sung dầu phanh DOT4.', 'Xưởng FleetCare Quận 7', 3, 705000, 'COMPLETED', 'Lịch sử sửa chữa xe tải 51C-256.89', 3, 3),
(13, 4, 7, 'PREVENTIVE', 'Kiểm tra lốp và cân bằng động 28.000 km', DATE_SUB(CURDATE(), INTERVAL 46 DAY), DATE_SUB(CURDATE(), INTERVAL 45 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 45 DAY), '09:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 45 DAY), '10:30:00'), 28000, 'Đảo lốp, cân bằng động, kiểm tra áp suất.', 'Xưởng FleetCare Quận 7', 3, 180000, 'COMPLETED', 'Bổ sung lịch sử xe 60C-990.12', 3, 3),
(14, 6, NULL, 'PREVENTIVE', 'Bảo dưỡng tổng quát trước khi tạm ngưng 63.800 km', DATE_SUB(CURDATE(), INTERVAL 211 DAY), DATE_SUB(CURDATE(), INTERVAL 210 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 210 DAY), '07:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 210 DAY), '12:00:00'), 63800, 'Bảo dưỡng tổng quát trước khi xe tạm ngưng khai thác.', 'Xưởng FleetCare Bình Tân', 3, 835000, 'COMPLETED', 'Lịch sử xe chuyên dụng 51LD-123.45', 3, 3),
(15, 2, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 60.000 km', DATE_SUB(CURDATE(), INTERVAL 206 DAY), DATE_SUB(CURDATE(), INTERVAL 205 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 205 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 205 DAY), '09:30:00'), 60000, 'Thay dầu và lọc dầu — mốc trước khi đến hạn kiểm tra phanh.', 'Xưởng FleetCare Bình Tân', 3, 655000, 'COMPLETED', 'Lịch sử xe 50H-112.35 trước khi vào xưởng sửa phanh', 3, 3);

-- 2.9 Chi tiết hạng mục cho phiếu bổ sung
INSERT INTO `maintenance_record_items`
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
-- Phiếu 11
(11, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(11, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(11, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 12
(12, 4,  'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần',  90000, NULL),
(12, 10, 'PART', 'Má phanh trước',          1, 'Bộ',  520000, 'Thay má phanh sau'),
(12, 11, 'PART', 'Dầu phanh DOT4',          1, 'Chai',  95000, NULL),
-- Phiếu 13
(13, 5, 'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL),
-- Phiếu 14
(14, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(14, 3, 'WORK', 'Công thay lọc gió',      1, 'Lần',  60000, NULL),
(14, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(14, 9, 'PART', 'Lọc gió động cơ',        1, 'Cái', 120000, NULL),
(14, 6, 'WORK', 'Kiểm tra hệ thống làm mát', 1, 'Lần', 85000, NULL),
-- Phiếu 15
(15, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(15, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(15, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL);

-- 2.10 Kế hoạch bảo dưỡng cho xe mới + bổ sung xe hiện có
INSERT INTO `maintenance_plans`
(plan_id, vehicle_id, maintenance_type_id, interval_days, interval_km, last_service_date, last_service_odometer,
 next_due_date, next_due_odometer, alert_before_days, alert_before_km, is_active, notes, created_by, updated_by)
VALUES
-- Xe 43C-111.22: thay dầu OVERDUE theo ODO (85.200 >= 85.000)
(10, 7, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 190 DAY), 80000, DATE_ADD(CURDATE(), INTERVAL 170 DAY), 85000, 15, 500, 1, 'Thay dầu quá hạn theo ODO — xe vượt 85.000 km', 2, 2),
-- Xe 43C-111.22: kiểm tra phanh COMING_DUE theo ngày
(11, 7, 3, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 168 DAY), 82000, DATE_ADD(CURDATE(), INTERVAL 5 DAY),  92000, 15, 500, 1, 'Kiểm tra phanh sắp đến hạn trong 5 ngày', 2, 2),
-- Xe 51G-222.33: thay dầu + bảo dưỡng định kỳ
(12, 8, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 55 DAY),  38500, DATE_ADD(CURDATE(), INTERVAL 125 DAY), 43500, 15, 500, 1, 'Thay dầu xe City — còn hạn', 2, 2),
(13, 8, 1, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 55 DAY),  38500, DATE_ADD(CURDATE(), INTERVAL 125 DAY), 43500, 15, 500, 1, 'Bảo dưỡng định kỳ xe con', 2, 2),
-- Xe 50E-444.55: bảo dưỡng OVERDUE theo ngày
(14, 9, 1, 90,  10000, DATE_SUB(CURDATE(), INTERVAL 100 DAY), 108000, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 118000, 15, 700, 1, 'Bảo dưỡng định kỳ quá hạn 10 ngày', 2, 2),
-- Xe 50E-444.55: thay dầu COMING_DUE theo ODO
(15, 9, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 100 DAY), 108000, DATE_ADD(CURDATE(), INTERVAL 80 DAY), 113000, 15, 700, 1, 'Thay dầu sắp đến hạn (còn ~600 km)', 2, 2),
-- Xe 72A-777.88: làm mát + lốp
(16, 10, 5, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 90 DAY),  51000, DATE_ADD(CURDATE(), INTERVAL 90 DAY),  61000, 15, 500, 1, 'Kiểm tra làm mát xe cứu thương', 2, 2),
(17, 10, 4, 365, 20000, DATE_SUB(CURDATE(), INTERVAL 340 DAY), 35000, DATE_ADD(CURDATE(), INTERVAL 25 DAY), 55000, 15, 500, 1, 'Đảo lốp sắp đến hạn theo ngày', 2, 2),
-- Bổ sung xe hiện có
(18, 1, 3, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 76 DAY),  45500, DATE_ADD(CURDATE(), INTERVAL 104 DAY), 55500, 15, 500, 1, 'Kiểm tra phanh sau phiếu sửa #12', 2, 2),
(19, 2, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 205 DAY), 60000, DATE_ADD(CURDATE(), INTERVAL 170 DAY), 65000, 15, 500, 1, 'Thay dầu xe 50H — kỳ sau phiếu #15', 2, 2),
(20, 6, 2, 180, 5000,  DATE_SUB(CURDATE(), INTERVAL 210 DAY), 63800, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 68800, 15, 500, 0, 'Kế hoạch thay dầu đã hủy kích hoạt (xe tạm ngưng)', 2, 2);

-- 2.12 Phiếu bảo dưỡng mở rộng (xe mới + lịch sử dày hơn)
INSERT INTO `maintenance_records`
(record_id, vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes, created_by, updated_by)
VALUES
-- Xe 43C-111.22 (7)
(16, 7, 10, 'PREVENTIVE', 'Thay dầu định kỳ 80.000 km', DATE_SUB(CURDATE(), INTERVAL 191 DAY), DATE_SUB(CURDATE(), INTERVAL 190 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 190 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 190 DAY), '09:30:00'), 80000, 'Thay dầu, lọc dầu, kiểm tra tổng quát.', 'Xưởng FleetCare Biên Hòa', 3, 655000, 'COMPLETED', 'Lịch sử xe 43C-111.22', 3, 3),
(17, 7, NULL, 'CORRECTIVE', 'Sửa hệ thống điện 84.200 km', DATE_SUB(CURDATE(), INTERVAL 21 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 20 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 20 DAY), '14:00:00'), 84200, 'Sửa cầu chì, thay đèn pha, kiểm tra dây điện.', 'Xưởng FleetCare Biên Hòa', 3, 395000, 'COMPLETED', 'Sửa chữa điện xe tải', 3, 3),
(18, 7, 11, 'PREVENTIVE', 'Kiểm tra phanh định kỳ', DATE_ADD(CURDATE(), INTERVAL 5 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra má phanh, dầu phanh, đĩa phanh.', 'Xưởng FleetCare Biên Hòa', 3, 0, 'OPEN', 'Phiếu chờ đến ngày hẹn', 2, 2),
-- Xe 51G-222.33 (8)
(19, 8, 12, 'PREVENTIVE', 'Thay dầu định kỳ 38.500 km', DATE_SUB(CURDATE(), INTERVAL 56 DAY), DATE_SUB(CURDATE(), INTERVAL 55 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 55 DAY), '09:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 55 DAY), '10:15:00'), 38500, 'Thay dầu và lọc dầu xe City.', 'Xưởng FleetCare Quận 7', 3, 655000, 'COMPLETED', 'Bảo dưỡng định kỳ xe con', 3, 3),
(20, 8, NULL, 'CORRECTIVE', 'Thay lốp trước 40.500 km', DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '14:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '16:00:00'), 40500, 'Thay 2 lốp trước do mòn không đều.', 'Garage Honda Phú Mỹ Hưng', 3, 4380000, 'COMPLETED', 'Thay lốp xe con', 3, 3),
(21, 8, NULL, 'PREVENTIVE', 'Kiểm tra trước hành trình dài', DATE_ADD(CURDATE(), INTERVAL 3 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra áp suất lốp, dầu, nước làm mát.', 'Xưởng FleetCare Quận 7', 3, 0, 'OPEN', 'Phiếu hẹn trước chuyến đi', 2, 2),
-- Xe 50E-444.55 (9)
(22, 9, 14, 'PREVENTIVE', 'Bảo dưỡng định kỳ 108.000 km', DATE_SUB(CURDATE(), INTERVAL 101 DAY), DATE_SUB(CURDATE(), INTERVAL 100 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 100 DAY), '07:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 100 DAY), '16:00:00'), 108000, 'Bảo dưỡng tổng quát xe Transit 16 chỗ.', 'Xưởng Ford Miền Nam', 3, 2540000, 'COMPLETED', 'Bảo dưỡng lớn xe khách', 3, 3),
(23, 9, NULL, 'CORRECTIVE', 'Sửa điều hòa 110.800 km', DATE_SUB(CURDATE(), INTERVAL 36 DAY), DATE_SUB(CURDATE(), INTERVAL 35 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 35 DAY), '11:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 35 DAY), '15:30:00'), 110800, 'Nạp gas, sửa quạt điều hòa khoang hành khách.', 'Xưởng Ford Miền Nam', 3, 2850000, 'COMPLETED', 'Sửa điều hòa xe khách', 3, 3),
(24, 9, 15, 'PREVENTIVE', 'Thay dầu định kỳ', DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '08:00:00'), NULL, 112200, 'Thay dầu và lọc dầu — đang chờ hoàn tất.', 'Xưởng Ford Miền Nam', 3, 570000, 'IN_PROGRESS', 'Đang thay dầu, chưa hoàn tất', 3, 3),
-- Xe 72A-777.88 (10)
(25, 10, 16, 'PREVENTIVE', 'Kiểm tra làm mát 51.000 km', DATE_SUB(CURDATE(), INTERVAL 91 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '09:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '10:30:00'), 51000, 'Vệ sinh két, bổ sung nước làm mát.', 'Xưởng Mercedes VN', 3, 245000, 'COMPLETED', 'Bảo dưỡng làm mát xe cứu thương', 3, 3),
(26, 10, NULL, 'CORRECTIVE', 'Thay ắc quy 54.500 km', DATE_SUB(CURDATE(), INTERVAL 11 DAY), DATE_SUB(CURDATE(), INTERVAL 10 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '13:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '14:00:00'), 54500, 'Thay ắc quy 12V, kiểm tra hệ thống sạc.', 'Xưởng Mercedes VN', 3, 1930000, 'COMPLETED', 'Thay ắc quy xe chuyên dụng', 3, 3),
(27, 10, 17, 'PREVENTIVE', 'Đảo lốp định kỳ', DATE_ADD(CURDATE(), INTERVAL 25 DAY), NULL, NULL, NULL, NULL, 'Đảo lốp và cân bằng động xe Sprinter.', 'Xưởng Mercedes VN', 3, 0, 'OPEN', 'Phiếu hẹn đảo lốp', 2, 2),
-- Bổ sung lịch sử xe hiện có (đa tháng, đa trạng thái)
(28, 1, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 35.000 km', DATE_SUB(CURDATE(), INTERVAL 366 DAY), DATE_SUB(CURDATE(), INTERVAL 365 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 365 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 365 DAY), '09:30:00'), 35000, 'Thay dầu và lọc dầu.', 'Xưởng FleetCare Quận 7', 3, 655000, 'COMPLETED', 'Lịch sử năm trước — xe 51C-256.89', 3, 3),
(29, 3, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 82.000 km', DATE_SUB(CURDATE(), INTERVAL 166 DAY), DATE_SUB(CURDATE(), INTERVAL 165 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 165 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 165 DAY), '09:30:00'), 82000, 'Thay dầu, lọc dầu định kỳ.', 'Xưởng Thaco Bus Miền Nam', 3, 655000, 'COMPLETED', 'Lịch sử thêm xe 51B-345.67', 3, 3),
(30, 3, NULL, 'CORRECTIVE', 'Thay ắc quy 94.800 km', DATE_SUB(CURDATE(), INTERVAL 26 DAY), DATE_SUB(CURDATE(), INTERVAL 25 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 25 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 25 DAY), '11:00:00'), 94800, 'Thay ắc quy, kiểm tra máy phát điện.', 'Xưởng Thaco Bus Miền Nam', 3, 1930000, 'COMPLETED', 'Thay ắc quy xe khách', 3, 3),
(31, 5, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 24.000 km', DATE_SUB(CURDATE(), INTERVAL 106 DAY), DATE_SUB(CURDATE(), INTERVAL 105 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 105 DAY), '09:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 105 DAY), '10:00:00'), 24000, 'Thay dầu và lọc dầu xe Vios.', 'Xưởng FleetCare Quận 7', 3, 655000, 'COMPLETED', 'Lịch sử thêm xe 51F-888.66', 3, 3),
(32, 5, NULL, 'CORRECTIVE', 'Sửa điều hòa 27.200 km', DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 15 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '14:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 15 DAY), '16:30:00'), 27200, 'Nạp gas, vệ sinh dàn lạnh.', 'Garage Toyota Gò Vấp', 3, 250000, 'COMPLETED', 'Sửa điều hòa xe con', 3, 3),
(33, 4, NULL, 'CORRECTIVE', 'Sửa hệ thống phanh 31.200 km', DATE_SUB(CURDATE(), INTERVAL 61 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 60 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 60 DAY), '11:00:00'), 31200, 'Thay dầu phanh, kiểm tra đĩa phanh.', 'Xưởng FleetCare Quận 7', 3, 285000, 'COMPLETED', 'Sửa phanh xe bán tải', 3, 3),
(34, 2, NULL, 'CORRECTIVE', 'Sửa hộp số 68.500 km', DATE_SUB(CURDATE(), INTERVAL 91 DAY), DATE_SUB(CURDATE(), INTERVAL 90 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 90 DAY), '17:00:00'), 68500, 'Kiểm tra hộp số, thay dầu hộp số.', 'Xưởng Hyundai Miền Nam', 3, 2260000, 'COMPLETED', 'Sửa chữa lớn xe 50H-112.35', 3, 3),
(35, 1, 18, 'PREVENTIVE', 'Kiểm tra phanh định kỳ', DATE_ADD(CURDATE(), INTERVAL 14 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra phanh sau 45.500 km.', 'Xưởng FleetCare Quận 7', 3, 0, 'OPEN', 'Phiếu hẹn kiểm tra phanh', 2, 2),
(36, 6, NULL, 'CORRECTIVE', 'Sửa động cơ 65.000 km', DATE_SUB(CURDATE(), INTERVAL 241 DAY), DATE_SUB(CURDATE(), INTERVAL 240 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 240 DAY), '07:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 240 DAY), '18:00:00'), 65000, 'Sửa turbo, thay bugi, vệ sinh két nước.', 'Xưởng Hino Việt Nam', 3, 1210000, 'COMPLETED', 'Sửa chữa lớn trước khi tạm ngưng xe', 3, 3),
(37, 8, NULL, 'PREVENTIVE', 'Thay dầu nhỏ 32.000 km', DATE_SUB(CURDATE(), INTERVAL 196 DAY), DATE_SUB(CURDATE(), INTERVAL 195 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '11:00:00'), 32000, 'Thay dầu định kỳ lần trước.', 'Xưởng FleetCare Quận 7', 3, 655000, 'COMPLETED', 'Lịch sử sớm xe 51G-222.33', 3, 3),
(38, 9, NULL, 'PREVENTIVE', 'Bảo dưỡng 100.000 km', DATE_SUB(CURDATE(), INTERVAL 196 DAY), DATE_SUB(CURDATE(), INTERVAL 195 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '07:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '15:00:00'), 100000, 'Bảo dưỡng mốc 100.000 km.', 'Xưởng Ford Miền Nam', 3, 2400000, 'COMPLETED', 'Mốc 100k xe Transit', 3, 3),
(39, 7, NULL, 'PREVENTIVE', 'Thay dầu 72.000 km', DATE_SUB(CURDATE(), INTERVAL 286 DAY), DATE_SUB(CURDATE(), INTERVAL 285 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 285 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 285 DAY), '09:30:00'), 72000, 'Thay dầu định kỳ.', 'Xưởng FleetCare Biên Hòa', 3, 655000, 'COMPLETED', 'Lịch sử xa hơn xe 43C-111.22', 3, 3),
(40, 10, NULL, 'PREVENTIVE', 'Bảo dưỡng tổng quát 45.000 km', DATE_SUB(CURDATE(), INTERVAL 181 DAY), DATE_SUB(CURDATE(), INTERVAL 180 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 180 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 180 DAY), '12:00:00'), 45000, 'Bảo dưỡng tổng quát, thay lọc gió, kiểm tra phanh.', 'Xưởng Mercedes VN', 3, 420000, 'COMPLETED', 'Lịch sử bảo dưỡng Sprinter', 3, 3),
(41, 4, NULL, 'PREVENTIVE', 'Kiểm tra trước chuyến công tác', DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '15:00:00'), NULL, 31400, 'Kiểm tra nhanh trước chuyến đi — hủy do đổi lịch.', 'Xưởng FleetCare Quận 7', 3, 0, 'CANCELLED', 'Khách hủy lịch, không thực hiện', 2, 2);

-- 2.13 Chi tiết hạng mục cho phiếu mở rộng
INSERT INTO `maintenance_record_items`
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
-- Phiếu 16
(16, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(16, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(16, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 17
(17, 17, 'WORK', 'Sửa hệ thống điện', 1, 'Lần', 180000, NULL),
(17, 11, 'PART', 'Dầu phanh DOT4',   1, 'Chai',  95000, 'Kiểm tra điện phụ'),
(17, 9,  'PART', 'Lọc gió động cơ',  1, 'Cái', 120000, NULL),
-- Phiếu 19
(19, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(19, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(19, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 20
(20, 5,  'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL),
(20, 16, 'PART', 'Lốp 195/65R15',            2, 'Cái', 2100000, 'Thay 2 lốp trước'),
-- Phiếu 22
(22, 1, 'WORK', 'Công thay dầu',          1, 'Lần', 150000, NULL),
(22, 3, 'WORK', 'Công thay lọc gió',      1, 'Lần',  60000, NULL),
(22, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần',  90000, NULL),
(22, 7, 'PART', 'Dầu động cơ 10W-40 4L',  4, 'Can', 420000, NULL),
(22, 9, 'PART', 'Lọc gió động cơ',        2, 'Cái', 120000, NULL),
(22, 18, 'PART', 'Bugi đánh lửa',          1, 'Bộ',  320000, NULL),
-- Phiếu 23
(23, 13, 'WORK', 'Sửa hệ thống điều hòa', 1, 'Lần', 250000, NULL),
(23, NULL, 'PART', 'Máy nén điều hòa',       1, 'Cái', 2000000, 'Thay máy nén khoang hành khách'),
(23, NULL, 'PART', 'Gas điều hòa R134a',     3, 'Bình', 200000, 'Nạp gas sau thay máy nén'),
-- Phiếu 24 (IN_PROGRESS — đã có hạng mục)
(24, 1, 'WORK', 'Công thay dầu',          1, 'Lần', 150000, NULL),
(24, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
-- Phiếu 25
(25, 19, 'WORK', 'Vệ sinh két nước',          1, 'Lần', 120000, NULL),
(25, 6,  'WORK', 'Kiểm tra hệ thống làm mát', 1, 'Lần',  80000, NULL),
(25, 12, 'PART', 'Nước làm mát động cơ',       1, 'Lít',  45000, NULL),
-- Phiếu 26
(26, 14, 'WORK', 'Thay ắc quy', 1, 'Lần', 80000, NULL),
(26, 15, 'PART', 'Ắc quy 12V 65Ah', 1, 'Cái', 1850000, NULL),
-- Phiếu 28–40 (các phiếu COMPLETED chính)
(28, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(28, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(28, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(29, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(29, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(29, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(30, 14, 'WORK', 'Thay ắc quy', 1, 'Lần', 80000, NULL),
(30, 15, 'PART', 'Ắc quy 12V 65Ah', 1, 'Cái', 1850000, NULL),
(31, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(31, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(31, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(32, 13, 'WORK', 'Sửa hệ thống điều hòa', 1, 'Lần', 250000, NULL),
(33, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(33, 11, 'PART', 'Dầu phanh DOT4', 2, 'Chai', 95000, NULL),
(34, 17, 'WORK', 'Sửa hệ thống điện', 1, 'Lần', 180000, NULL),
(34, 10, 'PART', 'Má phanh trước', 4, 'Bộ', 520000, 'Kiểm tra hộp số + phanh'),
(36, 19, 'WORK', 'Vệ sinh két nước', 1, 'Lần', 120000, NULL),
(36, 18, 'PART', 'Bugi đánh lửa', 2, 'Bộ', 320000, NULL),
(36, 20, 'PART', 'Dây curoa động cơ', 1, 'Cái', 450000, NULL),
(37, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(37, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(37, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(38, 1, 'WORK', 'Công thay dầu', 1, 'Lần', 150000, NULL),
(38, 3, 'WORK', 'Công thay lọc gió', 1, 'Lần', 60000, NULL),
(38, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(38, 7, 'PART', 'Dầu động cơ 10W-40 4L', 5, 'Can', 420000, NULL),
(39, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(39, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(39, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(40, 1, 'WORK', 'Công thay dầu', 1, 'Lần', 150000, NULL),
(40, 3, 'WORK', 'Công thay lọc gió', 1, 'Lần', 60000, NULL),
(40, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(40, 9, 'PART', 'Lọc gió động cơ', 1, 'Cái', 120000, NULL);

-- 2.14 Lịch sử dày cho xe 51B-345.67 (vehicle_id = 3) — 15 phiếu, luân phiên 4 KTV
INSERT INTO `maintenance_records`
(record_id, vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes, created_by, updated_by)
VALUES
(42, 3, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 58.000 km', DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_SUB(CURDATE(), INTERVAL 350 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 350 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 350 DAY), '09:30:00'), 58000, 'Thay dầu và lọc dầu định kỳ.', 'Xưởng Thaco Bus Miền Nam', 4, 655000, 'COMPLETED', '[51B-345.67] KTV Minh — mốc 58k', 4, 4),
(43, 3, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 62.000 km', DATE_SUB(CURDATE(), INTERVAL 331 DAY), DATE_SUB(CURDATE(), INTERVAL 330 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 330 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 330 DAY), '10:00:00'), 62000, 'Thay dầu, kiểm tra bugi.', 'Xưởng Thaco Bus Miền Nam', 5, 975000, 'COMPLETED', '[51B-345.67] KTV Hùng — thay bugi kèm dầu', 5, 5),
(44, 3, NULL, 'PREVENTIVE', 'Kiểm tra phanh 66.000 km', DATE_SUB(CURDATE(), INTERVAL 311 DAY), DATE_SUB(CURDATE(), INTERVAL 310 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 310 DAY), '09:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 310 DAY), '10:30:00'), 66000, 'Kiểm tra má phanh, dầu phanh, đĩa phanh.', 'Xưởng Thaco Bus Miền Nam', 6, 280000, 'COMPLETED', '[51B-345.67] KTV Phúc — kiểm tra phanh', 6, 6),
(45, 3, NULL, 'PREVENTIVE', 'Bảo dưỡng tổng quát 70.000 km', DATE_SUB(CURDATE(), INTERVAL 281 DAY), DATE_SUB(CURDATE(), INTERVAL 280 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 280 DAY), '07:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 280 DAY), '14:00:00'), 70000, 'Bảo dưỡng mốc 70.000 km: dầu, lọc gió, làm mát.', 'Xưởng Thaco Bus Miền Nam', 3, 835000, 'COMPLETED', '[51B-345.67] KTV An — bảo dưỡng 70k', 3, 3),
(46, 3, NULL, 'PREVENTIVE', 'Đảo lốp và cân bằng 74.000 km', DATE_SUB(CURDATE(), INTERVAL 251 DAY), DATE_SUB(CURDATE(), INTERVAL 250 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 250 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 250 DAY), '11:30:00'), 74000, 'Đảo lốp, cân bằng động, kiểm tra áp suất.', 'Xưởng Thaco Bus Miền Nam', 4, 180000, 'COMPLETED', '[51B-345.67] KTV Minh — đảo lốp', 4, 4),
(47, 3, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 77.000 km', DATE_SUB(CURDATE(), INTERVAL 221 DAY), DATE_SUB(CURDATE(), INTERVAL 220 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 220 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 220 DAY), '09:15:00'), 77000, 'Thay dầu và lọc dầu.', 'Xưởng Thaco Bus Miền Nam', 5, 655000, 'COMPLETED', '[51B-345.67] KTV Hùng — thay dầu 77k', 5, 5),
(48, 3, NULL, 'CORRECTIVE', 'Sửa hệ thống điện 79.000 km', DATE_SUB(CURDATE(), INTERVAL 196 DAY), DATE_SUB(CURDATE(), INTERVAL 195 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '13:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 195 DAY), '16:00:00'), 79000, 'Sửa cầu chì, thay đèn hậu, kiểm tra dây điện.', 'Xưởng Thaco Bus Miền Nam', 6, 395000, 'COMPLETED', '[51B-345.67] KTV Phúc — sửa điện', 6, 6),
(49, 3, NULL, 'PREVENTIVE', 'Thay dầu định kỳ 83.500 km', DATE_SUB(CURDATE(), INTERVAL 176 DAY), DATE_SUB(CURDATE(), INTERVAL 175 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 175 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 175 DAY), '09:30:00'), 83500, 'Thay dầu, lọc dầu, kiểm tra nhanh.', 'Xưởng Thaco Bus Miền Nam', 3, 655000, 'COMPLETED', '[51B-345.67] KTV An — thay dầu 83.5k', 3, 3),
(50, 3, NULL, 'CORRECTIVE', 'Sửa điều hòa 86.500 km', DATE_SUB(CURDATE(), INTERVAL 146 DAY), DATE_SUB(CURDATE(), INTERVAL 145 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 145 DAY), '11:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 145 DAY), '15:00:00'), 86500, 'Vệ sinh dàn lạnh, nạp gas điều hòa.', 'Xưởng Thaco Bus Miền Nam', 4, 340000, 'COMPLETED', '[51B-345.67] KTV Minh — sửa điều hòa', 4, 4),
(51, 3, 3,  'PREVENTIVE', 'Bảo dưỡng định kỳ 90.500 km', DATE_SUB(CURDATE(), INTERVAL 116 DAY), DATE_SUB(CURDATE(), INTERVAL 115 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 115 DAY), '07:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 115 DAY), '15:00:00'), 90500, 'Bảo dưỡng tổng quát giữa kỳ: dầu, lọc gió, phanh.', 'Xưởng Thaco Bus Miền Nam', 5, 2130000, 'COMPLETED', '[51B-345.67] KTV Hùng — BD giữa kỳ 90.5k', 5, 5),
(52, 3, NULL, 'CORRECTIVE', 'Sửa phanh 92.500 km', DATE_SUB(CURDATE(), INTERVAL 96 DAY), DATE_SUB(CURDATE(), INTERVAL 95 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 95 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 95 DAY), '12:00:00'), 92500, 'Thay má phanh sau, bổ sung dầu phanh.', 'Xưởng Thaco Bus Miền Nam', 6, 705000, 'COMPLETED', '[51B-345.67] KTV Phúc — sửa phanh sau', 6, 6),
(53, 3, NULL, 'PREVENTIVE', 'Kiểm tra làm mát 94.200 km', DATE_SUB(CURDATE(), INTERVAL 71 DAY), DATE_SUB(CURDATE(), INTERVAL 70 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 70 DAY), '09:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 70 DAY), '10:45:00'), 94200, 'Kiểm tra nước làm mát, bổ sung dung dịch.', 'Xưởng Thaco Bus Miền Nam', 3, 125000, 'COMPLETED', '[51B-345.67] KTV An — kiểm tra làm mát', 3, 3),
(54, 3, 8,  'PREVENTIVE', 'Thay dầu định kỳ 95.800 km', DATE_SUB(CURDATE(), INTERVAL 43 DAY), DATE_SUB(CURDATE(), INTERVAL 42 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 42 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 42 DAY), '09:30:00'), 95800, 'Thay dầu trước mốc 96.500 km hiện tại.', 'Xưởng Thaco Bus Miền Nam', 4, 655000, 'COMPLETED', '[51B-345.67] KTV Minh — thay dầu 95.8k', 4, 4),
(55, 3, NULL, 'CORRECTIVE', 'Thay ắc quy 96.300 km', DATE_SUB(CURDATE(), INTERVAL 19 DAY), DATE_SUB(CURDATE(), INTERVAL 18 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 18 DAY), '10:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 18 DAY), '11:00:00'), 96300, 'Thay ắc quy, kiểm tra máy phát điện.', 'Xưởng Thaco Bus Miền Nam', 5, 1930000, 'COMPLETED', '[51B-345.67] KTV Hùng — thay ắc quy', 5, 5),
(56, 3, 8,  'PREVENTIVE', 'Thay dầu theo kế hoạch #8', DATE_ADD(CURDATE(), INTERVAL 3 DAY), NULL, NULL, NULL, NULL, 'Thay dầu theo kế hoạch — chờ đến ngày hẹn.', 'Xưởng Thaco Bus Miền Nam', 6, 0, 'OPEN', '[51B-345.67] KTV Phúc — phiếu hẹn thay dầu', 2, 2);

-- 2.16 Chi tiết hạng mục — lịch sử xe 51B-345.67 (phiếu 42–55)
INSERT INTO `maintenance_record_items`
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
-- Phiếu 42 (thay dầu — 655k)
(42, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(42, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(42, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 43 (dầu + bugi — 975k)
(43, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(43, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(43, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
(43, 18, 'PART', 'Bugi đánh lửa',          1, 'Bộ',  320000, NULL),
-- Phiếu 44 (phanh — 280k)
(44, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(44, 11, 'PART', 'Dầu phanh DOT4',         2, 'Chai', 95000, NULL),
-- Phiếu 45 (BD 70k — 835k)
(45, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(45, 6, 'WORK', 'Kiểm tra hệ thống làm mát', 1, 'Lần', 80000, NULL),
(45, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(45, 9, 'PART', 'Lọc gió động cơ',        1, 'Cái', 120000, NULL),
(45, 12, 'PART', 'Nước làm mát động cơ',   1, 'Lít',  45000, NULL),
-- Phiếu 46 (lốp — 180k)
(46, 5, 'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL),
-- Phiếu 47 (thay dầu — 655k)
(47, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(47, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(47, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 48 (điện — 395k)
(48, 17, 'WORK', 'Sửa hệ thống điện', 1, 'Lần', 180000, NULL),
(48, 9, 'PART', 'Lọc gió động cơ',  1, 'Cái', 120000, NULL),
(48, 11, 'PART', 'Dầu phanh DOT4',  1, 'Chai', 95000, NULL),
-- Phiếu 49 (thay dầu — 655k)
(49, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(49, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(49, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 50 (điều hòa — 350k)
(50, 13, 'WORK', 'Sửa hệ thống điều hòa', 1, 'Lần', 250000, NULL),
(50, 12, 'PART', 'Nước làm mát động cơ',   2, 'Lít',  45000, 'Vệ sinh kèm bổ sung'),
-- Phiếu 51 (BD giữa kỳ — 2130k)
(51, 1, 'WORK', 'Công thay dầu',          1, 'Lần', 150000, NULL),
(51, 3, 'WORK', 'Công thay lọc gió',      1, 'Lần',  60000, NULL),
(51, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần',  90000, NULL),
(51, 7, 'PART', 'Dầu động cơ 10W-40 4L',  4, 'Can', 420000, NULL),
(51, 9, 'PART', 'Lọc gió động cơ',        2, 'Cái', 120000, NULL),
-- Phiếu 52 (phanh sau — 705k)
(52, 4, 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
(52, 10, 'PART', 'Má phanh trước',         1, 'Bộ', 520000, 'Thay má phanh sau'),
(52, 11, 'PART', 'Dầu phanh DOT4',         1, 'Chai', 95000, NULL),
-- Phiếu 53 (làm mát — 125k)
(53, 6, 'WORK', 'Kiểm tra hệ thống làm mát', 1, 'Lần', 80000, NULL),
(53, 12, 'PART', 'Nước làm mát động cơ',      1, 'Lít',  45000, NULL),
-- Phiếu 54 (thay dầu — 655k)
(54, 1, 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
(54, 7, 'PART', 'Dầu động cơ 10W-40 4L',  1, 'Can', 420000, NULL),
(54, 8, 'PART', 'Lọc dầu động cơ',        1, 'Cái',  85000, NULL),
-- Phiếu 55 (ắc quy — 1930k)
(55, 14, 'WORK', 'Thay ắc quy', 1, 'Lần', 80000, NULL),
(55, 15, 'PART', 'Ắc quy 12V 65Ah', 1, 'Cái', 1850000, NULL);

-- 2.17 Cảnh báo hệ thống — PHẢI chạy sau giấy tờ + kế hoạch; tra FK qua số giấy tờ / plan_id
INSERT INTO `alerts`
(alert_id, alert_type, vehicle_id, document_id, plan_id, target_role_id, title, message, due_date, due_odometer, severity, alert_status)
SELECT 1, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Phí đường bộ quá hạn', 'Xe 51C-256.89 — phí đường bộ đã hết hạn, cần gia hạn.',
       DATE_SUB(CURDATE(), INTERVAL 2 DAY), NULL, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'PDB-51C25689'
WHERE v.license_plate = '51C-256.89'
UNION ALL
SELECT 2, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Đăng kiểm quá hạn', 'Xe 50H-112.35 — đăng kiểm đã quá hạn 12 ngày.',
       DATE_SUB(CURDATE(), INTERVAL 12 DAY), NULL, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'DK-50-03V-014'
WHERE v.license_plate = '50H-112.35'
UNION ALL
SELECT 3, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Đăng kiểm sắp hết hạn', 'Xe 51C-256.89 — đăng kiểm hết hạn trong 7 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 7 DAY), NULL, 'WARNING', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'DK-50-01S-001'
WHERE v.license_plate = '51C-256.89'
UNION ALL
SELECT 4, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 3,
       'Kiểm tra phanh quá hạn', 'Xe 50H-112.35 — kế hoạch kiểm tra phanh đã quá hạn theo ngày.',
       DATE_SUB(CURDATE(), INTERVAL 15 DAY), 73000, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 2
WHERE v.license_plate = '50H-112.35'
UNION ALL
SELECT 5, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Thay dầu sắp đến hạn', 'Xe 51B-345.67 — thay dầu còn khoảng 500 km (ODO hiện tại 96.500 km).',
       NULL, 97000, 'WARNING', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 8
WHERE v.license_plate = '51B-345.67'
UNION ALL
SELECT 6, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Đảo lốp sắp đến hạn', 'Xe 51F-888.66 — đảo lốp/cân bằng động đến hạn trong 10 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 10 DAY), 29000, 'WARNING', 'READ'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 5
WHERE v.license_plate = '51F-888.66'
UNION ALL
SELECT 7, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Đăng kiểm quá hạn', 'Xe 43C-111.22 — đăng kiểm quá hạn 8 ngày.',
       DATE_SUB(CURDATE(), INTERVAL 8 DAY), NULL, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'DK-43-01S-041'
WHERE v.license_plate = '43C-111.22'
UNION ALL
SELECT 8, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Phí đường bộ sắp hết hạn', 'Xe 51G-222.33 — phí đường bộ hết hạn trong 3 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 3 DAY), NULL, 'WARNING', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'PDB-51G22233'
WHERE v.license_plate = '51G-222.33'
UNION ALL
SELECT 9, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Bảo hiểm sắp hết hạn', 'Xe 50E-444.55 — bảo hiểm hết hạn trong 11 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 11 DAY), NULL, 'WARNING', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'BH-AAA-50E44455'
WHERE v.license_plate = '50E-444.55'
UNION ALL
SELECT 10, 'DOCUMENT_EXPIRY', v.vehicle_id, vd.document_id, NULL, 2,
       'Bảo hiểm quá hạn', 'Xe 72A-777.88 — bảo hiểm quá hạn 6 ngày.',
       DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN vehicle_documents vd ON vd.vehicle_id = v.vehicle_id AND vd.document_number = 'BH-LIB-72A77788'
WHERE v.license_plate = '72A-777.88'
UNION ALL
SELECT 11, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 3,
       'Thay dầu quá hạn', 'Xe 43C-111.22 — thay dầu quá hạn theo ODO (85.200 km).',
       NULL, 85000, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 10
WHERE v.license_plate = '43C-111.22'
UNION ALL
SELECT 12, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Kiểm tra phanh sắp đến hạn', 'Xe 43C-111.22 — kiểm tra phanh trong 5 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 5 DAY), 92000, 'WARNING', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 11
WHERE v.license_plate = '43C-111.22'
UNION ALL
SELECT 13, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Bảo dưỡng quá hạn', 'Xe 50E-444.55 — bảo dưỡng định kỳ quá hạn 10 ngày.',
       DATE_SUB(CURDATE(), INTERVAL 10 DAY), 118000, 'CRITICAL', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 14
WHERE v.license_plate = '50E-444.55'
UNION ALL
SELECT 14, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 3,
       'Thay dầu sắp đến hạn', 'Xe 50E-444.55 — thay dầu còn ~600 km.',
       NULL, 113000, 'WARNING', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 15
WHERE v.license_plate = '50E-444.55'
UNION ALL
SELECT 15, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Đảo lốp sắp đến hạn', 'Xe 72A-777.88 — đảo lốp trong 25 ngày.',
       DATE_ADD(CURDATE(), INTERVAL 25 DAY), 55000, 'WARNING', 'NEW'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 17
WHERE v.license_plate = '72A-777.88'
UNION ALL
SELECT 16, 'MAINTENANCE_DUE', v.vehicle_id, NULL, mp.plan_id, 2,
       'Bảo dưỡng quá hạn', 'Xe 51LD-123.45 — kế hoạch bảo dưỡng quá hạn (xe tạm ngưng).',
       DATE_SUB(CURDATE(), INTERVAL 30 DAY), 68800, 'CRITICAL', 'RESOLVED'
FROM vehicles v
JOIN maintenance_plans mp ON mp.vehicle_id = v.vehicle_id AND mp.plan_id = 6
WHERE v.license_plate = '51LD-123.45';

ALTER TABLE `alerts` AUTO_INCREMENT = 17;

-- =====================================================================
-- Kết thúc — sau khi nạp, kiểm tra nhanh:
--   SELECT due_status, COUNT(*) FROM vw_due_maintenance_plans GROUP BY due_status;
--   SELECT period_ym, SUM(maintenance_cost) FROM vw_vehicle_cost_monthly GROUP BY period_ym;
--   SELECT record_status, COUNT(*) FROM maintenance_records GROUP BY record_status;
--   SELECT COUNT(*) AS vehicles FROM vehicles;
--   SELECT COUNT(*) AS records FROM maintenance_records;
--   SELECT technician_id, COUNT(*) FROM maintenance_records WHERE vehicle_id = 3 GROUP BY technician_id;
-- Tổng sau khi nạp: 10 xe | 7 tài khoản (4 KTV) | 20 kế hoạch | 56 phiếu | 16 cảnh báo
-- Xe 51B-345.67: 20 phiếu (5 cũ + 15 mới) — lịch sử dày nhất trong demo
-- =====================================================================
