CREATE TABLE vehicle_documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    issue_date DATE,
    expiry_date DATE NOT NULL,
    note TEXT,

    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- INSERT INTO vehicle_documents
-- (vehicle_id, document_type, issue_date, expiry_date, note)
-- VALUES
-- (1, 'Đăng kiểm', '2025-12-01', '2026-06-01', 'Đăng kiểm định kỳ'),
-- (1, 'Bảo hiểm', '2025-06-01', '2026-06-05', 'Bảo hiểm TNDS'),
-- (2, 'Phí đường bộ', '2025-08-01', '2026-06-10', 'Phí đường bộ năm');