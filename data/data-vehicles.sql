SET NAMES utf8mb4;
USE vehicle_maintenance_management;

INSERT INTO vehicles
(vehicle_code, license_plate, vehicle_type, brand, model, manufacture_year,
 purchase_date, chassis_number, engine_number, color, current_odometer,
 vehicle_status, notes)
VALUES
('VH001', '51A-12345', 'Xe tai', 'Hyundai', 'Mighty', 2020,
 '2022-01-15', 'CHS001', 'ENG001', 'Trang', 35000,
 'ACTIVE', 'Xe tai giao hang'),

('VH002', '51B-67890', 'Xe khach', 'Thaco', 'County', 2019,
 '2021-05-20', 'CHS002', 'ENG002', 'Xanh', 62000,
 'ACTIVE', 'Xe khach tuyen noi bo'),

('VH003', '51C-24680', 'Xe ban tai', 'Ford', 'Ranger', 2021,
 '2023-03-10', 'CHS003', 'ENG003', 'Den', 28000,
 'UNDER_MAINTENANCE', 'Dang kiem tra dinh ky')
ON DUPLICATE KEY UPDATE
 vehicle_type = VALUES(vehicle_type),
 brand = VALUES(brand),
 model = VALUES(model),
 manufacture_year = VALUES(manufacture_year),
 purchase_date = VALUES(purchase_date),
 color = VALUES(color),
 current_odometer = VALUES(current_odometer),
 vehicle_status = VALUES(vehicle_status),
 notes = VALUES(notes);

INSERT INTO vehicle_documents
(vehicle_id, document_type_id, document_number, issuer_name,
 issue_date, effective_date, expiry_date, fee_amount, paid_date,
 document_status, is_current, note)
VALUES
((SELECT vehicle_id FROM vehicles WHERE license_plate = '51A-12345'), 1, 'DK-001', 'Trung tam dang kiem 50-01S',
 '2025-12-01', '2025-12-01', '2026-06-01', 340000, '2025-12-01',
 'VALID', 1, 'Dang kiem xe 51A-12345'),

((SELECT vehicle_id FROM vehicles WHERE license_plate = '51A-12345'), 2, 'BH-001', 'Bao hiem Bao Viet',
 '2025-06-01', '2025-06-01', '2026-06-05', 1200000, '2025-06-01',
 'VALID', 1, 'Bao hiem trach nhiem dan su'),

((SELECT vehicle_id FROM vehicles WHERE license_plate = '51B-67890'), 3, 'PDB-001', 'Cuc duong bo',
 '2025-08-01', '2025-08-01', '2026-05-20', 2160000, '2025-08-01',
 'EXPIRED', 1, 'Phi duong bo da het han'),

((SELECT vehicle_id FROM vehicles WHERE license_plate = '51C-24680'), 1, 'DK-003', 'Trung tam dang kiem 50-02S',
 '2026-01-01', '2026-01-01', '2026-07-01', 340000, '2026-01-01',
 'VALID', 1, 'Con han')
ON DUPLICATE KEY UPDATE
 document_number = VALUES(document_number),
 issuer_name = VALUES(issuer_name),
 issue_date = VALUES(issue_date),
 effective_date = VALUES(effective_date),
 expiry_date = VALUES(expiry_date),
 fee_amount = VALUES(fee_amount),
 paid_date = VALUES(paid_date),
 document_status = VALUES(document_status),
 note = VALUES(note);
