-- Realistic seed data cho module bảo dưỡng (Nguyễn Quang Huy)
-- Chạy sau data/Dump20260524.sql + data/seed-auth.sql + data/data-vehicles.sql + data/seed-maintenance-plans.sql.

SET NAMES utf8mb4;
USE vehicle_maintenance_management;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM maintenance_record_items;
DELETE FROM maintenance_records;
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
