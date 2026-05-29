-- Dữ liệu mẫu kế hoạch bảo dưỡng
-- Chạy sau khi đã import Dump20260524.sql + seed-auth.sql + data-vehicles.sql
-- Tạo 5 kế hoạch với trạng thái OVERDUE, COMING_DUE, NORMAL để demo

USE vehicle_maintenance_management;

-- ─── Kế hoạch 1: 51A-12345 + Thay dầu → OVERDUE (quá hạn theo ngày và ODO) ───
INSERT INTO maintenance_plans (
    vehicle_id, maintenance_type_id,
    interval_days, interval_km,
    last_service_date, last_service_odometer,
    next_due_date, next_due_odometer,
    is_active, notes
)
SELECT v.vehicle_id, 2,
       180, 5000,
       '2025-11-11', 30000,
       '2026-05-10', 35000,
       1, 'Thay dầu định kỳ — đã quá hạn'
FROM vehicles v WHERE v.license_plate = '51A-12345';

-- ─── Kế hoạch 2: 51A-12345 + Kiểm tra phanh → COMING_DUE (sắp đến hạn theo ngày) ───
INSERT INTO maintenance_plans (
    vehicle_id, maintenance_type_id,
    interval_days,
    last_service_date,
    next_due_date, next_due_odometer,
    is_active, notes
)
SELECT v.vehicle_id, 3,
       180,
       '2025-12-02',
       '2026-06-01', 45000,
       1, 'Kiểm tra phanh — sắp đến hạn (còn 3 ngày)'
FROM vehicles v WHERE v.license_plate = '51A-12345';

-- ─── Kế hoạch 3: 51B-67890 + Bảo dưỡng định kỳ → OVERDUE (quá hạn theo ngày) ───
INSERT INTO maintenance_plans (
    vehicle_id, maintenance_type_id,
    interval_days, interval_km,
    last_service_date, last_service_odometer,
    next_due_date, next_due_odometer,
    is_active, notes
)
SELECT v.vehicle_id, 1,
       90, 10000,
       '2026-01-15', 56000,
       '2026-04-15', 67000,
       1, 'Bảo dưỡng định kỳ — quá hạn 44 ngày'
FROM vehicles v WHERE v.license_plate = '51B-67890';

-- ─── Kế hoạch 4: 51B-67890 + Lốp → COMING_DUE (sắp đến hạn theo ODO) ───
INSERT INTO maintenance_plans (
    vehicle_id, maintenance_type_id,
    interval_km,
    last_service_odometer,
    next_due_date, next_due_odometer,
    is_active, notes
)
SELECT v.vehicle_id, 4,
       20000,
       42000,
       '2026-10-20', 62300,
       1, 'Thay lốp — sắp đến hạn theo ODO (còn 300 km)'
FROM vehicles v WHERE v.license_plate = '51B-67890';

-- ─── Kế hoạch 5: 51C-24680 + Bảo dưỡng định kỳ → NORMAL (chưa đến hạn) ───
INSERT INTO maintenance_plans (
    vehicle_id, maintenance_type_id,
    interval_days, interval_km,
    last_service_date, last_service_odometer,
    next_due_date, next_due_odometer,
    is_active, notes
)
SELECT v.vehicle_id, 1,
       180, 5000,
       '2026-03-01', 23000,
       '2026-08-28', 33000,
       1, 'Bảo dưỡng định kỳ — còn 91 ngày và 5000 km'
FROM vehicles v WHERE v.license_plate = '51C-24680';
