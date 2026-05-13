/*
 Navicat Premium Data Transfer

 Source Server         : myfurn
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : xfs_ai_tourism_system

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 09/05/2026 15:53:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for reserve_record
-- ----------------------------
DROP TABLE IF EXISTS `reserve_record`;
CREATE TABLE `reserve_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单流水号',
  `tourist_id` bigint NOT NULL COMMENT '游客ID',
  `area_id` bigint NOT NULL COMMENT '景区ID',
  `spot_id` bigint NULL DEFAULT NULL COMMENT '具体景点ID(可为空)',
  `slot_id` bigint NOT NULL COMMENT '预约时段ID',
  `reserve_date` date NOT NULL COMMENT '游玩日期',
  `pay_amount` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '支付总金额',
  `pay_status` tinyint NULL DEFAULT 0 COMMENT '支付状态：0未支付 1已支付 2已退款',
  `verify_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生成的核销二维码字符串',
  `status` tinyint NULL DEFAULT 1 COMMENT '核销状态：1待入园 2已核销 3已取消 4已过期',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '核销更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_verify_code`(`verify_code` ASC) USING BTREE,
  UNIQUE INDEX `uk_active_reserve_once`(`tourist_id`, `area_id`, `slot_id`, `reserve_date`, ((case when `status` = 1 then 1 else NULL end))) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游客门票预约与订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reserve_record
-- ----------------------------
INSERT INTO `reserve_record` VALUES (1, 'XFS17776135340022876', 1, 1, NULL, 1, '2026-05-01', 0.00, 1, NULL, 1, NULL, '2026-05-01 13:32:14', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (2, 'XFS1778055485038b7bf', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, NULL, 1, NULL, '2026-05-06 16:18:05', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (3, 'XFS17780554882410146', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, NULL, 3, NULL, '2026-05-06 16:18:08', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (4, 'XFS17780554889225b79', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, NULL, 3, NULL, '2026-05-06 16:18:08', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (5, 'XFS177805548916041a9', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, NULL, 3, NULL, '2026-05-06 16:18:09', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (6, 'XFS177805548932954b9', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, NULL, 3, NULL, '2026-05-06 16:18:09', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (7, 'XFS1234567890', 1, 1, NULL, 1, '2026-05-06', 50.00, 1, 'V-123456', 3, NULL, '2026-05-06 16:40:50', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (10, 'XFS1778057219088aea0', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-71D83592', 3, NULL, '2026-05-06 16:46:59', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (11, 'XFS1778057244189e52a', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-9A02234D', 3, NULL, '2026-05-06 16:47:24', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (12, 'XFS1778057633165021b', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-91870DBA', 2, NULL, '2026-05-06 16:53:53', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (13, 'XFS17780576369385eb1', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-2920D0C4', 2, NULL, '2026-05-06 16:53:56', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (14, 'XFS1778057638118ab38', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-96612115', 2, NULL, '2026-05-06 16:53:58', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (15, 'XFS1778057653601dbb1', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-4CD96604', 2, NULL, '2026-05-06 16:54:13', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (16, 'XFS177805774804994f6', 1, 1, NULL, 1, '2026-05-06', 0.00, 1, 'V-102BB8B6', 2, NULL, '2026-05-06 16:55:48', '2026-05-06 17:14:41');
INSERT INTO `reserve_record` VALUES (17, 'XFS1778136270830054a', 1, 1, NULL, 1, '2026-05-07', 0.00, 1, 'V-5D15BFDF', 1, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for scenic_area
-- ----------------------------
DROP TABLE IF EXISTS `scenic_area`;
CREATE TABLE `scenic_area`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '景区名称',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '景区官方介绍',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属县市',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '景区等级',
  `tel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '咨询电话',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '景区封面图URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 2停用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `reserve_count` int NULL DEFAULT 0 COMMENT '预约量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '雪峰山五大景区表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scenic_area
-- ----------------------------
INSERT INTO `scenic_area` VALUES (1, '雪峰山国家森林公园', '原生态森林景区，天然氧吧，红色森林康养基地', '怀化洪江市', '3A', '0745-0000000', NULL, 1, '2026-05-01 11:00:24', 1250, 300);
INSERT INTO `scenic_area` VALUES (2, '穿岩山景区', '我国首个民企申报成功的国家级森林公园，森林覆盖率86.3%', '怀化溆浦县', '4A', '0745-3506661', NULL, 1, '2026-05-01 11:00:24', 3800, 850);
INSERT INTO `scenic_area` VALUES (3, '山背花瑶梯田', '绵延8公里、面积达6万多亩的梯田，世界农耕文化的活化石', '怀化溆浦县', '3A', '0745-3670779', NULL, 1, '2026-05-01 11:00:24', 640, 120);
INSERT INTO `scenic_area` VALUES (4, '阳雀坡抗战古村', '由6座四合院组成的古村落，1945年雪峰山会战指挥中心所在地', '怀化溆浦县', '3A', '0745-7846333', NULL, 1, '2026-05-01 11:00:24', 2100, 500);
INSERT INTO `scenic_area` VALUES (5, '虎形山大花瑶景区', '平均海拔1350米，花瑶民族文化鲜明，夏季避暑胜地', '邵阳隆回县', '4A', '0739-8188150', NULL, 1, '2026-05-01 11:00:24', 980, 210);

-- ----------------------------
-- Table structure for scenic_spot
-- ----------------------------
DROP TABLE IF EXISTS `scenic_spot`;
CREATE TABLE `scenic_spot`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_id` bigint NOT NULL COMMENT '所属景区ID，关联scenic_area表',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '景点名称',
  `intro` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '景点详细介绍',
  `price` decimal(8, 2) NULL DEFAULT NULL COMMENT '门票价格(元)',
  `open_time` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '开放时间',
  `spot_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '景点图片URL',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 2下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '景区内部景点表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scenic_spot
-- ----------------------------
INSERT INTO `scenic_spot` VALUES (1, 1, '英雄山', '红色爬山步道，森林康养核心区', 30.00, '08:00-17:30', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (2, 1, '原始竹海', '天然竹林，休闲散步好去处', 20.00, '全天开放', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (3, 2, '枫香瑶寨', '云上之都，环形古瑶寨+仙境泳池+篝火晚会', 45.00, '08:00-20:00', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (4, 2, '山鬼玻璃栈道', '高空玻璃观景栈道，5D特效体验', 55.00, '08:00-18:00', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (5, 2, '穿岩峰', '核心景区，山高、峰秀、石奇、洞幽、林深', 40.00, '08:00-17:30', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (6, 3, '枫木坳观景台', '山背梯田最佳观赏点，一览无余连片梯田', 25.00, '全天开放', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (7, 3, '星空云舍', '海拔1300余米山顶别墅，躺着看梯田和云海', 128.00, '全天开放', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (8, 4, '清朝六院落', '明清建筑风格的6座四合院，保存完整的古村落', 20.00, '09:00-17:00', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (9, 4, '雪峰山会战地下指挥所', '连环密洞，声光技术展示抗战故事', 25.00, '09:00-17:00', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (10, 5, '崇木凼花瑶古寨', '中国花瑶第一村，百年古树林', 35.00, '08:00-19:00', NULL, 1, '2026-05-01 11:00:24');
INSERT INTO `scenic_spot` VALUES (11, 5, '旺溪瀑布群', '原始次森林覆盖率90.7%，避暑胜地', 40.00, '08:00-17:30', NULL, 1, '2026-05-01 11:00:24');

-- ----------------------------
-- Table structure for spot_comment
-- ----------------------------
DROP TABLE IF EXISTS `spot_comment`;
CREATE TABLE `spot_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `area_id` bigint NOT NULL COMMENT '景区ID',
  `tourist_id` bigint NOT NULL COMMENT '游客ID',
  `score` tinyint NOT NULL DEFAULT 5 COMMENT '评分：1-5星',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '文字评价内容',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常展示 2违规隐藏',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '游客游玩评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of spot_comment
-- ----------------------------

-- ----------------------------
-- Table structure for sys_admin
-- ----------------------------
DROP TABLE IF EXISTS `sys_admin`;
CREATE TABLE `sys_admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'manager' COMMENT '角色：admin/manager',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统后台管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_admin
-- ----------------------------
INSERT INTO `sys_admin` VALUES (1, 'admin', '$2a$10$Xi3R0HGIewq22zVaT4x3Ue.JH.hjCHbblLah.K75KV..JbUzN1aTK', 'admin', '2026-05-01 11:00:24');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人账号',
  `action_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作动作，如：删除景点',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统后台操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for time_slot
-- ----------------------------
DROP TABLE IF EXISTS `time_slot`;
CREATE TABLE `time_slot`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `slot_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '时段名称，如 08:00-12:00',
  `max_people` int NOT NULL COMMENT '该时段最大预约名额',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约时段限流表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of time_slot
-- ----------------------------
INSERT INTO `time_slot` VALUES (1, '08:00-10:00', 500, '2026-05-01 11:00:24');
INSERT INTO `time_slot` VALUES (2, '10:00-12:00', 800, '2026-05-01 11:00:24');
INSERT INTO `time_slot` VALUES (3, '13:00-15:00', 800, '2026-05-01 11:00:24');
INSERT INTO `time_slot` VALUES (4, '15:00-17:00', 500, '2026-05-01 11:00:24');

-- ----------------------------
-- Table structure for tour_route
-- ----------------------------
DROP TABLE IF EXISTS `tour_route`;
CREATE TABLE `tour_route`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '路线标题，如：雪峰山避暑两日游',
  `days` int NULL DEFAULT NULL COMMENT '游玩天数',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '路线详细节点说明(支持富文本)',
  `recommend_index` tinyint NULL DEFAULT 5 COMMENT '推荐指数(1-5)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '官方推荐游玩路线表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tour_route
-- ----------------------------
INSERT INTO `tour_route` VALUES (1, '雪峰山抗战文化与梯田风光2日游', 2, 'Day1: 阳雀坡抗战古村 -> 枫香瑶寨。Day2: 山背花瑶梯田 -> 观景台看云海。', 5, '2026-05-01 11:00:24');
INSERT INTO `tour_route` VALUES (2, '森林氧吧徒步1日游', 1, '雪峰山国家森林公园 -> 英雄山爬山 -> 原始竹海洗肺之旅。', 4, '2026-05-01 11:00:24');

-- ----------------------------
-- Table structure for tourist_user
-- ----------------------------
DROP TABLE IF EXISTS `tourist_user`;
CREATE TABLE `tourist_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信用户唯一标识',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系手机号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '小程序游客表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tourist_user
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
