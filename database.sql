/*
 Navicat Premium Data Transfer

 Source Server         : Localhost
 Source Server Type    : MySQL
 Source Server Version : 80000
 Source Host           : localhost:3306
 Source Schema         : campus_sport

 Target Server Type    : MySQL
 Target Server Version : 80000
 File Encoding         : 65001

 Date: 03/01/2026 10:45:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. Create Database
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `campus_sport` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `campus_sport`;

-- ----------------------------
-- 2. Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名/学号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`) VALUES ('admin', '123456', '系统管理员', 'ADMIN');
INSERT INTO `sys_user` (`username`, `password`, `name`, `role`) VALUES ('1001', '123456', '张三', 'USER');

-- ----------------------------
-- 3. Table structure for venue
-- ----------------------------
DROP TABLE IF EXISTS `venue`;
CREATE TABLE `venue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '场馆名称',
  `type` varchar(50) NOT NULL COMMENT '场馆类型',
  `location` varchar(200) DEFAULT NULL COMMENT '位置',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1正常, 0维护',
  `open_days` varchar(50) DEFAULT NULL COMMENT '开放日期(1-7, 逗号分隔)',
  `open_start_time` int DEFAULT 9 COMMENT '开放开始时间(0-23)',
  `open_end_time` int DEFAULT 22 COMMENT '开放结束时间(0-23)',
  `default_max_people` int DEFAULT 20 COMMENT '默认最大人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场馆表';

-- ----------------------------
-- 4. Table structure for venue_schedule
-- ----------------------------
DROP TABLE IF EXISTS `venue_schedule`;
CREATE TABLE `venue_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `venue_id` bigint NOT NULL COMMENT '场馆ID',
  `date` date NOT NULL COMMENT '日期',
  `time_slot` int NOT NULL COMMENT '时段(9代表9:00-10:00)',
  `max_people` int NOT NULL DEFAULT '0' COMMENT '最大人数',
  `current_people` int NOT NULL DEFAULT '0' COMMENT '当前已约人数',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态: 1开放, 0关闭',
  PRIMARY KEY (`id`),
  KEY `idx_venue_date` (`venue_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场馆排班表';

-- ----------------------------
-- 5. Table structure for reservation
-- ----------------------------
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `venue_id` bigint NOT NULL COMMENT '场馆ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态: 0已预约, 1已完成, 2已取消, 3已拒绝',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';

-- ----------------------------
-- 6. Table structure for sport_record
-- ----------------------------
DROP TABLE IF EXISTS `sport_record`;
CREATE TABLE `sport_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sport_type` varchar(50) NOT NULL COMMENT '运动类型',
  `duration` int NOT NULL COMMENT '时长(分钟)',
  `calories` double NOT NULL COMMENT '消耗卡路里(kcal)',
  `record_date` date NOT NULL COMMENT '运动日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动数据表';

-- ----------------------------
-- 7. Table structure for health_record
-- ----------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `height` double DEFAULT NULL COMMENT '身高(cm)',
  `weight` double DEFAULT NULL COMMENT '体重(kg)',
  `record_date` date NOT NULL COMMENT '记录日期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据表';

-- ----------------------------
-- 8. Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `time` bigint DEFAULT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';

SET FOREIGN_KEY_CHECKS = 1;
