-- =====================================================================
-- FleetCare - Quản lý hồ sơ & Bảo dưỡng phương tiện
-- FILE SCHEMA: chỉ chứa cấu trúc CSDL (bảng + view), KHÔNG có dữ liệu.
-- Dữ liệu mẫu nằm ở file data/data.sql (chạy sau file này).
--
-- Cách chạy — DÙNG redirection để giữ đúng UTF-8 tiếng Việt:
--   cmd (Command Prompt):
--     mysql -u root -p --default-character-set=utf8mb4 < data\schema.sql
--     mysql -u root -p --default-character-set=utf8mb4 < data\data.sql
--   PowerShell:
--     cmd /c "mysql -u root -p123456 --default-character-set=utf8mb4 < data\schema.sql"
--     cmd /c "mysql -u root -p123456 --default-character-set=utf8mb4 < data\data.sql"
--   LƯU Ý: KHÔNG dùng "Get-Content file.sql | mysql" — PowerShell sẽ làm hỏng
--   ký tự tiếng Việt (lưu thành dấu ?). Hoặc mở bằng MySQL Workbench rồi Run.
-- =====================================================================

DROP DATABASE IF EXISTS `vehicle_maintenance_management`;
CREATE DATABASE `vehicle_maintenance_management`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE `vehicle_maintenance_management`;

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- Vai trò & phân quyền
-- ---------------------------------------------------------------------
CREATE TABLE `roles` (
  `role_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `role_code` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uq_roles_role_code` (`role_code`),
  UNIQUE KEY `uq_roles_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `permissions` (
  `permission_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `permission_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `module_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uq_permissions_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `role_permissions` (
  `role_id` bigint unsigned NOT NULL,
  `permission_id` bigint unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permissions_permission` (`permission_id`),
  CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Người dùng
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
  `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role_id` bigint unsigned NOT NULL,
  `account_status` enum('ACTIVE','LOCKED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `must_change_password` tinyint(1) NOT NULL DEFAULT '0',
  `last_login_at` datetime DEFAULT NULL,
  `created_by` bigint unsigned DEFAULT NULL,
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uq_users_username` (`username`),
  UNIQUE KEY `uq_users_email` (`email`),
  KEY `fk_users_role` (`role_id`),
  KEY `fk_users_created_by` (`created_by`),
  KEY `fk_users_updated_by` (`updated_by`),
  CONSTRAINT `fk_users_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_users_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `fk_users_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Phương tiện & giấy tờ
-- ---------------------------------------------------------------------
CREATE TABLE `vehicles` (
  `vehicle_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `vehicle_code` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `license_plate` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vehicle_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `brand` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `manufacture_year` smallint unsigned DEFAULT NULL,
  `purchase_date` date DEFAULT NULL,
  `chassis_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `engine_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `color` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `current_odometer` int unsigned NOT NULL DEFAULT '0',
  `vehicle_status` enum('ACTIVE','UNDER_MAINTENANCE','INACTIVE','DISPOSED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint unsigned DEFAULT NULL,
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`vehicle_id`),
  UNIQUE KEY `uq_vehicles_license_plate` (`license_plate`),
  UNIQUE KEY `uq_vehicles_chassis_number` (`chassis_number`),
  UNIQUE KEY `uq_vehicles_engine_number` (`engine_number`),
  UNIQUE KEY `uq_vehicles_vehicle_code` (`vehicle_code`),
  KEY `fk_vehicles_created_by` (`created_by`),
  KEY `fk_vehicles_updated_by` (`updated_by`),
  KEY `idx_vehicles_status` (`vehicle_status`),
  KEY `idx_vehicles_type` (`vehicle_type`),
  KEY `idx_vehicles_current_odometer` (`current_odometer`),
  CONSTRAINT `fk_vehicles_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicles_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `document_types` (
  `document_type_id` tinyint unsigned NOT NULL AUTO_INCREMENT,
  `document_type_code` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `document_type_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `default_alert_days` smallint unsigned NOT NULL DEFAULT '15',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`document_type_id`),
  UNIQUE KEY `uq_document_types_code` (`document_type_code`),
  UNIQUE KEY `uq_document_types_name` (`document_type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `vehicle_documents` (
  `document_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint unsigned NOT NULL,
  `document_type_id` tinyint unsigned NOT NULL,
  `document_number` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issuer_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `issue_date` date DEFAULT NULL,
  `effective_date` date DEFAULT NULL,
  `expiry_date` date NOT NULL,
  `fee_amount` decimal(14,2) NOT NULL DEFAULT '0.00',
  `paid_date` date DEFAULT NULL,
  `document_status` enum('VALID','EXPIRED','REPLACED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VALID',
  `is_current` tinyint(1) NOT NULL DEFAULT '1',
  `current_token` tinyint GENERATED ALWAYS AS ((case when (`is_current` = 1) then 1 else NULL end)) STORED,
  `note` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint unsigned DEFAULT NULL,
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`document_id`),
  UNIQUE KEY `uq_vehicle_document_current` (`vehicle_id`,`document_type_id`,`current_token`),
  KEY `fk_vehicle_documents_type` (`document_type_id`),
  KEY `fk_vehicle_documents_created_by` (`created_by`),
  KEY `fk_vehicle_documents_updated_by` (`updated_by`),
  KEY `idx_vehicle_documents_vehicle_expiry` (`vehicle_id`,`expiry_date`),
  KEY `idx_vehicle_documents_expiry` (`expiry_date`),
  KEY `idx_vehicle_documents_status` (`document_status`),
  CONSTRAINT `fk_vehicle_documents_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_documents_type` FOREIGN KEY (`document_type_id`) REFERENCES `document_types` (`document_type_id`),
  CONSTRAINT `fk_vehicle_documents_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_vehicle_documents_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_vehicle_documents_date_range` CHECK ((((`effective_date` is null) or (`expiry_date` >= `effective_date`)) and ((`issue_date` is null) or (`effective_date` is null) or (`effective_date` >= `issue_date`))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Bảo dưỡng
-- ---------------------------------------------------------------------
CREATE TABLE `maintenance_types` (
  `maintenance_type_id` smallint unsigned NOT NULL AUTO_INCREMENT,
  `maintenance_code` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `maintenance_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `default_interval_days` int unsigned DEFAULT NULL,
  `default_interval_km` int unsigned DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`maintenance_type_id`),
  UNIQUE KEY `uq_maintenance_types_code` (`maintenance_code`),
  UNIQUE KEY `uq_maintenance_types_name` (`maintenance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `maintenance_plans` (
  `plan_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint unsigned NOT NULL,
  `maintenance_type_id` smallint unsigned NOT NULL,
  `interval_days` int unsigned DEFAULT NULL,
  `interval_km` int unsigned DEFAULT NULL,
  `last_service_date` date DEFAULT NULL,
  `last_service_odometer` int unsigned DEFAULT NULL,
  `next_due_date` date DEFAULT NULL,
  `next_due_odometer` int unsigned DEFAULT NULL,
  `alert_before_days` smallint unsigned DEFAULT NULL,
  `alert_before_km` int unsigned DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_token` tinyint GENERATED ALWAYS AS ((case when (`is_active` = 1) then 1 else NULL end)) STORED,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint unsigned DEFAULT NULL,
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`plan_id`),
  UNIQUE KEY `uq_maintenance_plan_active` (`vehicle_id`,`maintenance_type_id`,`active_token`),
  KEY `fk_maintenance_plans_type` (`maintenance_type_id`),
  KEY `fk_maintenance_plans_created_by` (`created_by`),
  KEY `fk_maintenance_plans_updated_by` (`updated_by`),
  KEY `idx_maintenance_plans_due_date` (`next_due_date`),
  KEY `idx_maintenance_plans_due_odometer` (`next_due_odometer`),
  CONSTRAINT `fk_maintenance_plans_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_plans_type` FOREIGN KEY (`maintenance_type_id`) REFERENCES `maintenance_types` (`maintenance_type_id`),
  CONSTRAINT `fk_maintenance_plans_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_plans_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_maintenance_plans_interval` CHECK (((`interval_days` is not null) or (`interval_km` is not null)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `maintenance_items` (
  `item_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `item_code` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `item_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `item_type` enum('WORK','PART') COLLATE utf8mb4_unicode_ci NOT NULL,
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'UNIT',
  `default_unit_cost` decimal(14,2) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `uq_maintenance_items_name_type` (`item_name`,`item_type`),
  UNIQUE KEY `uq_maintenance_items_code` (`item_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `maintenance_records` (
  `record_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `vehicle_id` bigint unsigned NOT NULL,
  `plan_id` bigint unsigned DEFAULT NULL,
  `record_type` enum('PREVENTIVE','CORRECTIVE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scheduled_date` date DEFAULT NULL,
  `service_date` date DEFAULT NULL,
  `started_at` datetime DEFAULT NULL,
  `completed_at` datetime DEFAULT NULL,
  `odometer` int unsigned DEFAULT NULL,
  `work_summary` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_provider_name` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `technician_id` bigint unsigned DEFAULT NULL,
  `total_cost` decimal(14,2) NOT NULL DEFAULT '0.00',
  `record_status` enum('OPEN','IN_PROGRESS','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OPEN',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_by` bigint unsigned DEFAULT NULL,
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`),
  KEY `fk_maintenance_records_plan` (`plan_id`),
  KEY `fk_maintenance_records_created_by` (`created_by`),
  KEY `fk_maintenance_records_updated_by` (`updated_by`),
  KEY `idx_maintenance_records_vehicle_date` (`vehicle_id`,`service_date`),
  KEY `idx_maintenance_records_status` (`record_status`),
  KEY `idx_maintenance_records_technician` (`technician_id`),
  KEY `idx_maintenance_records_record_type` (`record_type`),
  CONSTRAINT `fk_maintenance_records_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_records_plan` FOREIGN KEY (`plan_id`) REFERENCES `maintenance_plans` (`plan_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_records_technician` FOREIGN KEY (`technician_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_records_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_records_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `maintenance_record_items` (
  `record_item_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `record_id` bigint unsigned NOT NULL,
  `item_id` bigint unsigned DEFAULT NULL,
  `item_type` enum('WORK','PART') COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity` decimal(10,2) NOT NULL DEFAULT '1.00',
  `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit_cost` decimal(14,2) NOT NULL DEFAULT '0.00',
  `line_total` decimal(14,2) GENERATED ALWAYS AS (round((`quantity` * `unit_cost`),2)) STORED,
  `notes` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_item_id`),
  KEY `fk_maintenance_record_items_item` (`item_id`),
  KEY `idx_maintenance_record_items_record` (`record_id`),
  KEY `idx_maintenance_record_items_type` (`item_type`),
  CONSTRAINT `fk_maintenance_record_items_item` FOREIGN KEY (`item_id`) REFERENCES `maintenance_items` (`item_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_maintenance_record_items_record` FOREIGN KEY (`record_id`) REFERENCES `maintenance_records` (`record_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Cảnh báo & cấu hình
-- ---------------------------------------------------------------------
CREATE TABLE `alert_settings` (
  `setting_id` tinyint unsigned NOT NULL AUTO_INCREMENT,
  `document_alert_days` smallint unsigned NOT NULL DEFAULT '15',
  `maintenance_alert_days` smallint unsigned NOT NULL DEFAULT '7',
  `maintenance_alert_km` int unsigned NOT NULL DEFAULT '500',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `updated_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`setting_id`),
  KEY `fk_alert_settings_updated_by` (`updated_by`),
  CONSTRAINT `fk_alert_settings_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `alerts` (
  `alert_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `alert_type` enum('DOCUMENT_EXPIRY','MAINTENANCE_DUE') COLLATE utf8mb4_unicode_ci NOT NULL,
  `vehicle_id` bigint unsigned NOT NULL,
  `document_id` bigint unsigned DEFAULT NULL,
  `plan_id` bigint unsigned DEFAULT NULL,
  `target_role_id` bigint unsigned DEFAULT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `due_date` date DEFAULT NULL,
  `due_odometer` int unsigned DEFAULT NULL,
  `severity` enum('INFO','WARNING','CRITICAL') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'WARNING',
  `alert_status` enum('NEW','READ','RESOLVED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NEW',
  `resolved_at` datetime DEFAULT NULL,
  `resolved_by` bigint unsigned DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`alert_id`),
  KEY `fk_alerts_document` (`document_id`),
  KEY `fk_alerts_plan` (`plan_id`),
  KEY `fk_alerts_target_role` (`target_role_id`),
  KEY `fk_alerts_resolved_by` (`resolved_by`),
  KEY `idx_alerts_status_role` (`alert_status`,`target_role_id`),
  KEY `idx_alerts_vehicle` (`vehicle_id`),
  KEY `idx_alerts_due_date` (`due_date`),
  KEY `idx_alerts_created_at` (`created_at`),
  CONSTRAINT `fk_alerts_document` FOREIGN KEY (`document_id`) REFERENCES `vehicle_documents` (`document_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_alerts_plan` FOREIGN KEY (`plan_id`) REFERENCES `maintenance_plans` (`plan_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_alerts_resolved_by` FOREIGN KEY (`resolved_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_alerts_target_role` FOREIGN KEY (`target_role_id`) REFERENCES `roles` (`role_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_alerts_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicles` (`vehicle_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- VIEW nghiệp vụ
-- ---------------------------------------------------------------------

-- Xe đến hạn / sắp đến hạn bảo dưỡng (theo ngày hoặc ODO)
CREATE OR REPLACE VIEW `vw_due_maintenance_plans` AS
SELECT mp.plan_id,
       v.vehicle_id,
       v.license_plate,
       v.vehicle_type,
       mt.maintenance_name,
       v.current_odometer,
       mp.next_due_date,
       mp.next_due_odometer,
       COALESCE(mp.alert_before_days, aset.maintenance_alert_days) AS effective_alert_days,
       COALESCE(mp.alert_before_km,   aset.maintenance_alert_km)   AS effective_alert_km,
       CASE
         WHEN mp.next_due_date IS NOT NULL AND mp.next_due_date < CURDATE() THEN 'OVERDUE'
         WHEN mp.next_due_odometer IS NOT NULL AND v.current_odometer >= mp.next_due_odometer THEN 'OVERDUE'
         WHEN mp.next_due_date IS NOT NULL
              AND (TO_DAYS(mp.next_due_date) - TO_DAYS(CURDATE())) <= COALESCE(mp.alert_before_days, aset.maintenance_alert_days) THEN 'COMING_DUE'
         WHEN mp.next_due_odometer IS NOT NULL
              AND (mp.next_due_odometer - v.current_odometer) <= COALESCE(mp.alert_before_km, aset.maintenance_alert_km) THEN 'COMING_DUE'
         ELSE 'NORMAL'
       END AS due_status
FROM maintenance_plans mp
JOIN vehicles v          ON v.vehicle_id = mp.vehicle_id
JOIN maintenance_types mt ON mt.maintenance_type_id = mp.maintenance_type_id
JOIN alert_settings aset  ON aset.setting_id = 1
WHERE mp.is_active = 1;

-- Giấy tờ xe đến hạn / sắp đến hạn
CREATE OR REPLACE VIEW `vw_due_vehicle_documents` AS
SELECT vd.document_id,
       v.vehicle_id,
       v.license_plate,
       v.vehicle_type,
       dt.document_type_name,
       vd.document_number,
       vd.issuer_name,
       vd.expiry_date,
       (TO_DAYS(vd.expiry_date) - TO_DAYS(CURDATE())) AS days_to_expiry,
       CASE
         WHEN vd.expiry_date < CURDATE() THEN 'OVERDUE'
         WHEN (TO_DAYS(vd.expiry_date) - TO_DAYS(CURDATE())) <= dt.default_alert_days THEN 'COMING_DUE'
         ELSE 'NORMAL'
       END AS due_status
FROM vehicle_documents vd
JOIN vehicles v       ON v.vehicle_id = vd.vehicle_id
JOIN document_types dt ON dt.document_type_id = vd.document_type_id
WHERE vd.is_current = 1
  AND vd.document_status IN ('VALID','EXPIRED');

-- Chi phí theo xe theo tháng (phiếu bảo dưỡng COMPLETED + chi phí giấy tờ)
CREATE OR REPLACE VIEW `vw_vehicle_cost_monthly` AS
SELECT x.vehicle_id,
       x.license_plate,
       x.period_ym,
       SUM(x.maintenance_cost) AS maintenance_cost,
       SUM(x.document_cost)    AS document_cost,
       SUM(x.maintenance_cost + x.document_cost) AS total_cost
FROM (
    SELECT v.vehicle_id,
           v.license_plate,
           DATE_FORMAT(mr.service_date, '%Y-%m') AS period_ym,
           mr.total_cost AS maintenance_cost,
           0.00 AS document_cost
    FROM maintenance_records mr
    JOIN vehicles v ON v.vehicle_id = mr.vehicle_id
    WHERE mr.record_status = 'COMPLETED' AND mr.service_date IS NOT NULL
    UNION ALL
    SELECT v.vehicle_id,
           v.license_plate,
           DATE_FORMAT(COALESCE(vd.paid_date, vd.issue_date, vd.expiry_date), '%Y-%m') AS period_ym,
           0.00 AS maintenance_cost,
           vd.fee_amount AS document_cost
    FROM vehicle_documents vd
    JOIN vehicles v ON v.vehicle_id = vd.vehicle_id
) x
GROUP BY x.vehicle_id, x.license_plate, x.period_ym;
