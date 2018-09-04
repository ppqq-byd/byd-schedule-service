/*
SQLyog 企业版 - MySQL GUI v8.14 
MySQL - 5.7.17-log : Database - wallet_common
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`wallet_common` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `wallet_common`;

/*Table structure for table `wallet_account` */

DROP TABLE IF EXISTS `wallet_account`;

CREATE TABLE `wallet_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wallet_account` varchar(128) DEFAULT NULL COMMENT '由种子生成的level2公钥导出的字符串',
  `create_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=209 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `wallet_account` */

insert  into `wallet_account`(`id`,`wallet_account`,`create_ts`,`update_ts`) values (1,'token_eth_send_neizhuannei','2018-08-29 19:14:57','2018-09-03 15:13:59'),(2,'token_eth_receive_neizhuannei','2018-09-03 14:37:40','2018-09-03 15:14:04'),(3,'token_eth_receive','2018-09-03 14:39:17','2018-09-03 15:16:42'),(4,'eth_neizhuannei_send','2018-09-03 15:16:37','2018-09-03 15:16:50'),(5,'eth_neizhuannei_receive','2018-09-03 15:17:00','2018-09-03 15:17:00'),(6,'eth_receive','2018-09-03 15:18:49','2018-09-03 15:18:49');

/*Table structure for table `wallet_account_balance` */

DROP TABLE IF EXISTS `wallet_account_balance`;

CREATE TABLE `wallet_account_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) NOT NULL,
  `coin_type` varchar(10) NOT NULL,
  `token_id` int(10) DEFAULT NULL,
  `total_balance` varchar(32) DEFAULT '0',
  `frozen_balance` varchar(32) DEFAULT '0',
  `create_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_account_coin` (`account_id`,`coin_type`,`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `wallet_account_balance` */

insert  into `wallet_account_balance`(`id`,`account_id`,`coin_type`,`token_id`,`total_balance`,`frozen_balance`,`create_ts`,`update_ts`) values (1,1,'ETH',NULL,'0','0','2018-08-29 19:15:33','2018-08-29 19:15:33'),(2,2,'ETH',NULL,'0','0','2018-09-03 14:38:43','2018-09-03 14:38:43'),(3,3,'ETH',NULL,'0','0','2018-09-03 14:39:27','2018-09-03 14:39:27'),(4,4,'ETH',NULL,'0','0','2018-09-03 15:17:29','2018-09-03 15:17:29'),(5,5,'ETH',NULL,'0','0','2018-09-03 15:17:33','2018-09-03 15:17:33'),(6,6,'ETH',NULL,'0','0','2018-09-03 15:18:59','2018-09-03 15:18:59'),(7,1,'ETH',4,'0','0','2018-09-03 15:26:03','2018-09-03 15:26:10'),(8,2,'ETH',4,'0','0','2018-09-03 15:26:09','2018-09-03 15:26:14'),(9,3,'ETH',1,'0','0','2018-09-03 15:26:22','2018-09-03 15:26:22');

/*Table structure for table `wallet_account_bind` */

DROP TABLE IF EXISTS `wallet_account_bind`;

CREATE TABLE `wallet_account_bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL,
  `coin_type` varchar(10) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `create_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `wallet_account_bind` */

insert  into `wallet_account_bind`(`id`,`account_id`,`coin_type`,`address`,`create_ts`,`update_ts`) values (1,1,'ETH','0x5225588b486e78099bb53c162ec8993ab8e6d678','2018-08-29 19:15:06','2018-09-03 14:37:25'),(2,2,'ETH','0xafa920790e1e9ef75c5f44fa847e8b0d04ee073f','2018-09-03 14:38:50','2018-09-03 14:38:50'),(3,3,'ETH','0x61a144dc9b55d73bbe9cbef7e546441bba6468d9','2018-09-03 14:39:35','2018-09-03 14:39:35'),(4,4,'ETH','0x80b9df95f6e9b2df2ca91f573844476f76dbb5c4','2018-09-03 15:17:16','2018-09-03 15:17:16'),(5,5,'ETH','0x4fed1fc4144c223ae3c1553be203cdfcbd38c581','2018-09-03 15:17:22','2018-09-03 15:17:22'),(6,6,'ETH','0x70faa28A6B8d6829a4b1E649d26eC9a2a39ba413','2018-09-03 15:18:40','2018-09-03 15:18:40');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
