/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : i_wenyiba_com

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2017-12-22 17:58:41
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for seckill
-- ----------------------------
DROP TABLE IF EXISTS `seckill`;
CREATE TABLE `seckill` (
  `seckill_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `name` varchar(120) NOT NULL COMMENT '商品名称',
  `number` int(11) NOT NULL COMMENT '库存数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀开始时间',
  `end_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '秒杀结束时间',
  PRIMARY KEY (`seckill_id`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- ----------------------------
-- Records of seckill
-- ----------------------------
INSERT INTO `seckill` VALUES ('1000', '1000元秒杀iphone6', '100', '2017-12-11 18:45:02', '2016-01-01 00:00:00', '2016-01-02 00:00:00');
INSERT INTO `seckill` VALUES ('1001', '800元秒杀ipad', '200', '2017-12-11 18:45:02', '2016-01-01 00:00:00', '2016-01-02 00:00:00');
INSERT INTO `seckill` VALUES ('1002', '6600元秒杀mac book pro', '300', '2017-12-11 18:45:02', '2016-01-01 00:00:00', '2016-01-02 00:00:00');
INSERT INTO `seckill` VALUES ('1003', '7000元秒杀iMac', '400', '2017-12-11 18:45:02', '2016-01-01 00:00:00', '2016-01-02 00:00:00');

-- ----------------------------
-- Table structure for secret
-- ----------------------------
DROP TABLE IF EXISTS `secret`;
CREATE TABLE `secret` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `app_id` varchar(50) NOT NULL COMMENT '应用编号',
  `app_secret` varchar(700) NOT NULL DEFAULT '' COMMENT '秘钥',
  `sign_type` varchar(5) NOT NULL COMMENT '签名算法类型：RSA，DSA，MD5',
  `app_name` varchar(50) DEFAULT '' COMMENT '应用名称',
  `app_type` varchar(50) DEFAULT '' COMMENT '客户端类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9004 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of secret
-- ----------------------------
INSERT INTO `secret` VALUES ('9001', '7c65257ce35d40a28bf1d0f0c1e15558', 'xkeshi_backend_secret_sdfhkljjkljksdfsadf', 'MD5', 'xkeshi_core_test', '');
INSERT INTO `secret` VALUES ('9002', 'abde89f0ea5643cb84f6cfcf8e12667c', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5662Ox99vDma1KiuzYjC+hPSZ7wEDKSonZH9wA4AEkEpebgteMPOXkffH225kFod+R0W03BdBuLqXO1wSAHGENGq/aMpCJa3CEhREt3Cdt/gTykoCV918gcpH19yKFcwMdTQh710XrjyTOTtfL435K9tYVkjye8rw1J3sx1jlFQIDAQAB', 'RSA', 'xkeshi_core_test', '');
INSERT INTO `secret` VALUES ('9003', 'abde89f0ea5643cb84f6cfcf8e12667c', 'MIIBtjCCASsGByqGSM44BAEwggEeAoGBAIIwzdoBRCbCrVzbI3PrHhhAATzcR/5e6qkV6hBv0i0ejNZrYDCfjTtTp2H+cfUVRX18aoNH4o8BqAiDkQ6q0zazjLJ4/7Hyw0EQD21vWxG4h5bnAJiXxeuN5tTF0Fxr9R8UVai09Ig0r4fICp31Ww50+sbLf58nnZQRf9yVKLnzAhUArCq0ZNhwE0K2YCMnP3VoUKfDYV8CgYAMyBvkT47+3I46iJ6GDLyF8aKcTKD4IzGEAdJwa3DdYS3ChKV2wg5d2uNPfbKqXU6BHGKgM/DvSBm3hIniPwT98tDUnXXDpEKRq/zizfNUBEn1zF1bikgf8K0c3ymHxzKuWlZnQLf2CgrSKuF/eNwYLr2ga4ucJ/2EFno4C8G1KQOBhAACgYAs6Ms4T62lFjLX73+kYal7fB9djtTwcjgYfZfxidoRWbfXBrhlrRhJzctNKFsKq+4kNmjD+wISoGKkjIwGa/8aRxjf2Ka1TLd5mnL3dXMVKOldjORHQc9JTFCSccz/TFKc+ddpcgB6tfJJsW4ngqPfDAkym3Frfpb/y2kbZ5Ru/A==', 'DSA', 'xkeshi_core_test', '');

-- ----------------------------
-- Table structure for success_killed
-- ----------------------------
DROP TABLE IF EXISTS `success_killed`;
CREATE TABLE `success_killed` (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品ID',
  `user_phone` bigint(20) NOT NULL COMMENT '用户手机号',
  `state` tinyint(4) NOT NULL DEFAULT '-1' COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`seckill_id`,`user_phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='秒杀成功明细表';

-- ----------------------------
-- Records of success_killed
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', '2017-12-22 16:21:16', 'admin', '管理员', '57eb72e6b78a87a12d46a7f5e9315138', '180238023', '1', '1', '2323', '2017-12-22 16:22:17', '2323', '2323', '2323', '2323', 'v', '2323', '2323', '2323', '2017-12-22 16:22:40', '1', '0', '2323', '123', '2323', '2323', '2323', '12', '1');
INSERT INTO `t_user` VALUES ('2', '2017-12-22 16:21:36', '8446666@qq.com', 'soso', '3fcedb135e7f253a390afb7b9a95f564', '180238021', '2', '1', '23', '2017-12-22 16:22:20', '23', '23', '23', '23', 'v', '23', '23', '23', '2017-12-22 16:22:42', '1', '0', '23', '232', '23', '23', '23', '23', '2');
INSERT INTO `t_user` VALUES ('3', '2017-12-22 16:21:38', '8446666', '8446666', 'eb49550dd8a1937923cec0749e17eabb', '180238024', '3', '1', '2132', '2017-12-22 16:22:23', '2132', '2132', '2132', '2132', 'v', '2132', '2132', '2132', '2017-12-22 16:22:47', '1', '0', '2132', '123', '2132', '2132', '2132', '212', '1');

-- ----------------------------
-- Table structure for u_permission
-- ----------------------------
DROP TABLE IF EXISTS `u_permission`;
CREATE TABLE `u_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `url` varchar(256) DEFAULT NULL COMMENT 'url地址',
  `name` varchar(64) DEFAULT NULL COMMENT 'url描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_permission
-- ----------------------------
INSERT INTO `u_permission` VALUES ('4', '/permission/index.shtml', '权限列表');
INSERT INTO `u_permission` VALUES ('6', '/permission/addPermission.shtml', '权限添加');
INSERT INTO `u_permission` VALUES ('7', '/permission/deletePermissionById.shtml', '权限删除');
INSERT INTO `u_permission` VALUES ('8', '/member/list.shtml', '用户列表');
INSERT INTO `u_permission` VALUES ('9', '/member/online.shtml', '在线用户');
INSERT INTO `u_permission` VALUES ('10', '/member/changeSessionStatus.shtml', '用户Session踢出');
INSERT INTO `u_permission` VALUES ('11', '/member/forbidUserById.shtml', '用户激活&禁止');
INSERT INTO `u_permission` VALUES ('12', '/member/deleteUserById.shtml', '用户删除');
INSERT INTO `u_permission` VALUES ('13', '/permission/addPermission2Role.shtml', '权限分配');
INSERT INTO `u_permission` VALUES ('14', '/role/clearRoleByUserIds.shtml', '用户角色分配清空');
INSERT INTO `u_permission` VALUES ('15', '/role/addRole2User.shtml', '角色分配保存');
INSERT INTO `u_permission` VALUES ('16', '/role/deleteRoleById.shtml', '角色列表删除');
INSERT INTO `u_permission` VALUES ('17', '/role/addRole.shtml', '角色列表添加');
INSERT INTO `u_permission` VALUES ('18', '/role/index.shtml', '角色列表');
INSERT INTO `u_permission` VALUES ('19', '/permission/allocation.shtml', '权限分配');
INSERT INTO `u_permission` VALUES ('20', '/role/allocation.shtml', '角色分配');

-- ----------------------------
-- Table structure for u_role
-- ----------------------------
DROP TABLE IF EXISTS `u_role`;
CREATE TABLE `u_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `type` varchar(10) DEFAULT NULL COMMENT '角色类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_role
-- ----------------------------
INSERT INTO `u_role` VALUES ('1', '系统管理员', '888888');
INSERT INTO `u_role` VALUES ('3', '权限角色', '100003');
INSERT INTO `u_role` VALUES ('4', '用户中心', '100002');

-- ----------------------------
-- Table structure for u_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `u_role_permission`;
CREATE TABLE `u_role_permission` (
  `rid` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `pid` bigint(20) DEFAULT NULL COMMENT '权限ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_role_permission
-- ----------------------------
INSERT INTO `u_role_permission` VALUES ('4', '8');
INSERT INTO `u_role_permission` VALUES ('4', '9');
INSERT INTO `u_role_permission` VALUES ('4', '10');
INSERT INTO `u_role_permission` VALUES ('4', '11');
INSERT INTO `u_role_permission` VALUES ('4', '12');
INSERT INTO `u_role_permission` VALUES ('3', '4');
INSERT INTO `u_role_permission` VALUES ('3', '6');
INSERT INTO `u_role_permission` VALUES ('3', '7');
INSERT INTO `u_role_permission` VALUES ('3', '13');
INSERT INTO `u_role_permission` VALUES ('3', '14');
INSERT INTO `u_role_permission` VALUES ('3', '15');
INSERT INTO `u_role_permission` VALUES ('3', '16');
INSERT INTO `u_role_permission` VALUES ('3', '17');
INSERT INTO `u_role_permission` VALUES ('3', '18');
INSERT INTO `u_role_permission` VALUES ('3', '19');
INSERT INTO `u_role_permission` VALUES ('3', '20');
INSERT INTO `u_role_permission` VALUES ('1', '4');
INSERT INTO `u_role_permission` VALUES ('1', '6');
INSERT INTO `u_role_permission` VALUES ('1', '7');
INSERT INTO `u_role_permission` VALUES ('1', '8');
INSERT INTO `u_role_permission` VALUES ('1', '9');
INSERT INTO `u_role_permission` VALUES ('1', '10');
INSERT INTO `u_role_permission` VALUES ('1', '11');
INSERT INTO `u_role_permission` VALUES ('1', '12');
INSERT INTO `u_role_permission` VALUES ('1', '13');
INSERT INTO `u_role_permission` VALUES ('1', '14');
INSERT INTO `u_role_permission` VALUES ('1', '15');
INSERT INTO `u_role_permission` VALUES ('1', '16');
INSERT INTO `u_role_permission` VALUES ('1', '17');
INSERT INTO `u_role_permission` VALUES ('1', '18');
INSERT INTO `u_role_permission` VALUES ('1', '19');
INSERT INTO `u_role_permission` VALUES ('1', '20');

-- ----------------------------
-- Table structure for u_user
-- ----------------------------
DROP TABLE IF EXISTS `u_user`;
CREATE TABLE `u_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱|登录帐号',
  `pswd` varchar(32) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` bigint(1) DEFAULT '1' COMMENT '1:有效，0:禁止登录',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_user
-- ----------------------------
INSERT INTO `u_user` VALUES ('1', '管理员', 'admin', '57eb72e6b78a87a12d46a7f5e9315138', '2016-06-16 11:15:33', '2017-09-13 15:36:13', '1');
INSERT INTO `u_user` VALUES ('11', 'soso', '8446666@qq.com', '3fcedb135e7f253a390afb7b9a95f564', '2016-05-26 20:50:54', '2016-06-16 11:24:35', '1');
INSERT INTO `u_user` VALUES ('12', '8446666', '8446666', 'eb49550dd8a1937923cec0749e17eabb', '2016-05-27 22:34:19', '2017-09-13 10:32:12', '1');

-- ----------------------------
-- Table structure for u_user_role
-- ----------------------------
DROP TABLE IF EXISTS `u_user_role`;
CREATE TABLE `u_user_role` (
  `uid` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `rid` bigint(20) DEFAULT NULL COMMENT '角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of u_user_role
-- ----------------------------
INSERT INTO `u_user_role` VALUES ('12', '4');
INSERT INTO `u_user_role` VALUES ('11', '3');
INSERT INTO `u_user_role` VALUES ('11', '4');
INSERT INTO `u_user_role` VALUES ('1', '1');

-- ----------------------------
-- Procedure structure for execute_seckill
-- ----------------------------
DROP PROCEDURE IF EXISTS `execute_seckill`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `execute_seckill`(IN v_seckill_id BIGINT, IN v_phone BIGINT,
   IN v_kill_time  TIMESTAMP, OUT r_result INT)
BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION;
    INSERT IGNORE INTO success_killed
    (seckill_id, user_phone, state)
    VALUES (v_seckill_id, v_phone, 0);
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF (insert_count < 0) THEN
	ROLLBACK;
	SET r_result = -2;
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
	    AND end_time > v_kill_time
	    AND start_time < v_kill_time
	    AND number > 0;
      SELECT row_count() INTO insert_count;
      IF (insert_count = 0) THEN
	ROLLBACK;
	SET r_result = 0;
      ELSEIF (insert_count < 0) THEN
	  ROLLBACK;
	  SET r_result = -2;
      ELSE
	COMMIT;
	SET r_result = 1;
      END IF;
    END IF;
  END
;;
DELIMITER ;
