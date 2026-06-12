-- Realistic seed data cho kế hoạch bảo dưỡng
-- Chạy sau data/Dump20260524.sql + data/seed-auth.sql + data/data-vehicles.sql.

SET NAMES utf8mb4;
USE vehicle_maintenance_management;

SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM alerts;
DELETE FROM maintenance_record_items;
DELETE FROM maintenance_records;
DELETE FROM maintenance_plans;
SET FOREIGN_KEY_CHECKS = 1;

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
