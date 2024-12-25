/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 80012
Source Host           : localhost:3306
Source Database       : accounting_system

Target Server Type    : MYSQL
Target Server Version : 80012
File Encoding         : 65001

Date: 2024-11-19 21:12:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类类型：INCOME收入/EXPENSE支出',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统预设',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name_type` (`name`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES ('1', '工资', 'INCOME', '月度工资收入', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('2', '奖金', 'INCOME', '奖金收入', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('3', '投资收益', 'INCOME', '投资理财收入', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('4', '其他收入', 'INCOME', '其他类型收入', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('5', '餐饮', 'EXPENSE', '日常饮食支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('6', '交通', 'EXPENSE', '交通出行费用', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('7', '购物', 'EXPENSE', '日常购物支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('8', '居住', 'EXPENSE', '房租水电等支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('9', '医疗', 'EXPENSE', '医疗健康支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('10', '教育', 'EXPENSE', '学习培训支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('11', '娱乐', 'EXPENSE', '休闲娱乐支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');
INSERT INTO `categories` VALUES ('12', '其他支出', 'EXPENSE', '其他类型支出', '1', '1', '2024-11-19 19:58:27', '2024-11-19 19:58:27');

-- ----------------------------
-- Table structure for records
-- ----------------------------
DROP TABLE IF EXISTS `records`;
CREATE TABLE `records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '记录类型：INCOME收入/EXPENSE支出',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `date` datetime NOT NULL COMMENT '记录日期',
  `description` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '记录描述',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='记账记录表';

-- ----------------------------
-- Records of records
-- ----------------------------
