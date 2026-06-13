-- FleetCare realistic demo seed data
-- Chạy sau khi import data/Dump20260524.sql và data/seed-auth.sql.
-- File này thay dữ liệu mẫu ít/ảo bằng bộ dữ liệu demo thực tế hơn.




SET NAMES utf8mb4;
USE vehicle_maintenance_management;

SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;
-- DELETE FROM alerts;
-- DELETE FROM maintenance_record_items;
-- DELETE FROM maintenance_records;
-- DELETE FROM maintenance_plans;
-- DELETE FROM vehicle_documents;
-- DELETE FROM vehicles;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Danh mục hạng mục bảo dưỡng/phụ tùng dùng cho phiếu sửa chữa.
INSERT INTO maintenance_items (item_code, item_name, item_type, unit, default_unit_cost, notes) VALUES
('W001', 'Thay dầu động cơ',              'WORK', 'Lần',  150000, 'Công thay dầu động cơ'),
('W002', 'Thay lọc dầu',                  'WORK', 'Lần',   50000, 'Công thay lọc dầu'),
('W003', 'Thay lọc gió động cơ',          'WORK', 'Lần',   60000, 'Công thay lọc gió'),
('W004', 'Kiểm tra hệ thống phanh',       'WORK', 'Lần',   90000, 'Kiểm tra má phanh, dầu phanh'),
('W005', 'Đảo lốp và cân bằng động',      'WORK', 'Lần',  180000, 'Đảo lốp, cân chỉnh bánh'),
('W006', 'Kiểm tra hệ thống làm mát',     'WORK', 'Lần',   80000, 'Kiểm tra nước làm mát, két nước'),
('P001', 'Dầu động cơ 10W-40 4L',         'PART', 'Can',  420000, 'Dầu động cơ xe tải nhẹ'),
('P002', 'Lọc dầu động cơ',               'PART', 'Cái',   85000, 'Phụ tùng thay định kỳ'),
('P003', 'Lọc gió động cơ',               'PART', 'Cái',  120000, 'Phụ tùng thay định kỳ'),
('P004', 'Má phanh trước',                'PART', 'Bộ',   520000, 'Má phanh trước theo bộ'),
('P005', 'Dầu phanh DOT4',                'PART', 'Chai',  95000, 'Dầu phanh'),
('P006', 'Nước làm mát động cơ',          'PART', 'Lít',   45000, 'Nước làm mát')
ON DUPLICATE KEY UPDATE
    item_name = VALUES(item_name),
    item_type = VALUES(item_type),
    unit = VALUES(unit),
    default_unit_cost = VALUES(default_unit_cost),
    notes = VALUES(notes),
    updated_at = NOW();

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

-- 4. Kế hoạch bảo dưỡng: có xe quá hạn, sắp đến hạn theo ngày và sắp đến hạn theo ODO.
INSERT INTO maintenance_plans
(vehicle_id, maintenance_type_id, interval_days, interval_km, last_service_date, last_service_odometer,
 next_due_date, next_due_odometer, alert_before_days, alert_before_km, is_active, notes, created_by, updated_by)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), 2, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 176 DAY), 43200, DATE_ADD(CURDATE(), INTERVAL 4 DAY), 48700, 15, 700, 1, 'Thay dầu sắp đến hạn theo ngày và ODO', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), 3, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 205 DAY), 63000, DATE_SUB(CURDATE(), INTERVAL 15 DAY), 73000, 15, 500, 1, 'Kiểm tra phanh đã quá hạn, xe đang ở xưởng', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), 1, 90, 10000, DATE_SUB(CURDATE(), INTERVAL 105 DAY), 86000, DATE_ADD(CURDATE(), INTERVAL 30 DAY), 96500, 15, 700, 1, 'Bảo dưỡng định kỳ đến hạn theo ODO', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='60C-990.12'), 5, 180, 10000, DATE_SUB(CURDATE(), INTERVAL 60 DAY), 26000, DATE_ADD(CURDATE(), INTERVAL 120 DAY), 36000, 15, 500, 1, 'Kiểm tra hệ thống làm mát còn hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), 4, 365, 20000, DATE_SUB(CURDATE(), INTERVAL 350 DAY), 9000, DATE_ADD(CURDATE(), INTERVAL 10 DAY), 29000, 15, 500, 1, 'Đảo lốp/cân bằng động sắp đến hạn theo ngày', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51LD-123.45'), 1, 180, 5000, DATE_SUB(CURDATE(), INTERVAL 210 DAY), 63800, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 68800, 15, 500, 1, 'Xe tạm ngưng nhưng vẫn có kế hoạch bảo dưỡng quá hạn', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1));

-- 5. Phiếu bảo dưỡng/sửa chữa: dùng cho dashboard kỹ thuật và báo cáo chi phí.
INSERT INTO maintenance_records
(vehicle_id, plan_id, record_type, title, scheduled_date, service_date, started_at, completed_at,
 odometer, work_summary, service_provider_name, technician_id, total_cost, record_status, notes,
 created_by, updated_by)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51C-256.89') AND maintenance_type_id=2 LIMIT 1), 'PREVENTIVE', 'Thay dầu định kỳ 48.000 km', DATE_SUB(CURDATE(), INTERVAL 32 DAY), DATE_SUB(CURDATE(), INTERVAL 31 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '08:30:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 31 DAY), '10:15:00'), 48050, 'Thay dầu động cơ, lọc dầu và kiểm tra tổng quát.', 'Xưởng FleetCare Quận 7', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), 705000, 'COMPLETED', 'Hoàn tất, xe vận hành ổn định', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), (SELECT user_id FROM users WHERE username='tech' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35') AND maintenance_type_id=3 LIMIT 1), 'CORRECTIVE', 'Sửa hệ thống phanh trước', CURDATE(), NULL, TIMESTAMP(CURDATE(), '09:00:00'), NULL, 73000, 'Kiểm tra phanh, thay má phanh trước và dầu phanh.', 'Xưởng FleetCare Bình Tân', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), 0, 'IN_PROGRESS', 'Đang chờ phụ tùng má phanh trước', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), (SELECT user_id FROM users WHERE username='tech' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='50H-112.35') AND maintenance_type_id=3 LIMIT 1), 'PREVENTIVE', 'Kiểm tra phanh sau sửa chữa', DATE_ADD(CURDATE(), INTERVAL 1 DAY), NULL, NULL, NULL, NULL, 'Kiểm tra lại hệ thống phanh sau khi hoàn tất sửa chữa.', 'Xưởng FleetCare Bình Tân', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), 0, 'OPEN', 'Phiếu chờ xử lý ngày mai', (SELECT user_id FROM users WHERE username='manager' LIMIT 1), (SELECT user_id FROM users WHERE username='manager' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51B-345.67') AND maintenance_type_id=1 LIMIT 1), 'PREVENTIVE', 'Bảo dưỡng định kỳ xe khách 95.000 km', DATE_SUB(CURDATE(), INTERVAL 60 DAY), DATE_SUB(CURDATE(), INTERVAL 59 DAY), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '08:00:00'), TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 59 DAY), '15:30:00'), 95000, 'Bảo dưỡng tổng quát, thay dầu, lọc gió, kiểm tra phanh.', 'Xưởng Thaco Bus Miền Nam', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), 2380000, 'COMPLETED', 'Hoàn tất bảo dưỡng định kỳ', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), (SELECT user_id FROM users WHERE username='tech' LIMIT 1)),
((SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66'), (SELECT plan_id FROM maintenance_plans WHERE vehicle_id=(SELECT vehicle_id FROM vehicles WHERE license_plate='51F-888.66') AND maintenance_type_id=4 LIMIT 1), 'PREVENTIVE', 'Đảo lốp và cân bằng động', CURDATE(), CURDATE(), TIMESTAMP(CURDATE(), '07:45:00'), TIMESTAMP(CURDATE(), '09:10:00'), 27800, 'Đảo lốp, cân bằng động, kiểm tra áp suất lốp.', 'Xưởng FleetCare Quận 7', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), 280000, 'COMPLETED', 'Hoàn tất trong ngày', (SELECT user_id FROM users WHERE username='tech' LIMIT 1), (SELECT user_id FROM users WHERE username='tech' LIMIT 1));

-- 6. Chi tiết phụ tùng/công việc cho phiếu đã hoàn thành và đang xử lý.
INSERT INTO maintenance_record_items
(record_id, item_id, item_type, description, quantity, unit, unit_cost, notes)
VALUES
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W001' LIMIT 1), 'WORK', 'Công thay dầu động cơ', 1, 'Lần', 150000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P001' LIMIT 1), 'PART', 'Dầu động cơ 10W-40 4L', 1, 'Can', 420000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Thay dầu định kỳ 48.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P002' LIMIT 1), 'PART', 'Lọc dầu động cơ', 1, 'Cái', 85000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Sửa hệ thống phanh trước' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W004' LIMIT 1), 'WORK', 'Kiểm tra hệ thống phanh', 1, 'Lần', 90000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Sửa hệ thống phanh trước' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P004' LIMIT 1), 'PART', 'Má phanh trước', 1, 'Bộ', 520000, 'Chờ nhập kho'),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W001' LIMIT 1), 'WORK', 'Công thay dầu', 1, 'Lần', 150000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W003' LIMIT 1), 'WORK', 'Công thay lọc gió', 1, 'Lần', 60000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P001' LIMIT 1), 'PART', 'Dầu động cơ 10W-40 4L', 4, 'Can', 420000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Bảo dưỡng định kỳ xe khách 95.000 km' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='P003' LIMIT 1), 'PART', 'Lọc gió động cơ', 2, 'Cái', 120000, NULL),
((SELECT record_id FROM maintenance_records WHERE title='Đảo lốp và cân bằng động' LIMIT 1), (SELECT item_id FROM maintenance_items WHERE item_code='W005' LIMIT 1), 'WORK', 'Đảo lốp và cân bằng động', 1, 'Lần', 180000, NULL);

-- 7. Kiểm tra nhanh số liệu sau khi seed.
SELECT 'vehicles' AS table_name, COUNT(*) AS total FROM vehicles
UNION ALL SELECT 'vehicle_documents', COUNT(*) FROM vehicle_documents
UNION ALL SELECT 'document_alerts', COUNT(*) FROM vw_due_vehicle_documents WHERE due_status IN ('OVERDUE', 'COMING_DUE')
UNION ALL SELECT 'maintenance_plans', COUNT(*) FROM maintenance_plans
UNION ALL SELECT 'maintenance_due_alerts', COUNT(*) FROM vw_due_maintenance_plans WHERE due_status IN ('OVERDUE', 'COMING_DUE')
UNION ALL SELECT 'maintenance_records', COUNT(*) FROM maintenance_records;
