-- Realistic seed data cho module phương tiện & giấy tờ pháp lý (Lê Tiến Hưng)
-- Chạy sau data/Dump20260524.sql và data/seed-auth.sql.
-- Dùng CURDATE() để cảnh báo giấy tờ luôn có dữ liệu OVERDUE/COMING_DUE khi demo.

SET NAMES utf8mb4;
USE vehicle_maintenance_management;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM alerts;
DELETE FROM maintenance_record_items;
DELETE FROM maintenance_records;
DELETE FROM maintenance_plans;
DELETE FROM vehicle_documents;
DELETE FROM vehicles;
SET FOREIGN_KEY_CHECKS = 1;

-- 2. Hồ sơ phương tiện thực tế hơn: biển số theo định dạng Việt Nam, hãng/dòng xe rõ ràng.
INSERT INTO vehicles
(vehicle_code, license_plate, vehicle_type, brand, model, manufacture_year,
 purchase_date, chassis_number, engine_number, color, current_odometer,
 vehicle_status, notes, created_by, updated_by)
VALUES
('VH-FC-001', '51C-256.89',  'Xe tải',       'Isuzu',   'QKR 270',       2022, '2023-02-10', 'RL4QKR77NN100001', '4JH1E500001', 'Trắng', 48200, 'ACTIVE',            'Xe tải giao hàng nội thành TP.HCM', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
('VH-FC-002', '50H-112.35',  'Xe tải',       'Hyundai', 'Mighty EX8',    2021, '2022-06-18', 'KMFWBX7KCMU00002', 'D4CCM200002', 'Xanh',  73000, 'UNDER_MAINTENANCE','Đang sửa hệ thống phanh tại xưởng', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
('VH-FC-003', '51B-345.67',  'Xe khách',     'Thaco',   'County TB85S',  2020, '2021-11-05', 'RN2TB85S0L000003', 'D4DBL300003', 'Vàng',  96500, 'ACTIVE',            'Xe đưa rước nhân viên tuyến Bình Dương', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
('VH-FC-004', '60C-990.12',  'Xe bán tải',   'Ford',    'Ranger XLS',    2023, '2024-01-22', 'MPBUMFF60PX00004', 'P4ATPX00004',  'Xám',   31500, 'ACTIVE',            'Xe hỗ trợ kỹ thuật hiện trường', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
('VH-FC-005', '51F-888.66',  'Xe con',       'Toyota',  'Vios G',        2022, '2022-09-12', 'RL4VIO22NN000005', '2NRFE500005', 'Đen',   27800, 'ACTIVE',            'Xe điều hành văn phòng', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
('VH-FC-006', '51LD-123.45', 'Xe chuyên dụng','Hino',    'XZU650L',       2021, '2021-08-30', 'JHHDXZU65M000006', 'N04CM600006', 'Trắng', 68800, 'INACTIVE',          'Xe nâng thùng tạm ngưng khai thác để kiểm tra tổng quát', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1));

-- 3. Giấy tờ pháp lý: có đủ OVERDUE, COMING_DUE trong 15 ngày và NORMAL.
INSERT INTO vehicle_documents
(vehicle_id, document_type_id, document_number, issuer_name,
 issue_date, effective_date, expiry_date, fee_amount, paid_date,
 document_status, is_current, note, created_by, updated_by)
VALUES
-- 51C-256.89
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 1, 'DK-50-01S-240612-001', 'Trung tâm Đăng kiểm 50-01S', DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_SUB(CURDATE(), INTERVAL 173 DAY), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 173 DAY), 'VALID', 1, 'Đăng kiểm sắp hết hạn trong 7 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 2, 'BH-BVI-51C25689-2026', 'Bảo Việt Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_SUB(CURDATE(), INTERVAL 245 DAY), DATE_ADD(CURDATE(), INTERVAL 120 DAY), 1580000, DATE_SUB(CURDATE(), INTERVAL 245 DAY), 'VALID', 1, 'Bảo hiểm TNDS còn hiệu lực', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 3, 'PDB-51C25689-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 367 DAY), DATE_SUB(CURDATE(), INTERVAL 2 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 367 DAY), 'EXPIRED', 1, 'Phí đường bộ đã quá hạn 2 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
-- 50H-112.35
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 1, 'DK-50-03V-231201-014', 'Trung tâm Đăng kiểm 50-03V', DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 194 DAY), DATE_SUB(CURDATE(), INTERVAL 12 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 194 DAY), 'EXPIRED', 1, 'Đăng kiểm đã quá hạn, cần xử lý trước khi khai thác', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 2, 'BH-PVI-50H11235-2026', 'PVI Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_SUB(CURDATE(), INTERVAL 351 DAY), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 1760000, DATE_SUB(CURDATE(), INTERVAL 351 DAY), 'VALID', 1, 'Bảo hiểm sắp hết hạn trong 14 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 3, 'PDB-50H11235-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_SUB(CURDATE(), INTERVAL 125 DAY), DATE_ADD(CURDATE(), INTERVAL 240 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 125 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
-- 51B-345.67
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 1, 'DK-50-02S-260301-008', 'Trung tâm Đăng kiểm 50-02S', DATE_SUB(CURDATE(), INTERVAL 95 DAY), DATE_SUB(CURDATE(), INTERVAL 95 DAY), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 370000, DATE_SUB(CURDATE(), INTERVAL 95 DAY), 'VALID', 1, 'Đăng kiểm còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 2, 'BH-PTI-51B34567-2026', 'PTI Hồ Chí Minh', DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 369 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY), 2450000, DATE_SUB(CURDATE(), INTERVAL 369 DAY), 'EXPIRED', 1, 'Bảo hiểm đã quá hạn 4 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 3, 'PDB-51B34567-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_SUB(CURDATE(), INTERVAL 360 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 3240000, DATE_SUB(CURDATE(), INTERVAL 360 DAY), 'VALID', 1, 'Phí đường bộ sắp hết hạn trong 5 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
-- 60C-990.12
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 1, 'DK-60-01S-260501-022', 'Trung tâm Đăng kiểm 60-01S', DATE_SUB(CURDATE(), INTERVAL 42 DAY), DATE_SUB(CURDATE(), INTERVAL 42 DAY), DATE_ADD(CURDATE(), INTERVAL 140 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 42 DAY), 'VALID', 1, 'Đăng kiểm còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 2, 'BH-MIC-60C99012-2026', 'MIC Đồng Nai', DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_SUB(CURDATE(), INTERVAL 180 DAY), DATE_ADD(CURDATE(), INTERVAL 185 DAY), 1320000, DATE_SUB(CURDATE(), INTERVAL 180 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 3, 'PDB-60C99012-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_ADD(CURDATE(), INTERVAL 305 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
-- 51F-888.66
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 1, 'DK-50-05V-260612-017', 'Trung tâm Đăng kiểm 50-05V', DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_SUB(CURDATE(), INTERVAL 170 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 340000, DATE_SUB(CURDATE(), INTERVAL 170 DAY), 'VALID', 1, 'Đăng kiểm sắp hết hạn trong 12 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 2, 'BH-BSH-51F88866-2026', 'BSH Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 335 DAY), 980000, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 3, 'PDB-51F88866-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 370 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1560000, DATE_SUB(CURDATE(), INTERVAL 370 DAY), 'EXPIRED', 1, 'Phí đường bộ quá hạn 1 ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
-- 51LD-123.45
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 1, 'DK-50-04D-250101-033', 'Trung tâm Đăng kiểm 50-04D', DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 530 DAY), DATE_SUB(CURDATE(), INTERVAL 165 DAY), 370000, DATE_SUB(CURDATE(), INTERVAL 530 DAY), 'EXPIRED', 1, 'Xe tạm ngưng khai thác, cần kiểm định lại trước khi hoạt động', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 2, 'BH-PJICO-51LD12345-2026', 'PJICO Sài Gòn', DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_ADD(CURDATE(), INTERVAL 205 DAY), 1850000, DATE_SUB(CURDATE(), INTERVAL 160 DAY), 'VALID', 1, 'Bảo hiểm còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 3, 'PDB-51LD12345-2026', 'Cục Đường bộ Việt Nam', DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_SUB(CURDATE(), INTERVAL 80 DAY), DATE_ADD(CURDATE(), INTERVAL 285 DAY), 2160000, DATE_SUB(CURDATE(), INTERVAL 80 DAY), 'VALID', 1, 'Phí đường bộ còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1));
