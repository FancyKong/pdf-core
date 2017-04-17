/*
Navicat MySQL Data Transfer

Source Server         : 127.0.0.1
Source Server Version : 50711
Source Host           : 127.0.0.1:3306
Source Database       : pdf-core

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2017-03-26 19:52:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_customer
-- ----------------------------
DROP TABLE IF EXISTS `t_customer`;
CREATE TABLE `t_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_active` int(11) NOT NULL,
  `created_time` datetime NOT NULL,
  `modified_time` datetime DEFAULT NULL,
  `nickname` varchar(32) NOT NULL,
  `password` varchar(16) NOT NULL,
  `telephone` varchar(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hhsuwu3qpbo96h3o6osun9v86` (`telephone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_customer
-- ----------------------------

-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(1024) NOT NULL DEFAULT '',
  `permit` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', '编辑用户', 'user:update');
INSERT INTO `t_permission` VALUES ('2', '删除用户', 'user:delete');
INSERT INTO `t_permission` VALUES ('3', '新增用户', 'user:add');
INSERT INTO `t_permission` VALUES ('4', '查看用户', 'user:show');
INSERT INTO `t_permission` VALUES ('5', '新增企业资讯', 'info:add');
INSERT INTO `t_permission` VALUES ('6', '删除企业资讯', 'info:delete');
INSERT INTO `t_permission` VALUES ('7', '更新企业资讯', 'info:update');
INSERT INTO `t_permission` VALUES ('8', '查看企业资讯', 'info:show');
INSERT INTO `t_permission` VALUES ('9', '查看客户信息', 'customer:show');
INSERT INTO `t_permission` VALUES ('10', '客户信息修改', 'customer:update');
INSERT INTO `t_permission` VALUES ('11', '删除客户/冻结', 'customer:delete');
INSERT INTO `t_permission` VALUES ('12', '新增客户，帮客户注册', 'customer:add');

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(1024) NOT NULL DEFAULT '',
  `name` varchar(16) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', '管理员', 'admin');
INSERT INTO `t_role` VALUES ('2', '超级管理员', 'super');
INSERT INTO `t_role` VALUES ('4', '接待员/前台人员/收银员', 'receptionist');
INSERT INTO `t_role` VALUES ('5', '编写人员/资讯编辑/资料收录', 'editor');
INSERT INTO `t_role` VALUES ('6', '大师傅', 'master');

-- ----------------------------
-- Table structure for t_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  KEY `FKjobmrl6dorhlfite4u34hciik` (`permission_id`),
  KEY `FK90j038mnbnthgkc17mqnoilu9` (`role_id`),
  CONSTRAINT `FK90j038mnbnthgkc17mqnoilu9` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FKjobmrl6dorhlfite4u34hciik` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_role_permission
-- ----------------------------
INSERT INTO `t_role_permission` VALUES ('5', '5');
INSERT INTO `t_role_permission` VALUES ('5', '6');
INSERT INTO `t_role_permission` VALUES ('5', '7');
INSERT INTO `t_role_permission` VALUES ('5', '8');
INSERT INTO `t_role_permission` VALUES ('1', '1');
INSERT INTO `t_role_permission` VALUES ('1', '2');
INSERT INTO `t_role_permission` VALUES ('1', '3');
INSERT INTO `t_role_permission` VALUES ('1', '4');
INSERT INTO `t_role_permission` VALUES ('1', '5');
INSERT INTO `t_role_permission` VALUES ('1', '6');
INSERT INTO `t_role_permission` VALUES ('1', '7');
INSERT INTO `t_role_permission` VALUES ('1', '8');
INSERT INTO `t_role_permission` VALUES ('1', '9');
INSERT INTO `t_role_permission` VALUES ('1', '10');
INSERT INTO `t_role_permission` VALUES ('1', '11');
INSERT INTO `t_role_permission` VALUES ('1', '12');
INSERT INTO `t_role_permission` VALUES ('4', '9');
INSERT INTO `t_role_permission` VALUES ('2', '1');
INSERT INTO `t_role_permission` VALUES ('2', '2');
INSERT INTO `t_role_permission` VALUES ('2', '3');
INSERT INTO `t_role_permission` VALUES ('2', '4');
INSERT INTO `t_role_permission` VALUES ('2', '5');
INSERT INTO `t_role_permission` VALUES ('2', '6');
INSERT INTO `t_role_permission` VALUES ('2', '7');
INSERT INTO `t_role_permission` VALUES ('2', '8');
INSERT INTO `t_role_permission` VALUES ('2', '9');
INSERT INTO `t_role_permission` VALUES ('2', '10');
INSERT INTO `t_role_permission` VALUES ('2', '11');
INSERT INTO `t_role_permission` VALUES ('2', '12');

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `is_active` tinyint(6) NOT NULL,
  `created_time` datetime NOT NULL,
  `description` varchar(1024) NOT NULL DEFAULT '',
  `modified_time` datetime NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `password` varchar(40) NOT NULL,
  `telephone` varchar(16) NOT NULL,
  `username` varchar(16) NOT NULL,
  `position` varchar(16) NOT NULL,
  `store_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `username_index` (`username`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', '1', '2017-01-31 23:13:55', '就是我', '2017-02-05 15:15:55', '2017-02-03 23:00:44', 'Cherish', '342401bf3afbe8b5f7c742f99b4759bf4a21a933', '18826137274', 'cherish', '超级大哥', '0');
INSERT INTO `t_user` VALUES ('2', '1', '2017-01-31 23:26:50', '好像还是有点小问题', '2017-02-18 18:43:52', '2017-02-03 00:00:00', 'admin1', '342401bf3afbe8b5f7c742f99b4759bf4a21a933', '15521243755', 'admin1', '煮饭做吃', '0');
INSERT INTO `t_user` VALUES ('3', '1', '2017-02-06 10:49:35', '没有备注', '2017-02-18 18:23:19', '2017-02-06 00:00:00', '蔡大哥', 'd70c0b393abe4f664f1e461b777cc1df1ef8ce15', '18826137274', 'admin2', '洗碗清洁', '0');
INSERT INTO `t_user` VALUES ('4', '0', '2017-02-06 11:46:46', '再次冻结', '2017-02-18 18:19:44', '2017-02-06 00:00:00', 'admin3', 'ea903fb94fce27574d59598a4301989d05297471', '18826137274', 'admin3', '斟茶递水', '0');
INSERT INTO `t_user` VALUES ('5', '1', '2017-02-06 11:52:12', '新建冻结不行么', '2017-02-18 16:28:04', '2017-01-01 00:00:00', 'admin4', 'e76f1293fec32b13ee1b43951a5179bf11dbae1d', '18826137274', 'admin4', '管理员', '0');
INSERT INTO `t_user` VALUES ('6', '0', '2017-02-06 14:10:27', 'admin5', '2017-02-24 15:43:17', '2017-02-06 00:00:00', 'admin5', 'df1da040fa651b871219bb22af9c060b8e71ea6a', '18826137274', 'admin5', 'admin5', '0');
INSERT INTO `t_user` VALUES ('7', '1', '2017-02-17 00:45:56', 'admin6', '2017-02-17 00:45:56', '2017-02-17 00:00:00', 'admin6', '029939f71abfa3cde9ca2807eb8916fa3d75d656', '18826137274', 'admin6', 'gg', '0');

-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  KEY `FKa9c8iiy6ut0gnx491fqx4pxam` (`role_id`),
  KEY `FKq5un6x7ecoef5w1n39cop66kl` (`user_id`),
  CONSTRAINT `FKa9c8iiy6ut0gnx491fqx4pxam` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FKq5un6x7ecoef5w1n39cop66kl` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('2', '1');
INSERT INTO `t_user_role` VALUES ('2', '2');
INSERT INTO `t_user_role` VALUES ('3', '1');
INSERT INTO `t_user_role` VALUES ('6', '1');
INSERT INTO `t_user_role` VALUES ('1', '1');
INSERT INTO `t_user_role` VALUES ('1', '2');
