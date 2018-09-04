/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50722
Source Host           : localhost:3306
Source Database       : coin_btc

Target Server Type    : MYSQL
Target Server Version : 50722
File Encoding         : 65001

Date: 2018-08-24 15:56:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for block
-- ----------------------------
DROP TABLE IF EXISTS `block`;
CREATE TABLE `block` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `block_hash` char(64) NOT NULL,
  `previous_block_hash` char(64) DEFAULT NULL,
  `merkleroot` char(64) DEFAULT NULL,
  `chainwork` char(64) DEFAULT NULL,
  `size` bigint(20) DEFAULT NULL,
  `height` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `median_time` bigint(20) DEFAULT NULL,
  `bits` varchar(255) DEFAULT NULL,
  `nonce` bigint(20) DEFAULT NULL,
  `difficulty` double DEFAULT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '0:无效，1:有效',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `block_hash_UNIQUE` (`block_hash`) USING BTREE,
  KEY `idx_height` (`height`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for input
-- ----------------------------
DROP TABLE IF EXISTS `input`;
CREATE TABLE `input` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_txid` char(64) NOT NULL COMMENT '当前vin所属transaction',
  `wallet_account_id` bigint(20) DEFAULT NULL,
  `address` varchar(512) DEFAULT NULL,
  `txid` char(64) DEFAULT NULL,
  `coinbase` varchar(512) DEFAULT NULL,
  `vout` int(11) DEFAULT NULL,
  `sequence` bigint(20) DEFAULT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '1',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for output
-- ----------------------------
DROP TABLE IF EXISTS `output`;
CREATE TABLE `output` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wallet_account_id` bigint(20) DEFAULT NULL,
  `transaction_txid` char(64) NOT NULL COMMENT '当前vout所属transaction',
  `coinbase` tinyint(3) NOT NULL DEFAULT '0' COMMENT '0:false,1:true',
  `value` varchar(64) DEFAULT NULL,
  `value_sat` varchar(64) DEFAULT NULL,
  `n` int(11) DEFAULT NULL,
  `script_pub_key_asm` varchar(512) DEFAULT NULL,
  `script_pub_key_hex` mediumtext,
  `script_pub_key_req_sigs` int(11) DEFAULT NULL,
  `script_pub_key_type` char(64) DEFAULT NULL,
  `script_pub_key_addresses` varchar(512) DEFAULT NULL,
  `status` tinyint(3) NOT NULL DEFAULT '1',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_txid_n` (`transaction_txid`,`n`) USING BTREE,
  KEY `idx_address` (`script_pub_key_addresses`) USING BTREE,
  KEY `idx_wallet_account_id` (`wallet_account_id`) USING BTREE,
  KEY `idx_transaction_txid` (`transaction_txid`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for transaction
-- ----------------------------
DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `txid` char(64) NOT NULL,
  `hex` mediumtext,
  `size` bigint(20) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `locktime` bigint(20) DEFAULT NULL,
  `height` bigint(20) DEFAULT NULL,
  `time` bigint(20) DEFAULT NULL,
  `block_hash` char(64) DEFAULT NULL,
  `coinbase` tinyint(3) NOT NULL DEFAULT '0' COMMENT '0：非coinbase交易，1：coinbase交易',
  `block_time` bigint(20) DEFAULT NULL,
  `trans_dire` tinyint(3) NOT NULL COMMENT '0:INTERNAL,1:INPUT,2:OUTPUT',
  `trans_status` tinyint(3) NOT NULL COMMENT '0:SENDING,1:SENT,2:CONFIRMING,3:COMPLETE,4:ISOLATED,5:ISOLATEDCONRIMING,6:CHAINFAILED',
  `status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '0:无效,1:有效',
  `create_ts` datetime DEFAULT NULL,
  `update_ts` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `txid_UNIQUE` (`txid`) USING BTREE,
  KEY `idx_block_hash` (`block_hash`) USING BTREE,
  KEY `idx_trans_status` (`trans_status`) USING BTREE,
  KEY `idx_txid` (`txid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
