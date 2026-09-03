-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.32 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.4.0.6659
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for scm_db
CREATE DATABASE IF NOT EXISTS `scm_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `scm_db`;

-- Dumping structure for table scm_db.audit_log_entry
CREATE TABLE IF NOT EXISTS `audit_log_entry` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `COMPONENT` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `DETAIL` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `method_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `OUTCOME` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `recorded_at` datetime(6) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.audit_log_entry: ~0 rows (approximately)

-- Dumping structure for table scm_db.customs_document
CREATE TABLE IF NOT EXISTS `customs_document` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `country_code` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `document_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `submission_deadline` date NOT NULL,
  `shipment_id` bigint NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_customs_document_shipment_id` (`shipment_id`),
  CONSTRAINT `FK_customs_document_shipment_id` FOREIGN KEY (`shipment_id`) REFERENCES `shipment` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.customs_document: ~0 rows (approximately)

-- Dumping structure for table scm_db.inventory_item
CREATE TABLE IF NOT EXISTS `inventory_item` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `DESCRIPTION` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `quantity_on_hand` int NOT NULL,
  `reorder_threshold` int NOT NULL,
  `SKU` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `VERSION` bigint DEFAULT NULL,
  `warehouse_location` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SKU` (`SKU`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.inventory_item: ~0 rows (approximately)

-- Dumping structure for table scm_db.personnel
CREATE TABLE IF NOT EXISTS `personnel` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `EMAIL` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ROLE` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `USERNAME` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`),
  KEY `FK_personnel_vendor_id` (`vendor_id`),
  CONSTRAINT `FK_personnel_vendor_id` FOREIGN KEY (`vendor_id`) REFERENCES `vendor` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.personnel: ~4 rows (approximately)
REPLACE INTO `personnel` (`ID`, `EMAIL`, `full_name`, `password_hash`, `ROLE`, `USERNAME`, `vendor_id`) VALUES
	(1, 'tobias.reinholt@globaltradelogistics.com', 'Tobias Reinholt', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'WAREHOUSE_MANAGER', 'warehouse1', NULL),
	(2, 'elena.marchetti@globaltradelogistics.com', 'Elena Marchetti', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'CUSTOMS_AGENT', 'customs1', NULL),
	(3, 'amara.osei@pacificrimfreight.com', 'Amara Osei', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'VENDOR_REPRESENTATIVE', 'vendor1', NULL),
	(4, 'priya.nakamura@globaltradelogistics.com', 'Priya Nakamura', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'COORDINATOR', 'coordinator1', NULL);

-- Dumping structure for table scm_db.purchase_order
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `order_date` date NOT NULL,
  `QUANTITY` int NOT NULL,
  `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `inventory_item_id` bigint NOT NULL,
  `vendor_id` bigint NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_purchase_order_inventory_item_id` (`inventory_item_id`),
  KEY `FK_purchase_order_vendor_id` (`vendor_id`),
  CONSTRAINT `FK_purchase_order_inventory_item_id` FOREIGN KEY (`inventory_item_id`) REFERENCES `inventory_item` (`ID`),
  CONSTRAINT `FK_purchase_order_vendor_id` FOREIGN KEY (`vendor_id`) REFERENCES `vendor` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.purchase_order: ~0 rows (approximately)

-- Dumping structure for table scm_db.shipment
CREATE TABLE IF NOT EXISTS `shipment` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `carrier_id` bigint DEFAULT NULL,
  `DESTINATION` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estimated_arrival` datetime(6) DEFAULT NULL,
  `estimated_departure` datetime(6) DEFAULT NULL,
  `ORIGIN` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tracking_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `tracking_number` (`tracking_number`),
  KEY `FK_shipment_vendor_id` (`vendor_id`),
  CONSTRAINT `FK_shipment_vendor_id` FOREIGN KEY (`vendor_id`) REFERENCES `vendor` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.shipment: ~0 rows (approximately)

-- Dumping structure for table scm_db.vendor
CREATE TABLE IF NOT EXISTS `vendor` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `contact_email` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `COUNTRY` varchar(2) COLLATE utf8mb4_unicode_ci NOT NULL,
  `NAME` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `performance_score` double NOT NULL,
  `STATUS` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table scm_db.vendor: ~0 rows (approximately)

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
