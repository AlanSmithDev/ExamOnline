/*
Navicat MySQL Data Transfer

Source Server         : 210.44.185.50
Source Server Version : 80028
Source Host           : 210.44.185.50:3306
Source Database       : eo

Target Server Type    : MYSQL
Target Server Version : 80028
File Encoding         : 65001

Date: 2022-04-18 20:52:25
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `eo_depart`
-- ----------------------------
DROP TABLE IF EXISTS `eo_depart`;
CREATE TABLE `eo_depart` (
  `id` bigint NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '鐖堕儴闂↖D',
  `code` varchar(8) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `dept_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '部门ID',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_depart
-- ----------------------------
INSERT INTO `eo_depart` VALUES ('3964581068226560', '高一1班', '11223344', '2022-03-20 22:33:50', '2022-03-20 22:33:50', '高一1班');
INSERT INTO `eo_depart` VALUES ('224442771826048', '高一2班', '57818460', '2022-04-18 15:18:45', '2022-04-18 15:18:45', '高一2班');
INSERT INTO `eo_depart` VALUES ('224443067524482', '高一3班', '20993999', '2022-04-18 15:18:49', '2022-04-18 15:18:49', '高一3班');
INSERT INTO `eo_depart` VALUES ('224443457070466', '高二1班', '84750466', '2022-04-18 15:18:55', '2022-04-18 15:18:55', '高二1班');
INSERT INTO `eo_depart` VALUES ('224443812013442', '高二2班', '02278782', '2022-04-18 15:19:01', '2022-04-18 15:19:01', '高二2班');
INSERT INTO `eo_depart` VALUES ('224444118328706', '高二3班', '29575136', '2022-04-18 15:19:05', '2022-04-18 15:19:05', '高二3班');
INSERT INTO `eo_depart` VALUES ('224444491621761', '高三1班', '20818317', '2022-04-18 15:19:11', '2022-04-18 15:19:11', '高三1班');
INSERT INTO `eo_depart` VALUES ('224444797281665', '高三2班', '48768423', '2022-04-18 15:19:16', '2022-04-18 15:19:16', '高三2班');
INSERT INTO `eo_depart` VALUES ('224445042124160', '高三3班', '24642545', '2022-04-18 15:19:20', '2022-04-18 15:19:20', '高三3班');

-- ----------------------------
-- Table structure for `eo_depart_member`
-- ----------------------------
DROP TABLE IF EXISTS `eo_depart_member`;
CREATE TABLE `eo_depart_member` (
  `id` bigint NOT NULL,
  `depart_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `departId` (`depart_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_depart_member
-- ----------------------------
INSERT INTO `eo_depart_member` VALUES ('225505290347904', '224444491621761', '3953496957202436');
INSERT INTO `eo_depart_member` VALUES ('5562541484912640', '3964581068226560', '3953496957202434');
INSERT INTO `eo_depart_member` VALUES ('8521743518900224', '3964581068226560', '3953496957202433');

-- ----------------------------
-- Table structure for `eo_exam`
-- ----------------------------
DROP TABLE IF EXISTS `eo_exam`;
CREATE TABLE `eo_exam` (
  `id` bigint NOT NULL,
  `join_type` int NOT NULL,
  `paper_id` bigint NOT NULL DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `state` int NOT NULL DEFAULT '0',
  `total_score` int NOT NULL DEFAULT '0',
  `time_limit` tinyint(1) NOT NULL DEFAULT '0',
  `face_check` tinyint(1) NOT NULL DEFAULT '0',
  `join_limit` int NOT NULL DEFAULT '0',
  `submit_limit` int NOT NULL,
  `duration` int NOT NULL DEFAULT '0',
  `depart_ids` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  `creator` bigint NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_exam
-- ----------------------------
INSERT INTO `eo_exam` VALUES ('225737641137536', '1', '225750801319296', '高一年级第一次月考', '高一年级第一次月考', '1', '20', '1', '0', '0', '0', '60', '3964581068226560,224442771826048,224443067524482', '3953496957202434', '2022-04-17 20:00:00', '2022-04-17 21:00:00', '2022-04-18 20:48:03', '2022-04-18 20:51:24');

-- ----------------------------
-- Table structure for `eo_exam_question_intelligent`
-- ----------------------------
DROP TABLE IF EXISTS `eo_exam_question_intelligent`;
CREATE TABLE `eo_exam_question_intelligent` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `repo_id` bigint NOT NULL,
  `question_type` int NOT NULL DEFAULT '0',
  `amount` int NOT NULL DEFAULT '0',
  `score` int NOT NULL DEFAULT '0',
  `difficulty` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `examId` (`exam_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_exam_question_intelligent
-- ----------------------------
INSERT INTO `eo_exam_question_intelligent` VALUES ('225750797882752', '225737641137536', '225437492835712', '0', '2', '10', '1');

-- ----------------------------
-- Table structure for `eo_exam_question_manual`
-- ----------------------------
DROP TABLE IF EXISTS `eo_exam_question_manual`;
CREATE TABLE `eo_exam_question_manual` (
  `id` bigint NOT NULL,
  `exam_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `score` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `examId` (`exam_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_exam_question_manual
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_file`
-- ----------------------------
DROP TABLE IF EXISTS `eo_file`;
CREATE TABLE `eo_file` (
  `id` bigint NOT NULL,
  `ext` varchar(111) DEFAULT NULL,
  `size` int NOT NULL,
  `uploader` bigint NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_file
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_log_access`
-- ----------------------------
DROP TABLE IF EXISTS `eo_log_access`;
CREATE TABLE `eo_log_access` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `ip` varchar(255) NOT NULL DEFAULT '',
  `user_agent` varchar(255) NOT NULL DEFAULT '',
  `uri` varchar(255) NOT NULL DEFAULT '',
  `method` varchar(255) NOT NULL,
  `params` longtext,
  `resp_code` int NOT NULL DEFAULT '0',
  `resp_body` longtext,
  `error` varchar(255) DEFAULT NULL,
  `time` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time` (`create_time`) USING BTREE,
  KEY `user` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_log_access
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_log_login`
-- ----------------------------
DROP TABLE IF EXISTS `eo_log_login`;
CREATE TABLE `eo_log_login` (
  `id` bigint NOT NULL,
  `type` tinyint NOT NULL DEFAULT '0',
  `msg` varchar(255) NOT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `ip` varchar(255) NOT NULL,
  `browser` varchar(255) NOT NULL,
  `os` varchar(255) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time` (`create_time`) USING BTREE,
  KEY `user` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_log_login
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_log_operation`
-- ----------------------------
DROP TABLE IF EXISTS `eo_log_operation`;
CREATE TABLE `eo_log_operation` (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `ip` varchar(255) NOT NULL DEFAULT '',
  `user_agent` varchar(255) NOT NULL DEFAULT '',
  `uri` varchar(255) NOT NULL DEFAULT '',
  `method` varchar(255) NOT NULL,
  `params` longtext,
  `state` int NOT NULL DEFAULT '1',
  `type` int NOT NULL DEFAULT '0',
  `time` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `time` (`create_time`) USING BTREE,
  KEY `user` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_log_operation
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_message`
-- ----------------------------
DROP TABLE IF EXISTS `eo_message`;
CREATE TABLE `eo_message` (
  `id` bigint NOT NULL,
  `title` varchar(255) DEFAULT '',
  `content` longtext,
  `create_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_message
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_message_record`
-- ----------------------------
DROP TABLE IF EXISTS `eo_message_record`;
CREATE TABLE `eo_message_record` (
  `id` bigint NOT NULL,
  `mid` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `read_time` datetime DEFAULT NULL,
  `is_delete` tinyint NOT NULL DEFAULT '0',
  `delete_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mid` (`mid`) USING BTREE,
  KEY `userId` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_message_record
-- ----------------------------

-- ----------------------------
-- Table structure for `eo_paper`
-- ----------------------------
DROP TABLE IF EXISTS `eo_paper`;
CREATE TABLE `eo_paper` (
  `id` bigint NOT NULL,
  `title` varchar(255) NOT NULL DEFAULT '',
  `total_score` int NOT NULL DEFAULT '0',
  `time_limit` tinyint(1) NOT NULL DEFAULT '0',
  `face_check` tinyint(1) NOT NULL DEFAULT '0',
  `join_limit` int NOT NULL DEFAULT '0',
  `submit_limit` int NOT NULL DEFAULT '0',
  `state` tinyint NOT NULL DEFAULT '0',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_paper
-- ----------------------------
INSERT INTO `eo_paper` VALUES ('225750801319296', '高一年级第一次月考', '20', '1', '0', '0', '0', '0', '2022-04-17 20:00:00', '2022-04-17 21:00:00', '2022-04-18 20:51:24', '2022-04-18 20:51:24');

-- ----------------------------
-- Table structure for `eo_paper_answer`
-- ----------------------------
DROP TABLE IF EXISTS `eo_paper_answer`;
CREATE TABLE `eo_paper_answer` (
  `id` bigint NOT NULL,
  `paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `is_right` tinyint(1) NOT NULL DEFAULT '0',
  `content` text,
  `score` int NOT NULL DEFAULT '0',
  `corrector` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_paper_answer
-- ----------------------------
INSERT INTO `eo_paper_answer` VALUES ('225750805251457', '225750801319296', '225749637657984', '3953496957202433', '0', null, '0', '0', '2022-04-18 20:51:24', '2022-04-18 20:51:24');
INSERT INTO `eo_paper_answer` VALUES ('225750805251458', '225750801319296', '225746780681600', '3953496957202433', '0', null, '0', '0', '2022-04-18 20:51:24', '2022-04-18 20:51:24');
INSERT INTO `eo_paper_answer` VALUES ('225750805251460', '225750801319296', '225749637657984', '3953496957202434', '0', null, '0', '0', '2022-04-18 20:51:24', '2022-04-18 20:51:24');
INSERT INTO `eo_paper_answer` VALUES ('225750805251461', '225750801319296', '225746780681600', '3953496957202434', '0', null, '0', '0', '2022-04-18 20:51:24', '2022-04-18 20:51:24');

-- ----------------------------
-- Table structure for `eo_paper_question`
-- ----------------------------
DROP TABLE IF EXISTS `eo_paper_question`;
CREATE TABLE `eo_paper_question` (
  `id` bigint NOT NULL,
  `paper_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `score` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_paper_question
-- ----------------------------
INSERT INTO `eo_paper_question` VALUES ('225750802564480', '225750801319296', '225749637657984', '10');
INSERT INTO `eo_paper_question` VALUES ('225750802564481', '225750801319296', '225746780681600', '10');

-- ----------------------------
-- Table structure for `eo_paper_record`
-- ----------------------------
DROP TABLE IF EXISTS `eo_paper_record`;
CREATE TABLE `eo_paper_record` (
  `id` bigint NOT NULL,
  `paper_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `score` int NOT NULL DEFAULT '0',
  `state` tinyint NOT NULL DEFAULT '0',
  `start_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `end_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `create_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `update_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_paper_record
-- ----------------------------
INSERT INTO `eo_paper_record` VALUES ('225750805251456', '225750801319296', '3953496957202433', '0', '0', '2022-04-18 20:51:24', '2022-03-10 00:00:00', '2022-04-18 20:51:24', '2022-04-18 20:51:24');
INSERT INTO `eo_paper_record` VALUES ('225750805251459', '225750801319296', '3953496957202434', '0', '0', '2022-04-18 20:51:24', '2022-03-10 00:00:00', '2022-04-18 20:51:24', '2022-04-18 20:51:24');

-- ----------------------------
-- Table structure for `eo_question`
-- ----------------------------
DROP TABLE IF EXISTS `eo_question`;
CREATE TABLE `eo_question` (
  `id` bigint NOT NULL COMMENT '棰樼洰ID',
  `type` int NOT NULL DEFAULT '0' COMMENT '题目类型',
  `difficulty` int NOT NULL,
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '棰樼洰鍐呭',
  `voice_file` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `repo_id` bigint NOT NULL COMMENT '棰樼洰鎵€灞炰粨搴揑D',
  `parent_id` bigint NOT NULL DEFAULT '0',
  `creator` bigint NOT NULL COMMENT '鍒涘缓鑰匢D',
  `analysis` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '棰樼洰瑙ｆ瀽',
  `analysis_video` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '' COMMENT '棰樼洰瑙ｆ瀽瑙嗛',
  `create_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `update_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  PRIMARY KEY (`id`),
  KEY `repoId` (`repo_id`) USING BTREE,
  KEY `parentId` (`parent_id`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_question
-- ----------------------------
INSERT INTO `eo_question` VALUES ('225749637657984', '0', '1', '', '', '225437492835712', '0', '3953496957202434', '', '', '2022-04-18 20:51:06', '2022-04-18 20:51:06');
INSERT INTO `eo_question` VALUES ('225746780681600', '0', '1', '<p>函数y=1x-1在[2,3]上的最小值为( )<br/></p>', '', '225437492835712', '0', '3953496957202434', '', '', '2022-04-18 20:50:23', '2022-04-18 20:50:23');

-- ----------------------------
-- Table structure for `eo_question_answer`
-- ----------------------------
DROP TABLE IF EXISTS `eo_question_answer`;
CREATE TABLE `eo_question_answer` (
  `id` bigint NOT NULL COMMENT '棰勯€夌瓟妗圛D',
  `content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '棰勯€夌瓟妗堝唴瀹?',
  `is_right` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为正确答案',
  `question_id` bigint NOT NULL COMMENT '鎵€灞為鐩甀D',
  `update_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `create_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  PRIMARY KEY (`id`),
  KEY `questionId` (`question_id`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_question_answer
-- ----------------------------
INSERT INTO `eo_question_answer` VALUES ('225749637657988', '120.25万元', '0', '225749637657984', '2022-04-18 20:51:06', '2022-04-18 20:51:06');
INSERT INTO `eo_question_answer` VALUES ('225749637657987', '120万元', '1', '225749637657984', '2022-04-18 20:51:06', '2022-04-18 20:51:06');
INSERT INTO `eo_question_answer` VALUES ('225749637657986', '60万元', '0', '225749637657984', '2022-04-18 20:51:06', '2022-04-18 20:51:06');
INSERT INTO `eo_question_answer` VALUES ('225749637657985', '90万元', '0', '225749637657984', '2022-04-18 20:51:06', '2022-04-18 20:51:06');
INSERT INTO `eo_question_answer` VALUES ('225746780747138', '-12', '0', '225746780681600', '2022-04-18 20:50:23', '2022-04-18 20:50:23');
INSERT INTO `eo_question_answer` VALUES ('225746780747137', '13', '0', '225746780681600', '2022-04-18 20:50:23', '2022-04-18 20:50:23');
INSERT INTO `eo_question_answer` VALUES ('225746780747136', '12', '1', '225746780681600', '2022-04-18 20:50:23', '2022-04-18 20:50:23');
INSERT INTO `eo_question_answer` VALUES ('225746780681601', '2', '0', '225746780681600', '2022-04-18 20:50:23', '2022-04-18 20:50:23');

-- ----------------------------
-- Table structure for `eo_question_repo`
-- ----------------------------
DROP TABLE IF EXISTS `eo_question_repo`;
CREATE TABLE `eo_question_repo` (
  `id` bigint NOT NULL COMMENT '妫版ê绨盜D',
  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '题库名称',
  `creator` bigint NOT NULL COMMENT '閸掓稑缂撻懓鍖',
  `password` varchar(255) DEFAULT NULL,
  `type` tinyint(1) DEFAULT '0',
  `update_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  `create_time` datetime NOT NULL DEFAULT '2022-03-10 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `creator` (`creator`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_question_repo
-- ----------------------------
INSERT INTO `eo_question_repo` VALUES ('225437492835712', '高中数学', '3953496957202434', '', '0', '2022-04-18 19:31:43', '2022-04-18 19:31:43');
INSERT INTO `eo_question_repo` VALUES ('7597411799244800', '私有题库', '3953496957202433', '', '0', '2022-03-30 23:09:24', '2022-03-30 23:09:24');

-- ----------------------------
-- Table structure for `eo_user`
-- ----------------------------
DROP TABLE IF EXISTS `eo_user`;
CREATE TABLE `eo_user` (
  `id` varchar(32) NOT NULL,
  `user_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `real_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `role` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'student',
  `avatar` varchar(255) DEFAULT NULL,
  `birthday` date DEFAULT NULL,
  `gender` int NOT NULL DEFAULT '0',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `state` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`user_name`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of eo_user
-- ----------------------------
INSERT INTO `eo_user` VALUES ('3953496957202436', 'student', '学生', '$2a$04$sa.hsk9ES7FFafT36M7/aeWmjZz3UlW2NL2Wr01fD2oxC6f4aEKAG', 'student', 'https://examonline-bucket.oss-accelerate.aliyuncs.com/avatar_BZltydWcPKSFUZRE.jpg', '2020-10-01', '1', '2022-03-15 21:15:48', '2022-04-18 19:18:24', '0');
INSERT INTO `eo_user` VALUES ('3953496957202434', 'teacher', '教师', '$2a$04$SCUKlHdb7SSDiCQ17Ps0FeaHk9loNVopST0sCZ3mhaUD6vKt5E08S', 'teacher', 'https://examonline-bucket.oss-accelerate.aliyuncs.com/avatar_JAkaCnleMypQgxUo.jpg', '2022-04-07', '2', '2022-03-06 19:26:41', '2022-04-18 19:14:29', '0');
INSERT INTO `eo_user` VALUES ('3953496957202433', 'admin', '管理员', '$2a$04$7ex2T82e31HycKmtPowsfeyuri85Qbqhn49FFH7NcVmjQJGF3SGa2', 'admin', 'https://examonline-bucket.oss-accelerate.aliyuncs.com/avatar_dxyeHmyFWVKPutGl.png', '2022-01-01', '1', '2022-03-06 19:26:32', '2022-04-16 23:04:33', '0');
