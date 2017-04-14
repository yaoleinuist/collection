/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-04-14 09:15:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `createTime` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `site` varchar(255) DEFAULT NULL,
  `fixedtel` varchar(255) DEFAULT NULL,
  `establishmentDate` datetime DEFAULT NULL,
  `scale` int(255) DEFAULT NULL,
  `license` varchar(255) DEFAULT NULL,
  `idfront` varchar(255) DEFAULT NULL,
  `idopposite` varchar(255) DEFAULT NULL,
  `works1` varchar(255) DEFAULT NULL,
  `works2` varchar(255) DEFAULT NULL,
  `works3` varchar(255) DEFAULT NULL,
  `city` int(255) DEFAULT NULL,
  `updateTimes` datetime DEFAULT NULL,
  `status` int(255) DEFAULT NULL,
  `del` int(255) DEFAULT NULL,
  `remianSpace` int(255) DEFAULT NULL,
  `authentication` int(255) DEFAULT NULL,
  `contacts` varchar(255) DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `province` int(255) DEFAULT NULL,
  `sex` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
