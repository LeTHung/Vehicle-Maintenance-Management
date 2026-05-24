CREATE DATABASE  IF NOT EXISTS `fleet_maintenance_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `fleet_maintenance_db`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: fleet_maintenance_db
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `alert_settings`
--

DROP TABLE IF EXISTS `alert_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alert_settings`
--

LOCK TABLES `alert_settings` WRITE;
/*!40000 ALTER TABLE `alert_settings` DISABLE KEYS */;
INSERT INTO `alert_settings` VALUES (1,15,7,500,1,NULL,'2026-04-12 16:30:50','2026-04-12 16:30:50');
/*!40000 ALTER TABLE `alert_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `alerts`
--

DROP TABLE IF EXISTS `alerts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `alerts`
--

LOCK TABLES `alerts` WRITE;
/*!40000 ALTER TABLE `alerts` DISABLE KEYS */;
/*!40000 ALTER TABLE `alerts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_types`
--

DROP TABLE IF EXISTS `document_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_types`
--

LOCK TABLES `document_types` WRITE;
/*!40000 ALTER TABLE `document_types` DISABLE KEYS */;
INSERT INTO `document_types` VALUES (1,'REGISTRATION_INSPECTION','Đăng kiểm',15,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(2,'INSURANCE','Bảo hiểm',15,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(3,'ROAD_FEE','Phí đường bộ',15,1,'2026-04-12 16:30:50','2026-04-12 16:30:50');
/*!40000 ALTER TABLE `document_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_items`
--

DROP TABLE IF EXISTS `maintenance_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_items`
--

LOCK TABLES `maintenance_items` WRITE;
/*!40000 ALTER TABLE `maintenance_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenance_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_plans`
--

DROP TABLE IF EXISTS `maintenance_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_plans`
--

LOCK TABLES `maintenance_plans` WRITE;
/*!40000 ALTER TABLE `maintenance_plans` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenance_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_record_items`
--

DROP TABLE IF EXISTS `maintenance_record_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_record_items`
--

LOCK TABLES `maintenance_record_items` WRITE;
/*!40000 ALTER TABLE `maintenance_record_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenance_record_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_records`
--

DROP TABLE IF EXISTS `maintenance_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_records`
--

LOCK TABLES `maintenance_records` WRITE;
/*!40000 ALTER TABLE `maintenance_records` DISABLE KEYS */;
/*!40000 ALTER TABLE `maintenance_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maintenance_types`
--

DROP TABLE IF EXISTS `maintenance_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maintenance_types`
--

LOCK TABLES `maintenance_types` WRITE;
/*!40000 ALTER TABLE `maintenance_types` DISABLE KEYS */;
INSERT INTO `maintenance_types` VALUES (1,'PERIODIC_SERVICE','Bảo dưỡng định kỳ','Bảo dưỡng tổng quát theo chu kỳ',180,5000,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(2,'OIL_CHANGE','Thay dầu','Thay dầu động cơ',180,5000,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(3,'BRAKE_CHECK','Kiểm tra phanh','Kiểm tra và bảo dưỡng hệ thống phanh',180,10000,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(4,'TIRE_SERVICE','Lốp','Đảo lốp / thay lốp / cân bằng lốp',365,20000,1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(5,'COOLING_SYSTEM','Hệ thống làm mát','Kiểm tra nước làm mát và két nước',180,10000,1,'2026-04-12 16:30:50','2026-04-12 16:30:50');
/*!40000 ALTER TABLE `maintenance_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'USER_MANAGE','Quản lý tài khoản','AUTH','Tạo/sửa/khóa tài khoản','2026-04-12 16:30:50','2026-04-12 16:30:50'),(2,'ROLE_ASSIGN','Phân quyền vai trò','AUTH','Gán vai trò cho người dùng','2026-04-12 16:30:50','2026-04-12 16:30:50'),(3,'SETTINGS_UPDATE','Cấu hình hệ thống','SYSTEM','Cập nhật cấu hình cảnh báo','2026-04-12 16:30:50','2026-04-12 16:30:50'),(4,'VEHICLE_VIEW','Xem phương tiện','VEHICLE','Xem danh sách và chi tiết phương tiện','2026-04-12 16:30:50','2026-04-12 16:30:50'),(5,'VEHICLE_CREATE','Thêm phương tiện','VEHICLE','Tạo hồ sơ phương tiện','2026-04-12 16:30:50','2026-04-12 16:30:50'),(6,'VEHICLE_UPDATE','Sửa phương tiện','VEHICLE','Cập nhật hồ sơ phương tiện','2026-04-12 16:30:50','2026-04-12 16:30:50'),(7,'DOCUMENT_VIEW','Xem giấy tờ','DOCUMENT','Xem giấy tờ pháp lý','2026-04-12 16:30:50','2026-04-12 16:30:50'),(8,'DOCUMENT_UPDATE','Cập nhật giấy tờ','DOCUMENT','Cập nhật đăng kiểm/bảo hiểm/phí đường bộ','2026-04-12 16:30:50','2026-04-12 16:30:50'),(9,'MAINTENANCE_PLAN_VIEW','Xem kế hoạch bảo dưỡng','PLAN','Xem kế hoạch bảo dưỡng','2026-04-12 16:30:50','2026-04-12 16:30:50'),(10,'MAINTENANCE_PLAN_CREATE','Lập kế hoạch bảo dưỡng','PLAN','Tạo kế hoạch bảo dưỡng','2026-04-12 16:30:50','2026-04-12 16:30:50'),(11,'MAINTENANCE_PLAN_UPDATE','Sửa kế hoạch bảo dưỡng','PLAN','Cập nhật kế hoạch bảo dưỡng','2026-04-12 16:30:50','2026-04-12 16:30:50'),(12,'MAINTENANCE_RECORD_VIEW','Xem hồ sơ bảo dưỡng','MAINTENANCE','Xem lịch sử bảo dưỡng/sửa chữa','2026-04-12 16:30:50','2026-04-12 16:30:50'),(13,'MAINTENANCE_RECORD_CREATE','Tạo phiếu bảo dưỡng','MAINTENANCE','Tạo phiếu bảo dưỡng/sửa chữa','2026-04-12 16:30:50','2026-04-12 16:30:50'),(14,'MAINTENANCE_RECORD_UPDATE','Cập nhật phiếu bảo dưỡng','MAINTENANCE','Cập nhật phụ tùng, chi phí, ODO','2026-04-12 16:30:50','2026-04-12 16:30:50'),(15,'ALERT_VIEW','Xem cảnh báo','ALERT','Xem các cảnh báo đến hạn','2026-04-12 16:30:50','2026-04-12 16:30:50'),(16,'COST_REPORT_VIEW','Xem báo cáo chi phí','REPORT','Xem báo cáo chi phí bảo dưỡng và giấy tờ','2026-04-12 16:30:50','2026-04-12 16:30:50');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` bigint unsigned NOT NULL,
  `permission_id` bigint unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permissions_permission` (`permission_id`),
  CONSTRAINT `fk_role_permissions_permission` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_permissions_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES (1,1,'2026-04-12 16:30:50'),(1,2,'2026-04-12 16:30:50'),(1,3,'2026-04-12 16:30:50'),(1,4,'2026-04-12 16:30:50'),(1,5,'2026-04-12 16:30:50'),(1,6,'2026-04-12 16:30:50'),(1,7,'2026-04-12 16:30:50'),(1,8,'2026-04-12 16:30:50'),(1,9,'2026-04-12 16:30:50'),(1,10,'2026-04-12 16:30:50'),(1,11,'2026-04-12 16:30:50'),(1,12,'2026-04-12 16:30:50'),(1,13,'2026-04-12 16:30:50'),(1,14,'2026-04-12 16:30:50'),(1,15,'2026-04-12 16:30:50'),(1,16,'2026-04-12 16:30:50'),(2,4,'2026-04-12 16:30:50'),(2,5,'2026-04-12 16:30:50'),(2,6,'2026-04-12 16:30:50'),(2,7,'2026-04-12 16:30:50'),(2,8,'2026-04-12 16:30:50'),(2,9,'2026-04-12 16:30:50'),(2,10,'2026-04-12 16:30:50'),(2,11,'2026-04-12 16:30:50'),(2,12,'2026-04-12 16:30:50'),(2,15,'2026-04-12 16:30:50'),(2,16,'2026-04-12 16:30:50'),(3,4,'2026-04-12 16:30:50'),(3,9,'2026-04-12 16:30:50'),(3,12,'2026-04-12 16:30:50'),(3,13,'2026-04-12 16:30:50'),(3,14,'2026-04-12 16:30:50'),(3,15,'2026-04-12 16:30:50');
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN','Quản trị hệ thống','Quản trị hệ thống, tài khoản, cấu hình',1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(2,'FLEET_MANAGER','Quản lý đội xe','Quản lý hồ sơ xe, giấy tờ, kế hoạch, báo cáo',1,'2026-04-12 16:30:50','2026-04-12 16:30:50'),(3,'TECHNICIAN','Nhân viên kỹ thuật','Cập nhật bảo dưỡng, sửa chữa, phụ tùng, ODO',1,'2026-04-12 16:30:50','2026-04-12 16:30:50');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle_documents`
--

DROP TABLE IF EXISTS `vehicle_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_documents`
--

LOCK TABLES `vehicle_documents` WRITE;
/*!40000 ALTER TABLE `vehicle_documents` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehicle_documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicles`
--

DROP TABLE IF EXISTS `vehicles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicles`
--

LOCK TABLES `vehicles` WRITE;
/*!40000 ALTER TABLE `vehicles` DISABLE KEYS */;
/*!40000 ALTER TABLE `vehicles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `vw_due_maintenance_plans`
--

DROP TABLE IF EXISTS `vw_due_maintenance_plans`;
/*!50001 DROP VIEW IF EXISTS `vw_due_maintenance_plans`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_due_maintenance_plans` AS SELECT 
 1 AS `plan_id`,
 1 AS `vehicle_id`,
 1 AS `license_plate`,
 1 AS `vehicle_type`,
 1 AS `maintenance_name`,
 1 AS `current_odometer`,
 1 AS `next_due_date`,
 1 AS `next_due_odometer`,
 1 AS `effective_alert_days`,
 1 AS `effective_alert_km`,
 1 AS `due_status`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_due_vehicle_documents`
--

DROP TABLE IF EXISTS `vw_due_vehicle_documents`;
/*!50001 DROP VIEW IF EXISTS `vw_due_vehicle_documents`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_due_vehicle_documents` AS SELECT 
 1 AS `document_id`,
 1 AS `vehicle_id`,
 1 AS `license_plate`,
 1 AS `vehicle_type`,
 1 AS `document_type_name`,
 1 AS `document_number`,
 1 AS `issuer_name`,
 1 AS `expiry_date`,
 1 AS `days_to_expiry`,
 1 AS `due_status`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `vw_vehicle_cost_monthly`
--

DROP TABLE IF EXISTS `vw_vehicle_cost_monthly`;
/*!50001 DROP VIEW IF EXISTS `vw_vehicle_cost_monthly`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_vehicle_cost_monthly` AS SELECT 
 1 AS `vehicle_id`,
 1 AS `license_plate`,
 1 AS `period_ym`,
 1 AS `maintenance_cost`,
 1 AS `document_cost`,
 1 AS `total_cost`*/;
SET character_set_client = @saved_cs_client;

--
-- Dumping events for database 'fleet_maintenance_db'
--

--
-- Dumping routines for database 'fleet_maintenance_db'
--

--
-- Final view structure for view `vw_due_maintenance_plans`
--

/*!50001 DROP VIEW IF EXISTS `vw_due_maintenance_plans`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_due_maintenance_plans` AS select `mp`.`plan_id` AS `plan_id`,`v`.`vehicle_id` AS `vehicle_id`,`v`.`license_plate` AS `license_plate`,`v`.`vehicle_type` AS `vehicle_type`,`mt`.`maintenance_name` AS `maintenance_name`,`v`.`current_odometer` AS `current_odometer`,`mp`.`next_due_date` AS `next_due_date`,`mp`.`next_due_odometer` AS `next_due_odometer`,coalesce(`mp`.`alert_before_days`,`aset`.`maintenance_alert_days`) AS `effective_alert_days`,coalesce(`mp`.`alert_before_km`,`aset`.`maintenance_alert_km`) AS `effective_alert_km`,(case when ((`mp`.`next_due_date` is not null) and (`mp`.`next_due_date` < curdate())) then 'OVERDUE' when ((`mp`.`next_due_odometer` is not null) and (`v`.`current_odometer` >= `mp`.`next_due_odometer`)) then 'OVERDUE' when ((`mp`.`next_due_date` is not null) and ((to_days(`mp`.`next_due_date`) - to_days(curdate())) <= coalesce(`mp`.`alert_before_days`,`aset`.`maintenance_alert_days`))) then 'COMING_DUE' when ((`mp`.`next_due_odometer` is not null) and ((`mp`.`next_due_odometer` - `v`.`current_odometer`) <= coalesce(`mp`.`alert_before_km`,`aset`.`maintenance_alert_km`))) then 'COMING_DUE' else 'NORMAL' end) AS `due_status` from (((`maintenance_plans` `mp` join `vehicles` `v` on((`v`.`vehicle_id` = `mp`.`vehicle_id`))) join `maintenance_types` `mt` on((`mt`.`maintenance_type_id` = `mp`.`maintenance_type_id`))) join `alert_settings` `aset` on((`aset`.`setting_id` = 1))) where (`mp`.`is_active` = 1) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_due_vehicle_documents`
--

/*!50001 DROP VIEW IF EXISTS `vw_due_vehicle_documents`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_due_vehicle_documents` AS select `vd`.`document_id` AS `document_id`,`v`.`vehicle_id` AS `vehicle_id`,`v`.`license_plate` AS `license_plate`,`v`.`vehicle_type` AS `vehicle_type`,`dt`.`document_type_name` AS `document_type_name`,`vd`.`document_number` AS `document_number`,`vd`.`issuer_name` AS `issuer_name`,`vd`.`expiry_date` AS `expiry_date`,(to_days(`vd`.`expiry_date`) - to_days(curdate())) AS `days_to_expiry`,(case when (`vd`.`expiry_date` < curdate()) then 'OVERDUE' when ((to_days(`vd`.`expiry_date`) - to_days(curdate())) <= `dt`.`default_alert_days`) then 'COMING_DUE' else 'NORMAL' end) AS `due_status` from ((`vehicle_documents` `vd` join `vehicles` `v` on((`v`.`vehicle_id` = `vd`.`vehicle_id`))) join `document_types` `dt` on((`dt`.`document_type_id` = `vd`.`document_type_id`))) where ((`vd`.`is_current` = 1) and (`vd`.`document_status` in ('VALID','EXPIRED'))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `vw_vehicle_cost_monthly`
--

/*!50001 DROP VIEW IF EXISTS `vw_vehicle_cost_monthly`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `vw_vehicle_cost_monthly` AS select `x`.`vehicle_id` AS `vehicle_id`,`x`.`license_plate` AS `license_plate`,`x`.`period_ym` AS `period_ym`,sum(`x`.`maintenance_cost`) AS `maintenance_cost`,sum(`x`.`document_cost`) AS `document_cost`,sum((`x`.`maintenance_cost` + `x`.`document_cost`)) AS `total_cost` from (select `v`.`vehicle_id` AS `vehicle_id`,`v`.`license_plate` AS `license_plate`,date_format(`mr`.`service_date`,'%Y-%m') AS `period_ym`,`mr`.`total_cost` AS `maintenance_cost`,0.00 AS `document_cost` from (`maintenance_records` `mr` join `vehicles` `v` on((`v`.`vehicle_id` = `mr`.`vehicle_id`))) where ((`mr`.`record_status` = 'COMPLETED') and (`mr`.`service_date` is not null)) union all select `v`.`vehicle_id` AS `vehicle_id`,`v`.`license_plate` AS `license_plate`,date_format(coalesce(`vd`.`paid_date`,`vd`.`issue_date`,`vd`.`expiry_date`),'%Y-%m') AS `period_ym`,0.00 AS `maintenance_cost`,`vd`.`fee_amount` AS `document_cost` from (`vehicle_documents` `vd` join `vehicles` `v` on((`v`.`vehicle_id` = `vd`.`vehicle_id`)))) `x` group by `x`.`vehicle_id`,`x`.`license_plate`,`x`.`period_ym` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-24 18:19:46
