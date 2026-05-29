-- Seed data cho module bảo dưỡng (Nguyễn Quang Huy)
-- Chạy SAU khi đã import Dump20260524.sql và seed-auth.sql

USE vehicle_maintenance_management;

-- ============================================================
-- 1. maintenance_items — Danh mục hạng mục công việc & phụ tùng
-- ============================================================
INSERT INTO maintenance_items (item_code, item_name, item_type, unit, default_unit_cost, notes) VALUES
-- Công việc bảo dưỡng (WORK)
('W001', 'Thay dầu động cơ',          'WORK', 'Lần',  150000, NULL),
('W002', 'Thay lọc dầu',              'WORK', 'Lần',   50000, NULL),
('W003', 'Thay lọc không khí',        'WORK', 'Lần',   40000, NULL),
('W004', 'Kiểm tra phanh',            'WORK', 'Lần',   80000, NULL),
('W005', 'Thay má phanh trước',       'WORK', 'Lần',  200000, NULL),
('W006', 'Thay má phanh sau',         'WORK', 'Lần',  180000, NULL),
('W007', 'Đảo lốp',                   'WORK', 'Lần',   60000, NULL),
('W008', 'Cân bằng động lốp',        'WORK', 'Bánh',  30000, NULL),
('W009', 'Kiểm tra hệ thống làm mát', 'WORK', 'Lần',   50000, NULL),
('W010', 'Vệ sinh buồng đốt',         'WORK', 'Lần',  120000, NULL),
('W011', 'Kiểm tra ắc quy',           'WORK', 'Lần',   30000, NULL),
('W012', 'Thay bugi',                 'WORK', 'Bộ',   100000, NULL),

-- Phụ tùng (PART)
('P001', 'Dầu động cơ 10W-40 (4L)',   'PART', 'Can',  350000, NULL),
('P002', 'Lọc dầu',                   'PART', 'Cái',   45000, NULL),
('P003', 'Lọc không khí',             'PART', 'Cái',   80000, NULL),
('P004', 'Má phanh trước (bộ)',       'PART', 'Bộ',   350000, NULL),
('P005', 'Má phanh sau (bộ)',         'PART', 'Bộ',   280000, NULL),
('P006', 'Bugi (bộ 4 cái)',           'PART', 'Bộ',   200000, NULL),
('P007', 'Nước làm mát',              'PART', 'Lít',   25000, NULL),
('P008', 'Dầu phanh',                 'PART', 'Lít',   60000, NULL),
('P009', 'Đai truyền động (dây cu-roa)', 'PART', 'Cái', 250000, NULL),
('P010', 'Ắc quy 12V 45Ah',          'PART', 'Cái', 1200000, NULL);
