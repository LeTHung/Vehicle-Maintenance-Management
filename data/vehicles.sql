CREATE TABLE vehicles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type VARCHAR(50) NOT NULL,
    chassis_number VARCHAR(50),
    engine_number VARCHAR(50),
    manufacture_year INT,
    purchase_date DATE,
    status VARCHAR(30),
    current_odo INT DEFAULT 0
);

-- INSERT INTO vehicles 
-- (license_plate, vehicle_type, chassis_number, engine_number, manufacture_year, purchase_date, status, current_odo)
-- VALUES
-- ('51A-12345', 'Xe tải', 'CHS001', 'ENG001', 2020, '2022-01-15', 'Đang hoạt động', 35000),
-- ('51B-67890', 'Xe khách', 'CHS002', 'ENG002', 2019, '2021-05-20', 'Đang hoạt động', 60000);