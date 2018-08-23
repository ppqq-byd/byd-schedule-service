/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : coin_dark

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-08-17 08:49:28
*/

CREATE DATABASE /*!32312 IF NOT EXISTS*/`coin_eos` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `coin_eos`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for block
-- ----------------------------
DROP TABLE IF EXISTS `block`;
CREATE TABLE `block` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timestamp` char(32) NOT NULL,
  `producer` char(16) NOT NULL,
  `confirmed` tinyint(3) DEFAULT NULL,
  `previous` char(128) NOT NULL,
  `transaction_mroot` char(128) NOT NULL,
  `action_mroot` char(128) NOT NULL,
  `schedule_version` char(20) DEFAULT NULL,
  `new_producers` varchar(128) DEFAULT NULL,
  `header_extensions` varchar(512) DEFAULT NULL,
  `producer_signature` char(128) NOT NULL,
  `block_extensions` varchar(512) DEFAULT NULL,
  `block_id` char(128) NOT NULL,
  `block_num` bigint(20) NOT NULL,
  `ref_block_prefix` bigint(20) NOT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '0:无效，1:有效',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `block_num_index` (`block_num`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tx_status` char(16) DEFAULT NULL,
  `cpu_usage_us` char(12) DEFAULT NULL,
  `net_usage_words` char(12) DEFAULT NULL,
  `tx_id` char(128) NOT NULL,
  `account` char(16) NOT NULL COMMENT 'contract account name',
  `name` char(16) NOT NULL COMMENT 'contract action',
  `authorization` varchar(255) NOT NULL COMMENT 'authorization',
  `from` char(16) NOT NULL COMMENT 'from account',
  `to` char(16) NOT NULL COMMENT 'to account',
  `quantity` char(32) NOT NULL,
  `memo` varchar(255) DEFAULT NULL,
  `signatures` mediumtext NOT NULL,
  `compression` char(16) DEFAULT NULL,
  `packed_context_free_data` varchar(256) DEFAULT NULL,
  `context_free_data` varchar(256) DEFAULT NULL,
  `packed_trx` char(255) NOT NULL,
  `expiration` char(32) NOT NULL,
  `ref_block_num` bigint(20) NOT NULL,
  `ref_block_prefix` bigint(20) NOT NULL,
  `max_net_usage_words` bigint(20) DEFAULT NULL,
  `max_cpu_usage_ms` bigint(20) DEFAULT NULL,
  `delay_sec` bigint(20) DEFAULT NULL,
  `context_free_actions` varchar(256) DEFAULT NULL,
  `actions` mediumtext NOT NULL,
  `transaction_extensions` varchar(256) DEFAULT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '0:无效,1:有效',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `tx_id_index` (`tx_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8;
