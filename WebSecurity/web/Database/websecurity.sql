/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50613
Source Host           : localhost:3306
Source Database       : websecurity

Target Server Type    : MYSQL
Target Server Version : 50613
File Encoding         : 65001

Date: 2014-09-25 18:06:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for m_member
-- ----------------------------
DROP TABLE IF EXISTS `m_member`;
CREATE TABLE `m_member` (
  `A` bigint(20) NOT NULL AUTO_INCREMENT,
  `B` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `C` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `D` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `E` varchar(10) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `F` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `G` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `H` varchar(50) NOT NULL,
  `I` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `J` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci DEFAULT NULL,
  `K` datetime DEFAULT NULL,
  `L` datetime NOT NULL,
  `M` datetime DEFAULT NULL,
  PRIMARY KEY (`A`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
